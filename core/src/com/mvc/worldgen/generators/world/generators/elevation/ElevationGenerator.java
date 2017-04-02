package com.mvc.worldgen.generators.world.generators.elevation;

import com.mvc.worldgen.generators.world.generators.AbstractGenerator;
import com.mvc.worldgen.generators.world.generators.PerlinNoiseGenerator;
import com.mvc.worldgen.generators.world.WorldGenContainer;

/**
 * Created by Alexander on 01.04.2017.
 */
public class ElevationGenerator extends AbstractGenerator {
	private int width;
	private int height;
	private float[][] elevation;

	public ElevationGenerator(WorldGenContainer container) {
		super(container);
		extractContainer(container);
	}

	private void extractContainer(WorldGenContainer container) {
		width = container.getConfig().getWidth();
		height = container.getConfig().getHeight();
		elevation = new float[width][height];
	}

	@Override
	public boolean execute() {
		System.out.println("generating elevation");
		perlin();
		return false;
	}

	private void perlin() {
		PerlinNoiseGenerator noise = new PerlinNoiseGenerator();
		elevation = noise.generateOctavedSimplexNoise(width, height, 7, 0.6f, 0.01f);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				elevation[x][y] += 1;
				container.setElevation(x, y, (float) (container.getElevation(x, y) + Math.pow(elevation[x][y], 2f)));
			}
		}
	}
}
