package bearmaps.proj2d.server.handler.impl;

import bearmaps.proj2d.AugmentedStreetMapGraph;
import bearmaps.proj2d.MapServerInitializer;
import bearmaps.proj2d.server.handler.APIRouteHandler;
import bearmaps.proj2d.server.handler.APIRouteHandlerFactory;
import spark.Request;
import spark.Response;
import bearmaps.proj2d.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bearmaps.proj2d.utils.Constants.*;
import static java.lang.Math.*;

/**
 * Handles requests from the web browser for map images. These images
 * will be rastered into one large image to be displayed to the user.
 * @author rahul, Josh Hug, Jasmine Tong-Seely
 */
public class RasterAPIHandler extends APIRouteHandler<Map<String, Double>, Map<String, Object>> {
    private static double ullon;
    private static double lrlon;
    private static double width;
    private static double height;
    private static double ullat;
    private static double lrlat;
    private static double requestResolution;
    private static int depth;
    private static double worldWidth = ROOT_LRLON - ROOT_ULLON;
    private static double worldHeight = ROOT_ULLAT - ROOT_LRLAT;
    private static double widthPerTile;
    private static double heightPerTile;

    private static int k;

    private static int leftX;
    private static int rightX;
    private static int topY;
    private static int bottomY;

    private static String[][] renderGrid;
    private static double raster_ul_lon;
    private static double raster_ul_lat;
    private static double raster_lr_lon;
    private static double raster_lr_lat;
    private static boolean query_success;

    private static Map<String, Object> returnMap;

    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside RasterAPIHandler.processRequest(). <br>
     * ullat : upper left corner latitude, <br> ullon : upper left corner longitude, <br>
     * lrlat : lower right corner latitude,<br> lrlon : lower right corner longitude <br>
     * w : user viewport window width in pixels,<br> h : user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
            "lrlon", "w", "h"};

    /**
     * The result of rastering must be a map containing all of the
     * fields listed in the comments for RasterAPIHandler.processRequest.
     **/
    private static final String[] REQUIRED_RASTER_RESULT_PARAMS = {"render_grid", "raster_ul_lon",
            "raster_ul_lat", "raster_lr_lon", "raster_lr_lat", "depth", "query_success"};


    @Override
    protected Map<String, Double> parseRequestParams(Request request) {
        return getRequestParams(request, REQUIRED_RASTER_REQUEST_PARAMS);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param requestParams Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @param response : Not used by this function. You may ignore.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image;
     *                    can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    @Override
    public Map<String, Object> processRequest(Map<String, Double> requestParams, Response response) {
        processInputParams(requestParams); //assigns class variables to input params
        requestResolution = calcLonDPP(lrlon, ullon, width); //calculates the resolution needed based on input params
        calcDepth(requestResolution); //determines the required depth of raster images
        calcK(depth);
        calcWidthAndHeightPerTile(depth);
        findTileVals();
        checkValidQuery();

        if (query_success) {
            cornerCaseAdjustments();
            createRenderGrid();
            calcReturnVals();
            createReturnMap();
            return returnMap;
        } else {
            return queryFail();
        }
    }

    private static void processInputParams(Map<String, Double> requestParams) {
        ullon = requestParams.get("ullon");
        lrlon = requestParams.get("lrlon");
        width = requestParams.get("w");
        height = requestParams.get("h");
        ullat = requestParams.get("ullat");
        lrlat = requestParams.get("lrlat");
    }

    //returns the LonDPP in lon/px
    private static double calcLonDPP(double lrlon, double ullon, double width) {
        double xDist = lrlon - ullon;
        return xDist/width;
    }


    private static double imgLonDPPbyDepth(int depth) {
        double xDist = ROOT_LRLON - ROOT_ULLON;
        double d0LonDPP = xDist / TILE_SIZE;
        double divideFactor = pow(2, depth);
        return d0LonDPP / divideFactor;
    }

    private static void calcDepth(double requestResolution) {
        if (requestResolution <= imgLonDPPbyDepth(7)) {
            depth = 7;
        } else {
            int d = 0;
            while (requestResolution < imgLonDPPbyDepth(d)) {
                d += 1;
            }
            depth = d;
        }
    }

    private static void calcK(int depth) {
        k = (int) (pow(2, depth) - 1);
    }

    private static void calcWidthAndHeightPerTile(int depth) {
        double divideFactor = pow(2, depth);
        widthPerTile = worldWidth / divideFactor;
        heightPerTile = worldHeight / divideFactor;
    }

    private static void findTileVals() {
        leftX = (int) floor((ullon - ROOT_ULLON) / widthPerTile);
        rightX = (int) (k - floor((ROOT_LRLON - lrlon) / widthPerTile));
        topY = (int) floor((ROOT_ULLAT - ullat) / heightPerTile);
        bottomY = (int) (k - floor((lrlat - ROOT_LRLAT) / heightPerTile));
    }

    private static void checkValidQuery() {
        if ((ullon > lrlon) || (ullat < lrlat)) { //query box doesn't make sense
            query_success = false;
        } else if (bottomY < 0) { //query box is above the world map
            query_success = false;
        } else if (topY > k) { //query box is below the world map
            query_success = false;
        } else if (rightX < 0) { //query box is to the left of world map
            query_success = false;
        } else if (leftX > k) { //query box is to the right of world map
            query_success = false;
        } else {
            query_success = true;
        }
    }

    private static void cornerCaseAdjustments() {
        if (leftX < 0) {
            leftX = 0;
        }
        if (topY < 0) {
            topY = 0;
        }
        if (rightX > k) {
            rightX = k;
        }
        if (bottomY > k) {
            bottomY = k;
        }
    }

    private static void createRenderGrid() {
        int numRows = bottomY - topY + 1;
        int numCols = rightX - leftX + 1;
        renderGrid = new String[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            int rowNum = topY + r;
            for (int c = 0; c < numCols; c++) {
                int colNum = leftX + c;
                String imageName = "d" + depth + "_x" + colNum + "_y" + rowNum + ".png";
                renderGrid[r][c] = imageName;
            }
        }
    }

    private static void calcReturnVals() {
        raster_ul_lon = ROOT_ULLON + (leftX * widthPerTile);
        raster_ul_lat = ROOT_ULLAT - (topY * heightPerTile);
        raster_lr_lon = ROOT_ULLON + ((rightX + 1) * widthPerTile);
        raster_lr_lat = ROOT_ULLAT - ((bottomY + 1) * heightPerTile);
    }

    private static void createReturnMap() {
        returnMap = new HashMap<>();
        returnMap.put("render_grid", renderGrid);
        returnMap.put("raster_ul_lon", raster_ul_lon);
        returnMap.put("raster_ul_lat", raster_ul_lat);
        returnMap.put("raster_lr_lon", raster_lr_lon);
        returnMap.put("raster_lr_lat", raster_lr_lat);
        returnMap.put("depth", depth);
        returnMap.put("query_success", query_success);
    }

    @Override
    protected Object buildJsonResponse(Map<String, Object> result) {
        boolean rasterSuccess = validateRasteredImgParams(result);

        if (rasterSuccess) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writeImagesToOutputStream(result, os);
            String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
            result.put("b64_encoded_image_data", encodedImage);
        }
        return super.buildJsonResponse(result);
    }

    private Map<String, Object> queryFail() {
        Map<String, Object> results = new HashMap<>();
        results.put("render_grid", null);
        results.put("raster_ul_lon", 0);
        results.put("raster_ul_lat", 0);
        results.put("raster_lr_lon", 0);
        results.put("raster_lr_lat", 0);
        results.put("depth", 0);
        results.put("query_success", false);
        return results;
    }

    /**
     * Validates that Rasterer has returned a result that can be rendered.
     * @param rip : Parameters provided by the rasterer
     */
    private boolean validateRasteredImgParams(Map<String, Object> rip) {
        for (String p : REQUIRED_RASTER_RESULT_PARAMS) {
            if (!rip.containsKey(p)) {
                System.out.println("Your rastering result is missing the " + p + " field.");
                return false;
            }
        }
        if (rip.containsKey("query_success")) {
            boolean success = (boolean) rip.get("query_success");
            if (!success) {
                System.out.println("query_success was reported as a failure");
                return false;
            }
        }
        return true;
    }

    /**
     * Writes the images corresponding to rasteredImgParams to the output stream.
     * In Spring 2016, students had to do this on their own, but in 2017,
     * we made this into provided code since it was just a bit too low level.
     */
    private  void writeImagesToOutputStream(Map<String, Object> rasteredImageParams,
                                                  ByteArrayOutputStream os) {
        String[][] renderGrid = (String[][]) rasteredImageParams.get("render_grid");
        int numVertTiles = renderGrid.length;
        int numHorizTiles = renderGrid[0].length;

        BufferedImage img = new BufferedImage(numHorizTiles * Constants.TILE_SIZE,
                numVertTiles * Constants.TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphic = img.getGraphics();
        int x = 0, y = 0;

        for (int r = 0; r < numVertTiles; r += 1) {
            for (int c = 0; c < numHorizTiles; c += 1) {
                graphic.drawImage(getImage(Constants.IMG_ROOT + renderGrid[r][c]), x, y, null);
                x += Constants.TILE_SIZE;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += Constants.TILE_SIZE;
                }
            }
        }

        /* If there is a route, draw it. */
        double ullon = (double) rasteredImageParams.get("raster_ul_lon"); //tiles.get(0).ulp;
        double ullat = (double) rasteredImageParams.get("raster_ul_lat"); //tiles.get(0).ulp;
        double lrlon = (double) rasteredImageParams.get("raster_lr_lon"); //tiles.get(0).ulp;
        double lrlat = (double) rasteredImageParams.get("raster_lr_lat"); //tiles.get(0).ulp;

        final double wdpp = (lrlon - ullon) / img.getWidth();
        final double hdpp = (ullat - lrlat) / img.getHeight();
        AugmentedStreetMapGraph graph = SEMANTIC_STREET_GRAPH;
        List<Long> route = ROUTE_LIST;

        if (route != null && !route.isEmpty()) {
            Graphics2D g2d = (Graphics2D) graphic;
            g2d.setColor(Constants.ROUTE_STROKE_COLOR);
            g2d.setStroke(new BasicStroke(Constants.ROUTE_STROKE_WIDTH_PX,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            route.stream().reduce((v, w) -> {
                g2d.drawLine((int) ((graph.lon(v) - ullon) * (1 / wdpp)),
                        (int) ((ullat - graph.lat(v)) * (1 / hdpp)),
                        (int) ((graph.lon(w) - ullon) * (1 / wdpp)),
                        (int) ((ullat - graph.lat(w)) * (1 / hdpp)));
                return w;
            });
        }

        rasteredImageParams.put("raster_width", img.getWidth());
        rasteredImageParams.put("raster_height", img.getHeight());

        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BufferedImage getImage(String imgPath) {
        BufferedImage tileImg = null;
        if (tileImg == null) {
            try {
                File in = new File(imgPath);
                tileImg = ImageIO.read(in);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return tileImg;
    }

    private static Map<String, Double> createHardCodeExample() {
        Map<String, Double> requestParams = new HashMap<>();
        requestParams.put("ullon", -122.241632);
        requestParams.put("lrlon", -122.24053);
        requestParams.put("w", 892.0);
        requestParams.put("h", 875.0);
        requestParams.put("ullat", 37.87655);
        requestParams.put("lrlat", 37.87548);
        return requestParams;
    }
}
