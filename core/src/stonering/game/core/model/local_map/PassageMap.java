package stonering.game.core.model.local_map;

import stonering.game.core.model.util.UtilByteArray;
import stonering.util.geometry.Position;
import stonering.util.global.TagLoggersEnum;
import stonering.util.pathfinding.a_star.AStar;

import java.util.*;

/**
 * Manages isolated areas on localMap to prevent pathfinding between them.
 * Updates areas on local map change.
 * When tile becomes passable, some areas may merge.
 * When tile becomes impassable, some areas may split.
 * Building walls performed in two steps, building floor first, then building wall.
 * Destroying walls performed in opposite direction.
 *
 * @author Alexander on 05.11.2018.
 */
public class PassageMap {
    private LocalMap localMap;
    private AStar aStar;
    private UtilByteArray area; // number of area
    private UtilByteArray passage; // stores
    private Map<Byte, Integer> areaNumbers; // counts number of cells in areas

    public PassageMap(LocalMap localMap) {
        this.localMap = localMap;
        aStar = new AStar(localMap);
        areaNumbers = new HashMap<>();
        area = new UtilByteArray(localMap.xSize, localMap.ySize, localMap.zSize);
        passage = new UtilByteArray(localMap.xSize, localMap.ySize, localMap.zSize);
    }

    /**
     * Called when local map passage is updated. If cell becomes non-passable, it may split area into two.
     */
    public void updateCell(int x, int y, int z) {
        if (localMap.isWalkPassable(x, y, z)) { // areas should be merged
            passage.setValue(x, y, z, 1);
            Set<Byte> areas = observeAreasAround(x, y, z);
            if (areas.size() > 1) mergeAreas(areas);
        } else { // areas may split
            passage.setValue(x, y, z, 0);
            splitAreas(collectNeighbourPositions(x, y, z), new Position(x, y, z));
        }
    }

    /**
     * Return area numbers around given position.
     * Inaccessible tiles are skipped.
     */
    private Set<Byte> observeAreasAround(int cx, int cy, int cz) {
        Set<Byte> neighbours = new HashSet<>();
        for (int x = cx - 1; x < cx + 2; x++) {
            for (int y = cy - 1; y < cy + 2; y++) {
                for (int z = cz - 1; z < cz + 2; z++) {
                    if (!localMap.inMap(x, y, z)) continue;
                    byte areaValue = area.getValue(x, y, z);
                    if (areaValue == 0 || !localMap.hasPathBetween(x, y, z, cx, cy, cz)) continue;
                    neighbours.add(areaValue);
                }
            }
        }
        return neighbours;
    }

    /**
     * Refills areas if they were split. Areas that were different before update cannot be merged.
     */
    private void splitAreas(Map<Byte, List<Position>> posMap, Position center) {
        TagLoggersEnum.PATH.logDebug("Splitting areas around " + center + " in positions " + posMap);
        for (Byte areaValue : posMap.keySet()) {
            List<Position> posList = posMap.get(areaValue);
            if (posList.size() < 2) continue;
            List<Set<Position>> isolatedPositions = new ArrayList<>(); // positions in inner sets are connected.
            while (!posList.isEmpty()) {
                Position firstPos = posList.remove(0);
                Set<Position> connectedPositions = new HashSet<>();
                connectedPositions.add(firstPos);
                for (Iterator<Position> iterator = posList.iterator(); iterator.hasNext(); ) {
                    Position pos = iterator.next();
                    if (pos.isNeighbour(firstPos) && localMap.hasPathBetween(pos, firstPos) || aStar.makeShortestPath(pos, firstPos, true) != null) {
                        iterator.remove();
                        connectedPositions.add(pos);
                    }
                }
                isolatedPositions.add(connectedPositions);
            }
            if (isolatedPositions.size() < 2) continue; // all positions from old areas remain connected, do nothing.
            isolatedPositions.remove(0);
            for (Set<Position> positions : isolatedPositions) {
                int filledCells = fill(positions.iterator().next(), getUnusedAreaNumber()); // refill isolated area with new number

            }
        }
    }

    private byte getUnusedAreaNumber() {
        for (byte i = 0; i < Byte.MAX_VALUE; i++)
            if (!areaNumbers.keySet().contains(i)) return i;
        return 0;
    }

    private void mergeAreas(Set<Byte> areas) {
        TagLoggersEnum.PATH.logDebug("Merging areas " + areas);
        Optional<Byte> largestAreaOptional = areas.stream().max(Comparator.comparingInt(o -> areaNumbers.get(o)));
        if (!largestAreaOptional.isPresent()) return; //TODO
        byte largestArea = largestAreaOptional.get();
        areas.remove(largestArea);
        HashMap<Byte, Integer> areaSizes = new HashMap<>();
        areas.forEach(aByte -> areaSizes.put(aByte, areaNumbers.get(aByte)));
        for (int x = 0; x < localMap.xSize; x++) {
            for (int y = 0; y < localMap.ySize; y++) {
                for (int z = 0; z < localMap.zSize; z++) {
                    if (areas.contains(area.getValue(x, y, z))) {
                        area.setValue(x, y, z, largestArea);
                    }
                }
            }
        }
        // update merged area counters
        areas.forEach(areaNumber -> areaNumbers.put(largestArea, areaNumbers.remove(areaNumber) + areaNumbers.get(largestArea)));
    }

    /**
     * Gathers all passable near positions and splits them by areas. Unassigned, inaccessible, impassable positions are skipped.
     */
    private Map<Byte, List<Position>> collectNeighbourPositions(int cx, int cy, int cz) {
        Map<Byte, List<Position>> positionLists = new HashMap<>();
        for (int x = cx - 1; x < cx + 2; x++) {
            for (int y = cy - 1; y < cy + 2; y++) {
                for (int z = cz - 1; z < cz + 2; z++) {
                    if (!localMap.inMap(x, y, z)) continue;
                    byte areaValue = area.getValue(x, y, z);
                    if (areaValue == 0 || !localMap.hasPathBetween(x, y, z, cx, cy, cz)) continue;
                    List<Position> positions = positionLists.getOrDefault(areaValue, positionLists.put(areaValue, new LinkedList<>()));
                    positions.add(new Position(x, y, z));
                }
            }
        }
        return positionLists;
    }

    public int fill(Position start, byte value) {
        int counter = 0;
        Set<Position> openSet = new HashSet<>();
        for (openSet.add(start); !openSet.isEmpty(); counter++) {
            Position position = openSet.iterator().next();
            openSet.remove(position);
            updateValue(position, value);
            openSet.addAll(getNeighbours(position, value));
        }
        return counter;
    }

    private Set<Position> getNeighbours(Position center, byte blockedArea) {
        Set<Position> neighbours = new HashSet<>();
        for (int x = center.x - 1; x < center.x + 2; x++) {
            for (int y = center.y - 1; y < center.y + 2; y++) {
                for (int z = center.z - 1; z < center.z + 2; z++) {
                    if (x == center.x && y == center.y && z == center.z) continue; // same position
                    Position position = new Position(center.x + x, center.y + y, center.z + z);
                    if (!localMap.inMap(position)) continue;             // neighbour is out of map
                    if (area.getValue(position) == blockedArea) continue; // same area
                    if (localMap.hasPathBetween(center, position)) neighbours.add(position);
                }
            }
        }
        return neighbours;
    }

    private void updateValue(Position position, byte value) {
        byte old = area.getValue(position);
        area.setValue(position, value);
        areaNumbers.put(value, areaNumbers.get(value) + 1);
        if (areaNumbers.get(old) == 1) {
            areaNumbers.remove(old);
        } else {
            areaNumbers.put(old, areaNumbers.get(old) - 1);
        }
    }

    public UtilByteArray getArea() {
        return area;
    }

    public Map<Byte, Integer> getAreaNumbers() {
        return areaNumbers;
    }
}
