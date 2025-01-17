package stonering.entity.job.action;

import stonering.entity.item.selectors.SeedItemSelector;
import stonering.entity.job.action.aspects.ItemPickAction;
import stonering.entity.job.action.target.ActionTarget;
import stonering.entity.item.Item;
import stonering.entity.item.aspects.SeedAspect;
import stonering.entity.plants.Plant;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.exceptions.DescriptionNotFoundException;
import stonering.game.GameMvc;
import stonering.game.model.system.items.ItemContainer;
import stonering.game.model.system.PlantContainer;
import stonering.generators.plants.PlantGenerator;
import stonering.util.global.Logger;

import java.util.List;

/**
 * Action for planting seed to a farm.
 * Planting always use single seed item.
 * Seed item should have {@link SeedAspect}
 */
public class PlantingAction extends Action {
    private SeedItemSelector seedSelector;

    public PlantingAction(ActionTarget actionTarget, SeedItemSelector seedSelector) {
        super(actionTarget);
        this.seedSelector = seedSelector;
    }

    @Override
    public int check() {
        Logger.TASKS.logDebug("Checking planting action");
        if(getSeedFromEquipment() != null) return OK;
        return tryCreatePickingAction();
    }

    @Override
    protected void performLogic() {
        Logger.TASKS.logDebug("Planting seed of " + seedSelector.getSpecimen() + " to " + actionTarget.getPosition());
        createPlant(spendSeed());
    }

    /**
     * Tries to pick seed item if none is available in performer's inventory.
     */
    private int tryCreatePickingAction() {
        Item item = GameMvc.instance().getModel().get(ItemContainer.class).util.getItemAvailableBySelector(seedSelector, task.getPerformer().position);
        if (item == null) return FAIL;
        task.addFirstPreAction(new ItemPickAction(item));
        Logger.TASKS.logDebug("Creating pocking action for " + seedSelector.getSpecimen() + " seed.");
        return NEW;
    }

    /**
     * Seeks seed item in performers inventory.
     */
    private Item spendSeed() {
        Item seed = getSeedFromEquipment(); // seed should never be null after check()
        task.getPerformer().getAspect(EquipmentAspect.class).dropItem(seed);
        return seed;
    }

    /**
     * Looks for seed item in performer's inventory.
     */
    private Item getSeedFromEquipment() {
        EquipmentAspect equipmentAspect = task.getPerformer().getAspect(EquipmentAspect.class);
        List<Item> items = equipmentAspect.hauledItems;
        Item foundItem = seedSelector.selectItem(items);
        return foundItem;
    }

    /**
     * Creates new plant from seed item in target position.
     */
    private void createPlant(Item seed) {
        try {
            PlantContainer plantContainer = GameMvc.instance().getModel().get(PlantContainer.class);
            PlantGenerator plantGenerator = new PlantGenerator();
            Plant plant = plantGenerator.generatePlant(seed.getAspect(SeedAspect.class));
            plantContainer.place(plant, actionTarget.getPosition());
        } catch (DescriptionNotFoundException e) {
            e.printStackTrace();
        }
    }
}
