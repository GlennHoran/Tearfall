package stonering.entity.building.aspects;

import stonering.entity.Aspect;
import stonering.entity.Entity;
import stonering.entity.item.Item;
import stonering.entity.item.aspects.FuelAspect;
import stonering.enums.time.TimeUnitEnum;
import stonering.game.GameMvc;
import stonering.game.model.system.items.ItemContainer;

/**
 * Buildings with this aspect can be loaded with fuel item, to make them operational.
 * TODO update render
 *
 * @author Alexander_Kuzyakov
 */
public class FuelConsumerAspect extends Aspect {
    public int remainingTime; // in minutes

    public FuelConsumerAspect(Entity entity) {
        super(entity);
    }

    @Override
    public void turnUnit(TimeUnitEnum unit) {
        if (isFueled() && unit == TimeUnitEnum.MINUTE) remainingTime--;
    }

    public boolean isFueled() {
        return remainingTime > 0;
    }

    /**
     * Called by
     * @param item
     */
    public void acceptFuel(Item item) {
        if (item.hasAspect(FuelAspect.class) && item.getAspect(FuelAspect.class).isEnabled()) {
            remainingTime += 30; //TODO calculate by item size.
            GameMvc.instance().getModel().get(ItemContainer.class).removeItem(item); // item is destroyed immediately
        }
    }
}
