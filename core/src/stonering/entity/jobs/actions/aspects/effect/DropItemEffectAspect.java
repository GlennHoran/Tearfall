package stonering.entity.jobs.actions.aspects.effect;

import stonering.entity.jobs.actions.Action;
import stonering.entity.local.items.Item;
import stonering.entity.local.unit.aspects.EquipmentAspect;

/**
 * @author Alexander on 08.07.2018.
 */
public class DropItemEffectAspect extends EffectAspect {
    private Item item;

    public DropItemEffectAspect(Action action, Item item) {
        super(action, 10);
        this.item = item;
    }

    @Override
    protected void applyEffect() {
        EquipmentAspect equipmentAspect = (EquipmentAspect) action.getTask().getPerformer().getAspects().get("equipment");
        if (equipmentAspect != null && equipmentAspect.checkItem(item)) {
            equipmentAspect.dropItem(item);
            action.getGameContainer().getItemContainer().putItem(item, action.getTask().getPerformer().getPosition());
        }
    }
}