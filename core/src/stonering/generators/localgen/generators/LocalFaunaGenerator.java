package stonering.generators.localgen.generators;

import stonering.game.core.model.LocalMap;
import stonering.generators.creatures.CreatureGenerator;
import stonering.generators.localgen.LocalGenConfig;
import stonering.generators.localgen.LocalGenContainer;

/**
 * Created by Alexander on 03.12.2017.
 */
public class LocalFaunaGenerator {
    private LocalGenContainer container;
    private LocalGenConfig config;
    private LocalMap localMap;
    private CreatureGenerator creatureGenerator;

    public LocalFaunaGenerator(LocalGenContainer container) {
        this.container = container;
        config = container.getConfig();
        localMap = container.getLocalMap();
        creatureGenerator = new CreatureGenerator();
    }

    public void execute() {
        for (int i = 0; i < 100; i++) {
            creatureGenerator.generateWildAnimal("dog");
        }
    }
}
