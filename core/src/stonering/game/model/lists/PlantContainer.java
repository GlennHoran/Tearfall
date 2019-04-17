package stonering.game.model.lists;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import stonering.enums.OrientationEnum;
import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.GameMvc;
import stonering.game.model.IntervalTurnable;
import stonering.game.model.ModelComponent;
import stonering.game.model.local_map.LocalMap;
import stonering.generators.items.PlantProductGenerator;
import stonering.util.geometry.Position;
import stonering.entity.local.items.Item;
import stonering.entity.local.plants.AbstractPlant;
import stonering.entity.local.plants.Plant;
import stonering.entity.local.plants.PlantBlock;
import stonering.entity.local.plants.Tree;
import stonering.util.global.CompatibleArray;
import stonering.util.global.Initable;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains plants on localMap. Trees are stored by their parts as separate plants.
 * Destroyed entity do not persist in container and their blocks are not in localMap.
 *
 * @author Alexander Kuzyakov on 09.11.2017.
 */
public class PlantContainer extends IntervalTurnable implements Initable, ModelComponent {
    private GameMvc gameMvc;
    private CompatibleArray<AbstractPlant> plants;
    private LocalMap localMap;
    private final int WALL_CODE = BlockTypesEnum.WALL.CODE;

    public PlantContainer(List<AbstractPlant> plants) {
        this.plants = new CompatibleArray<>(plants);
    }

    public PlantContainer() {
        this(new ArrayList<>());
    }

    @Override
    public void init() {
        gameMvc = GameMvc.instance();
        localMap = gameMvc.getModel().get(LocalMap.class);
        plants.forEach(this::place);
    }

    @Override
    public void turn() {
        plants.forEach(abstractPlant -> abstractPlant.turn());
    }

    public void place(AbstractPlant plant) {
        if (plant.getType().isTree() && plant instanceof Tree) placeTree((Tree) plant);
        if (plant instanceof Plant) placePlant((Plant) plant);
    }

    private void placePlant(Plant plant) {
        localMap.setPlantBlock(plant.getPosition(), plant.getBlock());
        plant.getBlock().setPosition(plant.getPosition());
    }

    /**
     * Places tree on local map.
     * Tree should have position, pointing on its stomp (for growing from sapling).
     * //TODO checking space for placing
     * //TODO merging overlaps with other trees.
     *
     * @param tree Tree object with not null tree field
     */
    private void placeTree(Tree tree) {
        List<Integer> treeForm = tree.getCurrentStage().treeForm;
        int radius = treeForm.get(0);
        Position vector = Position.sub(tree.getPosition(), radius, radius, treeForm.get(2)); // position of 0,0,0 tree part on map
        PlantBlock[][][] treeParts = tree.getBlocks();
        for (int x = 0; x < treeParts.length; x++) {
            for (int y = 0; y < treeParts[x].length; y++) {
                for (int z = 0; z < treeParts[x][y].length; z++) {
                    if (treeParts[x][y][z] == null) continue;
                    Position onMapPosition = Position.add(vector, x, y, z);
                    if (!localMap.inMap(onMapPosition)) continue;
                    localMap.setPlantBlock(onMapPosition, treeParts[x][y][z]);
                    treeParts[x][y][z].setPosition(onMapPosition);
                }
            }
        }
    }

    /**
     * Removes plant from container and it's block from map.
     * Can leave block's products (creates cut and harvest).
     */
    public void removePlant(AbstractPlant plant, boolean leaveProduct) {
        if (plants.removeValue(plant, true)) {
            if (plant instanceof Plant) {
                PlantBlock block = ((Plant) plant).getBlock();
                if (block != null) removePlantBlock(block, leaveProduct);
            } else if (plant instanceof Tree) {
                PlantBlock[][][] treeParts = ((Tree) plant).getBlocks();
                for (PlantBlock[][] treePart : treeParts) {
                    for (PlantBlock[] plantBlocks : treePart) {
                        for (PlantBlock block : plantBlocks) {
                            if (block != null) removePlantBlock(block, leaveProduct);
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes single plant block from map. Can leave products.
     */
    private void removePlantBlock(PlantBlock block, boolean leaveProduct) {
        if (block == null) return;
        localMap.setPlantBlock(block.getPosition(), null);
        if (!leaveProduct) return;
        for (Item item : new PlantProductGenerator().generateCutProduct(block)) {
            if(item != null) gameMvc.getModel().get(ItemContainer.class).addItem(item, block.getPosition());
        }
    }

    /**
     * Fells given tree, in specified direction, and creates tasks to chop logs if needed (after manual chopping).
     * //TODO implement fallen trees.
     * //TODO add direction.
     * //TODO add tasks creation.
     */
    public void fellTree(Tree tree, OrientationEnum orientation, boolean createTasks) {
        removePlant(tree, true);
//        if (orientation == OrientationEnum.N) {
//            Position treePosition = tree.getPosition();
//            int stompZ = tree.getCurrentStage().treeForm.get(2);
//            PlantBlock[][][] treeParts = tree.getBlocks();
//            for (int x = 0; x < treeParts.length; x++) {
//                for (int y = 0; y < treeParts[x].length; y++) {
//                    for (int z = stompZ; z < treeParts[x][y].length; z++) {
//                        PlantBlock block = treeParts[x][y][z];
//                        if (block == null) continue;
//                        Position newPosition = translatePosition(block.getPosition().toVector3(), treePosition.toVector3(), orientation);
//                        if (localMap.getBlockType(newPosition) != WALL_CODE) {
//                            localMap.setPlantBlock(block.getPosition(), null);
//                            block.setPosition(newPosition);
//                            localMap.setPlantBlock(block.getPosition(), block);
//                        } else {
//                            treeParts[x][y][z] = null;
//                        }
//                    }
//                }
//            }
//        }
    }

    private Position translatePosition(Vector3 position, Vector3 center, OrientationEnum orientation) {
        Vector3 offset = position.sub(center);
        Matrix3 matrix3 = new Matrix3();
        matrix3.setToRotation(new Vector3(1, 0, 0), -90);
        offset.mul(matrix3);
        return new Position(center.add(offset.mul(matrix3)));
    }

    /**
     * Removes blocks of given plant from map.
     */
    public void removePlantBlocks(AbstractPlant plant) {
        if (plant instanceof Tree) {
            Tree tree = (Tree) plant;
            PlantBlock[][][] treeParts = tree.getBlocks();
            for (int x = 0; x < treeParts.length; x++) {
                for (int y = 0; y < treeParts[x].length; y++) {
                    for (int z = 0; z < treeParts[x][y].length; z++) {
                        if (treeParts[x][y][z] != null) localMap.setPlantBlock(treeParts[x][y][z].getPosition(), null);
                    }
                }
            }
        } else if (plant instanceof Plant) {
            localMap.setPlantBlock(((Plant) plant).getBlock().getPosition(), null);
        }
    }
}
