package stonering.generators.worldgen;

import stonering.generators.worldgen.generators.drainage.ErosionGenerator;
import stonering.generators.worldgen.generators.drainage.OceanFiller;
import stonering.generators.worldgen.generators.drainage.RainfallGenerator;
import stonering.generators.worldgen.generators.elevation.*;
import stonering.generators.worldgen.generators.elevation.PlateGenerator;
import stonering.generators.worldgen.generators.drainage.RiverGenerator;
import stonering.generators.worldgen.generators.temperature.TemperatureGenerator;

public class WorldGeneratorContainer {
    private boolean rejected;

    private WorldGenContainer worldGenContainer;

    private PlateGenerator plateGenerator;
    private MountainGenerator mountainGenerator;
    private ValleyGenerator valleyGenerator;
    private MountainRenderer mountainRenderer;
    private ValleyRenderer valleyRenderer;
    private HillGenerator hillGenerator;
    private HillRenderer hillRenderer;
    private OceanFiller oceanFiller;
    private TemperatureGenerator temperatureGenerator;
    private ElevationGenerator elevationGenerator;
    private RainfallGenerator rainfallGenerator;
    private ErosionGenerator erosionGenerator;
    private ElevationModifier elevationModifier;
    private RiverGenerator riverGenerator;

    public void init(WorldGenConfig config) {
        worldGenContainer = new WorldGenContainer(config);
        plateGenerator = new PlateGenerator(worldGenContainer);
        mountainGenerator = new MountainGenerator(worldGenContainer);
        valleyGenerator = new ValleyGenerator(worldGenContainer);
        mountainRenderer = new MountainRenderer(worldGenContainer);
        valleyRenderer = new ValleyRenderer(worldGenContainer);
        hillGenerator = new HillGenerator(worldGenContainer);
        hillRenderer = new HillRenderer(worldGenContainer);
        oceanFiller = new OceanFiller(worldGenContainer);
        riverGenerator = new RiverGenerator(worldGenContainer);
        temperatureGenerator = new TemperatureGenerator(worldGenContainer);
        elevationGenerator = new ElevationGenerator(worldGenContainer);
        rainfallGenerator = new RainfallGenerator(worldGenContainer);
        erosionGenerator = new ErosionGenerator(worldGenContainer);
        elevationModifier = new ElevationModifier(worldGenContainer);
    }

    public void runContainer() {
        do {
            rejected = runGenerators();
            if (rejected) {
                worldGenContainer.reset();
            }
        } while (rejected);
    }

    private boolean runGenerators() {
//        if (plateGenerator.execute()) return true;
//        if (mountainGenerator.execute()) return true;
//        if (valleyGenerator.execute()) return true;
//        if (hillGenerator.execute()) return true;
        elevationGenerator.execute();
//        mountainRenderer.execute();
//        valleyRenderer.execute();
//        worldGenContainer.fillMap();
        oceanFiller.execute();
//        hillRenderer.execute();
        erosionGenerator.execute();
        temperatureGenerator.execute();
        rainfallGenerator.execute();
//        elevationModifier.execute();
        riverGenerator.execute();
        worldGenContainer.fillMap();

        return false;
    }

    public WorldMap getWorldMap() {
        return worldGenContainer.getMap();
    }
}