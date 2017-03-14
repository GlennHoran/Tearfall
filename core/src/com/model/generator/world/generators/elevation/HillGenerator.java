package com.model.generator.world.generators.elevation;

import com.model.generator.world.map_objects.WorldGenContainer;
import com.model.generator.world.world_objects.Mountain;
import com.model.utils.Position;
import com.model.utils.Vector;

import java.util.List;
import java.util.Random;

/**
 * Created by Alexander on 11.03.2017.
 */
public class HillGenerator {
	private final WorldGenContainer container;
	private Random random;
	private int width;
	private int height;
	private int hillDensity;
	private float hillMargin;


	public HillGenerator(WorldGenContainer container) {
		this.container = container;
		random = container.getConfig().getRandom();
		width = container.getConfig().getWidth();
		height = container.getConfig().getHeight();
		hillDensity = container.getConfig().getHillDensity();
		hillMargin = container.getConfig().getHillMargin();
	}

	public boolean execute() {
		int num = width * height / hillDensity;
		int widthMargin = (int) (width * hillMargin);
		int heightMargin = (int) (height * hillMargin);
		List<Mountain> hills = container.getHills();
		for (int i = 0; i < num; i++) {
			Mountain hill = createHill(widthMargin + random.nextInt(width - 2 * widthMargin), heightMargin + random.nextInt(width - 2 * heightMargin));
			hills.add(hill);
		}
		return false;
	}

	private Mountain createHill(int x, int y) {
		Mountain Hill = new Mountain();
		Hill.setTop(new Position(x,y,random.nextInt(3) + 2));
		int slopeCount = random.nextInt(2) + 6 + Hill.getTop().getZ() / 39;
		int[] slopeAngles = new int[slopeCount];
		int spinAngle = random.nextInt(360);
		for (int i = 0; i < slopeCount; i++) {
			slopeAngles[i] = random.nextInt(30) - 15 + 360 / slopeCount * i;
			slopeAngles[i] += spinAngle;
			slopeAngles[i] %= 360;
		}
		for (int i = 0; i < slopeCount; i++) {
			int height = Hill.getTop().getZ();
			int hillRadius = height > 0 ? height * 3 + random.nextInt(height) : 3;
			Vector vector = new Vector(Hill.getTop().getX(), Hill.getTop().getY(), (float) slopeAngles[i], hillRadius);
			Hill.addCorner(vector.getEndPoint());
		}
		return Hill;
	}
}