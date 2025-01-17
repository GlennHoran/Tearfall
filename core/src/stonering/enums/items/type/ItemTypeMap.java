package stonering.enums.items.type;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import stonering.enums.items.type.raw.RawItemType;
import stonering.enums.items.type.raw.RawItemTypeProcessor;
import stonering.util.global.FileLoader;
import stonering.util.global.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton map for all {@link ItemType}s. Types should have unique names.
 */
public class ItemTypeMap {
    private static ItemTypeMap instance;
    private HashMap<String, ItemType> types;

    private ItemTypeMap() {
        types = new HashMap<>();
        loadItemTypes();
    }

    public static ItemTypeMap getInstance() {
        if (instance == null)
            instance = new ItemTypeMap();
        return instance;
    }

    private void loadItemTypes() {
        Logger.LOADING.logDebug("loading item types");
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle itemsDirectory = FileLoader.getFile(FileLoader.ITEMS_PATH);
        RawItemTypeProcessor processor = new RawItemTypeProcessor();
        for (FileHandle fileHandle : itemsDirectory.list()) {
            if(fileHandle.isDirectory()) continue;
            ArrayList<RawItemType> elements = json.fromJson(ArrayList.class, RawItemType.class, fileHandle);
            elements.forEach(rawItemType -> types.put(rawItemType.name, processor.process(rawItemType)));
            Logger.LOADING.logDebug(elements.size() + " loaded from " + fileHandle.path());
        }
    }

    public ItemType getItemType(String name) {
        return types.get(name);
    }

    public boolean contains(String title) {
        return types.containsKey(title);
    }
}
