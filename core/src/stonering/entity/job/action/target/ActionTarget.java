package stonering.entity.job.action.target;

import stonering.entity.job.action.MoveAction;
import stonering.game.GameMvc;
import stonering.game.model.local_map.LocalMap;
import stonering.util.geometry.Position;
import stonering.entity.job.action.Action;
import stonering.util.global.Logger;

import java.util.List;
import java.util.Random;

public abstract class ActionTarget {
    public static final int READY = 1; // target position reached
    public static final int WAIT = 0; // target position no reached
    public static final int NEW = 2; // new action created. planning aspect should update task
    public static final int FAIL = -1; // failed to create action

    public static final int EXACT = 0;
    public static final int NEAR = 1;
    public static final int ANY = 2;
    private int targetPlacement;

    protected GameMvc gameMvc;
    protected Action action;
    private Random random;

    public ActionTarget(int targetPlacement) {
        gameMvc = GameMvc.instance();
        this.targetPlacement = targetPlacement;
        random = new Random();
    }

    public abstract Position getPosition();

    public Position findPositionToStepOff(Position from) {
        List<Position> positions = gameMvc.getModel().get(LocalMap.class).getFreeBlockNear(from);
        if (!positions.isEmpty()) {
            return positions.get(random.nextInt(positions.size()));
        }
        Logger.PATH.logWarn("Cant find tile to step out from " + from);
        return null;
    }

    /**
     * Creates action to free target position. Can fail.
     */
    public int createActionToStepOff(Position from) {
        Position to = findPositionToStepOff(from);
        if (to == null) return FAIL;
        action.getTask().addFirstPreAction(new MoveAction(to));
        return NEW;
    }

    /**
     * Checks if task performer has reached task target.
     */
    public int check(Position currentPosition) {
        switch(targetPlacement) {
            case(EXACT) :
                return currentPosition.equals(getPosition()) ? READY : WAIT;
            case(NEAR) :
                if(currentPosition.equals(getPosition())) return createActionToStepOff(currentPosition);
                return currentPosition.isNeighbour(getPosition()) ? READY : WAIT;
        }
        return currentPosition.equals(getPosition()) || currentPosition.isNeighbour(getPosition()) ? READY : WAIT;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
