package stonering.generators.creatures;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import stonering.generators.creatures.needs.NeedAspectGenerator;
import stonering.global.utils.Position;
import stonering.objects.local_actors.Aspect;
import stonering.objects.local_actors.unit.aspects.MovementAspect;
import stonering.objects.local_actors.unit.aspects.PlanningAspect;
import stonering.objects.local_actors.unit.Unit;
import stonering.utils.global.FileLoader;

/**
 * Creates creatures from json files by specimen title.
 *
 * @author Alexander Kuzyakov on 03.12.2017.
 */
public class CreatureGenerator {
    private JsonReader reader;
    private BodyGenerator bodyGenerator;
    private EquipmentAspectGenerator equipmentAspectGenerator;
    private NeedAspectGenerator needAspectGenerator;
    private JsonValue creatures;

    public CreatureGenerator() {
        reader = new JsonReader();
        bodyGenerator = new BodyGenerator();
        equipmentAspectGenerator = new EquipmentAspectGenerator();
        needAspectGenerator = new NeedAspectGenerator();
        creatures = reader.parse(FileLoader.getCreatureFile());
    }

    public Unit generateUnit(String specimen) {
        JsonValue creature = null;
        for (JsonValue c : creatures) {
            if (c.getString("title").equals(specimen)) {
                creature = c;
                break;
            }
        }
        Unit unit = new Unit(new Position(0, 0, 0)); //TODO change constructor.
        Aspect bodyAspect = generateBodyAspect(unit, creature);
        if (bodyAspect != null) {
            unit.addAspect(bodyAspect);
            //TODO remove unit argument from generators
            unit.addAspect(generatePlanningAspect(unit));
            unit.addAspect(generateMovementAspect(unit));
            unit.addAspect(generateEquipmentAspect(creature));
            unit.addAspect(generateNeedsAspect(creature));
            return unit;
        } else {
            return null;
        }
    }

    private Aspect generateMovementAspect(Unit unit) {
        return new MovementAspect(unit);
    }

    private Aspect generateBodyAspect(Unit unit, JsonValue creature) {
        return bodyGenerator.generateBody(creature, unit);
    }

    private Aspect generatePlanningAspect(Unit unit) {
        return new PlanningAspect(unit);
    }

    private Aspect generateEquipmentAspect(JsonValue creature) {
        return equipmentAspectGenerator.generateEquipmentAspect(creature);
    }

    private Aspect generateNeedsAspect(JsonValue creature) {
        return needAspectGenerator.generateNeedAspect(creature);
    }
}
