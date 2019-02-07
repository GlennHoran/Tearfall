package stonering.game.core.view.render.stages.base;

import stonering.game.core.model.EntitySelector;
import stonering.game.core.model.GameModel;

/**
 * @author Alexander on 06.02.2019.
 */
public class SelectionFrameRenderer extends Renderer {

    public SelectionFrameRenderer(GameModel gameModel, DrawingUtil drawingUtil) {
        super(gameModel, drawingUtil);
    }

    @Override
    public void render() {
        //TODO add landscape dependant rendering
        EntitySelector selector = gameModel.get(EntitySelector.class);
        if (selector.getFrameStart() != null) {
            int minX = Math.min(selector.getFrameStart().getX(), selector.getPosition().getX());
            int maxX = Math.max(selector.getFrameStart().getX(), selector.getPosition().getX());
            int minY = Math.min(selector.getFrameStart().getY(), selector.getPosition().getY());
            int maxY = Math.max(selector.getFrameStart().getY(), selector.getPosition().getY());
            int minZ = Math.min(selector.getFrameStart().getZ(), selector.getPosition().getZ());
            int maxZ = selector.getPosition().getZ();
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {

                        if (y == maxY && z == maxZ)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 0, 1), x, y, z, selector.getPosition());
                        if (y == minY && z == maxZ)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 1, 1), x, y, z, selector.getPosition());
                        if (x == minX && z == maxZ)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 2, 1), x, y, z, selector.getPosition());
                        if (x == maxX && z == maxZ)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 3, 1), x, y, z, selector.getPosition());
                        if (y == minY && z == minZ)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 4, 1), x, y, z, selector.getPosition());
                        if (y == minY && x == minX)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 5, 1), x, y, z, selector.getPosition());
                        if (y == minY && x == maxX)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 6, 1), x, y, z, selector.getPosition());
                        if (y == maxY && z == minZ)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 7, 1), x, y, z, selector.getPosition());
                        if (x == minX && z > minZ && y == minY)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 8, 1), x, y, z, selector.getPosition());
                        if (x == maxX && z > minZ && y == minY)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 9, 1), x, y, z, selector.getPosition());
                        drawingUtil.updateColorA(0.5f);
                        if (z == maxZ) drawingUtil.drawSprite(drawingUtil.selectSprite(4, 10, 1), x, y, z, selector.getPosition());
                        if (y == minY) drawingUtil.drawSprite(drawingUtil.selectSprite(4, 11, 1), x, y, z, selector.getPosition());
                        if (z > minZ && y == minY)
                            drawingUtil.drawSprite(drawingUtil.selectSprite(4, 12, 1), x, y, z, selector.getPosition());
                        drawingUtil.updateColorA(1f);
                    }
                }
            }
        }
    }
}