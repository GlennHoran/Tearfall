package stonering.game.view.render.stages.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import stonering.util.geometry.Position;

/**
 * Provides utility methods and rules for drawing scene.
 *
 * @author Alexander on 06.02.2019.
 */
public class DrawingUtil {
    private Batch batch;
    private Texture[] atlases;

    private float shadingStep = 0.06f;
    private Color batchColor;               // default batch color without light or transparency

    public static final int tileWidth = 64;             // x size(left-right)
    public static final int tileDepth = 64;             // y size(back-forth)
    public static final int tileHeight = 96;            // z size(up-down) plus depth
    public static final int topingTileHeight = 70;      // depth plus floor height(10)
    public static final int blockTileHeight = 166;      // total block height
    private Vector2 screenCenter;

    public DrawingUtil(Batch batch) {
        this.batch = batch;
        batch.enableBlending();
        batchColor = new Color();
        createAtlases();
    }

    public void drawSprite(TextureRegion sprite, Position position, Position selectorPosition) {
        drawSprite(sprite, position.x, position.y, position.z, selectorPosition);
    }

    /**
     * Draws sprite on localMap position.
     */
    public void drawSprite(TextureRegion sprite, int x, int y, int z, Position selectorPosition) {
        float screenX = getScreenPosX(x - selectorPosition.x);
        float screenY = getScreenPosY(y - selectorPosition.y, z - selectorPosition.z);
        batch.draw(sprite, screenX, screenY);
    }

    /**
     * Cuts standard tile from x y position in specified atlas.
     */
    public TextureRegion selectSprite(int atlas, int x, int y) {
        return new TextureRegion(atlases[atlas],
                x * tileWidth,
                y * (blockTileHeight) + topingTileHeight,
                tileWidth, tileHeight);
    }

    /**
     * Cuts tile toping from x y position in specified atlas.
     */
    public TextureRegion selectToping(int atlas, int x, int y) {
        return new TextureRegion(atlases[atlas],
                x * tileWidth,
                y * blockTileHeight,
                tileWidth, topingTileHeight);
    }

    private void createAtlases() {
        atlases = new Texture[7];
        atlases[0] = new Texture("sprites/blocks.png");
        atlases[1] = new Texture("sprites/plants.png");
        atlases[2] = new Texture("sprites/units.png");
        atlases[3] = new Texture("sprites/buildings.png");
        atlases[4] = new Texture("sprites/ui_tiles.png");
        atlases[5] = new Texture("sprites/items.png");
        atlases[6] = new Texture("sprites/substrates.png");
    }


    protected float getScreenPosX(int x) {
        return x * tileWidth + screenCenter.x;
    }

    protected float getScreenPosY(int y, int z) {
        return y * tileDepth + z * (tileHeight - tileDepth) + screenCenter.y;
    }

    /**
     * Makes color transparent.
     *
     * @param a
     */
    public void updateColorA(float a) {
        Color color = batch.getColor();
        batch.setColor(color.r, color.g, color.b, a);
    }

    /**
     * Shades batch color to correspond lowering z coord.
     *
     * @param dz
     */
    public void shadeByZ(int dz) {
        float shadedColorChannel = 1 - (dz) * shadingStep;
        batchColor.set(shadedColorChannel, shadedColorChannel, shadedColorChannel, 1f);
        resetColor();
    }

    public void resetColor() {
        batch.setColor(batchColor);
    }

    public void shadeByLight(byte lightLevel) {
        float mod = lightLevel / (float) Byte.MAX_VALUE;
        batch.setColor(batchColor.r * mod, batchColor.g * mod, batchColor.b * mod, batchColor.a);
    }

    public void begin() {
        batch.begin();
        screenCenter = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
//        System.out.println(screenCenter);
    }

    public void end() {
        batch.end();
    }

    public Vector2 getScreenCenter() {
        return screenCenter;
    }

    public Batch getBatch() {
        return batch;
    }
}
