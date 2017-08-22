package stonering.generators.localgen;

import stonering.game.core.model.LocalMap;
import stonering.generators.worldgen.WorldMap;
import stonering.utils.Position;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Alexander on 21.08.2017.
 */
public class LocalHeightsGenerator {
    private WorldMap worldMap;
    private LocalMap localMap;
    private LocalGenConfig config;
    private int x;
    private int y;
    private int xSize;
    private int ySize;
    private int squreSize = 48;
    private int[][] globalElevation;

    public LocalHeightsGenerator(WorldMap worldMap) {
        this.worldMap = worldMap;
        config = new LocalGenConfig();
    }

    public void generateHeights(int x, int y, int width, int heigth) {
        adjustSize(x, y, width, heigth);

    }

    private void adjustSize(int x, int y, int width, int heigth) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + xSize > worldMap.getWidth()) xSize = worldMap.getWidth() - x;
        if (y + ySize > worldMap.getHeight()) ySize = worldMap.getHeight() - y;
    }

    private void createMap() {
        int localElavation = 0;
        for (int x = this.x; x < this.x + xSize; x++) {
            for (int y = this.y; y < this.y + ySize; y++) {
                localElavation = Math.max(localElavation, worldMap.getElevation(x, y));
            }
        }
        localMap = new LocalMap(xSize * squreSize + 1, ySize * squreSize + 1, localElavation * config.getWorldToLocalElevationModifier() + 30);
    }

    private void generateHights(int x, int y, int lx, int ly) {
        //calculating corners
        int[][] localHights = new int[squreSize + 1][squreSize + 1];
        Arrays.fill(localHights, -1);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                localHights[i * squreSize][j * squreSize] = calculateMidElevation(x + i, y + j);
            }
        }
        //calculating borders
        recursiveMidpoint(localHights[0], 0, localHights[0].length, new Random(x * y + 1));
        recursiveMidpoint(localHights[squreSize], 0, localHights[squreSize].length, new Random((x + 1) * y + 1));
        int[] border = new int[squreSize + 1];
        for (int i = 0; i < squreSize + 1; i++) {
            border[i] = localHights[i][0];
        }
        recursiveMidpoint(border, 0, border.length, new Random(x * y));
        for (int i = 0; i < squreSize + 1; i++) {
            localHights[i][0] = border[i];
            border[i] = localHights[i][squreSize];
        }
        recursiveMidpoint(border, 0, border.length, new Random(x * (y + 1)));
        for (int i = 0; i < squreSize + 1; i++) {
            localHights[i][squreSize] = border[i];
        }

        performSquare(localHights, squreSize);

    }

    private void performSquare(int[][] array, int step) {
        for (int x = 0; x < array.length; x+=step) {
            for (int y = 0; y < array[0].length; y+=step) {
                float midValue = array[x * step][y * step];
                midValue += array[(x + 1) * step][y * step];
                midValue += array[x * step][(y + 1) * step];
                midValue += array[(x + 1) * step][(y + 1) * step];
                array[x * step + step / 2][y * step + step / 2] = (int) (midValue / 4);
            }
        }
    }

    private void performDiamond(int[][] array, int step) {
        for (int x = 0; x < array.length; x+=step) {
            for (int y = 0; y < array[0].length + 1; y+=step) {
                if (x < numX) {
                    float midValue = array[x * step][y * step];
                    midValue += array[(x + 1) * step][y * step];
                    int count = 2;
                    if (y > 0) {
                        midValue += array[x * step + step / 2][y * step - step / 2];
                        count++;
                    }
                    if (y < numY - 1) {
                        midValue += array[x * step + step / 2][y * step + step / 2];
                        count++;
                    }
                    array[x * step + step / 2][y * step] = midValue / count;
                }
                if (y < numY) {
                    float midValue = array[x * step][y * step];
                    midValue += array[x * step][(y + 1) * step];
                    int count = 2;
                    if (x > 0) {
                        midValue += array[x * step - step / 2][y * step + step / 2];
                        count++;
                    }
                    if (x < numX - 1) {
                        midValue += array[x * step + step / 2][y * step + step / 2];
                        count++;
                    }
                    array[x * step][y * step + step / 2] = midValue / count;
                }
            }
        }
    }

    private void recursiveMidpoint(int[] array, int left, int right, Random random) {
        if (right - left > 6) {
            array[(right + left) / 2] = (array[left] + array[right]) / 2 + random.nextInt(1);
            recursiveMidpoint(array, left, (right + left) / 2, random);
            recursiveMidpoint(array, (right + left), right / 2, random);
        } else {
            for (int i = left + 1; i < right; i++) {
                array[i] = array[left] + ((array[left] - array[right]) / (right - left)) * i;
            }
        }
    }

    private int calculateMidElevation(int x, int y) {
        int elevation = 0;
        for (int i = x - 1; i < x + 1; i++) {
            for (int j = y - 1; j < y + 1; j++) {
                elevation += worldMap.getElevation(i, j);
            }
        }
        return elevation / 4;
    }

//    private void createGlobalElevation() {
//        Position SWcorner = new Position(x ,y,0);
//        Position NEcorner = new Position(x + xSize,y + ySize,0);
//
//
//        globalElevation = new int[xSize + 2][ySize + 2];
//        for (int x = 0; x < xSize + 2; x++) {
//            for (int y = 0; y < ySize + 2; y++) {
//
//            }
//        }
//    }
}





