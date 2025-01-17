package stonering.stage.toolbar.menus;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Align;
import stonering.widget.ToolbarSubMenuMenu;

/**
 * Component of toolbar.
 *
 * @author Alexander Kuzyakov on 19.12.2017.
 */
public class ParentMenu extends ToolbarSubMenuMenu {

    public ParentMenu(Toolbar toolbar) {
        super(toolbar);
        this.align(Align.bottom);
        createMenus();
    }

    private void createMenus() {
        addMenu(new PlantsMenu(toolbar), Input.Keys.P, "plants", "plants_menu");
        addMenu(new DiggingMenu(toolbar), Input.Keys.O, "digging", "digging_menu");
        addMenu(new ToolbarBuildingMenu(toolbar), Input.Keys.I, "building", "building_menu");
        addMenu(new ZonesMenu(toolbar), Input.Keys.U, "zones", "zones_menu");
    }

    /**
     * Overrode to prevent closing.
     */
    @Override
    public void hide() {
    }
}
