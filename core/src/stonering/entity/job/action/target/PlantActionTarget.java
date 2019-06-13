package stonering.entity.job.action.target;

import stonering.util.geometry.Position;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.Plant;

public class PlantActionTarget extends ActionTarget {
    protected AbstractPlant plant;

    public PlantActionTarget(AbstractPlant plant) {
        super(true, true);
        this.plant = plant;
    }

    @Override
    public Position getPosition() {
        return plant.getPosition();
    }

    public Plant getPlant() {
        return (Plant) plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}