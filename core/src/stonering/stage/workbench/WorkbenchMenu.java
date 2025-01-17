package stonering.stage.workbench;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import stonering.entity.building.Building;
import stonering.entity.building.aspects.WorkbenchAspect;
import stonering.stage.workbench.details.OrderDetailsSection;
import stonering.stage.workbench.orderlist.OrderListSection;
import stonering.stage.workbench.recipelist.RecipeListSection;
import stonering.widget.util.WrappedLabel;
import stonering.util.global.StaticSkin;

/**
 * Contains three sections: {@link RecipeListSection}, {@link OrderListSection}, {@link OrderDetailsSection}.
 *
 * @author Alexander on 12.08.2019.
 */
public class WorkbenchMenu extends Window {
    public final RecipeListSection recipeListSection;
    public final OrderListSection orderListSection;
    public final OrderDetailsSection orderDetailsSection;
    public final WrappedLabel recipesHeader;
    public final WrappedLabel ordersHeader;
    public final WrappedLabel detailsHeader;
    public final WrappedLabel hintLabel;

    public WorkbenchMenu(Building workbench) {
        super(workbench.getType().title, StaticSkin.getSkin());
        defaults().size(300, 700).pad(5);
        add(recipesHeader = new WrappedLabel("Recipes:")).height(20);
        add(ordersHeader = new WrappedLabel("Orders:")).height(20);
        add(detailsHeader = new WrappedLabel("Details:")).height(20).row();
        add(recipeListSection = new RecipeListSection(workbench.getAspect(WorkbenchAspect.class), this));
        add(orderListSection = new OrderListSection(workbench.getAspect(WorkbenchAspect.class), this));
        add(orderDetailsSection = new OrderDetailsSection(workbench.getAspect(WorkbenchAspect.class), this)).row();
        add(hintLabel = new WrappedLabel("")).colspan(3).size(900, 30).height(30).align(Align.left);
    }

    public void initFocus() {
        Actor target = orderListSection;
        if(orderListSection.isEmpty())  target = recipeListSection; // focus recipes, if no orders
        getStage().setKeyboardFocus(target);
    }
}
