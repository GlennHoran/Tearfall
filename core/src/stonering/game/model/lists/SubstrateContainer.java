package stonering.game.model.lists;

import com.badlogic.gdx.utils.Array;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.PlantBlock;
import stonering.entity.local.plants.SubstratePlant;
import stonering.game.model.IntervalTurnable;
import stonering.game.model.ModelComponent;
import stonering.util.geometry.Position;
import stonering.util.global.CompatibleArray;
import stonering.util.global.Initable;

import java.util.HashMap;

/**
 * Container for substrates
 * @author Alexander_Kuzyakov on 18.06.2019.
 */
public class SubstrateContainer extends IntervalTurnable implements Initable, ModelComponent {
    private Array<SubstratePlant> substratePlants;
    private HashMap<Position, PlantBlock> substrateBlocks;

    public SubstrateContainer() {
        substratePlants = new CompatibleArray<>();
        substrateBlocks = new HashMap<>();
    }

    @Override
    public void init() {
    }

    @Override
    public void turn() {
        substratePlants.forEach(SubstratePlant::turn);
    }

    public void place(SubstratePlant plant, Position position) {
        if (substrateBlocks.containsKey(plant.getPosition())) return;
        plant.setPosition(position);
        substratePlants.add(plant);
        substrateBlocks.put(plant.getPosition(), plant.getBlock());
    }

    /**
     * Removes plant from map completely.
     */
    public void remove(AbstractPlant plant) {
        if (plant == null) return;
        if (substrateBlocks.containsKey(plant.getPosition())) return;
        substratePlants.removeValue((SubstratePlant) plant, true);
        substrateBlocks.remove(plant.getPosition());
    }

    public PlantBlock getSubstrateBlock(Position position) {
        return substrateBlocks.get(position);
    }

    public SubstratePlant getSubstrateInPosition(Position position) {
        return substrateBlocks.containsKey(position) ? (SubstratePlant) substrateBlocks.get(position).getPlant() : null;
    }

    public boolean isSubstrateBlockExists(Position position) {
        return substrateBlocks.containsKey(position);
    }
}