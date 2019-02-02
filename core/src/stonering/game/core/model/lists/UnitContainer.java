package stonering.game.core.model.lists;

import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.core.model.GameContainer;
import stonering.game.core.model.local_map.LocalMap;
import stonering.util.geometry.Position;
import stonering.entity.local.unit.Unit;

import java.util.ArrayList;

/**
 * Contains all Units on localMap.
 *
 * @author Alexander Kuzyakov on 03.12.2017.
 */
public class UnitContainer {
    private GameContainer gameContainer;
    private ArrayList<Unit> units;
    private LocalMap localMap;

    public UnitContainer(ArrayList<Unit> units, GameContainer gameContainer) {
        this.units = units;
        this.gameContainer = gameContainer;
    }

    public void turn() {
        units.forEach((unit) -> unit.turn());
    }

    public void placeUnits() {
        units.forEach((unit) -> placeUnit(unit));
    }

    public void placeUnit(Unit unit) {
        while (true) {
            int x = localMap.getxSize() / 2;
            int y = localMap.getySize() / 2 - 15;
            for (int z = localMap.getzSize() - 1; z > 0; z--) {
                if (localMap.getBlockType(x, y, z) == BlockTypesEnum.FLOOR.getCode()
                        && localMap.getBlockType(x, y, z - 1) == BlockTypesEnum.WALL.getCode()) {
                    System.out.println("placed: " + x + " " + y + " " + z);
                    unit.setPosition(new Position(x, y, z));
                    return;
                }
            }
        }
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public void setLocalMap(LocalMap localMap) {
        this.localMap = localMap;
    }

    /**
     * Inits unit's aspects.
     */
    public void init() {
        units.forEach((unit) -> unit.getAspects().values().forEach((aspect) -> aspect.init(gameContainer)));
    }
}
