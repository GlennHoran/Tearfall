package stonering.game.view.render.ui.menus.toolbar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Align;
import stonering.game.GameMvc;
import stonering.game.view.render.stages.PauseMenuStage;
import stonering.game.view.render.ui.menus.building.ToolbarBuildingMenu;
import stonering.game.view.render.ui.menus.util.SubMenuMenu;
import stonering.game.view.render.ui.menus.zone.ZonesMenu;

/**
 * Component of toolbar.
 * All screen commands first dispatched here, and then passed active screen.
 *
 * @author Alexander Kuzyakov on 19.12.2017.
 */
public class ParentMenu extends SubMenuMenu {

    public ParentMenu() {
        this.align(Align.bottom);
        createMenus();
    }

    private void createMenus() {
        addMenu(new PlantsMenu(), Input.Keys.P, "P: plants");
        addMenu(new DiggingMenu(), Input.Keys.O, "O: digging");
        addMenu(new ToolbarBuildingMenu(), Input.Keys.I, "I: building");
        addMenu(new ZonesMenu(), Input.Keys.U, "U: zones");
    }

    /**
     * Overrode to prevent closing.
     */
    @Override
    public void hide() {
//        GameMvc.instance().getView().addStageToList(new PauseMenuStage());
    }
}
