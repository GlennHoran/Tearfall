package stonering.enums.plants;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class PlantType {
    private String specimen;
    private String title;

    private String description;
    private int minTemperature;
    private int maxTemperature;
    private int minGrowingTemperature;
    private int maxGrowingTemperature;
    private int minRainfall;
    private int maxRainfall;
    private String waterSource;
    private int lightNeed;
    private ArrayList<String> soilType;
    private int atlasY;
    private Color color;

    private String harvestProduct;
    private String cutProduct;

    public String getSpecimen() {
        return specimen;
    }

    public void setSpecimen(String specimen) {
        this.specimen = specimen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getMinGrowingTemperature() {
        return minGrowingTemperature;
    }

    public void setMinGrowingTemperature(int minGrowingTemperature) {
        this.minGrowingTemperature = minGrowingTemperature;
    }

    public int getMaxGrowingTemperature() {
        return maxGrowingTemperature;
    }

    public void setMaxGrowingTemperature(int maxGrowingTemperature) {
        this.maxGrowingTemperature = maxGrowingTemperature;
    }

    public int getMinRainfall() {
        return minRainfall;
    }

    public void setMinRainfall(int minRainfall) {
        this.minRainfall = minRainfall;
    }

    public int getMaxRainfall() {
        return maxRainfall;
    }

    public void setMaxRainfall(int maxRainfall) {
        this.maxRainfall = maxRainfall;
    }

    public String getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(String waterSource) {
        this.waterSource = waterSource;
    }

    public int getLightNeed() {
        return lightNeed;
    }

    public void setLightNeed(int lightNeed) {
        this.lightNeed = lightNeed;
    }

    public ArrayList<String> getSoilType() {
        return soilType;
    }

    public void setSoilType(ArrayList<String> soilTypes) {
        this.soilType = soilTypes;
    }

    public int getAtlasY() {
        return atlasY;
    }

    public void setAtlasY(int atlasY) {
        this.atlasY = atlasY;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getHarvestProduct() {
        return harvestProduct;
    }

    public void setHarvestProduct(String harvestProduct) {
        this.harvestProduct = harvestProduct;
    }

    public String getCutProduct() {
        return cutProduct;
    }

    public void setCutProduct(String cutProduct) {
        this.cutProduct = cutProduct;
    }
}
