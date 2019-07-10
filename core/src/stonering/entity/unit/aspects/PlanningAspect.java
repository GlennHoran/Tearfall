package stonering.entity.unit.aspects;

import stonering.entity.job.Task;
import stonering.entity.job.action.Action;
import stonering.entity.Aspect;
import stonering.entity.Entity;
import stonering.entity.PositionAspect;
import stonering.entity.job.action.PlantingAction;
import stonering.game.GameMvc;
import stonering.game.model.lists.tasks.TaskContainer;
import stonering.util.geometry.Position;
import stonering.entity.unit.Unit;
import stonering.util.global.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * Holds current creature's task and it's steps. resolves behavior, if some step fails.
 * Selects new tasks.
 * Updates target for movement on task switching.
 *
 * @author Alexander Kuzyakov on 10.10.2017.
 */
public class PlanningAspect extends Aspect {
    public final static String NAME = "planning";
    private Task task;
    private Action action;
    private Position target;

    public PlanningAspect(Entity entity) {
        super(entity);
    }

    public void turn() {
        if (hasNoActiveTask() && !trySelectTask()) return; // no active task, and no new found
        if (!action.getActionTarget().check(getEntityPosition())) return; // keep moving to target
        if (!action.perform()) return; // keep performing action
        updateState(task); // update state after finishing action
    }

    /**
     * Checks if unit has no task or current is finished.
     * Finished tasks remove themselves from container, so only link nullifying is needed.
     */
    private boolean hasNoActiveTask() {
        if (task != null && task.isFinished()) updateState(null);
        return task == null;
    }

    /**
     * Finds appropriate task for this performer.
     * Checks priorities of all available tasks.
     * After this method task is updated.
     * TODO combat tasks
     * TODO non possible tasks with high priority can block other tasks
     */
    private boolean trySelectTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(takeTaskFromNeedsAspect());
        tasks.add(getTaskFromContainer());
        Task task = tasks.stream().filter(Objects::nonNull).max(Comparator.comparingInt(Task::getPriority)).orElse(null);
        return updateState(task); // claim task, if any
    }

    /**
     * Changes state of this aspect to given task. Passing null means no task is performed.
     * With finished task, state of this aspect is reset.
     */
    private boolean updateState(Task task) {
        if (task != null) {
            if (task.getInitialAction() instanceof PlantingAction) {
                System.out.println();
            }
            Logger.TASKS.logDebug("Checking of task " + task.toString() + " for " + entity.toString());
            task.setPerformer((Unit) entity); // performer is required for checking
            if (checkActionSequence(task)) { // valid task
                this.task = task;
                action = task.getNextAction();
                target = action.getActionTarget().getPosition();
                return true;
            }
        }
        // clear state or invalid task
        this.task = null;
        if (task != null) task.reset();
        action = null;
        target = null;
        return false;
    }

    /**
     * Checks if task can be performed.
     * In this method requirement aspects of actions create additional actions.
     *
     * @return false, if some action in sequence cannot be performed.
     */
    private boolean checkActionSequence(Task task) {
        if (task.isFinished()) return false;
        int result;
        while ((result = task.getNextAction().check()) == Action.NEW) { // can create sub actions
        }
        return result == Action.OK;
    }

    /**
     * For cancelling task, caused by external factor (path blocking, enemy, player).
     */
    public void interrupt() {
        if (task == null) return;
        Logger.TASKS.logDebug("Resetting planning aspect of " + toString());
        Task task = this.task;
        updateState(null);
        task.reset();
    }

    /**
     * Calls NeedAspect to create task for satisfying strongest need.
     * Can return null.
     */
    private Task takeTaskFromNeedsAspect() {
        if (!entity.hasAspect(NeedsAspect.class)) return null;
        NeedsAspect aspect = entity.getAspect(NeedsAspect.class);
        if (aspect.getStrongestNeed() != null) return aspect.getStrongestNeed().tryCreateTask(entity);
        return null;
    }

    private Task getTaskFromContainer() {
        return GameMvc.instance().getModel().get(TaskContainer.class).getActiveTask(getEntityPosition());
    }

    public Position getTarget() {
        return target;
    }

    private Position getEntityPosition() {
        return entity.getAspect(PositionAspect.class).position;
    }

    public boolean isTargetExact() {
        return action.getActionTarget().isExactTarget();
    }
}