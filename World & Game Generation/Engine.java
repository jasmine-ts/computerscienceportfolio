package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Engine {
    TERenderer ter;
    public static final int WIDTH = 90;
    public static final int HEIGHT = 48;
    KeyboardInputSource inputSource;
    long inputSeed;
    WorldGenerator worldGen;
    TETile[][] worldFrame;
    boolean inGame;
    boolean readInputStringMode;
    String inputString = "";
    SaveLoad saveLoad;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        inputSource = new KeyboardInputSource();
        drawMenu();
        saveLoad = new SaveLoad();
        keyboardInputReactor();
    }

    private void keyboardInputReactor() {
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c != ':') {
                inputString += c;
            }
            if (inGame) {
                if (c == 'W') {
                    worldGen.avatar.moveUp();
                    ter.renderFrame(worldFrame);
                } else if (c == 'A') {
                    worldGen.avatar.moveLeft();
                    ter.renderFrame(worldFrame);
                } else if (c == 'S') {
                    worldGen.avatar.moveDown();
                    ter.renderFrame(worldFrame);
                } else if (c == 'D') {
                    worldGen.avatar.moveRight();
                    ter.renderFrame(worldFrame);
                } else if (c == ':') {
                    char quitCheck = inputSource.getNextKey();
                    if (quitCheck == 'Q') {
                        System.out.println(inputString);
                        this.saveLoad.saveWorld(inputString);
                        inGame = false;
                        System.exit(0);
                    }
                    if (quitCheck == 'E') {
                        inGame = false;
                        drawMenu();
                        keyboardInputReactor();
                    }
                }
                System.out.println(worldGen.avatar.location.y);
                //HUD code below
                while (!StdDraw.hasNextKeyTyped()) {
                    hudMethod();
                }
            } else if (!inGame) {
                if (c == 'N') {
                    String seedString = "";
                    activateNewGame(seedString);
                    c = inputSource.getNextKey();
                    if (c != ':') {
                        inputString += c;
                    }
                    while (inputSource.possibleNextInput()) { //maybe change this conditional?
                        if (c == 'S' && !(seedString.equals(""))) {
                            inputSeed = Long.parseLong(seedString);
                            inGame = true;
                            renderWorld(); //creates a world with inputSeed and renders it
                            break;
                        } else if (Character.isDigit(c)) {
                            seedString = seedString + c;
                            activateNewGame(seedString);
                        } else {
                            seedString = "Invalid";
                            activateNewGame(seedString);
                            StdDraw.pause(250);
                            seedString = "";
                            activateNewGame(seedString);
                        }
                        c = inputSource.getNextKey();
                        if (c != ':') {
                            inputString += c;
                        }
                    }
                } else if (c == 'Q') {
                    System.exit(0);
                } else if (c == 'L') {
                    inputString = saveLoad.loadWorld();
                    parseInputString(inputString, false);
                }
            }
        }
    }

    private void activateNewGame(String seed) {
        StdDraw.clear(Color.BLACK);

        //Display Game Title
        Font font = new Font("Avenir", Font.BOLD, 100);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.text(45, 35, "THE GAME");

        //Display Menu Options
        Font option1 = new Font("Avenir", Font.ITALIC, 35);
        StdDraw.setFont(option1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(45, 25, "Type seed and hit 'S' to enter");

        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.text(45, 22, seed);


        //Display Authors
        Font option2 = new Font("Avenir", Font.ITALIC, 12);
        StdDraw.setFont(option2);

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textRight(85, 5, "Try seed 69 ;)");


        //Reset Font Options
        StdDraw.setFont();

        StdDraw.pause(20);
        StdDraw.show();
    }

    private void drawMenu() {
        //Create Menu Screen
        int width = 90;
        int height = 48;
        StdDraw.setCanvasSize(width * 16, height * 16);
        StdDraw.clear(Color.BLACK);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        //Display Game Title
        Font font = new Font("Avenir", Font.BOLD, 100);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.text(45, 35, "THE GAME");

        //Display Menu Options
        Font option1 = new Font("Avenir", Font.ITALIC, 20);
        StdDraw.setFont(option1);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(45, 25, "Hit 'N' for a New Game");

        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.text(45, 20, "Hit 'L' to load previous game");

        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.text(45, 15, "Hit 'Q' to quit");


        //Display Authors
        Font option2 = new Font("Avenir", Font.ITALIC, 12);
        StdDraw.setFont(option2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textRight(85, 5,
                "Developed by Jasmine Tong-Seely & Ethan Fang        COPYRIGHT © 2020");


        //Reset Font Options
        StdDraw.setFont();
        StdDraw.show();
    }

    private void hudMethod() {

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        TETile mouseTileType = worldFrame[mouseX][mouseY];
        String tileTypeString = "";
        if (mouseTileType == Tileset.WALL) {
            tileTypeString = "Hovered Tile: Wall";
        } else if (mouseTileType == Tileset.FLOOR) {
            tileTypeString = "Hovered Tile: Floor";
        } else if (mouseTileType == Tileset.NOTHING) {
            tileTypeString = "Hovered Tile: Outside The Explorable World";
        } else if (mouseTileType == Tileset.NICE) {
            tileTypeString = "Hovered Tile: 69 ;))";
        } else if (mouseTileType == Tileset.TELEPORT) {
            tileTypeString = "Hovered Tile: Teleporter (Navigate to Teleport)";
        } else if (mouseTileType == Tileset.AVATAR) {
            tileTypeString = "Hovered Tile: Current Location";
        }

        // Draw HUD Box
        StdDraw.setPenColor(new Color(209, 209, 209));
        StdDraw.filledRectangle(45, 47, 45, 1);
        StdDraw.setPenColor(Color.darkGray);
        StdDraw.filledRectangle(45, 47, 4, 0.8);

        StdDraw.setPenColor(Color.lightGray);
        StdDraw.filledRectangle(9, 47, 8, 0.6);

        StdDraw.filledRectangle(30, 47, 8, 0.6);

        StdDraw.filledRectangle(60, 47, 8, 0.6);

        StdDraw.filledRectangle(81, 47, 8, 0.6);


        Font option1 = new Font("Avenir", Font.BOLD, 11);
        StdDraw.setFont(option1);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.textLeft(2, 47, tileTypeString);


        Font option2 = new Font("Avenir", Font.BOLD, 20);
        StdDraw.setFont(option2);
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.text(45, 47, "THE GAME");

        Font option3 = new Font("Avenir", Font.BOLD, 11);
        StdDraw.setFont(option3);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(30, 47, "Controls: W - Up, A - Left, S - Down, D - Right");
        StdDraw.text(60, 47, "ENTER ':Q' to QUIT    or    Enter ':E' to EXIT ");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = dtf.format(now);

        String time = dtf2.format(now);
        StdDraw.textLeft(75, 47, "Date: " + date);
        StdDraw.textLeft(83, 47, "Time: " + time);

        //worldGen.drawRoomNums();

        StdDraw.show();
        StdDraw.pause(5);
        //StdDraw.clear();
        //StdDraw.enableDoubleBuffering();
        ter.renderFrame(worldFrame);
    }

    /**
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
 
        if (saveLoad == null) {
            saveLoad = new SaveLoad();
        }
        readInputStringMode = true;

        parseInputString(input.toUpperCase(), true);
        return worldFrame;
    }

    private void parseInputString(String input, Boolean autoGrader) {
        inputString = input;

        if (!autoGrader) {
            if (input.isEmpty()) {
                StdDraw.clear(Color.BLACK);
                
                //Display Game Title
                Font font = new Font("Avenir", Font.BOLD, 100);
                StdDraw.setFont(font);
                StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                StdDraw.text(45, 35, "THE GAME");

                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                StdDraw.text(45, 20, "No Saved Game");
                StdDraw.pause(500);

                interactWithKeyboard();
                return;
            }
        }
        char[] charArray = input.toCharArray();
        if (charArray.length == 0) {
            return;
        }
        if (charArray[0] == 'N') {
            String seedString = "";
            int i = 1;
            while (charArray[i] != 'S') {
                char c = charArray[i];
                seedString = seedString + c;
                i++;
            } //after this while loop exits, i = index of 'S' after the seed
            inputSeed = Long.parseLong(seedString);
            if (!autoGrader) {
                renderWorld(); 

                //deals with avatar movement
                additionalStringInput(charArray, i + 1);

                ter.renderFrame(worldFrame);

                inGame = true;
                keyboardInputReactor();
            } else {
                createWorld();

                if (charArray.length > i + 1) {
                    additionalStringInput(charArray, i + 1);
                }

            }
        } else if (charArray[0] == 'L') {
            inputString = saveLoad.loadWorld();
            parseInputString(inputString, autoGrader);
            if (charArray.length > 1) {
                additionalStringInput(charArray, 1);
            }
        }
    }

    /** Creates TERenderer, initializes it, creates world, then renders it.
     */
    private void renderWorld() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        createWorld();
        ter.renderFrame(worldFrame);
    }

    /** Creates a world but does not render it.
     */
    private void createWorld() {
        worldFrame = new TETile[WIDTH][HEIGHT];
        worldGen = new WorldGenerator();
        worldGen.initiateWorld(worldFrame, inputSeed, this);
    }

    /** Deals with additional string input from the index of 'S' onwards.
     */
    private void additionalStringInput(char[] charArray, int i) {
        char c = charArray[i];
        while (c != ':' && i < charArray.length - 1) {
            if (c == 'W') {
                worldGen.avatar.moveUp();
            } else if (c == 'A') {
                worldGen.avatar.moveLeft();
            } else if (c == 'S') {
                worldGen.avatar.moveDown();
            } else if (c == 'D') {
                worldGen.avatar.moveRight();
            }
            i += 1;
            c = charArray[i];
        }

        if (c == ':' && charArray[i + 1] == 'Q') {
            saveLoad.saveWorld(inputString);
            return;
        }

        return;
    }
