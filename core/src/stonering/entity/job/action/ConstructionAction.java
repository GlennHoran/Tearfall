package stonering.entity.job.action;

import stonering.entity.job.action.target.GenericBuildingAction;
import stonering.entity.job.designation.BuildingDesignation;
import stonering.entity.item.Item;
import stonering.entity.item.selectors.ItemSelector;
import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.GameMvc;
import stonering.game.model.system.PlantContainer;
import stonering.game.model.system.SubstrateContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.util.geometry.Position;
import stonering.util.global.Logger;

import java.util.Collection;
import java.util.List;

/**
 * Action for creating constructions on map. Constructions are just blocks of material.
 *
 * @author Alexander on 12.03.2019.
 */
public class ConstructionAction extends GenericBuildingAction {
    private byte blockType;

    public ConstructionAction(BuildingDesignation designation, Collection<ItemSelector> itemSelectors) {
        super(designation, itemSelectors);
        blockType = BlockTypesEnum.getType(designation.getBuilding()).CODE;
    }

    @Override
    public void performLogic() {
        Logger.TASKS.logDebug("Construction of " + BlockTypesEnum.getType(blockType).NAME
                + " started at " + actionTarget.getPosition()
                + " by " + task.getPerformer().toString());
        Position target = actionTarget.getPosition();
        List<Item> items = selectItemsToConsume();
        int material = items.get(0).getMaterial();
        GameMvc.instance().getModel().get(LocalMap.class).setBlock(target, blockType, material); // create block
        PlantContainer container = GameMvc.instance().getModel().get(PlantContainer.class);
        container.remove(container.getPlantInPosition(target), true); // remove plant
        SubstrateContainer substrateContainer = GameMvc.instance().getModel().get(SubstrateContainer.class);
        substrateContainer.remove(substrateContainer.getSubstrateInPosition(target)); // remove substrate
        consumeItems(items);
    }

    @Override
    public String toString() {
        return "Construction name: " + BlockTypesEnum.getType(blockType).NAME;
    }
}
