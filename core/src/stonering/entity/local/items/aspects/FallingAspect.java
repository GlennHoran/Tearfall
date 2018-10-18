package stonering.entity.local.items.aspects;

import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.core.model.GameContainer;
import stonering.game.core.model.LocalMap;
import stonering.global.utils.Position;
import stonering.entity.local.Aspect;
import stonering.entity.local.AspectHolder;
import stonering.entity.local.items.Item;

/**
 * Aspect for items to fall down.
 *
 * @author Savva Kodeikin
 */
public class FallingAspect extends Aspect {

    private static final String FALLING_ASPECT_NAME = "falling";

    private LocalMap localMap;

    public FallingAspect(AspectHolder aspectHolder) {
        super(FALLING_ASPECT_NAME, aspectHolder);
    }

    @Override
    public void init(GameContainer gameContainer) {
        super.init(gameContainer);
        localMap = gameContainer.getLocalMap();
    }

    @Override
    public void turn() {
        Position position = aspectHolder.getPosition();
        if(position != null) { //TODO add aspect turn on pickup
            Position lowerPosition = aspectHolder.getPosition().getPositionByOffset(0, 0, -1);
            boolean isCurrentBlockSpace = localMap.getBlockType(position) == BlockTypesEnum.SPACE.getCode();
            boolean isLowerBlockWall = localMap.getBlockType(lowerPosition) == BlockTypesEnum.WALL.getCode();

            if (localMap.inMap(lowerPosition) && isCurrentBlockSpace && !isLowerBlockWall) {
                gameContainer.getItemContainer().moveItem((Item) aspectHolder, lowerPosition);
            }
        }
    }
}