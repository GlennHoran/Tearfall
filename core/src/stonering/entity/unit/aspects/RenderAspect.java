package stonering.entity.unit.aspects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import stonering.entity.Aspect;
import stonering.entity.Entity;
import stonering.stage.renderer.AtlasesEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores Entity's render information.
 * Used for rendering moving creatures 'between' the tiles.
 * //TODO add texture.
 */
public class RenderAspect extends Aspect {
    public final int[] atlasXY;
    public final AtlasesEnum atlas;
    public boolean needsVisible = true; // needs icons are visible only for controlled units.
    public final List<CreatureStatusIcon> icons = new ArrayList<>();

    public RenderAspect(Entity entity, int[] xy, AtlasesEnum atlas) {
        super(entity);
        atlasXY = xy;
        this.atlas = atlas;
    }

    public TextureRegion getTile() {
        return atlas.getBlockTile(atlasXY);
    }
}
