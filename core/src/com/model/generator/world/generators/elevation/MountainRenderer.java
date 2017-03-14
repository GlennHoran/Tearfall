package com.model.generator.world.generators.elevation;

import com.model.generator.world.map_objects.WorldGenContainer;
import com.model.generator.world.world_objects.Edge;
import com.model.generator.world.world_objects.Mountain;
import com.model.generator.world.world_objects.Slope;
import com.model.utils.Position;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexander on 03.03.2017.
 */
public class MountainRenderer {
	private WorldGenContainer container;
	private int width;
	private int height;
	private float[][] elevationBuffer;

	public MountainRenderer(WorldGenContainer container) {
		this.container = container;
		this.width = container.getConfig().getWidth();
		this.height = container.getConfig().getHeight();
		elevationBuffer = new float[width][height];
	}

	public void execute() {
		for (Iterator<Edge> edgeIterator = container.getEdges().iterator(); edgeIterator.hasNext(); ) {
			for (Iterator<Mountain> iterator = edgeIterator.next().getMountains().iterator(); iterator.hasNext(); ) {
				Mountain mountain = iterator.next();
				renderMountain(mountain);
				smoothMountains(1);
			}
		}
	}

	private void renderMountain(Mountain mountain) {
		List<Position> corners = mountain.getCorners();
		Position prevCorner = corners.get(corners.size() - 1);
		for (Iterator<Position> iterator = mountain.getCorners().iterator(); iterator.hasNext(); ) {
			Position corner = iterator.next();
			Slope slope = new Slope(mountain.getTop(), prevCorner, corner);
			prevCorner = corner;
			renderSlope(slope);
		}
	}

	private void renderSlope(Slope slope) {
		int minX = slope.getMinX() > 0 ? slope.getMinX() : 0;
		int minY = slope.getMinY() > 0 ? slope.getMinY() : 0;
		int maxX = slope.getMaxX() < width - 1 ? slope.getMaxX() : width - 1;
		int maxY = slope.getMaxY() < height - 1 ? slope.getMaxY() : height - 1;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (slope.isInside(x, y)) {
					float z = slope.getAltitude(x, y);
					if (container.getMountainElevation(x, y) < z) {
						container.setMountainElevation(x, y, Math.round(z));
					}
				}
			}
		}
	}

	private void smoothMountains(int iterations) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				elevationBuffer[x][y] = container.getMountainElevation(x, y);
			}
		}
		float[][] innerElevationBuffer = new float[width][height];
		for (int i = 0; i < iterations; i++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					innerElevationBuffer[x][y] = countMiddleElevation(x, y, 1, true);
				}
			}
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					elevationBuffer[x][y] = innerElevationBuffer[x][y];
				}
			}
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (elevationBuffer[x][y] > container.getMountainElevation(x, y)) {
					container.setMountainElevation(x, y, Math.round(elevationBuffer[x][y]));
				}
			}
		}
	}

	private float countMiddleElevation(int x, int y, int radius, boolean lockBorders) {
		int minX = x - radius;
		int maxX = x + radius + 1;
		int minY = y - radius;
		int maxY = y + radius + 1;
		int xWidth = 2 * radius + 1;
		int yWidth = 2 * radius + 1;
		float sum = 0;

		if (minX < 0) {
			if (!lockBorders) {
				xWidth += minX;
				minX = 0;
			} else {
				return elevationBuffer[x][y];
			}
		}
		if (maxX > width) {
			if (!lockBorders) {

				xWidth -= maxX - width;
				maxX = width;
			} else {
				return elevationBuffer[x][y];
			}
		}
		if (minY < 0) {
			if (!lockBorders) {

				yWidth += minY;
				minY = 0;
			} else {
				return elevationBuffer[x][y];
			}
		}
		if (maxY > height) {
			if (!lockBorders) {
				yWidth -= maxY - height;
				maxY = height;
			} else {
				return elevationBuffer[x][y];
			}
		}
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				sum += elevationBuffer[i][j];
			}
		}
		return sum / (xWidth * yWidth);
	}
}