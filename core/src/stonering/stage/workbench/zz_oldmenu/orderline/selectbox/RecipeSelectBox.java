package stonering.stage.workbench.zz_oldmenu.orderline.selectbox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import stonering.enums.ControlActionsEnum;
import stonering.enums.items.recipe.Recipe;
import stonering.widget.lists.PlaceHolderSelectBox;

import java.util.List;

/**
 * Select box for selecting recipes in workbenches.
 *
 * @author Alexander on 26.06.2019.
 */
public class RecipeSelectBox extends PlaceHolderSelectBox<Recipe> {
    private EventListener selectListener;
    private EventListener cancelListener;

    public RecipeSelectBox(List<Recipe> recipes) {
        super(new Recipe("Select item"));
        getSelection().setProgrammaticChangeEvents(false);
        setItems(recipes);
        createListeners();
    }

    private void createListeners() {
        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectListener.handle(event);
            }
        });
        getListeners().insert(0, new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (ControlActionsEnum.getAction(keycode)) {
                    case RIGHT:
                    case SELECT: { // opens list or saves selected value
                        return selectListener.handle(event);
                    }
                    case LEFT:
                    case CANCEL: {
                        return cancelListener.handle(event);
                    }
                }
                return super.keyDown(event, keycode);
            }

//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                super.touchDown(event, x, y, pointer, button);
//                navigate(1);
//                return true;
//            }
        });
    }

    public void setSelectListener(EventListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setCancelListener(EventListener cancelListener) {
        this.cancelListener = cancelListener;
    }
}
