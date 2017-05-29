package stonering.menu.worldgen.generators.world.generators.drainage;

import stonering.menu.worldgen.generators.world.generators.AbstractGenerator;
import stonering.menu.worldgen.generators.world.WorldGenContainer;
import stonering.menu.worldgen.generators.world.WorldMap;

import java.util.Random;

/**
 * Created by Alexander on 12.03.2017.
 */
public class OceanFiller extends AbstractGenerator {
	private Random random;
	private int width;
	private int height;
	private int seaLevel;

	public OceanFiller(WorldGenContainer container) {
		super(container);
		this.random = container.getConfig().getRandom();
		this.width = container.getConfig().getWidth();
		this.height = container.getConfig().getHeight();
		seaLevel = container.getConfig().getSeaLevel();
	}

	public boolean execute() {
		System.out.println("filling oceans");
		WorldMap map = container.getMap();
		float oceanCount = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (map.getElevation(x,y) < seaLevel) {
					oceanCount++;
				}
			}
		}
		container.setLandPart(1.0f - oceanCount / (width * height));
		return false;
	}
}
