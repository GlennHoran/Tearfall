package stonering.enums.buildings;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import stonering.entity.building.Blueprint;
import stonering.entity.building.RawBlueprint;
import stonering.util.global.FileLoader;
import stonering.util.global.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores all blueprints from blueprints.json.
 */
public class BlueprintsMap {
    private static BlueprintsMap instance;
    public final HashMap<String, Blueprint> blueprints;
    private Json json;

    private BlueprintsMap() {
        blueprints = new HashMap<>();
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        loadBlueprints();
    }

    public static BlueprintsMap getInstance() {
        if (instance == null)
            instance = new BlueprintsMap();
        return instance;
    }

    private void loadBlueprints() {
        Logger.LOADING.log("blueprints");
        ArrayList<RawBlueprint> elements = json.fromJson(ArrayList.class, RawBlueprint.class, FileLoader.getFile(FileLoader.BLUEPRINTS_PATH));
        for (RawBlueprint rawBlueprint : elements) {
            blueprints.put(rawBlueprint.name, new Blueprint(rawBlueprint));
        }
        Logger.LOADING.log(blueprints.keySet().size() + " loaded from " + FileLoader.BLUEPRINTS_PATH);
    }

    public Blueprint getBlueprint(String name) {
        return blueprints.get(name);
    }
}
