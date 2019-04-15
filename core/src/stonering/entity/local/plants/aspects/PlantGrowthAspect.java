package stonering.entity.local.plants.aspects;

import stonering.entity.local.Aspect;
import stonering.entity.local.AspectHolder;
import stonering.entity.local.environment.GameCalendar;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.Plant;
import stonering.entity.local.plants.Tree;
import stonering.enums.OrientationEnum;
import stonering.game.GameMvc;
import stonering.game.model.lists.PlantContainer;
import stonering.generators.plants.PlantGenerator;
import stonering.generators.plants.TreeGenerator;

/**
 * Switches plant life stages. Restructures tree if needed to represent growth.
 *
 * @author Alexander on 13.02.2019.
 */
public class PlantGrowthAspect extends Aspect {
    public static final String NAME = "tree_growth";
    private static int MONTH_SIZE = GameCalendar.MONTH_SIZE * GameCalendar.DAY_SIZE * GameCalendar.HOUR_SIZE;

    private int counter = 0;

    public PlantGrowthAspect(AspectHolder aspectHolder) {
        super(aspectHolder);
    }

    /**
     * Increases plant age if month has ended.
     */
    @Override
    public void turn() {
        if (counter++ != MONTH_SIZE) return;
        counter = 0;
        switch (((AbstractPlant) aspectHolder).increaceAge()) {
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
        if (aspectHolder instanceof Tree) {
            Tree tree = (Tree) aspectHolder;
            plantContainer.removePlantBlocks(tree);
            TreeGenerator treeGenerator = new TreeGenerator();
            treeGenerator.applyTreeGrowth(tree);
            plantContainer.place(tree);
        } else if (aspectHolder instanceof Plant) {
            Plant plant = (Plant) aspectHolder;
            plantContainer.removePlantBlocks(plant);
            PlantGenerator plantGenerator = new PlantGenerator();
            plantGenerator.applyPlantGrowth(plant);
            plantContainer.place(plant);
        }
    }

    /**
     * Kill this plant and leave products(if any).
     */
    private void die() {
        if (aspectHolder instanceof Tree) {
            GameMvc.instance().getModel().get(PlantContainer.class).fellTree((Tree) aspectHolder, OrientationEnum.N, false);
        } else if (aspectHolder instanceof Plant) {
            GameMvc.instance().getModel().get(PlantContainer.class).removePlant((Plant) aspectHolder, true);
        }
    }
}
