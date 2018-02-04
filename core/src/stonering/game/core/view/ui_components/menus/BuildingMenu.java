package stonering.game.core.view.ui_components.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import stonering.enums.designations.DesignationsTypes;
import stonering.game.core.controller.controllers.BuildingController;
import stonering.game.core.controller.controllers.DesignationsController;
import stonering.game.core.view.ui_components.Toolbar;
import stonering.utils.global.StaticSkin;

/**
 * Created by Alexander on 25.01.2018.
 */
public class BuildingMenu extends Menu {
    private BuildingController controller;
    private Toolbar toolbar;

    public BuildingMenu() {
        super();
        createTable();
    }

    private void createTable() {
        this.defaults().padBottom(5).fillX();
        this.pad(10);
        this.right().bottom();

//        TextButton diggingButton = new TextButton("D: dig", StaticSkin.getSkin());
//        diggingButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                controller.setActiveDesignation(DesignationsTypes.DIG);
//                toolbar.closeMenus();
//            }
//        });
//        this.add(diggingButton).right().row();
//        hotkeyMap.put('d', diggingButton);
//
//        TextButton rampButton = new TextButton("R: ramp", StaticSkin.getSkin());
//        rampButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                controller.setActiveDesignation(DesignationsTypes.RAMP);
//                toolbar.closeMenus();
//            }
//        });
//        this.add(rampButton).row();
//        hotkeyMap.put('r', rampButton);
//
//        TextButton channelButton = new TextButton("C: channel", StaticSkin.getSkin());
//        channelButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                controller.setActiveDesignation(DesignationsTypes.CHANNEL);
//                toolbar.closeMenus();
//            }
//        });
//        this.add(channelButton).row();
//        hotkeyMap.put('c', channelButton);
//
//        TextButton stairsButton = new TextButton("S: stairs", StaticSkin.getSkin());
//        stairsButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                controller.setActiveDesignation(DesignationsTypes.STAIRS);
//                toolbar.closeMenus();
//            }
//        });
//        this.add(stairsButton).row();
//        hotkeyMap.put('s', stairsButton);
//
//        TextButton cancelButton = new TextButton("Z: cancel", StaticSkin.getSkin());
//        cancelButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                controller.setActiveDesignation(DesignationsTypes.NONE);
//                toolbar.closeMenus();
//            }
//        });
//        this.add(cancelButton).row();
//        hotkeyMap.put('z', cancelButton);
//
//        TextButton escButton = new TextButton("", StaticSkin.getSkin());
//        escButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                controller.setActiveDesignation(null);
//                toolbar.closeMenus();
//            }
//        });
//        hotkeyMap.put((char) 27, escButton);
    }

    public void openDiggingPanel() {

    }

    public void setController(DesignationsController controller) {
        //this.controller = controller;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }
}