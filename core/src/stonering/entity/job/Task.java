package stonering.entity.job;

import stonering.entity.job.designation.Designation;
import stonering.entity.local.unit.aspects.PlanningAspect;
import stonering.game.GameMvc;
import stonering.game.model.lists.tasks.TaskContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.game.model.util.UtilByteArray;
import stonering.util.geometry.Position;
import stonering.entity.job.action.Action;
import stonering.entity.job.action.TaskTypesEnum;
import stonering.entity.local.unit.Unit;
import stonering.util.global.Logger;

import java.util.LinkedList;

/**
 * Task object for units behavior in the game.
 * Consists of main action, sequence of action to be performed before main, and after main.
 * <p>
 * Firstly pre action with lowest indexes are executed, then initial action, and then post action with lowest indexes.
 *
 * @author Alexander Kuzyakov
 */
public class Task {
    private GameMvc gameMvc;
    private TaskContainer taskContainer;
    private String name;
    private Unit performer;
    private TaskTypesEnum taskType;
    private Action initialAction;
    private LinkedList<Action> preActions;
    private LinkedList<Action> postActions;
    private Designation designation;
    private int priority;

    public Task(String name, TaskTypesEnum taskType, Action initialAction, int priority) {
        gameMvc = GameMvc.instance();
        this.name = name;
        this.taskType = taskType;
        this.initialAction = initialAction;
        initialAction.setTask(this);
        this.taskContainer = gameMvc.getModel().get(TaskContainer.class);
        preActions = new LinkedList<>();
        postActions = new LinkedList<>();
        this.priority = priority;
    }

    /**
     * Removes this task from container  if it's finished.
     */
    public void tryFinishTask() {
        if (isFinished()) taskContainer.removeTask(this);
    }

    /**
     * Returns next action to be performed.
     *
     * @return
     */
    public Action getNextAction() {
        if (!preActions.isEmpty()) {
            return preActions.get(0);
        } else {
            if (initialAction.isFinished()) {
                if (!postActions.isEmpty()) {
                    return postActions.get(0);
                }
            } else {
                return initialAction;
            }
        }
        return null;
    }

    public void reset() {
        preActions = new LinkedList<>();
        postActions = new LinkedList<>();
        if (performer == null) return;
        PlanningAspect planningAspect = (performer.getAspect(PlanningAspect.class));
        performer = null;
        planningAspect.reset();
    }

    /**
     * Task is finished, if initial action is finished, and no other action remain.
     */
    public boolean isFinished() {
//        Logger.TASKS.logDebug("Checking task " + name +
//                " completion[preActions: " + preActions.size() +
//                ",postActions: " + postActions.size() +
//                ", initial finished:" + initialAction.isDefined() + "]");
        if (!preActions.isEmpty() && initialAction.isFinished()) {
            Logger.TASKS.logError("Task " + name + ": initial action finished before pre action.");
        }
        return preActions.isEmpty() && initialAction.isFinished() && postActions.isEmpty();
    }

    /**
     * Removes pre and post action from task
     */
    public void finishAction(Action action) {
        if (action != initialAction) {
            preActions.remove(action);
            postActions.remove(action);
        }
    }

    public void fail() {
        //TODO add interruption
        reset();
        taskContainer.removeTask(this);
    }

    public boolean isTaskTargetsAvailableFrom(Position position) {
        UtilByteArray area = gameMvc.getModel().get(LocalMap.class).getPassage().getArea();
        int sourceArea = area.getValue(position);
        Position target = initialAction.getActionTarget().getPosition();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if ((x != 0 || y != 0) && area.getValue(target.x + x, target.y + y, target.z) == sourceArea) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This action will be executed in the first place
     *
     * @param action
     */
    public void addFirstPreAction(Action action) {
        preActions.add(0, action);
        Logger.TASKS.logDebug("Action " + action + " added to task " + name);
        action.setTask(this);
    }

    /**
     * This action will be executed just before main action.
     *
     * @param action
     */
    public void addLastPreAction(Action action) {
        preActions.add(action);
        action.setTask(this);
    }

    /**
     * This action will be executed right after main action.
     *
     * @param action
     */
    public void addFirstPostAction(Action action) {
        preActions.add(0, action);
        action.setTask(this);
    }

    /**
     * This action will be executed in the last place.
     *
     * @param action
     */
    public void addLastPostAction(Action action) {
        preActions.add(action);
        action.setTask(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskTypesEnum getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypesEnum taskType) {
        this.taskType = taskType;
    }

    public Unit getPerformer() {
        return performer;
    }

    public Action getInitialAction() {
        return initialAction;
    }

    public void setInitialAction(Action initialAction) {
        this.initialAction = initialAction;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setPerformer(Unit performer) {
        this.performer = performer;
    }
}