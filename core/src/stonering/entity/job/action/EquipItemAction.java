package stonering.entity.job.action;

import stonering.entity.item.aspects.WearAspect;
import stonering.entity.job.action.target.ItemActionTarget;
import stonering.entity.item.Item;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.entity.unit.aspects.equipment.EquipmentSlot;
import stonering.game.GameMvc;
import stonering.game.model.system.items.ItemContainer;
import stonering.util.global.Logger;

/**
 * Action for equipping wear and tool items, and hauling other items.
 */
public class EquipItemAction extends Action {
    private Item item;
    private boolean force; //enables unequipping other items.

    public EquipItemAction(Item item, boolean force) {
        super(new ItemActionTarget(item));
        this.item = item;
        this.force = force;
    }

    @Override
    protected void performLogic() {
        ItemContainer container = GameMvc.instance().getModel().get(ItemContainer.class);
        if (!(task.getPerformer().getAspect(EquipmentAspect.class)).equipItem(item)) return; // equipping failed
        container.equipped.put(item, task.getPerformer().getAspect(EquipmentAspect.class));
        container.pickItem(item);
    }

    @Override
    public int check() {
        if (!task.getPerformer().hasAspect(EquipmentAspect.class))
            return failWithLog("unit " + task.getPerformer() + " has no Equipment Aspect.");
        EquipmentSlot slot = task.getPerformer().getAspect(EquipmentAspect.class).getSlotForItem(item);
        if (slot == null) return failWithLog("unit " + task.getPerformer() + " has no appropriate slots for item " + item);
        Item blockingItem = slot.getBlockingItem(item);
        if (blockingItem == null) return OK; // slot is not blocked
        if (!force) return failWithLog("unit " + task.getPerformer() + " cannot equip item " + item + " no empty slots.");
        if (item.hasAspect(WearAspect.class)) {
            return createUnequipWearAction(blockingItem); // wear can block only wear items
        } else if (item.isTool()) {
            return createUnequipToolAction(blockingItem);
        }
        return failWithLog("Invalid case in EquipItemAction:check()");
    }

    /**
     * Creates action for unequipping/equipping high-layer item and adds them to task,
     * so equipping of low-layer item will be in between them.
     * Equipping action will not be performed, if there is no room for item.
     *
     * @param item item(high-layer), that blocks equipping of main item(low-layer)
     * @return false if action are impossible or item is invalid.
     */
    private int createUnequipWearAction(Item item) {
        // unequip action
        UnequipItemAction action = new UnequipItemAction(item);
        task.addFirstPreAction(action);

        // equip action
        EquipItemAction equipItemAction = new EquipItemAction(item, false);
        task.addFirstPreAction(equipItemAction);
        return NEW;
    }

    /**
     * Creates action only for unequipping item. Used to unequip tool when equipping another tool, as tools are highest layer,
     * and two tools cannot be held simultaneously.
     */
    private int createUnequipToolAction(Item item) {
        UnequipItemAction unequipItemAction = new UnequipItemAction(item);
        task.addFirstPreAction(unequipItemAction);
        return NEW;
    }

    private int failWithLog(String message) {
        Logger.ITEMS.logError(message);
        return FAIL;
    }

    @Override
    public String toString() {
        return "Equipping action: " + item.getTitle();
    }
}
