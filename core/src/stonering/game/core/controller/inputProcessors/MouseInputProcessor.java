package stonering.game.core.controller.inputProcessors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import stonering.game.core.controller.controllers.GameController;
import stonering.game.core.controller.controllers.MouseController;

/**
 * Intercepts mouse clicks and moves.
 *
 * @author Alexander on 06.09.2018.
 */
public class MouseInputProcessor extends DragListener implements InputProcessor {
    public final static int DOWN_CODE = 0;
    public final static int UP_CODE = 1;
    public final static int MOVE_CODE = 2;

    private MouseController controller;

    public MouseInputProcessor(GameController controller) {
        this.controller = controller.getMouseController();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            controller.handleEvent(screenX, screenY, button, DOWN_CODE);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
            controller.handleEvent(screenX, screenY, button, UP_CODE);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            controller.handleEvent(Math.round(screenX), Math.round(screenY), Gdx.input.isButtonPressed(Input.Buttons.LEFT) ? 0 : 1, MOVE_CODE);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}