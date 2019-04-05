package stonering.entity.jobs.actions;

import stonering.entity.jobs.actions.target.PlantActionTarget;
import stonering.entity.local.items.Item;
import stonering.entity.local.items.selectors.ItemSelector;
import stonering.entity.local.items.selectors.ToolWithActionItemSelector;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.PlantBlock;
import stonering.entity.local.unit.aspects.equipment.EquipmentAspect;
import stonering.game.GameMvc;
import stonering.game.model.lists.ItemContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.generators.items.PlantProductGenerator;
import stonering.util.geometry.Position;

import java.util.List;

public class PlantHarvestAction extends Action {
    private ItemSelector toolItemSelector;

    public PlantHarvestAction(AbstractPlant plant) {
        super(new PlantActionTarget(plant));
        toolItemSelector = new ToolWithActionItemSelector("harvest_plants"); //TODO handle harvesting without tool.
    }

    @Override
    public boolean check() {
        EquipmentAspect aspect = task.getPerformer().getAspect(EquipmentAspect.class);
        if (aspect == null) return false;
        AbstractPlant abstractPlant = ((PlantActionTarget) actionTarget).getPlant();
        Position position = actionTarget.getPosition();
        PlantBlock block = GameMvc.instance().getModel().get(LocalMap.class).getPlantBlock(position);
        if (block == null || block.getPlant() != abstractPlant) return false;
        return toolItemSelector.check(aspect.getEquippedItems()) || addActionToTask();
    }

    private boolean addActionToTask() {
        Item target = GameMvc.instance().getModel().get(ItemContainer.class).getItemAvailableBySelector(toolItemSelector, task.getPerformer().getPosition());
        if (target == null) return false;
        EquipItemAction equipItemAction = new EquipItemAction(target, true);
        task.addFirstPreAction(equipItemAction);
        return true;
    }

    @Override
    public void performLogic() {
        System.out.println("harvesting plant");
        Position position = actionTarget.getPosition();
        PlantBlock block = GameMvc.instance().getModel().get(LocalMap.class).getPlantBlock(position);
        List<Item> items = new PlantProductGenerator().generateHarvestProduct(block);
        items.forEach(item -> GameMvc.instance().getModel().get(ItemContainer.class).putItem(item, position));
    }
}
