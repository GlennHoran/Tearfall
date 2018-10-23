package stonering.game.core.view.render.ui.components.menus;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import stonering.enums.designations.DesignationTypes;
import stonering.game.core.GameMvc;
import stonering.game.core.controller.controllers.DesignationsController;
import stonering.game.core.view.render.ui.components.menus.util.ButtonMenu;
import stonering.game.core.view.render.ui.components.menus.util.PlaceSelectComponent;

/**
 * ButtonMenu for selecting designation type.
 * Opens menu for selecting place.
 *
 * @author Alexander Kuzyakov
 */
public class DiggingMenu extends ButtonMenu {
    private DesignationsController controller;
    private PlaceSelectComponent placeSelectComponent;

    public DiggingMenu(GameMvc gameMvc) {
        super(gameMvc, true);
        hideable = true;
        initMenu();
    }

    public void init() {
        super.init();
        controller = gameMvc.getController().getDesignationsController();
        placeSelectComponent.init();
    }

    private void initMenu() {
        addButton("P: dig", DesignationTypes.DIG, Input.Keys.P);
        addButton("O: ramp", DesignationTypes.RAMP, Input.Keys.O);
        addButton("I: channel", DesignationTypes.CHANNEL, Input.Keys.I);
        addButton("U: stairs", DesignationTypes.STAIRS, Input.Keys.U);
        addButton("Y: clear", DesignationTypes.NONE, Input.Keys.Y);
        placeSelectComponent = new PlaceSelectComponent(gameMvc);
        placeSelectComponent.setSinglePoint(false);
    }

    private void addButton(String text, DesignationTypes type, int hotKey) {
        super.createButton(text, hotKey, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setActiveDesignation(type, null); //no buildings here
                placeSelectComponent.show(); //show place select for rectangle, without material select
            }
        }, true);
    }

    @Override
    public void reset() {
        controller.handleCancel();
    }
}
