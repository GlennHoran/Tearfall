package stonering.generators.worldgen.generators.drainage;

import stonering.generators.PerlinNoiseGenerator;
import stonering.generators.worldgen.generators.AbstractGenerator;
import stonering.generators.worldgen.WorldGenContainer;
import stonering.global.utils.Position;

import java.util.ArrayList;

/**
 * Created by Alexander on 31.03.2017.
 * <p>
 * Generates rainfall level in the world
 */
public class RainfallGenerator extends AbstractGenerator {
    private int width;
    private int height;
    private int seaLevel;
    private int minRainfall;
    private int maxRainfall;
    private float[][] humidity;
    private boolean[][] rainfallSet;

    public RainfallGenerator(WorldGenContainer container) {
        super(container);
    }

    @Override
    public boolean execute() {
        System.out.println("generating rainfall");
        extractContainer();
        addMainGradientOnWater();
        fillEmpty();
        addPerlinNoise();
        return false;
    }



    private void evaporate() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (container.getElevation(x, y) <= seaLevel) {
                    float t = (container.getSummerTemperature(x, y) + container.getWinterTemperature(x, y)) / 2f;
                    if (t > 0) {
                        float dewPoint = t - (1 - humidity[x][y]) / 0.05f;
                        float evaporation = ((t + 0.006f * container.getElevation(x, y))
                                / (100 - (Math.abs(1 - (y * 2 / height))) * 90)
                                + 15 * (t - dewPoint))
                                / (80 - t);
                    }
                }
            }
        }
    }

    private void addMainGradientOnWater() {
        float equator = height / 2f;
        for (int y = 0; y < height; y++) {
            float rainfall = ((-Math.abs(y - (equator))) / (equator) + 1) * (maxRainfall - minRainfall - 10) + minRainfall;
            for (int x = 0; x < width; x++) {
                if (container.getElevation(x, y) <= seaLevel) {
                    container.setRainfall(x, y, rainfall);
                    rainfallSet[x][y] = true;
                }
            }
        }
    }

    private void createEquatorialArea() {
        int beltWidth = height / 10;
        for (int x = 0; x < width; x++) {
            for (int dy = 0; dy < beltWidth; dy++) {
                float rainfall = maxRainfall / beltWidth * (beltWidth - dy);
                container.setRainfall(x, width / 2 - dy, rainfall);
                container.setRainfall(x, width / 2 + dy, rainfall);
            }
        }
    }

    private void fillEmpty() {
        for (int i = 0; i < Math.max(height, width); i++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (!rainfallSet[x][y] && hasNearRainfall(x, y)) {
                        humidity[x][y] = countNearRainfall(x, y);
                    }
                }
            }
            flushBufferToMap();
        }
    }

    private boolean hasNearRainfall(int x, int y) {
        int xStart = x > 0 ? x - 1 : 0;
        int xEnd = x < width - 1 ? x + 1 : width - 1;
        int yStart = y > 0 ? y - 1 : 0;
        int yEnd = y < height - 1 ? y + 1 : height - 1;
        for (x = xStart; x <= xEnd; x++) {
            for (y = yStart; y <= yEnd; y++) {
                if (rainfallSet[x][y]) return true;
            }
        }
        return false;
    }

    private boolean hasNearUnsetRainfall(int x, int y) {
        int xStart = x > 0 ? x - 1 : 0;
        int xEnd = x < width - 1 ? x + 1 : width - 1;
        int yStart = y > 0 ? y - 1 : 0;
        int yEnd = y < height - 1 ? y + 1 : height - 1;
        for (x = xStart; x <= xEnd; x++) {
            for (y = yStart; y <= yEnd; y++) {
                if (rainfallSet[x][y]) return true;
            }
        }
        return false;
    }

    private float countNearRainfall(int x, int y) {
        int xStart = x > 0 ? x - 1 : 0;
        int xEnd = x < width - 1 ? x + 1 : width - 1;
        int yStart = y > 0 ? y - 1 : 0;
        int yEnd = y < height - 1 ? y + 1 : height - 1;
        float rainfall = 0;
        int count = 0;
        for (x = xStart; x <= xEnd; x++) {
            for (y = yStart; y <= yEnd; y++) {
                if (rainfallSet[x][y]) {
                    rainfall += container.getRainfall(x, y);
                    count++;
                }
            }
        }
        rainfall = count != 0 ? rainfall / count : 0;
        rainfall = rainfall * (100 - container.getElevation(x, y)) / 100f;
        return rainfall;
    }

    private void flushBufferToMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (humidity[x][y] != 0) {
                    container.setRainfall(x, y, humidity[x][y]);
                    humidity[x][y] = 0;
                    rainfallSet[x][y] = true;
                }
            }
        }
    }

    private void addPerlinNoise() {
        PerlinNoiseGenerator generator = new PerlinNoiseGenerator();
        float[][] noise = generator.generateOctavedSimplexNoise(width, height, 7, 0.4f, 0.025f);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                container.setRainfall(x, y, Math.round(container.getRainfall(x, y) + noise[x][y] * 10));
            }
        }
    }

    private Position getNearestSeaPoint(int cx, int cy) {
        ArrayList<Position> positions = new ArrayList<>();
        for (int r = 1; ; r++) {
            positions.clear();
            for (int x = -r; x <= r; x++) {
                int y = (int) Math.round(Math.sqrt(Math.pow(r, 2) - Math.pow(x, 2)));
                if (container.inMap(cx + x, cy + y) && container.getElevation(cx + x, cy + y) > seaLevel) {
                    positions.add(new Position(cx + x, cy + y, 0));
                }
                if (container.inMap(cx + x, cy - y) && container.getElevation(cx + x, cy - y) > seaLevel) {
                    positions.add(new Position(cx + x, cy - y, 0));
                }
            }
            if (positions.size() > 0) {
                Position maxRainfall = positions.get(0);
                for (Position pos : positions) {
                    if (container.getRainfall(pos.getX(), pos.getY()) > container.getRainfall(maxRainfall.getX(), maxRainfall.getY())) {
                        maxRainfall = pos;
                    }
                }
                return maxRainfall;
            }
        }
    }


    private void extractContainer() {
        width = container.getConfig().getWidth();
        height = container.getConfig().getHeight();
        seaLevel = container.getConfig().getSeaLevel();
        minRainfall = container.getConfig().getMinRainfall();
        maxRainfall = container.getConfig().getMaxRainfall();
        humidity = new float[width][height];
        rainfallSet = new boolean[width][height];
    }
}