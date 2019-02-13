package stonering.generators.localgen.generators;

import stonering.enums.blocks.BlockTypesEnum;
import stonering.enums.materials.Material;
import stonering.enums.materials.MaterialMap;
import stonering.enums.plants.PlantMap;
import stonering.enums.plants.PlantType;
import stonering.exceptions.DescriptionNotFoundException;
import stonering.game.core.model.local_map.LocalMap;
import stonering.generators.PerlinNoiseGenerator;
import stonering.generators.localgen.LocalGenConfig;
import stonering.generators.localgen.LocalGenContainer;
import stonering.generators.plants.PlantGenerator;
import stonering.generators.plants.TreeGenerator;
import stonering.util.geometry.Position;
import stonering.entity.local.plants.Plant;
import stonering.entity.local.plants.PlantBlock;
import stonering.entity.local.plants.Tree;
import stonering.util.global.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Generates plants suitable for local climate and places them on local map.
 *
 * @author Alexander Kuzyakov on 10.04.2018.
 */
public class LocalFloraGenerator {
    private LocalGenContainer container;
    private LocalGenConfig config;
    private LocalMap localMap;
    private PerlinNoiseGenerator noiseGenerator;
    private float maxTemp;
    private float minTemp;
    private float rainfall;
    private int areaSize;

    private final int floorCode = BlockTypesEnum.FLOOR.getCode();

    private HashMap<String, Float> weightedPlantTypes;
    private HashMap<String, Float> weightedTreeTypes;

    public LocalFloraGenerator(LocalGenContainer container) {
        this.container = container;
    }

    public void execute() {
        System.out.println("generating flora");
        extractContainer();
        this.localMap = container.getLocalMap();
        weightedPlantTypes = new HashMap<>();
        weightedTreeTypes = new HashMap<>();
        countTemperature();
        filterPlants();
        generateFlora();
    }

    private void extractContainer() {
        this.config = container.getConfig();
        int x = config.getLocation().getX();
        int y = config.getLocation().getY();
        areaSize = config.getAreaSize();
        rainfall = container.getWorld().getWorldMap().getRainfall(x, y);
        noiseGenerator = new PerlinNoiseGenerator();
    }

    /**
     * Counts min and max temperature of the year.
     */
    private void countTemperature() {
        minTemp = container.getMonthlyTemperatures()[0];
        maxTemp = minTemp;
        for (float temp : container.getMonthlyTemperatures()) {
            minTemp = temp < minTemp ? temp : minTemp;
            maxTemp = temp > maxTemp ? temp : maxTemp;
        }
    }

    /**
     * Calls placing method for all filtered plants and trees.
     * Trees give shadow, therefore they should be placed before plants.
     */
    private void generateFlora() {
        weightedTreeTypes.forEach(this::placeTrees);
        weightedPlantTypes.forEach(this::placePlants);
    }

    /**
     * Generates and places trees on local map. Uses limited attempts with random positions.
     *
     * @param specimen PlantType key from PlantMap representing tree
     * @param amount   relative amount
     */
    private void placeTrees(String specimen, float amount) {
        float[][] forestArea = noiseGenerator.generateOctavedSimplexNoise(areaSize, areaSize, 6, 0.5f, 0.015f);
        int tries = 200;
        Random random = new Random();
        TreeGenerator treeGenerator = new TreeGenerator();
        Tree tree = treeGenerator.generateTree(specimen, 1);
        while (amount > 0 && tries > 0) {
            int x = random.nextInt(areaSize);
            int y = random.nextInt(areaSize);
            int z = container.getRoundedHeightsMap()[x][y] + 1;
            if (forestArea[x][y] > 0 && checkTreePlacing(tree, x, y, z)) {
                placeTree(tree, x, y, z);
                tree.setPosition(new Position(x, y, z));
                tree = treeGenerator.generateTree(specimen, 1);
                amount--;
            }
            tries--;
        }
    }

    /**
     * Places tree on map. Area on map should be checked before placing.
     *
     * @param tree tree to place
     */
    private void placeTree(Tree tree, int cx, int cy, int cz) {
        PlantBlock[][][] treeParts = tree.getBlocks();
        int treeCenterZ = tree.getCurrentStage().getTreeType().getRootDepth();
        int treeRadius = tree.getCurrentStage().getTreeType().getTreeRadius();
        for (int x = 0; x < treeParts.length; x++) {
            for (int y = 0; y < treeParts[x].length; y++) {
                for (int z = 0; z < treeParts[x][y].length; z++) {
                    if (treeParts[x][y][z] != null) {
                        Position onMapPosition = new Position(
                                cx + x - treeRadius,
                                cy + y - treeRadius,
                                cz + z - treeCenterZ);
                        localMap.setPlantBlock(onMapPosition, treeParts[x][y][z]);
                        treeParts[x][y][z].setPosition(onMapPosition);
                    }
                }
            }
        }
        container.getPlants().add(tree);
    }

    /**
     * Checks that desired area for tree is free.
     *
     * @return true if placing possible.
     */
    private boolean checkTreePlacing(Tree tree, int cx, int cy, int cz) {
        PlantBlock[][][] treeParts = tree.getBlocks();
        int treeCenterZ = tree.getCurrentStage().getTreeType().getRootDepth();
        int treeRadius = tree.getCurrentStage().getTreeType().getTreeRadius();
        String soilType = tree.getType().getSoilType();
        for (int x = 0; x < treeParts.length; x++) {
            for (int y = 0; y < treeParts[x].length; y++) {
                for (int z = 0; z < treeParts[x][y].length; z++) {
                    int mapX = cx + x - treeRadius;
                    int mapY = cy + y - treeRadius;
                    int mapZ = cz + z - treeCenterZ;
                    if (!localMap.inMap(mapX, mapY, mapZ)) return false;
                    if (treeParts[x][y][z] != null && localMap.getPlantBlock(mapX, mapY, mapZ) != null) return false;
                    Material material = MaterialMap.getInstance().getMaterial(localMap.getMaterial(x, y, z));
                    if (material != null && material.getTags().contains(soilType)) ;
                }
            }
        }
        return true;
    }

    /**
     * Generates and places plants in {@link LocalGenContainer}
     *
     * @param specimen PlantType key from PlantMap representing tree
     * @param amount   relative amount
     */
    private void placePlants(String specimen, float amount) {
        PlantGenerator plantGenerator = new PlantGenerator();
        Pair<boolean[][][], ArrayList<Position>> pair = findAllAvailablePositions(specimen);
        ArrayList<Position> positions = pair.getValue();
        boolean[][][] array = pair.getKey();
        Random random = new Random();
        for (int number = (int) (positions.size() * amount / 2); number > 0; number--) {
            try {
                Position position = positions.remove(random.nextInt(positions.size()));
                array[position.getX()][position.getY()][position.getZ()] = false;
                Plant plant = plantGenerator.generatePlant(specimen, 0);
                plant.setPosition(position);
                container.getPlants().add(plant);
            } catch (DescriptionNotFoundException e) {
                System.out.println("material for plant " + specimen + " not found");
            }
        }
    }

    /**
     * Collects all positions suitable for specific plant. Used only for single tile plants.
     *
     * @param specimen plant to check availability
     * @return
     */
    private Pair<boolean[][][], ArrayList<Position>> findAllAvailablePositions(String specimen) {
        //TODO should count plant requirements for light level, water source, soil type
        ArrayList<Position> positions = new ArrayList<>();
        boolean[][][] array = new boolean[localMap.getxSize()][localMap.getySize()][localMap.getzSize()];
        PlantType type = PlantMap.getInstance().getPlantType(specimen);
        String soilType = type.getSoilType();
        for (int x = 0; x < localMap.getxSize(); x++) {
            for (int y = 0; y < localMap.getySize(); y++) {
                for (int z = 0; z < localMap.getzSize(); z++) {
                    if (localMap.getBlockType(x, y, z) == floorCode
                            && localMap.getPlantBlock(x, y, z) == null) { // surface material should be suitable for plant
                        Material material = MaterialMap.getInstance().getMaterial(localMap.getMaterial(x, y, z));
                        if (material != null && material.getTags().contains(soilType)) {
                            positions.add(new Position(x, y, z));
                            array[x][y][z] = true;
                        }
                    }
                }
            }
        }
        Pair<boolean[][][], ArrayList<Position>> pair = new Pair<>(array, positions);
        return pair;
    }

    /**
     * Filters all PlantMap with local climate parameters and adds passed plants and trees to lists.
     */
    private void filterPlants() {
        PlantMap.getInstance().getAllTypes().forEach((type) -> {
            if (rainfall < type.getMinRainfall()
                    || rainfall > type.getMaxRainfall()) {
                return; // too dry or wet
            }
            if (minTemp < type.getMinRainfall()
                    || maxTemp > type.getMaxTemperature()) {
                return; // too hot or cold
            }
            if (minTemp > type.getMaxGrowingTemperature()
                    || maxTemp < type.getMinGrowingTemperature()) { // plant can grow
                return; // plant grow zone out of local temp zone
            }
            //TODO add grade of specimen spreading in this area.
            if (type.isTree()) { //is plant tree or not
                weightedTreeTypes.put(type.getName(), 100f);
            } else {
                weightedPlantTypes.put(type.getName(), 1f);
            }
        });
    }
}