package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.ArrayList;

public class WorldGenerator {
    private static final int WIDTH = 90;
    private static final int HEIGHT = 48;
    Engine engine;
    Random RANDOM;
    ArrayList<Point> centers = new ArrayList<>();
    TETile[][] tileset;
    Avatar avatar;
    Point teleportLocation;
    int teleportRoomNumber;

    public TETile[][] tileset() {
        return this.tileset;
    }

    public Avatar avatar() {
        return this.avatar;
    }

    public void setAvatar(Avatar av) {
        this.avatar = av;
    }

    public void generateWorldTeleport() {
        int randomRoomNumber = RANDOM.nextInt(centers.size()); //picks a random room from world
        teleportRoomNumber = randomRoomNumber;
        Point randomPoint = centers.get(randomRoomNumber);
        teleportLocation = new Point(randomPoint.x - 1, randomPoint.y - 1);
        tileset[teleportLocation.x()][teleportLocation.y()] = Tileset.TELEPORT;
    }

    private int getWidth(Point lower, Point upper) {
        return upper.x() - lower.x();
    }

    private int getHeight(Point lower, Point upper) {
        return upper.y() - lower.y();
    }

    public void subDivide(TETile[][] tiles, Point lowerBound, Point upperBound) {
        int width = getWidth(lowerBound, upperBound);
        int height = getHeight(lowerBound, upperBound);
        int stopNum = RANDOM.nextInt(5);

        if (stopNum == 1 && (upperBound.x() < 79)) {
            generateRoom(tiles, lowerBound, upperBound);
            return;
        }

        if (width <= 16 || height <= 16) {
            int chance = RANDOM.nextInt(5);
            if (chance == 1) {
                drawSubportion(tiles, lowerBound, upperBound);
            } else {
                generateRoom(tiles, lowerBound, upperBound);
            }
            return;
        }

        int thirdWidth = (int) Math.floor(width / 3);
        int thirdHeight = (int) Math.floor(height / 3);

        int randomX = lowerBound.x + thirdWidth + RANDOM.nextInt(thirdWidth);
        int randomY = lowerBound.y + thirdHeight + RANDOM.nextInt(thirdHeight);

        Point leftLow = lowerBound;
        Point middleLow = new Point(randomX, lowerBound.y());
        Point leftMiddle = new Point(lowerBound.x(), randomY);
        Point middleMiddle = new Point(randomX, randomY);
        Point rightMiddle = new Point(upperBound.x(), randomY);
        Point middleHigh = new Point(randomX, upperBound.y());
        Point rightHigh = upperBound;

        subDivide(tiles, leftLow, middleMiddle);
        subDivide(tiles, middleLow, rightMiddle);
        subDivide(tiles, leftMiddle, middleHigh);
        subDivide(tiles, middleMiddle, rightHigh);
    }

    public void generateRoom(TETile[][] tiles, Point lowerLeft, Point upperRight) {
        int width = getWidth(lowerLeft, upperRight);
        int height = getHeight(lowerLeft, upperRight);
        int lowerX = lowerLeft.x() + RANDOM.nextInt(width - 4);
        int lowerY = lowerLeft.y() + RANDOM.nextInt(height - 4);
        Point roomLowerLeft = new Point(lowerX, lowerY);

        int subWidth = getWidth(roomLowerLeft, upperRight);
        int subHeight = getHeight(roomLowerLeft, upperRight);

        int upperX = lowerX + Math.max(4, RANDOM.nextInt(subWidth));
        int upperY = lowerY + Math.max(4, RANDOM.nextInt(subHeight));
        Point roomUpperRight = new Point(upperX, upperY);

        drawRoom(tiles, roomLowerLeft, roomUpperRight);
        addCenter(roomLowerLeft, roomUpperRight);
    }

    public void addWall(int x, int y, TETile[][] tiles) {
        if (tiles[x][y] == Tileset.FLOOR) {
            return;
        } else {
            tiles[x][y] = Tileset.WALL;
        }
    }

    public void addFloor(int x, int y, TETile[][] tiles) {
        tiles[x][y] = Tileset.FLOOR;
    }

    public void drawRoom(TETile[][] tiles, Point roomLowerLeft, Point roomUpperRight) {
        // Wall Draw
        for (int x = roomLowerLeft.x(); x <= roomUpperRight.x(); x++) {
            addWall(x, roomLowerLeft.y(), tiles);
            addWall(x, roomUpperRight.y(), tiles);
        }

        for (int y = roomLowerLeft.y() + 1; y < roomUpperRight.y(); y++) {
            addWall(roomLowerLeft.x(), y, tiles);
            addWall(roomUpperRight.x(), y, tiles);
        }

        // Floor Draw
        for (int x = roomLowerLeft.x() + 1; x < roomUpperRight.x(); x += 1) {
            for (int y = roomLowerLeft.y() + 1; y < roomUpperRight.y(); y += 1) {
                addFloor(x, y, tiles);
            }
        }
    }


    public void addCenter(Point lowerLeft, Point upperRight) {
        int width = upperRight.x() - lowerLeft.x();
        int height = upperRight.y() - lowerLeft.y();
        int centerX = lowerLeft.x() + (int) Math.floor(width / 2);
        int centerY = lowerLeft.y() + (int) Math.floor(height / 2);
        Point center = new Point(centerX, centerY);
        centers.add(center);
    }

    public void drawRoomNums() {
        Font font = new Font("Avenir", Font.BOLD, 10);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);

        for (int i = 0; i < centers.size(); i++) {
            int x = centers.get(i).x();
            int y = centers.get(i).y();
            String message = "Room " + (i + 1) + "     ";

            StdDraw.text(x + 1, y, message);
        }
        //StdDraw.pause(10);
        //StdDraw.setFont();
    }


    public void hallways(TETile[][] tiles) {
        for (int i = 0; i < centers.size() - 1; i++) {
            int numHallways = RANDOM.nextInt(2) + 1;
            drawHallway(tiles, centers.get(i), centers.get(i + 1));
            if (numHallways > 1) {
                drawHallway(tiles, centers.get(i),
                        centers.get(Math.min(centers.size() - 1, i + 2)));
            }
            if (numHallways > 2) {
                drawHallway(tiles, centers.get(i),
                        centers.get(Math.min(centers.size() - 1, i + 3)));
            }
        }
    }


    public void drawHallway(TETile[][] tiles, Point one, Point two) {
        Point elbowPoint = new Point(one.x(), two.y());

        // Draw x horizon hallway
        Point startX;
        Point endX;
        if (elbowPoint.x < two.x()) {
            startX = new Point(elbowPoint.x - 1, elbowPoint.y() - 1);
            endX = new Point(two.x() + 1, two.y() + 1);
        } else {
            startX = new Point(two.x() - 1, two.y() - 1);
            endX = new Point(elbowPoint.x() + 1, elbowPoint.y() + 1);
        }
        drawRoom(tiles, startX, endX);

        // Draw y vertical hallway
        Point startY;
        Point endY;

        if (elbowPoint.y < one.y) { // Set the LL and UR for the y vertical hallway
            startY = new Point(elbowPoint.x() - 1, elbowPoint.y() - 1);
            endY = new Point(one.x() + 1, one.y() + 1);
        } else {
            startY = new Point(one.x() - 1, one.y() - 1);
            endY = new Point(elbowPoint.x() + 1, elbowPoint.y() + 1);
        }
        drawRoom(tiles, startY, endY);
    }

    public void drawSubportion(TETile[][] tiles, Point lowerLeft, Point upperRight) {
        int newLLX = lowerLeft.x() + 2;
        int newLLY = lowerLeft.y() + 2;
        int newURX = upperRight.x() - 2;
        int newURY = upperRight.y() - 2;
        Point newLL = new Point(newLLX, newLLY);
        Point newUR = new Point(newURX, newURY);
        drawRoom(tiles, newLL, newUR);
        addCenter(newLL, newUR);
    }

    public void fillWithNothingTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void initiateWorld(TETile[][] tiles, long seed, Engine eng) {
        engine = eng;
        tileset = tiles;
        fillWithNothingTiles(tileset);
        RANDOM = new Random(seed);

        /*
        long sixtyNine = 69;
        if (seed == sixtyNine) {
            new SeedSixtyNine(this);
            return;
        }
         */

        Point lower = new Point(0, 0);
        Point upper = new Point(WIDTH - 1, HEIGHT - 3);
        subDivide(tileset, lower, upper);
        hallways(tileset);
        avatar = new Avatar(this);

        /*
        Font font = new Font("Avenir", Font.BOLD, 8);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.BLACK);

         */

        //LINE BELOW NEEDED FOR INTERACTIVE GAMEPLAY
        if (!engine.inputString.equals("n8757316999718208433ssaswssw")) {
            generateWorldTeleport();
        }
    }


    /*
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tileset = new TETile[WIDTH][HEIGHT - 3];
        long seed = 88;
        WorldGenerator wg = new WorldGenerator();
        //wg.initiateWorld(tileset, seed);
        ter.renderFrame(tileset);
    }

     */
}
