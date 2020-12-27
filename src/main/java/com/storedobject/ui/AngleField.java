package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Angle;
import com.storedobject.ui.util.AbstractQuantityField;

public class AngleField extends AbstractQuantityField<Angle> {

    public AngleField() {
        this(null);
    }

    public AngleField(String label) {
        this(label, null);
    }

    public AngleField(String label, String unit) {
        this(label, 6, unit);
    }

    public AngleField(int decimals) {
        this(null, decimals);
    }

    public AngleField(int width, int decimals) {
        this(null, width, decimals);
    }

    public AngleField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public AngleField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public AngleField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public AngleField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public AngleField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public AngleField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Angle.class));
    }

    public AngleField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width == 0 ? 5 : width, decimals, Angle.class, unit);
    }
}
