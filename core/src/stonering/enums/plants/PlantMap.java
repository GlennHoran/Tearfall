package stonering.enums.plants;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import stonering.util.global.FileLoader;
import stonering.util.global.TagLoggersEnum;

import java.util.*;

/**
 * Load all {@link PlantType}s from jsons, and inits them.
 */
public class PlantMap {
    private static PlantMap instance;
    private Map<String, PlantType> plantTypes;
    private Map<String, PlantType> treeTypes;
    private Map<String, PlantType> substrateTypes;
    private Map<String, PlantType> domesticTypes;
    private Json json;

    private PlantMap() {
        plantTypes = new HashMap<>();
        treeTypes = new HashMap<>();
        substrateTypes = new HashMap<>();
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        TagLoggersEnum.LOADING.log("plant types");
        //TODO add json validation
        loadTypesFileToMap(FileLoader.PLANTS_PATH, plantTypes);
        loadTypesFileToMap(FileLoader.TREES_PATH, treeTypes);
        loadTypesFileToMap(FileLoader.SUBSTRATES_PATH, substrateTypes);
        fillDomesticTypes();
    }

    public static PlantMap getInstance() {
        if (instance == null)
            instance = new PlantMap();
        return instance;
    }

    /**
     * Loads {@link PlantType} from given file into given file.
     */
    private void loadTypesFileToMap(String filePath, Map<String, PlantType> map) {
        List<RawPlantType> elements = json.fromJson(ArrayList.class, RawPlantType.class, FileLoader.getFile(filePath));
        PlantTypeProcessor processor = new PlantTypeProcessor();
        for (RawPlantType rawType : elements) {
            PlantType type = processor.processRawType(rawType);
            map.put(rawType.name, type);
        }
        TagLoggersEnum.LOADING.logDebug(map.keySet().size() + " loaded from " + filePath);
    }

    private void fillDomesticTypes() {
        domesticTypes = new HashMap<>();
        plantTypes.values().stream().filter(type -> type.isPlant()).forEach(type -> domesticTypes.put(type.name, type));
    }

    public PlantType getPlantType(String specimen) {
        return plantTypes.get(specimen);
    }

    public PlantType getTreeType(String specimen) {
        return treeTypes.get(specimen);
    }

    public PlantType getSubstrateType(String specimen) {
        return treeTypes.get(specimen);
    }

    public Collection<PlantType> getDomesticTypes() {
        return domesticTypes.values();
    }

    public Map<String, PlantType> getPlantTypes() {
        return plantTypes;
    }

    public Map<String, PlantType> getTreeTypes() {
        return treeTypes;
    }

    public Map<String, PlantType> getSubstrateTypes() {
        return substrateTypes;
    }
}
