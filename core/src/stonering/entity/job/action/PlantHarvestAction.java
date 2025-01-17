package stonering.entity.job.action;

import stonering.entity.job.action.target.PlantActionTarget;
import stonering.entity.item.Item;
import stonering.entity.item.selectors.ItemSelector;
import stonering.entity.item.selectors.ToolWithActionItemSelector;
import stonering.entity.plants.AbstractPlant;
import stonering.entity.plants.PlantBlock;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.game.GameMvc;
import stonering.game.model.system.items.ItemContainer;
import stonering.game.model.system.PlantContainer;
import stonering.generators.items.PlantProductGenerator;
import stonering.util.global.Logger;

public class PlantHarvestAction extends Action {
    private ItemSelector toolItemSelector;

    public PlantHarvestAction(AbstractPlant plant) {
        super(new PlantActionTarget(plant));
        toolItemSelector = new ToolWithActionItemSelector("harvest_plants"); //TODO handle harvesting without tool.
    }

    @Override
    public int check() {
        PlantContainer container = GameMvc.instance().getModel().get(PlantContainer.class);
        EquipmentAspect aspect = task.getPerformer().getAspect(EquipmentAspect.class);
        if (aspect == null) return FAIL;
        AbstractPlant targetPlant = ((PlantActionTarget) actionTarget).getPlant();
        if (container.getPlantInPosition(actionTarget.getPosition()) != targetPlant) return FAIL;
        if(toolItemSelector.checkItems(aspect.equippedItems)) return OK;
        return addActionToTask();
    }

    private int addActionToTask() {
        Item target = GameMvc.instance().getModel().get(ItemContainer.class).util.getItemAvailableBySelector(toolItemSelector, task.getPerformer().position);
        if (target == null) return FAIL;
        EquipItemAction equipItemAction = new EquipItemAction(target, true);
        task.addFirstPreAction(equipItemAction);
        return NEW;
    }

    @Override
    public void performLogic() {
        Logger.PLANTS.logDebug("harvesting plant");
        PlantBlock block = GameMvc.instance().getModel().get(PlantContainer.class).getPlantBlock(actionTarget.getPosition());
        Item item = new PlantProductGenerator().generateHarvestProduct(block);
        GameMvc.instance().getModel().get(ItemContainer.class).addAndPut(item);
    }
}
