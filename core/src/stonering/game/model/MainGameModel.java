package stonering.game.model;

import stonering.entity.World;
import stonering.game.model.system.*;
import stonering.game.model.system.building.BuildingContainer;
import stonering.game.model.system.items.ItemContainer;
import stonering.game.model.system.tasks.TaskContainer;
import stonering.game.model.system.units.UnitContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.game.model.tilemaps.LocalTileMap;
import stonering.util.global.Logger;
import stonering.util.pathfinding.a_star.AStar;

/**
 * Model of game, contains LocalMap and sub-Containers. Inits all components after creation.
 * Time ticks are performed with Timer. Calls turning for all game entity.
 *
 * @author Alexander Kuzyakov on 10.06.2017.
 */
public class MainGameModel extends GameModel {

    public MainGameModel(LocalMap map) {
        super();
        put(map);
    }

    @Override
    public void init() {
        super.init();
    }

    /**
     * Creates model components.
     */
    public void createComponents(World world) {
        Logger.GENERAL.logDebug("creating model components");
        put(world);
        put(new LocalTileMap());
        put(new PlantContainer());
        put(new SubstrateContainer());
        put(new BuildingContainer());
        put(new UnitContainer());
        put(new ZonesContainer());
        put(new ItemContainer());
        put(new TaskContainer());
        put(new LiquidContainer());
        put(new EntitySelector());          // local map camera
        put(new AStar());
    }
}