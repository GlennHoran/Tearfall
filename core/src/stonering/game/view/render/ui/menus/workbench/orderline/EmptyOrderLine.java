package stonering.game.view.render.ui.menus.workbench.orderline;

import stonering.entity.local.crafting.ItemOrder;
import stonering.enums.items.recipe.Recipe;
import stonering.game.view.render.ui.menus.workbench.WorkbenchMenu;
import stonering.game.view.render.ui.menus.workbench.orderline.selectbox.RecipeSelectBox;

/**
 * This line shows select box with list of available crafting recipes.
 * When recipe is selected, this line replaces itself with {@link ItemCraftingOrderLine}
 *
 * @author Alexander_Kuzyakov on 24.06.2019.
 */
public class EmptyOrderLine extends OrderLine {
    private static final String LINE_HINT = "WS: select recipe, ED: confirm";
    private RecipeSelectBox selectBox;

    public EmptyOrderLine(WorkbenchMenu menu) {
        super(menu, LINE_HINT);
        leftHG.addActor(selectBox = new RecipeSelectBox(menu.getWorkbenchAspect().getRecipes()));
        selectBox.setSelectListener(e -> {
            e.stop();
            if (selectBox.getList().getStage() != null) { // list is shown
                selectBox.hideList();
                if (!selectBox.getSelected().equals(selectBox.getPlaceHolder())) { // placeholder is selected
                    replaceSelfWith(selectBox.getSelected());
                } else { // not a valid case
                    warningLabel.setText("Item not selected");
                }
            } else {  // open list
                selectBox.showList();
            }
            return true;
        });
        selectBox.setCancelListener(e -> {
            e.stop();
            hide();
            return true;
        });
    }

    private void replaceSelfWith(Recipe recipe) {
        hide();
        ItemCraftingOrderLine orderLine = new ItemCraftingOrderLine(menu, new ItemOrder(recipe));
        orderLine.show();
        orderLine.navigateToFirst();
    }

    @Override
    public void show() {
        super.show();
        getStage().setKeyboardFocus(selectBox);
    }
}
