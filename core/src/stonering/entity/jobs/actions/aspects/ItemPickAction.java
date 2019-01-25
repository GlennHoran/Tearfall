package stonering.entity.jobs.actions.aspects;

import stonering.entity.jobs.actions.Action;
import stonering.entity.jobs.actions.target.ItemActionTarget;
import stonering.entity.local.items.Item;
import stonering.entity.local.unit.aspects.EquipmentAspect;

/**
 * Action for picking and hauling item. Performer should have {@link EquipmentAspect}
 *
 * @author Alexander on 12.01.2019.
 */
public class ItemPickAction extends Action {

    public ItemPickAction(Item targetItem) {
        super(new ItemActionTarget(targetItem));
    }

    @Override
    public void performLogic() {
        Item targetItem = getTargetItem();
        ((EquipmentAspect) task.getPerformer().getAspects().get(EquipmentAspect.NAME)).pickupItem(targetItem);
        gameMvc.getModel().getItemContainer().pickItem(targetItem);
    }

    @Override
    public boolean check() {
        if (!task.getPerformer().getAspects().containsKey(EquipmentAspect.NAME)) return false;
        return gameMvc.getModel().getItemContainer().checkItem(getTargetItem());
    }

    private Item getTargetItem() {
        return ((ItemActionTarget) actionTarget).getItem();
    }
}