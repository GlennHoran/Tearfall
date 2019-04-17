package stonering.test_chamber.model;

import stonering.entity.local.environment.CelestialBody;
import stonering.entity.local.environment.GameCalendar;
import stonering.entity.local.environment.aspects.CelestialCycleAspect;
import stonering.entity.local.environment.aspects.CelestialLightSourceAspect;
import stonering.entity.local.unit.Unit;
import stonering.entity.local.zone.FarmZone;
import stonering.entity.world.World;
import stonering.enums.ZoneTypesEnum;
import stonering.enums.blocks.BlockTypesEnum;
import stonering.enums.materials.MaterialMap;
import stonering.enums.plants.PlantMap;
import stonering.game.model.EntitySelector;
import stonering.game.model.GameModel;
import stonering.game.model.lists.PlantContainer;
import stonering.game.model.lists.TaskContainer;
import stonering.game.model.lists.UnitContainer;
import stonering.game.model.lists.ZonesContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.game.view.tilemaps.LocalTileMap;
import stonering.generators.creatures.CreatureGenerator;
import stonering.util.geometry.Position;

/**
 * Model with small farm to simulate all cases with creation tasks by farm.
 */
public class FarmTasksModel extends GameModel {

    @Override
    public void init() {
        super.init();
        get(GameCalendar.class).addListener("minute", get(World.class).getStarSystem());
        get(GameCalendar.class).addListener("minute", get(PlantContainer.class));
    }

    /**
     * Recreates model.
     */
    public void reset() {
        put(createWorld());
        put(createMap());
        put(new PlantContainer());
        put(new LocalTileMap(get(LocalMap.class)));
        put(new EntitySelector());
        put(new GameCalendar());
        put(new UnitContainer());
        put(new TaskContainer());
        put(new ZonesContainer());
        createFarm();
        createUnit();
    }

    private void createFarm() {
        Position start = new Position(3,3,2);
        Position end = new Position(5,5,2);
        get(ZonesContainer.class).createNewZone(start, end, ZoneTypesEnum.FARM);
        FarmZone farm = (FarmZone) get(ZonesContainer.class).getZone(start);
        farm.setPlant(PlantMap.getInstance().getPlantType("tomato"), true);
    }

    private void createUnit() {
        Unit unit = new CreatureGenerator().generateUnit("human");
        unit.setPosition(new Position(0,0,2));
        get(UnitContainer.class).
    }

    private LocalMap createMap() {
        LocalMap localMap = new LocalMap(9, 9, 20);
        MaterialMap materialMap = MaterialMap.getInstance();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                localMap.setBlock(x, y, 0, BlockTypesEnum.WALL, materialMap.getId("soil"));
                localMap.setBlock(x, y, 1, BlockTypesEnum.WALL, materialMap.getId("soil"));
                localMap.setBlock(x, y, 2, BlockTypesEnum.FLOOR, materialMap.getId("soil"));
            }
        }
        return localMap;
    }

    private World createWorld() {
        World world = new World(1,1);
        CelestialBody sun = new CelestialBody();
        sun.addAspect(new CelestialLightSourceAspect(sun));
        float dayScale = 0.01f;
        sun.addAspect(new CelestialCycleAspect(dayScale, dayScale, sun));
        world.getStarSystem().getCelestialBodies().add(sun);
        return world;
    }

    @Override
    public String toString() {
        return "FarmTasksModel";
    }
}
