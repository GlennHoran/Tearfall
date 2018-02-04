package stonering.objects.local_actors;

import stonering.global.utils.Position;

import java.util.HashMap;

/**
 * Created by Alexander on 25.01.2018.
 */
public abstract class AspectHolder {
    protected HashMap<String, Aspect> aspects;
    protected Position position;

    protected AspectHolder(Position position) {
        this.aspects = new HashMap<>();
        this.position = position;
    }

    public HashMap<String, Aspect> getAspects() {
        return aspects;
    }

    public void addAspect(Aspect aspect) {
        aspects.put(aspect.getName(), aspect);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}