package stonering.entity;

import com.badlogic.gdx.math.Vector2;
import stonering.util.geometry.Position;

import java.io.Serializable;
import java.util.ArrayList;

public class WorldMap implements Serializable {
    private float[][] elevation;
    private float[][] drainage;
    private float[][] summerTemperature;
    private float[][] winterTemperature;
    private float[][] rainfall;
    private Vector2[][] rivers;
    private Vector2[][] brooks;
    private Vector2[][] debug;
    private int[][] biome;

    private int width;
    private int height;
    private long seed;
    private ArrayList<TectonicPlate> tectonicPlates;
    private ArrayList<Position> lakes;

    public WorldMap(int xSize, int ySize) {
        this.width = xSize;
        this.height = ySize;
        elevation = new float[xSize][ySize];
        drainage = new float[xSize][ySize];
        summerTemperature = new float[xSize][ySize];
        winterTemperature = new float[xSize][ySize];
        rainfall = new float[xSize][ySize];
        biome = new int[width][height];

        rivers = new Vector2[xSize][ySize];
        brooks = new Vector2[xSize][ySize];
        debug = new Vector2[xSize][ySize];
        lakes = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getElevation(int x, int y) {
        return elevation[x][y];
    }

    public void setElevation(int x, int y, float val) {
        elevation[x][y] = val;
    }

    public float getSummerTemperature(int x, int y) {
        return summerTemperature[x][y];
    }

    public void setSummerTemperature(int x, int y, float val) {
        summerTemperature[x][y] = val;
    }

    public float getWinterTemperature(int x, int y) {
        return winterTemperature[x][y];
    }

    public void setWinterTemperature(int x, int y, float val) {
        winterTemperature[x][y] = val;
    }

    public float getRainfall(int x, int y) {
        return rainfall[x][y];
    }

    public void setRainfall(int x, int y, float val) {
        rainfall[x][y] = val;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean inMap(int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < height);
    }

    public boolean inMap(float x, float y) {
        return (x >= 0 && y >= 0 && x < width && y < height);
    }

    public ArrayList<TectonicPlate> getTectonicPlates() {
        return tectonicPlates;
    }

    public void setTectonicPlates(ArrayList<TectonicPlate> tectonicPlates) {
        this.tectonicPlates = tectonicPlates;
    }

    public Vector2 getRiver(int x, int y) {
        return rivers[x][y];
    }

    public void setRiver(int x, int y, Vector2 river) {
        this.rivers[x][y] = river;
    }

    public Vector2 getBrook(int x, int y) {
        return brooks[x][y];
    }

    public void setBrook(int x, int y, Vector2 brook) {
        this.brooks[x][y] = brook;
    }

    public Vector2 getDebug(int x, int y) {
        return debug[x][y];
    }

    public void setDebug(int x, int y, Vector2 river) {
        this.debug[x][y] = river;
    }

    public ArrayList<Position> getLakes() {
        return lakes;
    }

    public void setLakes(ArrayList<Position> lakes) {
        this.lakes = lakes;
    }

    public float getDrainage(int x, int y) {
        return drainage[x][y];
    }

    public void setDrainage(int x, int y, float val) {
        drainage[x][y] = val;
    }

    public void setBiome(int x, int y, int value) {
        if (inMap(x, y)) {
            biome[x][y] = value;
        }
    }

    public int getBiome(int x, int y) {
        return inMap(x, y) ? biome[x][y] : 0;
    }
}
