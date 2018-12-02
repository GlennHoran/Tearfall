package stonering.game.core.view.render.ui.lists;

import com.badlogic.gdx.utils.Array;
import stonering.enums.materials.MaterialMap;
import stonering.game.core.controller.controllers.toolbar.DesignationsController;
import stonering.entity.local.items.Item;
import stonering.game.core.view.render.ui.util.ListItem;
import stonering.utils.global.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * List items and groups them by title and material.
 * List lines are linked to arrays of items for passing them to {@link DesignationsController}.
 *
 * @author Alexander Kuzyakov on 26.06.2018
 */
public abstract class ItemsCountList extends NavigableList {
    public ItemsCountList() {
        super();
    }

    /**
     * Groups given items by name and material and stores them as ListItems.
     *
     * @param items
     */
    public void addItems(List<Item> items) {
        HashMap<Pair<String, Integer>, ListItem> map = new HashMap<>(); // item name & material to ListItem
        MaterialMap materialMap = MaterialMap.getInstance();
        items.forEach(item -> { // groups items
            Pair<String, Integer> pair = new Pair<>(item.getName(), item.getMaterial());
            if (map.keySet().contains(pair)) {
                map.get(pair).getItems().add(item);
            } else {
                map.put(pair, new ListItem(item.getTitle(), materialMap.getMaterial(item.getMaterial()).getName(), item));
            }
        });
        Array<ListItem> listItems = new Array<>();
        listItems.addAll(map.values().toArray(new ListItem[1]));
        this.setItems(listItems);
    }

    protected ListItem getSelectedListItem() {
        return (ListItem) getSelected();
    }
}
