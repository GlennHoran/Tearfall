package stonering.game.view.render.stages.workbench.oldmenu.orderline.selectbox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import stonering.entity.crafting.ItemPartOrder;
import stonering.enums.ControlActionsEnum;
import stonering.game.view.render.ui.lists.NavigableSelectBox;
import stonering.game.view.render.ui.menus.util.Highlightable;
import stonering.game.view.render.ui.menus.util.HintedActor;
import stonering.game.view.render.stages.workbench.oldmenu.orderline.ItemPartSelection;
import stonering.game.view.render.stages.workbench.oldmenu.orderline.OrderLine;

/**
 * Select box to be shown within {@link OrderLine}.
 *
 * @author Alexander on 26.06.2019.
 */
public abstract class OrderLineSelectBox extends NavigableSelectBox<String> implements HintedActor, Highlightable {
    protected ItemPartOrder itemPartOrder; // updated on SB change.
    protected ItemPartSelection selection;
    private HighlightHandler highlightHandler;

    /**
     * @param itemPartOrder all data is taken from order for item part.
     */
    public OrderLineSelectBox(ItemPartOrder itemPartOrder, ItemPartSelection selection) {
        super();
        this.itemPartOrder = itemPartOrder;
        this.selection = selection;
        createListener();
        getList().addListener(createTouchListener());
        highlightHandler = new HighlightHandler();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateHighlighting(getStage().getKeyboardFocus() == this);
    }

    /**
     * Creates selectBox for selecting material for item part.
     * If no item is available,
     * SelectBox can have no item after this (if no item available on map).
     */
    private void createListener() {
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                // TODO update status and warning labels
                ControlActionsEnum action = ControlActionsEnum.getAction(keycode);
                switch (action) {
                    // confirms selected option, if dropdown is open. Otherwise, opens dropdown. no transition
                    case SELECT: {
                        if (getList().getStage() != null) {
                            handleSelection();
                        } else {
                            showList();
                        }
                        return true;
                    }
                    // confirms selected option, if dropdown is open. Then, moves to next or previous select box, or exits to list
                    case LEFT:
                    case RIGHT: {
                        if (getList().getStage() != null) handleSelection();
                        selection.navigate(action); // move to one SB left or right.
                        return true;
                    }
                    // hides dropdown and goes to list.
                    case CANCEL: {
                        hideList();
                        selection.navigate(action);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    protected void handleSelection() {
        hideList();
    }

    /**
     * Updates stage focus, so navigation can be continued from this SB after click.
     */
    private InputListener createTouchListener() {
        Actor actor = this;
        return new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                getStage().setKeyboardFocus(actor);
                return true;
            }
        };
    }

    private class HighlightHandler extends Highlightable.CheckHighlightHandler {

        @Override
        public void handle() {
            //TODO update background
        }
    }

    @Override
    public String getHint() {
        return "";
    }

    @Override
    public Highlightable.HighlightHandler getHighlightHandler() {
        return highlightHandler;
    }
}