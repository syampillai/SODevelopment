package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.TimeDuration;
import com.storedobject.ui.util.AbstractQuantityField;

public class TimeDurationField extends AbstractQuantityField<TimeDuration> {

    public TimeDurationField() {
        this(null);
    }

    public TimeDurationField(String label) {
        this(label, null);
    }

    public TimeDurationField(String label, String unit) {
        this(label, 6, unit);
    }

    public TimeDurationField(int decimals) {
        this(null, decimals);
    }

    public TimeDurationField(int width, int decimals) {
        this(null, width, decimals);
    }

    public TimeDurationField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public TimeDurationField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public TimeDurationField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public TimeDurationField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public TimeDurationField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public TimeDurationField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, TimeDuration.class));
    }

    public TimeDurationField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, TimeDuration.class, unit);
    }
}
