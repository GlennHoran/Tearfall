package stonering.game.core.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import stonering.game.core.model.GameContainer;
import stonering.game.core.model.LocalMap;
import stonering.game.core.view.tilemaps.LocalTileMap;
import stonering.global.utils.Position;
import stonering.objects.local_actors.building.BuildingBlock;
import stonering.objects.local_actors.unit.UnitBlock;

/**
 * Created by Alexander on 13.06.2017.
 *
 * Draws LocalMap. Blocks and plants are taken from LocalTileMap,
 * Buildings, units, and items are taken from LocalMap
 */
public class LocalWorldDrawer {
    private GameContainer container;
    private LocalTileMap localTileMap;
    private SpriteBatch batch;
    private Texture[] atlases;
    private Position camera;
    private LocalMap localMap;
    private int viewAreaWidth;
    private int viewAreDepth;
    private float shadingStep = 0.06f;
    private int tileWidth = 64;
    private int tileHeight = 96;
    private int tileDepth = 32;
    private int screenCenterX;
    private int screenCenterY;

    private int maxX;
    private int maxY;
    private int maxZ;
    private int minX;
    private int minY;
    private int minZ;

    public LocalWorldDrawer(LocalMap localMap) {
        this.localMap = localMap;
        initAtlases();
    }

    public void drawWorld() {
        if (localTileMap == null)
            localTileMap = container.getLocalTileMap();
        this.camera = container.getCamera().getPosition();
        defineframe();
        batch.begin();
        for (int z = minZ; z <= maxZ; z++) {
            float shading = (camera.getZ() - z) * shadingStep;
            batch.setColor(1 - shading, 1 - shading, 1 - shading, 1);
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    drawTile(x, y, z);
                }
            }
        }
        batch.end();
    }

    private void drawTile(int x, int y, int z) {
        if (localTileMap.getAtlasNum(x, y, z) >= 0) {
            drawSprite(localTileMap.getAtlasNum(x, y, z), x, y, z,
                    localTileMap.getAtlasX(x, y, z),
                    localTileMap.getAtlasY(x, y, z));
        }
        BuildingBlock buildingBlock = localMap.getBuildingBlock(x, y, z);
        if (buildingBlock != null) {
            drawSprite(3, x, y, z, 0, 0);
        }
        UnitBlock unitBlock = localMap.getUnitBlock(x, y, z);
        if (unitBlock != null) {
            drawSprite(2, x, y, z, 0, 0);
        }
    }

    private void drawSprite(int atlas, int x, int y, int z, int spriteX, int spriteY) {
        batch.draw(new TextureRegion(atlases[atlas],
                        spriteX * tileWidth,
                        spriteY * tileHeight,
                        tileWidth, tileHeight),
                getScreenPosX(x - camera.getX(), y - camera.getY()),
                getScreenPosY(x - camera.getX(), y - camera.getY(), z - camera.getZ()));
    }

    private void initAtlases() {
        atlases = new Texture[5];
        atlases[0] = new Texture("sprites/blocks.png");
        atlases[1] = new Texture("sprites/plants.png");
        atlases[2] = new Texture("sprites/units.png");
        atlases[3] = new Texture("sprites/buildings.png");
    }

    private void defineframe() {
        maxX = camera.getX() + viewAreaWidth;
        if (maxX > localTileMap.getxSize() - 1) {
            maxX = localTileMap.getxSize() - 1;
        }
        minX = camera.getX() - viewAreaWidth;
        if (minX < 0) {
            minX = 0;
        }
        maxY = camera.getY() + viewAreaWidth;
        if (maxY > localTileMap.getySize() - 1) {
            maxY = localTileMap.getySize() - 1;
        }
        minY = camera.getY() - viewAreaWidth;
        if (minY < 0) {
            minY = 0;
        }
        maxZ = camera.getZ();
        if (maxZ > localTileMap.getzSize() - 1) {
            maxZ = localTileMap.getzSize() - 1;
        }
        minZ = camera.getZ() - viewAreDepth;
        if (minZ < 0) {
            minZ = 0;
        }
    }

    public void setScreenCenterX(int screenCenterX) {
        this.screenCenterX = screenCenterX;
    }

    public void setScreenCenterY(int screenCenterY) {
        this.screenCenterY = screenCenterY;
    }

    public void setViewAreaWidth(int viewAreaWidth) {
        this.viewAreaWidth = viewAreaWidth;
    }

    public void setViewAreDepth(int viewAreDepth) {
        this.viewAreDepth = viewAreDepth;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    private int getScreenPosX(int x, int y) {
        return (x - y) * tileWidth / 2 + screenCenterX;
    }

    private int getScreenPosY(int x, int y, int z) {
        return -(x + y) * tileDepth / 2 + z * (tileHeight - tileDepth) + screenCenterY;
    }

    public void setContainer(GameContainer container) {
        this.container = container;
    }
}