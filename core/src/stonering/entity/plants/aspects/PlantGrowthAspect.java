package stonering.entity.plants.aspects;

import stonering.entity.Aspect;
import stonering.entity.Entity;
import stonering.game.model.system.GameCalendar;
import stonering.entity.plants.AbstractPlant;
import stonering.entity.plants.Plant;
import stonering.entity.plants.Tree;
import stonering.enums.plants.PlantType;
import stonering.enums.time.TimeUnitEnum;
import stonering.game.GameMvc;
import stonering.game.model.system.PlantContainer;
import stonering.generators.plants.PlantGenerator;
import stonering.generators.plants.TreeGenerator;
import stonering.util.geometry.Position;

/**
 * Switches plant life stages. Restructures tree if needed to represent growth.
 * Stage length is taken from {@link PlantType} and measured in weeks.
 *
 * @author Alexander on 13.02.2019.
 */
public class PlantGrowthAspect extends Aspect {
    private int weekSize; // week size in minutes.
    private int counter = 0;

    public PlantGrowthAspect(Entity entity) {
        super(entity);
        GameCalendar calendar = GameMvc.instance().getModel().getCalendar();
        weekSize = 7 * calendar.day.getSize() * calendar.hour.getSize();
    }

    /**
     * Increases plant age if month has ended.
     */
    @Override
    public void turnUnit(TimeUnitEnum unit) {
        if (unit == TimeUnitEnum.MINUTE) update();
    }

    /**
     * Increases growth counter(every minute) and plant age(counted in weeks).
     * When age increases, plant body is recreated with new life stage.
     */
    private void update() {
        if (counter++ < weekSize) return;
        counter = 0;
        switch (((AbstractPlant) entity).increaceAge()) {
            case 1:
                applyNewStage();
                return;
            case -1:
                die();
        }
    }

    /**
     * Changes plant loot and tree structure.
     */
    private void applyNewStage() {
        PlantContainer plantContainer = GameMvc.instance().getModel().get(PlantContainer.class);
        if (entity instanceof Tree) {
            Tree tree = (Tree) entity;
            plantContainer.removePlantBlocks(tree, false);
            TreeGenerator treeGenerator = new TreeGenerator();
            treeGenerator.applyTreeGrowth(tree);
            plantContainer.place(tree, tree.position);
        } else if (entity instanceof Plant) {
            Plant plant = (Plant) entity;
            Position oldPosition = plant.getPosition();
            plantContainer.removePlantBlocks(plant, false);
            PlantGenerator plantGenerator = new PlantGenerator();
            plantGenerator.applyPlantGrowth(plant);
            plantContainer.place(plant, oldPosition);
        }
    }

    /**
     * Kill this plant and leave products(if any).
     */
    private void die() {
        //TODO
        ((AbstractPlant) entity).setDead(true);
        GameMvc.instance().getModel().get(PlantContainer.class).remove((AbstractPlant) entity, true);
    }
}
