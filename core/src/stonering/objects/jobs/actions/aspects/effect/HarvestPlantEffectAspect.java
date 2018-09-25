package stonering.objects.jobs.actions.aspects.effect;

import stonering.exceptions.DescriptionNotFoundException;
import stonering.generators.items.ItemGenerator;
import stonering.generators.items.PlantProductGenerator;
import stonering.global.utils.Position;
import stonering.objects.jobs.actions.Action;
import stonering.objects.jobs.actions.aspects.target.PlantTargetAspect;
import stonering.objects.local_actors.items.Item;
import stonering.objects.local_actors.plants.AbstractPlant;
import stonering.objects.local_actors.plants.PlantBlock;

import java.util.ArrayList;

/**
 * @author Alexander on 25.09.2018.
 */
public class HarvestPlantEffectAspect extends EffectAspect {

    public HarvestPlantEffectAspect(Action action, int workAmount) {
        super(action, workAmount);
    }

    @Override
    protected void applyEffect() {
        System.out.println("harvesting plant");
        if (action.getTargetAspect() instanceof PlantTargetAspect) {
            AbstractPlant abstractPlant = ((PlantTargetAspect) action.getTargetAspect()).getPlant();
            Position position = action.getTargetAspect().getTargetPosition();
            PlantBlock block = action.getGameContainer().getLocalMap().getPlantBlock(position);
            if (block != null && block.getPlant() == abstractPlant) {
                PlantProductGenerator generator = new PlantProductGenerator();
                ArrayList<Item> items = generator.generateHarvestProduct(block);
                items.forEach(item -> action.getGameContainer().getItemContainer().putItem(item, position));
            }
        }
    }
}
