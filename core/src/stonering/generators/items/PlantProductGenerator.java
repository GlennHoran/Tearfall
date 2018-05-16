package stonering.generators.items;

import stonering.enums.materials.Material;
import stonering.enums.materials.MaterialMap;
import stonering.exceptions.DescriptionNotFoundException;
import stonering.objects.local_actors.items.Item;
import stonering.objects.local_actors.plants.Plant;

public class PlantProductGenerator {

    public Item generateCutProduct(Plant plant) {
        String[] productProperties = plant.getType().getCutProduct();
        if(productProperties == null) { // plant has no product
            return null;
        }
        try {
            Material material = MaterialMap.getInstance().getMaterial(productProperties[1]);
            Item item = new Item(null);
            item.setMaterial(material.getId());
            item.setVolume(100000); //TODO
            item.setWeight(Math.round(item.getVolume() * material.getDensity()));
            return item;
        } catch (DescriptionNotFoundException e) {
            System.out.println("error while leaving plant cut product: ");
            System.out.println(plant.toString());
            e.printStackTrace();
            return null;
        }
    }
}
