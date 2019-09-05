package stonering.test_chamber.model;

import stonering.entity.building.Building;
import stonering.game.model.EntitySelector;
import stonering.game.model.system.*;
import stonering.generators.buildings.BuildingGenerator;
import stonering.util.geometry.Position;

/**
 * Model with workbench for testing ui and crafting tasks.
 *
 * @author Alexander_Kuzyakov on 25.06.2019.
 */
public class WorkbenchModel extends TestModel {

    @Override
    public void init() {
        super.init();
        get(BuildingContainer.class).addBuilding(createBuilding());
        get(EntitySelector.class).setPosition(4, 4, 2);
    }

    private Building createBuilding() {
        return new BuildingGenerator().generateBuilding("campfire", new Position(4, 4, 2));
    }

    @Override
    public String toString() {
        return "WorkbenchModel";
    }
}
