package stonering.game.core.view.render.ui.menus.workbench;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import stonering.entity.local.building.Building;
import stonering.entity.local.building.aspects.WorkbenchAspect;
import stonering.entity.local.crafting.ItemOrder;
import stonering.game.core.GameMvc;
import stonering.game.core.view.render.ui.menus.util.Highlightable;
import stonering.game.core.view.render.ui.menus.util.HintedActor;
import stonering.game.core.view.render.ui.menus.util.NavigableVerticalGroup;
import stonering.game.core.view.render.ui.menus.workbench.orderline.ItemCraftingOrderLine;
import stonering.utils.global.StaticSkin;
import stonering.utils.global.TagLoggersEnum;

/**
 * Menu for workbenches to manage crafting orders.
 * Has list of orders and buttons for closing and creating new order.
 *
 * @author Alexander on 28.10.2018.
 */
public class WorkbenchMenu extends Window implements HintedActor {
    private static final String MENU_HINT = "E:create WASD:navigate Q:quit";

    private GameMvc gameMvc;
    private Building workbench;
    private WorkbenchAspect workbenchAspect; // aspect of selected workbench (M thing)

    private NavigableVerticalGroup orderList;
    private TextButton addOrderButton;
    private TextButton closeButton;
    private Label hintLabel;

    /**
     * Creates menu for selected built workbench on localMap. Can be used only for workbenches.
     * Will throw NPE if created on non-workbench workbench.
     */
    public WorkbenchMenu(GameMvc gameMvc, Building building) {
        super(building.getName(), StaticSkin.getSkin());
        this.gameMvc = gameMvc;
        this.workbench = building;
        workbenchAspect = (WorkbenchAspect) building.getAspects().get(WorkbenchAspect.NAME);
        setKeepWithinStage(true);
        createTable();
        refillWorkbenchOrders();
    }

    /**
     * Creates menu table.
     */
    private void createTable() {
        setDebug(true, true);
        add(createOrderList().fill()).prefWidth(600).prefHeight(200).expandX();
        add(createCloseButton()).prefWidth(20).prefHeight(20).right().top().row();
        add(createAddButton()).prefHeight(20).left().top();
        add(hintLabel = new Label(MENU_HINT, StaticSkin.getSkin()));
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                TagLoggersEnum.UI.logDebug("handling " + Input.Keys.toString(keycode) + " on WorkbenchMenu");
                event.stop();
                switch (keycode) {
                    case Input.Keys.E: {
                        return createNewOrderLine();
                    }
                    case Input.Keys.W:
                    case Input.Keys.A:
                    case Input.Keys.S:
                    case Input.Keys.D: {
                        if (orderList.hasChildren()) {
                            updateStageFocus(orderList);
                        }
                        return true;
                    }
                    case Input.Keys.Q: {
                        close();
                        return true;
                    }
                }
                return false;
            }
        });
        setWidth(800);
        setHeight(600);

    }

    private TextButton createCloseButton() {
        closeButton = new TextButton("X", StaticSkin.getSkin());
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                close();
                return true;
            }
        });
        return closeButton;
    }

    private TextButton createAddButton() {
        addOrderButton = new TextButton("New", StaticSkin.getSkin());
        addOrderButton.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return createNewOrderLine();
                    }
                });
        return addOrderButton;
    }

    private NavigableVerticalGroup createOrderList() {
        orderList = new NavigableVerticalGroup();
        orderList.grow();
        orderList.getSelectKeys().add(Input.Keys.D);
        orderList.setSelectListener(event -> {
            event.stop();
            Actor selected = orderList.getSelectedElement();
            updateStageFocus(selected != null ? selected : this);
            return true;
        });
        orderList.setCancelListener(event -> {
            event.stop();
            close();
            return true;
        });
        return orderList;
    }

    /**
     * Creates new empty order line, adds it to order list and moves focus to it.
     */
    private boolean createNewOrderLine() {
        ItemCraftingOrderLine orderLine = new ItemCraftingOrderLine(gameMvc, this);
        orderLine.show();
        updateStageFocus(orderLine.getRecipeSelectBox());
//        orderLine.setHighlighted(true);
//        orderLine.setFillParent(true);
        return true;
    }

    /**
     * Refills list of menu with existing orders.
     */
    private void refillWorkbenchOrders() {
        workbenchAspect.getOrders().forEach(order -> {
            orderList.addActor(createOrderLine(order));
        });
    }

    private ItemCraftingOrderLine createOrderLine(ItemOrder order) {
        return new ItemCraftingOrderLine(gameMvc, this, order);
    }

    /**
     * Checks if building is workbench (has workbench aspect).
     */
    private static boolean validateBuilding(Building building) {
        return building.getAspects().containsKey(WorkbenchAspect.NAME);
    }

    /**
     * Closes stage with this menu.
     */
    public void close() {
        gameMvc.getView().removeStage(getStage());
    }

    public NavigableVerticalGroup getOrderList() {
        return orderList;
    }

    public Building getWorkbench() {
        return workbench;
    }

    public WorkbenchAspect getWorkbenchAspect() {
        return workbenchAspect;
    }

    /**
     * Moves focus of stage to given actor, highlights it and changes hint, if possible.
     */
    public void updateStageFocus(Actor actor) {
        Actor old = getStage().getKeyboardFocus();
        if (old.getClass().isAssignableFrom(Highlightable.class)) {
            ((Highlightable) old).setHighlighted(false);
        }
        getStage().setKeyboardFocus(actor);
        if (actor.getClass().isAssignableFrom(HintedActor.class)) {
            hintLabel.setText(((HintedActor) actor).getHint());
        } else {
            hintLabel.setText("");
        }
        if (actor.getClass().isAssignableFrom(Highlightable.class)) {
            ((Highlightable) actor).setHighlighted(false);
        }
    }

    public String getHint() {
        return MENU_HINT;
    }
}
