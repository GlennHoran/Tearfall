package stonering.entity.jobs.actions;

import stonering.designations.Designation;
import stonering.entity.jobs.actions.target.PositionActionTarget;
import stonering.entity.local.items.Item;
import stonering.entity.local.items.selectors.ItemSelector;
import stonering.entity.local.items.selectors.ToolWithActionItemSelector;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.Plant;
import stonering.entity.local.plants.PlantBlock;
import stonering.entity.local.plants.Tree;
import stonering.entity.local.unit.aspects.equipment.EquipmentAspect;
import stonering.enums.OrientationEnum;
import stonering.game.GameMvc;
import stonering.game.model.lists.ItemContainer;
import stonering.game.model.lists.PlantContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.util.global.TagLoggersEnum;

/**
 * Action for chopping trees.
 * Checks that target position contains tree {@link PlantBlock} and performer has tool for chopping.
 */
public class ChopTreeAction extends Action {
    private ItemSelector toolItemSelector;

    //TODO replace target with plantActionTarget to track specific trees.
    public ChopTreeAction(Designation designation) {
        super(new PositionActionTarget(designation.getPosition(), false, true));
        toolItemSelector = new ToolWithActionItemSelector("chop");
    }

    @Override
    public boolean check() {
        EquipmentAspect aspect = task.getPerformer().getAspect(EquipmentAspect.class);
        if (aspect == null) return false;
        PlantBlock block = GameMvc.instance().getModel().get(LocalMap.class).getPlantBlock(actionTarget.getPosition());
        if (block == null) return false;
        if(!(block.getPlant() instanceof Tree)) return false;
        return toolItemSelector.check(aspect.getEquippedItems()) || addActionToTask();
    }

    /**
     * Create action for equipping available chopping tool.
     */
    private boolean addActionToTask() {
        Item target = GameMvc.instance().getModel().get(ItemContainer.class).getItemAvailableBySelector(toolItemSelector, task.getPerformer().getPosition());
        if (target == null) return false;
        EquipItemAction equipItemAction = new EquipItemAction(target, true);
        task.addFirstPreAction(equipItemAction);
        return true;
    }

    /**
     *
     */
    @Override
    public void performLogic() {
        TagLoggersEnum.TASKS.logDebug("tree chopping started at " + actionTarget.getPosition().toString() + " by " + task.getPerformer().toString());
        PlantBlock block = GameMvc.instance().getModel().get(LocalMap.class).getPlantBlock(actionTarget.getPosition());
        if(block == null) return;
        AbstractPlant plant = block.getPlant();
        if (plant instanceof Tree) {
            GameMvc.instance().getModel().get(PlantContainer.class).fellTree((Tree) plant, OrientationEnum.N, true);
        }
    }

    @Override
    public String toString() {
        return "Chopping tree action";
    }
}
