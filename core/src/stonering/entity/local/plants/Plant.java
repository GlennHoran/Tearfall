package stonering.entity.local.plants;

import stonering.enums.plants.PlantType;
import stonering.util.geometry.Position;

/**
 * Represents single-tile plant.
 *
 * @author Alexander Kuzyakov on 19.10.2017.
 */
public class Plant extends AbstractPlant {
    private PlantBlock block;

    public Plant(Position position, int age) {
        super(position);
        this.age = age;
    }

    /**
     * Small plants should be harvested if they currently have harvestProducts (like berries),
     * or cutProducts (like mushrooms)
     * @return
     */
    @Override
    public boolean isHarvestable() {
        PlantType.PlantLifeStage stage =getCurrentStage();
        return stage.harvestProducts != null || stage.cutProducts != null;
    }

    public PlantBlock getBlock() {
        return block;
    }

    public void setBlock(PlantBlock block) {
        this.block = block;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        super.setPosition(position);
        block.setPosition(position);
    }
}
