package stonering.game.model.lists;

import stonering.designations.BuildingDesignation;
import stonering.designations.Designation;
import stonering.designations.OrderDesignation;
import stonering.entity.jobs.actions.*;
import stonering.entity.local.building.BuildingOrder;
import stonering.enums.ZoneTypesEnum;
import stonering.enums.blocks.BlockTypesEnum;
import stonering.enums.buildings.BuildingTypeMap;
import stonering.enums.designations.DesignationTypeEnum;
import stonering.enums.designations.PlaceValidatorsEnum;
import stonering.game.GameMvc;
import stonering.game.model.ModelComponent;
import stonering.game.model.local_map.LocalMap;
import stonering.util.geometry.Position;
import stonering.entity.jobs.Task;
import stonering.entity.local.items.selectors.ItemSelector;
import stonering.entity.local.plants.PlantBlock;
import stonering.util.global.Initable;
import stonering.util.global.TagLoggersEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Contains all tasks for settlers on map and Designations for rendering.
 * {@link Task} are orders for units.
 * {@link Designation} are used for drawing given orders as tiles.
 *
 * @author Alexander Kuzyakov
 */
public class TaskContainer implements ModelComponent, Initable {
    private LocalMap localMap;
    private ArrayList<Task> tasks;
    private HashMap<Position, Designation> designations;
    private Position cachePosition; // state is not maintained. should be set before use

    public TaskContainer() {
        cachePosition = new Position(0, 0, 0);
        tasks = new ArrayList<>();
        designations = new HashMap<>();
    }

    @Override
    public void init() {
        localMap = GameMvc.instance().getModel().get(LocalMap.class);
    }

    public Task getActiveTask(Position pos) {
        for (Task task : tasks) {
            if (task.getPerformer() == null && task.isTaskTargetsAvaialbleFrom(pos)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Adds designation and creates comprehensive task.
     * All simple orders like digging and foraging submitted through this method.
     *
     * @param position
     * @param type
     */
    public void submitOrderDesignation(Position position, DesignationTypeEnum type, int priority) {
        if (!validateDesignations(position, type)) return; // no designation for invalid position
        OrderDesignation designation = new OrderDesignation(position, type);
        Task task = createOrderTask(designation, priority);
        if (task == null) return; // no designation with no task
        task.setDesignation(designation);
        designation.setTask(task);
        tasks.add(task);
        addDesignation(designation);
        TagLoggersEnum.TASKS.log(task.getName() + " designated");
    }

    /**
     * Called from {@link stonering.game.controller.controllers.designation.BuildingDesignationSequence}.
     * Adds designation and creates comprehensive task.
     * All single-tile buildings are constructed through this method.
     */
    public void submitBuildingDesignation(BuildingOrder order, int priority) {
        Position position = order.getPosition();
        if (!PlaceValidatorsEnum.getValidator(order.getBlueprint().getPlacing()).validate(localMap, position)) return;
        BuildingDesignation designation = new BuildingDesignation(position, DesignationTypeEnum.BUILD, order.getBlueprint().getBuilding());
        Task task = createBuildingTask(designation, order.getItemSelectors().values(), priority);
        designation.setTask(task);
        tasks.add(task);
        addDesignation(designation);
        TagLoggersEnum.TASKS.log(task.getName() + " designated");
    }

    private Task createOrderTask(OrderDesignation designation, int priority) {
        switch (designation.getType()) {
            case NONE:
                break;
            case DIG:
            case RAMP:
            case STAIRS:
            case CHANNEL: {
                DigAction digAction = new DigAction(designation);
                Task task = new Task("designation", TaskTypesEnum.DESIGNATION, digAction, priority);
                return task;
            }
            case CUT:
                //TODO add specific action for cutting all plants.
            case CHOP: {
                ChopTreeAction chopTreeAction = new ChopTreeAction(designation);
                Task task = new Task("designation", TaskTypesEnum.DESIGNATION, chopTreeAction, priority);
                return task;
            }
            case HARVEST: {
                PlantBlock block = localMap.getPlantBlock(designation.getPosition());
                if (block != null && block.getPlant().isHarvestable()) {
                    PlantHarvestAction plantHarvestAction = new PlantHarvestAction(block.getPlant());
                    Task task = new Task("designation", TaskTypesEnum.DESIGNATION, plantHarvestAction, priority);
                    return task;
                }
            }
            case HOE: {

            }
        }
        return null;
    }

    /**
     * Creates tasks for building various buildings.
     */
    private Task createBuildingTask(BuildingDesignation designation, Collection<ItemSelector> itemSelectors, int priority) {
        Action action;
        if (BuildingTypeMap.getInstance().getBuilding(designation.getBuilding()).isConstruction()) {
            action = new ConstructionAction(designation, itemSelectors);
        } else {
            action = new BuildingAction(designation, itemSelectors);
        }
        Task task = new Task("designation", TaskTypesEnum.DESIGNATION, action, priority);
        task.setDesignation(designation);
        return task;
    }

    /**
     * Validates applying given designation type to position.
     */
    private boolean validateDesignations(Position position, DesignationTypeEnum type) {
        BlockTypesEnum blockOnMap = BlockTypesEnum.getType(localMap.getBlockType(position));
        switch (type) {
            case DIG: { //makes floor
                return BlockTypesEnum.RAMP.equals(blockOnMap) ||
                        BlockTypesEnum.WALL.equals(blockOnMap) ||
                        BlockTypesEnum.STAIRS.equals(blockOnMap);
            }
            case CHANNEL: { //makes space and ramp lower
                return !BlockTypesEnum.SPACE.equals(blockOnMap);
            }
            case RAMP:
            case STAIRS: {
                return BlockTypesEnum.WALL.equals(blockOnMap);
            }
            case CHOP: {
                //TODO designate tree as whole
                PlantBlock block = localMap.getPlantBlock(position);
                return block != null &&
                        (BlockTypesEnum.SPACE.equals(blockOnMap) || BlockTypesEnum.FLOOR.equals(blockOnMap))
                        && block.getPlant().getType().isTree();
            }
            case NONE: {
                return true;
            }
            case CUT:
                break;
            case HARVEST:
                //TODO add harvesting from trees
                PlantBlock block = localMap.getPlantBlock(position);
                return block != null && !block.getPlant().getType().isTree();
            case BUILD:
                break;
            case HOE:
                return ZoneTypesEnum.FARM.getValidator().validate(localMap, position);
        }
        return false;
    }

    /**
     * Removes task. called if task is finished or canceled.
     * Removes tasks designation if there is one.
     *
     * @param task
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        if (task.getDesignation() != null) {
            removeDesignation(task.getDesignation());
        }
    }

    /**
     * Adds designation to designations map. Updates local map.
     */
    private void addDesignation(Designation designation) {
        designations.put(designation.getPosition(), designation);
    }

    /**
     * Removes designation from designations map. Updates local map.
     */
    private void removeDesignation(Designation designation) {
        designations.remove(designation.getPosition());
    }

    public Designation getDesignation(int x, int y, int z) {
        return designations.get(cachePosition.set(x, y, z));
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public HashMap<Position, Designation> getDesignations() {
        return designations;
    }
}