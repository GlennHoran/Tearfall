package stonering.generators.items;

import stonering.enums.plants.PlantBlocksTypeEnum;
import stonering.entity.local.items.Item;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.PlantBlock;
import stonering.entity.local.plants.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates product items from harvesting and cutting plants.
 * PlantBlocks are not changed here, it should be done in actions.
 */
public class PlantProductGenerator {
    private ItemGenerator itemGenerator;

    public PlantProductGenerator() {
        itemGenerator = new ItemGenerator();
    }

    /**
     * Generates cut products, including tree parts. Does not creates harvest products.
     */
    public ArrayList<Item> generateCutProduct(PlantBlock block) {
        AbstractPlant plant = block.getPlant();
        ArrayList<Item> items = new ArrayList<>();
        block.getCutProducts().forEach((product) -> items.add(itemGenerator.generateItem(product, plant.getType().materialName)));
        if (plant instanceof Tree) {
            Item cutItem = generateCutProductForTreePart(block);
            if (cutItem != null) items.add(cutItem);
        }
        return items;
    }

    /**
     * Generates only harvest products. Block is not modified.
     */
    public List<Item> generateHarvestProduct(PlantBlock block) {
        ArrayList<Item> items = new ArrayList<>();
        block.getHarvestProducts().forEach(s -> items.add(itemGenerator.generateItem(s, block.getMaterial())));
        return items;
    }

    /**
     * Generates tree specific items for blocks.
     * //TODO add tree age in account;
     */
    private Item generateCutProductForTreePart(PlantBlock block) {
        String itemTitle = "";
        switch (PlantBlocksTypeEnum.getType(block.getBlockType())) {
            case TRUNK:
            case STOMP: {
                itemTitle = "log";
                break;
            }
            case BRANCH: {
                itemTitle = "branch";
                break;
            }
            case ROOT: {
                itemTitle = "root";
            }
        }
        if (!itemTitle.isEmpty()) return itemGenerator.generateItem(itemTitle, block.getMaterial());
        return null;
    }
}
