package stonering.global;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Alexander on 06.08.2017.
 */
public class FileLoader {
    private final static String MATERIALS_PATH = "resources/materials.json";
    private final static String TREES_PATH = "resources/trees.json";
    private final static String PLANTS_PATH = "resources/plants.json";

    public static FileHandle getMineralsFile() {
        return new FileHandle(MATERIALS_PATH);
    }

    public static FileHandle getTreesFile() {
        return new FileHandle(TREES_PATH);
    }

    public static FileHandle getPlantsFile() {
        return new FileHandle(PLANTS_PATH);
    }

}