package stonering.generators.items;

import stonering.entity.local.items.Item;
import stonering.enums.materials.Material;
import stonering.enums.materials.MaterialMap;
import stonering.exceptions.FaultDescriptionException;

/**
 * @author Alexander Kuzyakov on 08.01.2018.
 *         <p>
 *         generates stone, ore, gem, clay, sand, items.
 */
public class DiggingProductGenerator {

    /**
     * @param materialId
     * @return
     */
    public Item generateDigProduct(int materialId) {
        Material material = MaterialMap.getInstance().getMaterial(materialId);
        if (material.getTypes().contains("stone") || material.getTypes().contains("ore")) {
//            try {
                return new ItemGenerator().generateItem("rock", materialId);
//            } catch (FaultDescriptionException e) {
//                e.printStackTrace();
//            }
        }
        return null;
    }

    public boolean productRequired(int materialId) {
        Material material = MaterialMap.getInstance().getMaterial(materialId);
        return material != null && (material.getTypes().contains("stone") || material.getTypes().contains("ore"));
    }
}
