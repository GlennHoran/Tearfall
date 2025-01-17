package stonering.entity.job.action;

import stonering.entity.job.action.target.ActionTarget;
import stonering.entity.item.Item;
import stonering.entity.item.selectors.ItemSelector;
import stonering.entity.item.selectors.ToolWithActionItemSelector;
import stonering.entity.unit.aspects.equipment.EquipmentAspect;
import stonering.enums.ZoneTypesEnum;
import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.GameMvc;
import stonering.game.model.system.items.ItemContainer;
import stonering.game.model.system.ZonesContainer;
import stonering.game.model.local_map.LocalMap;
import stonering.util.geometry.Position;
import stonering.util.global.Logger;

/**
 * @author Alexander on 20.03.2019.
 */
public class HoeingAction extends Action {

    public HoeingAction(ActionTarget actionTarget) {
        super(actionTarget);
    }

    /**
     * Checks that:
     * 1. target tile is soil FLOOR
     * 2. target tile is farm zone
     * 3. performer has hoe tool
     * Creates sub name only for equipping hoe, other cases handled by player.
     */
    @Override
    public int check() {
//        Logger.TASKS.logDebug("Checking hoeing of " + actionTarget.getPosition());
        Position target = actionTarget.getPosition();
        LocalMap localMap = GameMvc.instance().getModel().get(LocalMap.class);
        if (!ZoneTypesEnum.FARM.getValidator().validate(localMap, target)) return FAIL; // 1
        if (GameMvc.instance().getModel().get(ZonesContainer.class).getZone(target) == null) return FAIL; // 2
        EquipmentAspect equipmentAspect = task.getPerformer().getAspect(EquipmentAspect.class);
        if(equipmentAspect.toolWithActionEquipped("hoe")) return OK;
        return tryCreateEquippingAction();
    }

    @Override
    protected void performLogic() {
        Logger.TASKS.logDebug("Hoeing tile " + actionTarget.getPosition());
        LocalMap localMap = GameMvc.instance().getModel().get(LocalMap.class);
        localMap.setBlockType(actionTarget.getPosition(), BlockTypesEnum.FARM.CODE);
    }

    private int tryCreateEquippingAction() {
        Logger.TASKS.logDebug("Creating equipping action of hoe");
        ItemSelector toolItemSelector = new ToolWithActionItemSelector("hoe");
        ItemContainer itemContainer = GameMvc.instance().getModel().get(ItemContainer.class);
        Item item = itemContainer.util.getItemAvailableBySelector(toolItemSelector, actionTarget.getPosition());
        if(item == null) return FAIL;
        task.addFirstPreAction(new EquipItemAction(item, true));
        return OK;
    }
}
