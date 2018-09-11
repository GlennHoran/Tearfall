package stonering.objects.local_actors.items.selectors;

import stonering.enums.items.ToolItemType;
import stonering.objects.local_actors.items.Item;

import java.util.ArrayList;

/**
 * Selects tools with specified action
 *
 * @author Alexander on 11.09.2018.
 */
public class ToolWithActionItemSelector extends ItemSelector {
    private String actionName;

    public ToolWithActionItemSelector(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public boolean check(ArrayList<Item> items) {
        return !selectItems(items).isEmpty();
    }

    @Override
    public ArrayList<Item> selectItems(ArrayList<Item> items) {
        ArrayList<Item> foundItems = new ArrayList<>();
        for (Item item : items) {
            ToolItemType tool;
            if ((tool = item.getType().getTool()) != null) {
                if (tool.getActions().size() > 0) {
                    for (ToolItemType.ToolAction toolAction : tool.getActions()) {
                        if (toolAction.action.equals(actionName)) {
                            foundItems.add(item);
                        }
                    }
                }
            }
        }
        return foundItems;
    }
}
