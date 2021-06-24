package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Speed;
import com.storedobject.ui.util.AbstractQuantityField;

public class SpeedField extends AbstractQuantityField<Speed> {

    public SpeedField() {
        this(null);
    }

    public SpeedField(String label) {
        this(label, null);
    }

    public SpeedField(String label, String unit) {
        this(label, 6, unit);
    }

    public SpeedField(int decimals) {
        this(null, decimals);
    }

    public SpeedField(int width, int decimals) {
        this(null, width, decimals);
    }

    public SpeedField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public SpeedField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public SpeedField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public SpeedField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public SpeedField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public SpeedField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Speed.class));
    }

    public SpeedField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Speed.class, unit);
    }
}
