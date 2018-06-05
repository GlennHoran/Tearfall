package stonering.game.core.model.lists;

import stonering.enums.plants.TreeType;
import stonering.game.core.model.LocalMap;
import stonering.global.utils.Position;
import stonering.objects.local_actors.plants.AbstractPlant;
import stonering.objects.local_actors.plants.Plant;
import stonering.objects.local_actors.plants.Tree;

import java.util.ArrayList;

/**
 * Contains plants on localMap. Trees are stored by their parts as separate plants.
 * Destroyed objects do not persist in container and their blocks are not in localMap.
 * <p>
 * Created by Alexander on 09.11.2017.
 */
public class PlantContainer {
    private ArrayList<AbstractPlant> plants;
    private LocalMap localMap;

    public PlantContainer(ArrayList<AbstractPlant> plants) {
        this.plants = plants;
    }

    public ArrayList<AbstractPlant> getPlants() {
        return plants;
    }

    public void placePlants() {
        plants.forEach((plant) -> place(plant));
    }

    private void place(AbstractPlant plant) {
        if (plant.getType().isTree()) {
            if (plant instanceof Tree) {
                placeTree((Tree) plant);
            }
        } else {
            if (plant instanceof Plant) {
                placePlant((Plant) plant);
            }
        }

    }

    private void placePlant(Plant plant) {
        localMap.setPlantBlock(plant.getPosition(), plant.getBlock());
    }

    /**
     * Places tree on local map and adds it to container.
     * Tree should have position, pointing on its stomp (for growing from sapling).
     * //TODO checking space for placing
     * //TODO merging overlaps with other trees.
     *
     * @param tree Tree object with not null tree field
     */
    private void placeTree(Tree tree) {
        TreeType treeType = tree.getType().getTreeType();
        Plant[][][] treeParts = tree.getBlocks();
        Position vector = new Position(tree.getPosition().getX() - treeType.getTreeRadius(),
                tree.getPosition().getY() - treeType.getTreeRadius(),
                tree.getPosition().getZ() - treeType.getRootDepth()); // position of 0,0,0 tree part on map
        for (int x = 0; x < treeParts.length; x++) {
            for (int y = 0; y < treeParts[x].length; y++) {
                for (int z = 0; z < treeParts[x][y].length; z++) {
                    if (treeParts[x][y][z] != null) {
                        Position onMapPosition = new Position(vector.getX() + x,
                                vector.getY() + y,
                                vector.getZ() + z);
                        localMap.setPlantBlock(onMapPosition, treeParts[x][y][z].getBlock());
                        treeParts[x][y][z].setPosition(onMapPosition);
                        plants.add(treeParts[x][y][z]);
                    }
                }
            }
        }
    }

    /**
     * Deletes plant from map and container
     *
     * @param plant
     */
    public void removePlant(Plant plant) {
        if (plants.remove(plant)) {
            localMap.setPlantBlock(plant.getPosition(), null);
        }
    }

    public void setLocalMap(LocalMap localMap) {
        this.localMap = localMap;
    }

    public void turn() {
    }
}
