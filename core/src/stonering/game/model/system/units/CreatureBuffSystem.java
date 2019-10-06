package stonering.game.model.system.units;

import stonering.entity.unit.Unit;
import stonering.entity.unit.aspects.AttributeAspect;
import stonering.entity.unit.aspects.BuffAspect;
import stonering.entity.unit.aspects.RenderAspect;
import stonering.entity.unit.aspects.health.AttributeBuff;
import stonering.entity.unit.aspects.health.Buff;
import stonering.entity.unit.aspects.health.HealthAspect;
import stonering.entity.unit.aspects.health.HealthBuff;
import stonering.game.model.Turnable;
import stonering.util.global.Logger;
import stonering.util.global.Pair;

import java.util.Iterator;

/**
 * System for updating creatures {@link Buff}s.
 * All buff changes should be performed through this system.
 *
 * @author Alexander on 16.09.2019.
 */
public class CreatureBuffSystem {

    /**
     * Updates counters on creature's buffs, removing expired ones.
     */
    public void updateCreatureBuffs(Unit unit) {
        if (!unit.hasAspect(BuffAspect.class)) return;
        for (Iterator<Buff> iterator = unit.getAspect(BuffAspect.class).buffs.iterator(); iterator.hasNext(); ) {
            Buff buff = iterator.next();
            buff.decrease();
            if (!buff.expired()) continue; // skip active buffs
            iterator.remove();
            unapplyBuff(unit, buff);
        }
    }

    public boolean addBuff(Unit unit, Buff buff) {
        Logger.UNITS.logDebug("Adding buff " + buff + " to creature " + unit);
        if(buff == null) return failWithLog("Trying to add null buff to creature " + unit);
        if (!unit.hasAspect(BuffAspect.class)) return failWithLog("Trying to add buff " + buff + " to creature " + unit + " without BuffAspect");
        if (!buff.apply(unit)) return failWithLog("Failed to apply buff " + buff + " to creature " + unit);
        unit.getAspect(BuffAspect.class).buffs.add(buff);
        unit.getAspect(RenderAspect.class).icons.add(buff.icon);
        return true;
    }

    public boolean unapplyBuff(Unit unit, Buff buff) {
        Logger.UNITS.logDebug("Removing buff " + buff + " to creature " + unit);
        if (!buff.unapply(unit)) return failWithLog("Failed to unapply buff " + buff + " to creature " + unit);
        unit.getAspect(BuffAspect.class).buffs.remove(buff);
        unit.getAspect(RenderAspect.class).icons.remove(buff.icon);
        return true;
    }

    /**
     * Unapplies all buffs with given tag from creature.
     */
    public boolean unapplyByTag(Unit unit, String tag) {
        Logger.UNITS.logDebug("Removing buffs from creature " + unit + " by tag " + tag);
        for (Iterator<Buff> iterator = unit.getAspect(BuffAspect.class).buffs.iterator(); iterator.hasNext();) {
            Buff buff = iterator.next();
            if(buff.tags.contains(tag)) {
                iterator.remove();
                unit.getAspect(RenderAspect.class).icons.remove(buff.icon);
            }
        }
        return true;
    }

    private boolean failWithLog(String message) {
        Logger.UNITS.logError(message);
        return false;
    }
}
