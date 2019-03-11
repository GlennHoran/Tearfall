package stonering.generators.localgen.generators;

import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.core.model.local_map.LocalMap;
import stonering.generators.buildings.BuildingGenerator;
import stonering.generators.localgen.LocalGenConfig;
import stonering.generators.localgen.LocalGenContainer;
import stonering.util.geometry.Position;
import stonering.entity.local.building.Building;
import stonering.util.global.TagLoggersEnum;

/**
 * Generates buildings on local generation
 *
 * @author Alexander Kuzyakov on 07.12.2017.
 */
public class LocalBuildingGenerator {
    private LocalGenContainer container;
    private LocalGenConfig config;
    private BuildingGenerator buildingGenerator;

    public LocalBuildingGenerator(LocalGenContainer container) {
        this.container = container;
        config = container.getConfig();
        buildingGenerator = new BuildingGenerator();
    }

    public void execute() {
//        TagLoggersEnum.GENERATION.log("generating buildings");
//        Position position = findSurfacePosition();
//        Building building = buildingGenerator.generateBuilding("forge", position);
//        container.getBuildings().add(building);
    }

    /**
     * Returns position on the ground in the center of the map.
     * @return
     */
    private Position findSurfacePosition() {
        LocalMap localMap = container.getLocalMap();
        int x = localMap.xSize /2;
        int y = localMap.ySize /2;
        for (int z = localMap.zSize - 1; z > 0; z--) {
            if (localMap.getBlockType(x, y, z) == BlockTypesEnum.FLOOR.CODE) {
                return new Position(x, y, z);
            }
        }
        return null;
    }
}
