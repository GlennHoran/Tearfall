package stonering.entity.job.action;

import stonering.entity.item.Item;
import stonering.entity.job.action.target.ItemActionTarget;
import stonering.entity.unit.Unit;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.enums.items.TagEnum;

/**
 * Action from drinking items that are drinkable ({@link TagEnum}).
 *
 * @author Alexander on 09.10.2019.
 */
public class DrinkAction extends Action{
    private Item item;

    public DrinkAction(Item item) {
        super(new ItemActionTarget(item));
        this.item = item;
    }

    @Override
    public int check() {
        if(!item.tags.contains(TagEnum.DRINKABLE)) return FAIL; // item is not edible
        if(checkBetterDrink()) return FAIL; // better food is available, recreate.
        //TODO if tables available, use
        //TODO if dishes available, use
        Unit performer = task.getPerformer();
        if(performer.hasAspect(EquipmentAspect.class)) {
            if(performer.getAspect(EquipmentAspect.class).hauledItems.contains(item)) return OK;
        } else {

        }
        return OK;
    }

    @Override
    protected void performLogic() {

    }

    private boolean checkBetterDrink() {
        //TODO if target is spoiled and non-spoiled is available
        //TODO if target is spoiled and water source is available
        return false;
    }
}
