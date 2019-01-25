package stonering.util.pathfinding.a_star;

import stonering.enums.blocks.BlockTypesEnum;
import stonering.game.core.model.LocalMap;
import stonering.util.HashPriorityQueue;
import stonering.util.geometry.Position;
import stonering.util.global.TagLoggersEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class AStar {
    private LocalMap localMap;

    public AStar(LocalMap localMap) {
        this.localMap = localMap;
    }

    // The maximum number of completed nodes. After that number the algorithm returns null.
    // If negative, the search will run until the goal node is found.
    private int maxSteps = 10000;

    /**
     * Returns the shortest Path from a start node to an end node according to
     * the A* heuristics (h must not overestimate). initialNode and last found node included.
     */
    public List<Position> makeShortestPath(Position initialPos, Position targetPos, boolean exactTarget) {
        Node initialNode = new Node(initialPos, targetPos);
        //perform search and save the
        Node pathNode = search(initialNode, targetPos, exactTarget);
        if (pathNode == null)
            return null;
        //return shortest path according to AStar heuristics

        LinkedList<Position> path = new LinkedList<>();
        path.add(pathNode.getPosition());
        while (pathNode.getParent() != null) {
            pathNode = pathNode.getParent();
            path.add(0, pathNode.getPosition());
        }
        return path;
    }

    /**
     * @param initialNode start of the search
     * @param targetPos   end of the search
     * @return goal node from which you can reconstruct the path
     */
    private Node search(Node initialNode, Position targetPos, boolean exactTarget) {
        TagLoggersEnum.PATH.logDebug("searching path from " + initialNode.getPosition() + " to " + targetPos);
        HashPriorityQueue<Node, Node> openSet = new HashPriorityQueue(new NodeComparator());
        HashMap<Integer, Node> closedSet = new HashMap<>();

        // current iteration of the search
        int numSearchSteps = 0;

        openSet.add(initialNode, initialNode);
        while (openSet.size() > 0 && (maxSteps < 0 || numSearchSteps < maxSteps)) {
            //get element with the least sum of costs
            Node currentNode = openSet.poll();

            //path is complete
            if(exactTarget) {
                if (targetPos.equals(currentNode.getPosition())) {
                    return currentNode;
                }
            } else {
                if(isAdjacent(targetPos, currentNode.getPosition())) {
                    return currentNode;
                }
            }

            //get successor nodes
            ArrayList<Node> successorNodes = getSuccessors(currentNode, targetPos);

            //process successor nodes
            for (Node successorNode : successorNodes) {
                // node already closed, skip
                if (closedSet.containsValue(successorNode)) {
                    continue;
                }

                //compute tentativeG
                int pathLength = currentNode.getPathLength() + 1;

                /* Special rule for nodes that are generated within other nodes:
                 * We need to ensure that we use the node and
                 * its g value from the openSet if its already discovered
                 */
                Node discSuccessorNode = openSet.get(successorNode);
                boolean inOpenSet;
                if (discSuccessorNode != null) {
                    successorNode = discSuccessorNode;
                    inOpenSet = true;

                    if (inOpenSet && pathLength >= successorNode.getPathLength())
                        continue;

                } else {
                    inOpenSet = false;
                }
                //node was already discovered and this path is worse than the last one
                successorNode.setParent(currentNode);
                if (inOpenSet) {
                    // if successorNode is already in data structure it has to be inserted again to regain the order
                    openSet.remove(successorNode, successorNode);
                }
                successorNode.setPathLength(pathLength);
                openSet.add(successorNode, successorNode);
            }
            closedSet.put(currentNode.hashCode(), currentNode);
            numSearchSteps += 1;
        }
        TagLoggersEnum.PATH.logDebug("no path found");
        return null;
    }

    private ArrayList<Node> getSuccessors(Node node, Position target) {
        ArrayList<Node> nodes = new ArrayList<>();
        final Position nodePos = node.getPosition();
        for (int z = -1; z < 2; z++) {
            for (int y = -1; y < 2; y++) {
                for (int x = -1; x < 2; x++) {
                    if (x != 0 || y != 0 || z != 0) {
                        if (inMap(nodePos.getX() + x, nodePos.getY() + y, nodePos.getZ() + z)) {
                            Position newPos = new Position(nodePos.getX() + x, nodePos.getY() + y, nodePos.getZ() + z);
                            if (hasPathBetween(nodePos, newPos)) {
                                nodes.add(new Node(newPos, target));
                            }
                        }
                    }
                }
            }
        }
        return nodes;
    }

    private boolean isAdjacent(Position pos1, Position pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) < 2
                && Math.abs(pos1.getY() - pos2.getY()) < 2
                && Math.abs(pos1.getZ() - pos2.getZ()) < 2;
    }

    private static class NodeComparator implements Comparator<Node> {
        public int compare(Node node1, Node node2) {
            return Double.compare(node1.getCost(), node2.getCost());
        }
    }

    private boolean hasPathBetween(Position pos1, Position pos2) {
        boolean passable1 = BlockTypesEnum.getType(localMap.getBlockType(pos1)).getPassing() == 2;
        boolean passable2 = BlockTypesEnum.getType(localMap.getBlockType(pos2)).getPassing() == 2;
        boolean sameLevel = pos1.getZ() == pos2.getZ();
        boolean lowRamp = BlockTypesEnum.getType(localMap.getBlockType(lowerOf(pos1, pos2))) == BlockTypesEnum.RAMP;
        return (passable1 && passable2 && (sameLevel || lowRamp));
    }

    private boolean inMap(int x, int y, int z) {
        return !(x < 0 || y < 0 || z < 0 ||
                x >= localMap.getxSize() ||
                y >= localMap.getySize() ||
                z >= localMap.getzSize());
    }

    private Position lowerOf(Position pos1, Position pos2) {
        return pos1.getZ() < pos2.getZ() ? pos1 : pos2;
    }
}