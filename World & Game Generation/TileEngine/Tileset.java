package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('㋡', Color.white,
            new Color(174, 171, 116), "you");
    public static final TETile NICE = new TETile('☻', Color.white, new Color(126, 187, 82),
            "nice");
    public static final TETile WALL = new TETile(' ', Color.darkGray, new Color(155, 152, 105),
            "wall");
    public static final TETile FLOOR = new TETile(' ', new Color(174, 171, 116),
            new Color(174, 171, 116),
            "floor");
    public static final TETile NOTHING = new TETile('▩', new Color(142, 202, 99),
            new Color(126, 187, 82), "nothing");
    public static final TETile TELEPORT = new TETile('◈', new Color(0, 255, 149),
            new Color(174, 171, 116), "teleport");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
}


