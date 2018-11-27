package stonering.game.core.view.render.ui.lists;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import stonering.game.core.view.render.ui.menus.util.HideableComponent;
import stonering.game.core.view.render.ui.menus.util.Invokable;
import stonering.utils.global.StaticSkin;

/**
 * @author Alexander on 27.11.2018.
 */
public class NavigableSelectBox<T> extends SelectBox<T> implements Invokable, HideableComponent {
    private EventListener hideListener;
    private EventListener selectListener;
    private EventListener showListener;

    public NavigableSelectBox() {
        super(StaticSkin.getSkin());
    }

    @Override
    public boolean invoke(int keycode) {
        switch (keycode) {
            case Input.Keys.R:
                up();
                return true;
            case Input.Keys.F:
                down();
                return true;
            case Input.Keys.E:
                select();
                return true;
            case Input.Keys.Q:
                hide();
                return true;
        }
        return false;
    }

    public void up() {
        if (getSelectedIndex() > 0) {
            setSelectedIndex(getSelectedIndex() - 1);
        }
    }

    public void down() {
        if (getSelectedIndex() < getItems().size - 1) {
            setSelectedIndex(getSelectedIndex() + 1);
        }
    }

    public void select() {
        if(selectListener != null) {
            selectListener.handle(null);
        }
    }

    @Override
    public void show() {
        showListener.handle(null);
    }

    @Override
    public void hide() {
        hideListener.handle(null);
    }

    public void setHideListener(EventListener hideListener) {
        this.hideListener = hideListener;
    }

    public void setSelectListener(EventListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setShowListener(EventListener showListener) {
        this.showListener = showListener;
    }
}
