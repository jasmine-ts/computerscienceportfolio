package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;

public class Avatar {
    WorldGenerator world;
    Point location;
    int tpRoomNumber;

    public Avatar(WorldGenerator wg) {
        world = wg;
        int randomStartRoom = world.RANDOM.nextInt(world.centers.size());
        Point randomCenterPoint = world.centers.get(randomStartRoom);
        location = randomCenterPoint; //sets the avatar's starting point
        drawAvatar(location); //draws the avatar at the starting point
    }

    public void randomRoomTeleportSequence() {
        int randomRoomNumber = world.RANDOM.nextInt(world.centers.size());
        tpRoomNumber = randomRoomNumber;
        teleportToRoom(tpRoomNumber);
    }

    public void drawAvatar(Point loc) {
        int x = loc.x;
        int y = loc.y;
        world.tileset[x][y] = Tileset.AVATAR;
    }

    public void drawFloorTile(Point loc) {
        int x = loc.x;
        int y = loc.y;
        world.tileset[x][y] = Tileset.FLOOR;
    }

    public void moveRight() {
        Point rightPoint = new Point(location.x + 1, location.y);
        TETile tileType = world.tileset[rightPoint.x][rightPoint.y];
        if (tileType == Tileset.TELEPORT) {
            teleportSequence();
        } else if (tileType == Tileset.WALL) {
            return; //do nothing cuz it's a wall
        } else {
            drawFloorTile(location);
            drawAvatar(rightPoint);
            location = rightPoint;
        }
    }

    public void moveLeft() {
        Point leftPoint = new Point(location.x - 1, location.y);
        TETile tileType = world.tileset[leftPoint.x][leftPoint.y];
        if (tileType == Tileset.TELEPORT) {
            teleportSequence();
        } else if (tileType == Tileset.WALL) {
            return; //do nothing b/c it's a wall
        } else {
            drawFloorTile(location);
            drawAvatar(leftPoint);
            location = leftPoint;
        }
    }

    public void moveUp() {
        Point upPoint = new Point(location.x, location.y + 1);
        TETile tileType = world.tileset[upPoint.x][upPoint.y];
        if (tileType == Tileset.TELEPORT) {
            teleportSequence();
        } else if (tileType == Tileset.WALL) {
            return; 
        } else {
            drawFloorTile(location);
            drawAvatar(upPoint);
            location = upPoint;
        }
    }

    public void moveDown() {
        Point downPoint = new Point(location.x, location.y - 1);
        TETile tileType = world.tileset[downPoint.x][downPoint.y];
        if (tileType == Tileset.TELEPORT) {
            teleportSequence();
        } else if (tileType == Tileset.WALL) {
            return; 
        } else {
            drawFloorTile(location);
            drawAvatar(downPoint);
            location = downPoint;
        }
    }

    private void teleportSequence() {

        //alternate teleport sequence to deal with input string gameplay
        if (world.engine.readInputStringMode) {
            randomRoomTeleportSequence();
            return;
        }

        //normal keyboard-interactive gameplay teleport sequence
        int numRooms = world.centers.size();
        drawTeleportMenu(numRooms);

        boolean teleported = false;

        while (!teleported) {
            String roomNumString = "";
            char c = world.engine.inputSource.getNextKey();
            roomNumString += c;
            teleportRoomMenu(roomNumString, numRooms);
            while (roomNumString.length() < 2) {
                c = world.engine.inputSource.getNextKey();
                roomNumString += c;
                teleportRoomMenu(roomNumString, numRooms);
            }
            tpRoomNumber = Integer.parseInt(roomNumString);


            if (tpRoomNumber >= 1 && tpRoomNumber <= numRooms) {
                teleportToRoom(tpRoomNumber);

                StdDraw.clear(Color.BLACK);
                Font font = new Font("Avenir", Font.ITALIC, 35);
                StdDraw.setFont(font);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(45, 20, "Teleporting to Room " + tpRoomNumber);

                StdDraw.setPenColor(new Color(0, 255, 149));


                Font font2 = new Font("Avenir", Font.BOLD, 200);
                StdDraw.setFont(font2);
                StdDraw.text(45, 27, "◈");

                StdDraw.show();
                StdDraw.pause(1500);
                teleported = true;


            } else {
                roomNumString = "Invalid Input";
                teleportRoomMenu(roomNumString, numRooms);
                StdDraw.pause(250);
                roomNumString = "";
                teleportRoomMenu(roomNumString, numRooms);
            }

        }
        world.engine.ter.renderFrame(world.tileset);
    }

    private void teleportRoomMenu(String room, int numRooms) {
        StdDraw.clear(Color.BLACK);

        Font font = new Font("Avenir", Font.BOLD, 100);
        StdDraw.setFont(font);
        StdDraw.setPenColor(new Color(0, 255, 149));
        StdDraw.text(45, 35, "- TELEPORT MENU -");

        Font font1 = new Font("Avenir", Font.BOLD, 200);
        StdDraw.setFont(font1);
        StdDraw.text(45, 27, "◈");

        StdDraw.setPenColor(StdDraw.WHITE);


        Font font2 = new Font("Avenir", Font.BOLD, 25);
        StdDraw.setFont(font2);
        StdDraw.text(45, 20, "Input a room number you'd like to teleport to!");
        StdDraw.text(45, 18,  "Your choices range from 1 to " + numRooms + ".");

        Font font3 = new Font("Avenir", Font.ITALIC, 20);
        StdDraw.setFont(font3);
        StdDraw.text(45, 16, "(Please provide your choice as a 2-digit number. Ex: '01')");

        Font font4 = new Font("Avenir", Font.PLAIN, 25);
        StdDraw.setFont(font4);
        StdDraw.text(45, 10, room);

        StdDraw.pause(20);
        StdDraw.show();
    }


    private void drawTeleportMenu(int numRooms) {
        int width = 90;
        int height = 48;
        StdDraw.setCanvasSize(width * 16, height * 16);
        StdDraw.clear(Color.BLACK);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        Font font = new Font("Avenir", Font.BOLD, 100);
        StdDraw.setFont(font);
        StdDraw.setPenColor(new Color(0, 255, 149));
        StdDraw.text(45, 35, "- TELEPORT MENU -");

        Font font1 = new Font("Avenir", Font.BOLD, 200);
        StdDraw.setFont(font1);
        StdDraw.text(45, 27, "◈");

        StdDraw.setPenColor(StdDraw.WHITE);


        Font font2 = new Font("Avenir", Font.BOLD, 25);
        StdDraw.setFont(font2);
        StdDraw.text(45, 20, "Input a room number you'd like to teleport to!");
        StdDraw.text(45, 18,  "Your choices range from 1 to " + numRooms + ".");

        Font font3 = new Font("Avenir", Font.ITALIC, 20);
        StdDraw.setFont(font3);
        StdDraw.text(45, 16, "(Please provide your choice as a 2-digit number. Ex: '01')");

        Font font4 = new Font("Avenir", Font.PLAIN, 20);
        StdDraw.setFont(font4);
        StdDraw.text(45, 10, "Choose Wisely");

        StdDraw.show();

        if (StdDraw.hasNextKeyTyped()) {
            return;
        }
    }

    private void teleportToRoom(int roomNumber) {
        Point tpPosition = world.centers.get(roomNumber - 1);
        drawAvatar(tpPosition);
        drawFloorTile(location);
        location = tpPosition;

        //NEED LINE BELOW FOR PLAYER PLAYING THE GAME
        world.engine.ter.renderFrame(world.tileset);
    }

}
