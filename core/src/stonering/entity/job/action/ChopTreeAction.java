package stonering.entity.job.action;

import stonering.entity.job.designation.Designation;
import stonering.entity.job.action.target.PositionActionTarget;
import stonering.entity.item.Item;
import stonering.entity.item.selectors.ItemSelector;
import stonering.entity.item.selectors.ToolWithActionItemSelector;
import stonering.entity.plants.AbstractPlant;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.game.GameMvc;
import stonering.game.model.GameModel;
import stonering.game.model.lists.ItemContainer;
import stonering.game.model.lists.PlantContainer;
import stonering.util.global.Logger;

/**
 * Action for chopping trees.
 */
public class ChopTreeAction extends Action {
    private ItemSelector toolItemSelector;

    public ChopTreeAction(Designation designation) {
        super(new PositionActionTarget(designation.getPosition(), false, true));
        toolItemSelector = new ToolWithActionItemSelector("chop");
    }

    /**
     * Checks that tree exists on target position.
     * Checks that performer has chopping tool, creates equipping action if needed.
     */
    @Override
    public int check() {
        GameModel model = GameMvc.instance().getModel();
        EquipmentAspect aspect = task.getPerformer().getAspect(EquipmentAspect.class);
        if (aspect == null) return FAIL;
        if (!model.get(PlantContainer.class).isPlantBlockExists(actionTarget.getPosition())) {
            Logger.TASKS.logDebug("No tool equipped by performer for chopTreeAction");
            return FAIL;
        }
        if (toolItemSelector.checkItems(aspect.getEquippedItems())) return OK;

        Logger.TASKS.logDebug("No tool equipped by performer for chopTreeAction");
        Item target = model.get(ItemContainer.class).getItemAvailableBySelector(toolItemSelector, task.getPerformer().getPosition());
        if (target == null) {
            Logger.TASKS.logDebug("No tool item found for chopTreeAction");
            return FAIL;
        }
        task.addFirstPreAction(new EquipItemAction(target, true));
        return NEW;
    }

    @Override
    public void performLogic() {
        logStart();
        PlantContainer container = GameMvc.instance().getModel().get(PlantContainer.class);
        AbstractPlant plant = container.getPlantInPosition(actionTarget.getPosition());
        if (plant.getType().isTree()) container.remove(plant, true);
    }

    private void logStart() {
        Logger.TASKS.logDebug("tree chopping started at " + actionTarget.getPosition().toString() + " by " + task.getPerformer().toString());
    }

    @Override
    public String toString() {
        return "Chopping tree name";
    }
}
