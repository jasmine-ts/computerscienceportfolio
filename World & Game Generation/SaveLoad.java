package byow.Core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveLoad {
    File savedWorlds = null;

    public void saveWorld(String x) {
        /*
        savedWorlds = Paths.get("byow", "Core", "savedWorlds").toFile();
        if (!savedWorlds.exists()) {
            savedWorlds.mkdir();
        } */

        File file = new File("loadworld.txt");
        //File file = Paths.get("byow", "Core", "savedWorlds", "loadworld.txt").toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path fileName = Path.of("loadworld.txt");
        //Path fileName = Path.of("byow", "Core", "savedWorlds", "loadworld.txt");
        try {
            Files.writeString(fileName, x);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadWorld() {
        File worldFile = Paths.get("loadworld.txt").toFile();
        //File worldFile = Paths.get("byow", "Core", "savedWorlds", "loadworld.txt").toFile();
        if (!worldFile.exists()) {
            return "";
        } else {
            Path fileName = Path.of("loadworld.txt");
            //Path fileName = Path.of("byow", "Core", "savedWorlds", "loadworld.txt");
            try {
                String actual = Files.readString(fileName);
                return actual;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void main(String[] args) {
        SaveLoad sl = new SaveLoad();
        sl.saveWorld("yamum");
    }
}
