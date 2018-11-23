package stonering.game.core.view.render.ui.menus.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import stonering.entity.local.building.validators.PositionValidator;
import stonering.game.core.GameMvc;
import stonering.game.core.controller.controllers.toolbar.DesignationsController;
import stonering.game.core.controller.inputProcessors.GameInputProcessor;
import stonering.game.core.model.EntitySelector;
import stonering.game.core.view.render.ui.menus.Toolbar;
import stonering.global.utils.Position;
import stonering.utils.global.StaticSkin;

/**
 * Component for selecting places. Can be used many times.
 *
 * @author Alexander Kuzyakov on 12.07.2018.
 */
public class PlaceSelectComponent extends Label implements HideableComponent, MouseInvocable {
    private GameMvc gameMvc;
    private DesignationsController controller;
    private Toolbar toolbar;
    private EntitySelector selector;
    private PositionValidator positionValidator;

    public PlaceSelectComponent(GameMvc gameMvc) {
        super("", StaticSkin.getSkin());
        this.gameMvc = gameMvc;
    }

    public void init() {
        selector = gameMvc.getModel().getCamera();
        controller = gameMvc.getController().getDesignationsController();
        toolbar = gameMvc.getView().getUiDrawer().getToolbar();
    }

    @Override
    public boolean invoke(int keycode) {
        //TODO add reference to Key Settings
        switch (keycode) {
            case Input.Keys.E:
                handleConfirm(selector.getPosition());
                return true;
            case Input.Keys.Q:
                handleCancel();
                return true;
        }
        return false;
    }

    @Override
    public boolean invoke(int modelX, int modelY, int button, int action) {
        Position position = new Position(modelX, modelY, selector.getPosition().getZ());
        switch (action) {
            case GameInputProcessor.DOWN_CODE:
            case GameInputProcessor.UP_CODE: {
                handleConfirm(position);
            }
            break;
            case GameInputProcessor.MOVE_CODE: {
            }
        }
        return false;
    }

    /**
     * Finishes handling or adds position to select box
     *
     * @param eventPosition
     */
    private void handleConfirm(Position eventPosition) {
        controller.setRectangle(eventPosition, eventPosition);
        hide();
    }

    /**
     * Closes component or discards last position from select box.
     */
    private void handleCancel() {
        hide();
    }

    /**
     * Adds this to toolbar and sets position validator to selector.
     */
    @Override
    public void show() {
        toolbar.addMenu(this);
        selector.setPositionValidator(positionValidator);
    }

    @Override
    public void hide() {
        selector.setPositionValidator(null);
        toolbar.hideMenu(this);
    }

    public void setPositionValidator(PositionValidator positionValidator) {
        this.positionValidator = positionValidator;
    }
}
