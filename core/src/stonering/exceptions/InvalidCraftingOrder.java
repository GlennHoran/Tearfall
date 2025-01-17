package stonering.exceptions;

import stonering.entity.crafting.ItemOrder;

/**
 * Is thrown by consumers of item item orders ({@link stonering.generators.items.ItemGenerator}) if order is invalid.
 *
 * @author Alexander on 28.10.2018.
 */
public class InvalidCraftingOrder extends Exception {
    private ItemOrder order;

    public InvalidCraftingOrder(ItemOrder order) {
        this.order = order;
    }
}
