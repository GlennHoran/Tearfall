package stonering.entity.local;

import stonering.game.GameMvc;
import stonering.game.model.Turnable;
import stonering.util.global.Initable;

import java.io.Serializable;

/**
 * @author Alexander Kuzyakov on 10.10.2017.
 */
public abstract class Aspect extends Turnable implements Initable, Serializable {
    protected GameMvc gameMvc;
    protected AspectHolder aspectHolder;

    public Aspect(AspectHolder aspectHolder) {
        this.aspectHolder = aspectHolder;
    }

    public AspectHolder getAspectHolder() {
        return aspectHolder;
    }

    public void setAspectHolder(AspectHolder aspectHolder) {
        this.aspectHolder = aspectHolder;
    }

    @Override
    public void init() {
        gameMvc = GameMvc.instance();
    }

    @Override
    public void turn() {}
}
