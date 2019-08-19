package stonering.game.view.render.stages.workbench.recipelist;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;
import stonering.entity.building.aspects.WorkbenchAspect;
import stonering.entity.crafting.ItemOrder;
import stonering.enums.ControlActionsEnum;
import stonering.enums.items.recipe.Recipe;
import stonering.game.view.render.stages.workbench.WorkbenchMenu;
import stonering.game.view.render.ui.menus.util.Highlightable;
import stonering.game.view.render.ui.menus.util.NavigableVerticalGroup;
import stonering.game.view.render.util.WrappedTextButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows recipes divided into categories.
 * Manages and toggles categories and recipes buttons.
 * Creates new orders.
 * <p>
 * Controls:
 * E, D: expand category, create new order.
 * A: collapse {@see handleCollapse}.
 * W, S: navigation.
 * Q: quit to order list.
 *
 * @author Alexander on 12.08.2019.
 */
public class RecipeListSection extends NavigableVerticalGroup implements Highlightable {
    private Map<String, List<String>> recipeMap;
    private WorkbenchMenu menu;

    public RecipeListSection(WorkbenchAspect aspect, WorkbenchMenu menu) {
        this.menu = menu;
        fillCategoryMap(aspect);
        createCategoryItems();
        createListeners();
        keyMapping.put(Input.Keys.D, ControlActionsEnum.SELECT);
        align(Align.topLeft);
        RecipeListSection list = this;
        setHighlightHandler(new HighlightHandler() {
            @Override
            public void handle() { // fetch elements and change color
                Actor selected = list.getSelectedElement();
                for (Actor child : list.getChildren()) {
                    ((WrappedTextButton) child).getActor().setColor(child.equals(selected) ? Color.RED : Color.BLUE);
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateHighlighting(this.equals(getStage().getKeyboardFocus()));
    }

    /**
     * Collects recipes from flat list of aspect and maps them to categories in a {@link HashMap}
     */
    private void fillCategoryMap(WorkbenchAspect aspect) {
        recipeMap = new HashMap<>();
        for (Recipe recipe : aspect.getRecipes()) {
            if (!recipeMap.containsKey(recipe.category)) recipeMap.put(recipe.category, new ArrayList<>());
            recipeMap.get(recipe.category).add(recipe.name);
        }
    }

    /**
     * Creates buttons for categories. These cannot be hidden.
     */
    private void createCategoryItems() {
        for (String categoryName : recipeMap.keySet()) {
            addActor(new RecipeCategoryItem(categoryName, this, recipeMap.get(categoryName)));
        }
    }

    public void updateCategory(RecipeCategoryItem category) {
        if (!getChildren().contains(category, true)) return;
        if (category.isExpanded()) {
            for (RecipeItem recipeItem : category.getRecipeItems()) {
                addActorAfter(category, recipeItem);
            }
            setSelectedIndex(getChildren().indexOf(category, true) + 1);
        } else {
            for (RecipeItem recipeItem : category.getRecipeItems()) {
                removeActor(recipeItem);
            }
            setSelectedIndex(getChildren().indexOf(category, true));
        }
    }

    /**
     * Creates new {@link ItemOrder} and adds it to order list of this workbench.
     */
    public void createNewOrder(String recipeName) {
        //TODO create new order, add to list, show in right pane
        setSelectedIndex(-1);
    }

    private void createListeners() {
        setSelectListener(event -> { // toggles categories and recipes buttons
                    ((WrappedTextButton) getSelectedElement()).toggle();
                    return true;
                }
        );
        setCancelListener(event -> getStage().setKeyboardFocus(menu.orderListSection)); // quits to order list from any item
        addListener(new InputListener() { // for collapsing
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return keycode == Input.Keys.A && handleCollapse();
            }
        });
    }

    /**
     * Collapses selected actor with behaviour:
     */
    private boolean handleCollapse() {
        Actor selected = getSelectedElement();
        if (selected instanceof RecipeCategoryItem) {
            RecipeCategoryItem category = (RecipeCategoryItem) selected;
            if (category.isExpanded()) { // collapse expanded category
                category.update(false);
            } else { // collapse all categories if selected one is collapsed
                for (Actor child : getChildren()) {
                    if (child instanceof RecipeCategoryItem) {
                        ((RecipeCategoryItem) child).update(false);
                    }
                }
                setSelectedIndex(getChildren().indexOf(selected, true));
            }
        } else if (selected instanceof RecipeItem) { // go to category
            setSelectedIndex(getChildren().indexOf(((RecipeItem) selected).category, true));
        }
        return true;
    }

    @Override
    public boolean navigate(int delta) {
        super.navigate(delta);
        menu.orderDetailsSection.showItem(getSelectedElement());
        return true;
    }
}
