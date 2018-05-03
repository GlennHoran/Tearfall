package stonering.generators.worldgen.generators.drainage;

import com.badlogic.gdx.math.Vector2;
import stonering.generators.worldgen.WorldGenContainer;
import stonering.generators.worldgen.WorldMap;
import stonering.generators.worldgen.generators.AbstractGenerator;
import stonering.global.utils.Position;

import java.util.*;

/**
 * Created by Alexander on 14.03.2017.
 * <p>
 * Generates river.
 * For rivers additional elevation map (smoothed) is used. The map of slope vectors are based on it.
 * Rivers start from points close to mountains pikes, distributed with interspaces.
 * River has its own vector, which is modified by slope vectors on every passing cell.
 */
public class RiverGenerator extends AbstractGenerator {
    private Random random;
    private WorldMap map;
    private int width;
    private int height;
    private int riverCount;
    private Vector2[][] slopeInclination;
    private Vector2[][] endPoints;
    private Vector2[][] inflows;
    private Vector2[][] riverVectors;
    private float[][] waterAmount;
    private List<Position> cells;
    private float seaLevel;
    private float riverStartLevel;

    public RiverGenerator(WorldGenContainer container) {
        super(container);
    }

    private void extractContainer(WorldGenContainer container) {
        random = container.getConfig().getRandom();
        width = container.getConfig().getWidth();
        height = container.getConfig().getHeight();
        riverCount = (int) (width * height * container.getLandPart() / container.getConfig().getRiverDensity());
        slopeInclination = new Vector2[width][height];
        endPoints = new Vector2[width][height];
        inflows = new Vector2[width][height];
        riverVectors = new Vector2[width][height];
        waterAmount = new float[width][height];
        cells = new ArrayList<>();
        seaLevel = container.getConfig().getSeaLevel();
        riverStartLevel = container.getConfig().getLargeRiverStartLevel();
    }

    @Override
    public boolean execute() {
        extractContainer(container);
        System.out.println("generating rivers");
        map = container.getMap();
        countAngles();
        runWater();
        return false;
    }

    private void runWater() {
        //count water amount
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (container.getElevation(x, y) > seaLevel) {
                    float amount = getWaterAmount(x, y);
                    Vector2 current = new Vector2(x, y);
                    //run flow
                    do {
                        waterAmount[(int) current.x][(int) current.y] += amount;
                        current.add(slopeInclination[x][y]);
                    } while (container.inMap(current.x, current.y));
                }
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (container.getElevation(x, y) > seaLevel) {
                    inflows[x][y] = lookupMainInflow(x, y);
                }
            }
        }

        // create river vectors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (container.getElevation(x, y) > seaLevel) {
//                    if (inflows[x][y] != null) {
                    riverVectors[x][y] = slopeInclination[x][y].cpy();
                    riverVectors[x][y].setLength(waterAmount[x][y]);
//                    map.setRiver(x, y, riverVectors[x][y]);
//                    map.setDebug(x, y, inflows[x][y]);
                }
            }
        }

//        ArrayList<Vector2> riverOuts = findRiversOuts();
//        riverOuts.forEach((end) -> runRiverFromEnd(end));

//        riverOuts.forEach((out) -> runRiver(out));

        elevationStartPoints().forEach((point) -> runRiverFromStart(point));
    }

    private float getWaterAmount(int x, int y) {
        return 0.01f;
//        return container.getRainfall(x, y) / 10000f;
    }

    private ArrayList<Vector2> findRiversOuts() {
        ArrayList<Vector2> outs = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (riverVectors[x][y] != null) {
                    if (container.getElevation(Math.round(endPoints[x][y].x), Math.round(endPoints[x][y].y)) < seaLevel) { // river goes to sea
                        outs.add(new Vector2(x, y));
                    }
                }
            }
        }
        return outs;
    }

    private ArrayList<Vector2> elevationStartPoints() {
        ArrayList<Vector2> starts = new ArrayList<>();
        ArrayList<Vector2> potentialStarts = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (container.getElevation(x, y) > riverStartLevel) {
                    potentialStarts.add(new Vector2(x, y));
                }
            }
        }
        Random random = new Random();
        while (!potentialStarts.isEmpty()) {
            Vector2 start = potentialStarts.get(random.nextInt(potentialStarts.size()));
            starts.add(start);
            potentialStarts.removeIf(qwer -> qwer.cpy().sub(start).len() < 7);
        }
        return starts;
    }

    private ArrayList<Vector2> randomStartPoints() {
        ArrayList<Vector2> points = new ArrayList<>();
        int tries = 0;
        Random random = new Random();
        while (tries < 500 && points.size() < 50) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if (container.getElevation(x, y) > seaLevel) {
                points.add(new Vector2(x, y));
            }
            tries++;
        }
        return points;
    }

    private void runRiverFromEnd(Vector2 end) {
        ArrayList<Vector2> lookupList = new ArrayList<>();
        ArrayList<Vector2> riverPoints = new ArrayList<>();
        lookupList.add(end);
        while (lookupList.size() > 0) {
            Vector2 point = lookupList.remove(0);
            if (!riverPoints.contains(point)) {
                riverPoints.add(point);
                int x = Math.round(point.x);
                int y = Math.round(point.y);
                map.setRiver(x, y, riverVectors[x][y]);
                lookupList.addAll(lookupInflows(x, y));
            }
        }
    }

    private void recursiveRiverUp(Vector2 point) {
        int x = Math.round(point.x);
        int y = Math.round(point.y);
        map.setRiver(x, y, riverVectors[x][y]);
        lookupInflows(x, y).forEach((inflow) -> recursiveRiverUp(inflow));
    }

    private void runRiverFromStart(Vector2 start) {
        System.out.println("new river");
        int length = 0;
        while (length < 100) {
            System.out.println("start: " + start);
            int x = Math.round(start.x);
            int y = Math.round(start.y);
            if (container.getElevation(x, y) <= seaLevel) {
                break;
            }
            map.setRiver(x, y, riverVectors[x][y]);
            start = endPoints[x][y];
            length++;
        }
    }

    private void recursiveRiver(Vector2 start, int counter) {
        int x = Math.round(start.x);
        int y = Math.round(start.y);
        ArrayList<Vector2> inflowArray = lookupInflows(x, y);
//        inflowArray
    }

    private boolean hasNearSea(int cx, int cy) {
        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int y = cy - 1; y <= cy + 1; y++) {
                if (inMap(x, y)
                        && (x != cx || y != cy)
                        && container.getElevation(x, y) < seaLevel) {
                    return true;
                }
            }
        }
        return false;
    }

    private void markMainReavers() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

            }
        }
    }

    private ArrayList<Vector2> lookupInflows(int cx, int cy) {
        ArrayList<Vector2> inflowsArray = new ArrayList<>();
        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int y = cy - 1; y <= cy + 1; y++) {
                if ((x != cx || y != cx) // not center
                        && inMap(x, y)   // not out of map
                        && endPoints[x][y].x == cx
                        && endPoints[x][y].y == cy)  //is inflow
                {
                    inflowsArray.add(new Vector2(x, y));
                }
            }
        }
        return inflowsArray;
    }

    private Vector2 lookupMainInflow(int cx, int cy) {
        int maxX = -2;
        int maxY = -2;
        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int y = cy - 1; y <= cy + 1; y++) {
                if ((x != cx || y != cx) // not center
                        && inMap(x, y)   // not out of map
                        && endPoints[x][y].x == cx
                        && endPoints[x][y].y == cy  //is inflow
                        && (maxX < -1 || waterAmount[maxX][maxY] < waterAmount[x][y])) { // is first or greater inflow
                    maxX = x;
                    maxY = y;
                }
            }
        }
        return maxX >= -1 ? new Vector2(maxX - cx, maxY - cy) : null;
    }

    private void countRiverStart() {
        TreeSet<Position> sortedCells = new TreeSet<>(new ElevationComparator());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                sortedCells.add(new Position(x, y, Math.round(waterAmount[x][y])));
            }
        }
        int count = 0;
        cells.clear();
        for (Iterator<Position> iterator = sortedCells.iterator(); count < riverCount && iterator.hasNext(); ) {
            Position riverStart = iterator.next();
            boolean rejected = false;
            for (Iterator<Position> cellsIterator = cells.iterator(); cellsIterator.hasNext(); ) {
                if (isNear(riverStart, cellsIterator.next(), 18)) {
                    rejected = true;
                    break;
                }
            }
            if (!rejected) {
                cells.add(riverStart);
                count++;
            }
        }
    }

    private void runRiverFromStart(int x, int y, int maxLength, int branchingDepth) {
//        int i = 0;
//        float seaLevel = container.getConfig().getSeaLevel() - 1;
//        int savedAngle = 0;
//        if (!inMap(x, y)) return;
//        Vector riverVector = new Vector(x, y, container.getSlopeAngles(x, y), 2.0f);
//        int turningCounter = 0;
//        while (i < maxLength && container.getElevation(x, y) > seaLevel && inMap(x, y)) {
//
//            float curElevation = waterAmount[x][y]; // getting elevation in current point
//
//            if (turningCounter == 0) {  // starting river turn
//                turningCounter = random.nextInt(14);
//            }
//
//            Vector slopeVector = new Vector(0, 0, container.getSlopeAngles(x, y), 1); // getting slope vector
//            riverVector = riverVector.sum(slopeVector); // applying slope to river
//            riverVector.setLength(riverVector.getLength() / 2); // decreasing river speed
//            if (turningCounter != 0) { // turning river
//                int mod = Math.round(Math.copySign(1, turningCounter - 7));
//                riverVector.setAngle((riverVector.getAngle() + 15 * mod + 360) % 360);
//                turningCounter--;
//            }
//            // converting river angle to x45, and saving difference
//            riverVector.setAngle((riverVector.getAngle() + savedAngle + 360) % 360);
//            int targetAngle = ((int) ((riverVector.getAngle() + 22.5f + 360) % 360) / 45);
//            targetAngle *= 45;
//            savedAngle = (int) ((riverVector.getAngle() - targetAngle + 360) % 360);
//
//            //branching river
//            if (i > 6 && branchingDepth > 0) {
//                if (random.nextInt(100) < 15) {
//                    targetAngle = (targetAngle + 45) % 360;
//                    savedAngle = 0;
//                    int branchAngle = (targetAngle - 90 + 360) % 360;
//                    int bx = x + getXProject(branchAngle);
//                    int by = y + getYProject(branchAngle);
//                    riverVector.setLength(2);
//                    branchingDepth--;
//                    runRiver(bx, by, maxLength, branchingDepth);
//                }
//            }
//
//            riverVector.setAngle(targetAngle);
//            map.addRiverVector(new Vector(x, y, x + getXProject(targetAngle), y + getYProject(targetAngle)));  // set river in current point
//            x += getXProject(targetAngle); // getting next river point
//            y += getYProject(targetAngle);
//            if (!inMap(x, y) || map.getRivers().containsKey(new Position(x, y, 0)) || (waterAmount[x][y] - curElevation > 0.3f)) {
//                break;
//            }
//            i++;
//        }
    }

    private boolean isNear(Position pos1, Position pos2, float limit) {
        double distance = countDistance(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
        return distance < limit;
    }

    private int getXProject(float angle) {
        if ((angle < 62.5) || (angle > 292.5)) return 1;
        if ((angle > 112.5) && (angle < 247.5)) return -1;
        return 0;
    }

    private int getYProject(float angle) {
        if (angle > 22.5 && angle < 137.5) return 1;
        if (angle > 202.5 && angle < 337.5) return -1;
        return 0;
    }

    private boolean inMap(int x, int y) {
        if (x < 0 || x >= width) return false;
        if (y < 0 || y >= height) return false;
        return true;
    }

    private void countAngles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Vector2 slope = countSlopeAngle(x, y);
                slopeInclination[x][y] = slope;
                endPoints[x][y] = new Vector2(Math.round(x + slope.x), Math.round(y + slope.y)); // ok
            }
        }
    }

    private Vector2 countSlopeAngle(float cx, float cy) {
        float centerElevation = container.getElevation(Math.round(cx), Math.round(cy));
        Vector2 vector = new Vector2();
        for (int x = Math.round(cx) - 1; x <= cx + 1; x++) {
            for (int y = Math.round(cy) - 1; y <= cy + 1; y++) {
                if (container.inMap(x, y)) { // elevation decreases in this direction
                    float elevationDelta = centerElevation - container.getElevation(x, y);
                    vector.add((x - cx) * ((elevationDelta)), (y - cy) * ((elevationDelta)));
                }
            }
        }
        return vector.nor();
    }

    private float countDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private float countMiddleElevation(int x, int y, int radius) {
        int minX = x - radius;
        int maxX = x + radius + 1;
        int minY = y - radius;
        int maxY = y + radius + 1;
        float sum = 0;
        if (minX < 0) minX = 0;
        if (maxX > width) maxX = width;
        if (minY < 0) minY = 0;
        if (maxY > height) maxY = height;
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                sum += waterAmount[i][j];
            }
        }
        return sum / ((maxX - minX) * (maxY - minY));
    }

    private class ElevationComparator implements Comparator<Position> {
        @Override
        public int compare(Position o1, Position o2) {
            int value = 0;
            if (o1 != null && o2 != null) {
                value = o2.getZ() - o1.getZ();
                if (value == 0) {
                    value = 1;
                }
            }
            return value;
        }
    }
}