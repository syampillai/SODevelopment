package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Pressure;
import com.storedobject.ui.util.AbstractQuantityField;

public class PressureField extends AbstractQuantityField<Pressure> {

    public PressureField() {
        this(null);
    }

    public PressureField(String label) {
        this(label, null);
    }

    public PressureField(String label, String unit) {
        this(label, 6, unit);
    }

    public PressureField(int decimals) {
        this(null, decimals);
    }

    public PressureField(int width, int decimals) {
        this(null, width, decimals);
    }

    public PressureField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public PressureField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public PressureField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public PressureField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public PressureField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public PressureField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Pressure.class));
    }

    public PressureField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Pressure.class, unit);
    }
}
