package stonering.game.model.tilemaps;

import stonering.enums.blocks.BlockTypesEnum;
import stonering.enums.blocks.BlocksTileMapping;
import stonering.enums.materials.Material;
import stonering.enums.materials.MaterialMap;
import stonering.game.GameMvc;
import stonering.game.model.local_map.LocalMap;
import stonering.util.global.IntTriple;

/**
 * Updates LocalTileMap when blocks or plants on LocalMap are changed.
 * Is called from localMap, reference from other places not required.
 *
 * @author Alexander Kuzyakov on 03.08.2017.
 */
public class LocalTileMapUpdater {
    private LocalMap localMap;
    private LocalTileMap localTileMap;
    private transient MaterialMap materialMap;

    public LocalTileMapUpdater() {
        materialMap = MaterialMap.instance();
    }

    /**
     * Updates all tiles on local map.
     */
    public void flushLocalMap() {
        if (GameMvc.instance() == null) return;
        localMap = GameMvc.instance().getModel().get(LocalMap.class);
        for (int x = 0; x < localMap.xSize; x++) {
            for (int y = 0; y < localMap.ySize; y++) {
                for (int z = 0; z < localMap.zSize; z++) {
                    updateTile(x, y, z);
                }
            }
        }
    }

    /**
     * Updates single tile. Called from {@link LocalMap} when tile is changed.
     */
    public void updateTile(int x, int y, int z) {
        if (GameMvc.instance() == null) return;
        localMap = GameMvc.instance().getModel().get(LocalMap.class);
        localTileMap = GameMvc.instance().getModel().get(LocalTileMap.class);
        byte blockType = localMap.getBlockType(x, y, z);
        if (blockType == BlockTypesEnum.SPACE.CODE) {
            localTileMap.removeTile(x, y, z);
        } else { // non space
            Material material = materialMap.getMaterial(localMap.getMaterial(x, y, z));
            int atlasX;
            if (blockType == BlockTypesEnum.RAMP.CODE) {
                atlasX = countRamp(x, y, z);
            } else {
                atlasX = BlocksTileMapping.getType(blockType).ATLAS_X;
            }
            localTileMap.setTile(x, y, z, atlasX, material.getAtlasY(), 0);
        }
        updateRampsAround(x, y, z);
    }

    /**
     * Observes tiles around given one, and updates atlasX for ramps.
     */
    private void updateRampsAround(int xc, int yc, int z) {
        for (int y = yc - 1; y < yc + 2; y++) {
            for (int x = xc - 1; x < xc + 2; x++) {
                if (!localMap.inMap(x, y, z) || localMap.getBlockType(x, y, z) != BlockTypesEnum.RAMP.CODE) continue;
                IntTriple triple = localTileMap.get(x, y, z);
                localTileMap.setTile(x, y, z, countRamp(x, y, z), triple.getVal2(), triple.getVal3());
            }
        }
    }

    /**
     * Chooses ramp tile by surrounding walls.
     *
     * @return ramp atlas X
     */
    private byte countRamp(int x, int y, int z) {
        int walls = observeWalls(x, y, z);
        if ((walls & 0b00001010) == 0b00001010) {
            return BlocksTileMapping.RAMP_SW.ATLAS_X;
        } else if ((walls & 0b01010000) == 0b01010000) {
            return BlocksTileMapping.RAMP_NE.ATLAS_X;
        } else if ((walls & 0b00010010) == 0b00010010) {
            return BlocksTileMapping.RAMP_SE.ATLAS_X;
        } else if ((walls & 0b01001000) == 0b01001000) {
            return BlocksTileMapping.RAMP_NW.ATLAS_X;
        } else if ((walls & 0b00010000) != 0) {
            return BlocksTileMapping.RAMP_E.ATLAS_X;
        } else if ((walls & 0b01000000) != 0) {
            return BlocksTileMapping.RAMP_N.ATLAS_X;
        } else if ((walls & 0b00000010) != 0) {
            return BlocksTileMapping.RAMP_S.ATLAS_X;
        } else if ((walls & 0b00001000) != 0) {
            return BlocksTileMapping.RAMP_W.ATLAS_X;
        } else if ((walls & 0b10000000) != 0) {
            return BlocksTileMapping.RAMP_NEO.ATLAS_X;
        } else if ((walls & 0b00000100) != 0) {
            return BlocksTileMapping.RAMP_SEO.ATLAS_X;
        } else if ((walls & 0b00100000) != 0) {
            return BlocksTileMapping.RAMP_NWO.ATLAS_X;
        } else if ((walls & 0b00000001) != 0) {
            return BlocksTileMapping.RAMP_SWO.ATLAS_X;
        } else
            return BlocksTileMapping.FLOOR.ATLAS_X;
    }

    /**
     * Counts neighbour walls to choose ramp type and orientation.
     *
     * @return int representing adjacent walls.
     */
    public int observeWalls(int cx, int cy, int cz) {
        int bitpos = 1;
        int walls = 0;
        for (int y = cy - 1; y <= cy + 1; y++) {
            for (int x = cx - 1; x <= cx + 1; x++) {
                if ((x == cx) && (y == cy)) continue;
                if (localMap.getBlockType(x, y, cz) == BlockTypesEnum.WALL.CODE) walls |= bitpos;
                bitpos *= 2; // shift to 1 bit
            }
        }
        return walls;
    }
}
