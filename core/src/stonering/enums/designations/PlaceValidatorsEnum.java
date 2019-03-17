package stonering.enums.designations;

import stonering.entity.local.building.validators.FreeFloorValidator;
import stonering.entity.local.building.validators.NearSolidBlockValidator;
import stonering.entity.local.building.validators.PositionValidator;

import java.util.HashMap;

/**
 * Contains mapping from String placing fiels in blueprints.json to actual validators classes
 */
public enum PlaceValidatorsEnum {
    FLOOR(FreeFloorValidator.NAME, new FreeFloorValidator()),
    CONSTRUCTION("construction", new NearSolidBlockValidator());

    private static HashMap<String, PositionValidator> map;

    public final String NAME;
    public final PositionValidator VALIDATOR;

    static {
        map = new HashMap<>();
        for (PlaceValidatorsEnum placeValidatorsEnum : PlaceValidatorsEnum.values()) {
            map.put(placeValidatorsEnum.NAME, placeValidatorsEnum.VALIDATOR);
        }
    }

    PlaceValidatorsEnum(String NAME, PositionValidator VALIDATOR) {
        this.NAME = NAME;
        this.VALIDATOR = VALIDATOR;
    }

    public static PositionValidator getValidator(String name) {
        return map.get(name);
    }
}