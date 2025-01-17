package stonering.entity.job.action;

import stonering.entity.Entity;
import stonering.entity.building.aspects.FuelConsumerAspect;
import stonering.entity.item.Item;
import stonering.entity.item.aspects.FuelAspect;
import stonering.entity.item.selectors.FuelItemSelector;
import stonering.entity.job.action.aspects.ItemPickAction;
import stonering.entity.job.action.target.ActionTarget;
import stonering.entity.job.action.target.EntityActionTarget;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.game.GameMvc;
import stonering.game.model.system.items.ItemContainer;

/**
 * Action for putting fuel items to entities with {@link FuelConsumerAspect}.
 *
 * @author Alexander on 18.09.2019.
 */
public class FuelingAciton extends Action {
    public Item targetItem;

    protected FuelingAciton(Entity target) {
        super(new EntityActionTarget(target, ActionTarget.NEAR));
    }

    @Override
    public int check() {
        if (!((EntityActionTarget) actionTarget).entity.hasAspect(FuelConsumerAspect.class))
            return Action.FAIL; // invalid entity
        if (targetItem == null && (targetItem = lookupFuelItem()) == null) return FAIL; // no fuel item available
        if (!task.getPerformer().getAspect(EquipmentAspect.class).hauledItems.contains(targetItem)) {
            task.addFirstPreAction(new ItemPickAction(targetItem));
            return NEW;
        }
        return OK; // performer has item in inventory
    }

    @Override
    protected void performLogic() {
        task.getPerformer().getAspect(EquipmentAspect.class).dropItem(targetItem);
        ((EntityActionTarget) actionTarget).entity.getAspect(FuelConsumerAspect.class).acceptFuel(targetItem);
    }

    private Item lookupFuelItem() {
        Item foundItem = task.getPerformer().getAspect(EquipmentAspect.class).hauledItems.stream().filter(item -> item.hasAspect(FuelAspect.class)
                && item.getAspect(FuelAspect.class).isEnabled()).findFirst().orElse(null); // item from inventory
        if (foundItem != null) return foundItem;
        return GameMvc.instance().getModel().get(ItemContainer.class).util.getItemAvailableBySelector(new FuelItemSelector(), task.getPerformer().position);
    }
}
