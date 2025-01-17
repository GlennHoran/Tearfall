package stonering.entity.unit.aspects.health;

import stonering.enums.unit.health.HealthParameterEnum;

/**
 * Parameter of {@link HealthAspect}.
 * TODO store range index and bounds for faster checking.
 *
 * @author Alexander on 06.10.2019.
 */
public class HealthParameterState {
    public final HealthParameterEnum parameter;
    public float current = 0;
    public float max = 100;

    public HealthParameterState(HealthParameterEnum parameter) {
        this.parameter = parameter;
    }

    /**
     * Relative value in percents;
     */
    public float getRelativeValue() {
        return max / current * 100f;
    }
}
