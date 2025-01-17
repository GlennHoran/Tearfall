package stonering.generators.items;

import stonering.entity.item.Item;
import stonering.enums.materials.Material;
import stonering.enums.materials.MaterialMap;
import stonering.util.geometry.Position;

/**
 * Generates stone, ore, gem, clay, sand item for leaving while tile is dug out.
 *
 * @author Alexander Kuzyakov on 08.01.2018.
 */
public class DiggingProductGenerator {

    //TODO add other item classes
    public Item generateDigProduct(int materialId, Position position) {
        Material material = MaterialMap.instance().getMaterial(materialId);
        if (!material.getTags().contains("stone") && !material.getTags().contains("ore")) return null;
        return new ItemGenerator().generateItem("rock", materialId, position);
    }

    public boolean productRequired(int materialId) {
        Material material = MaterialMap.instance().getMaterial(materialId);
        return material != null && (material.getTags().contains("stone") || material.getTags().contains("ore"));
    }
}
