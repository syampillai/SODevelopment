package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Area;
import com.storedobject.ui.util.AbstractQuantityField;

public class AreaField extends AbstractQuantityField<Area> {

    public AreaField() {
        this(null);
    }

    public AreaField(String label) {
        this(label, null);
    }

    public AreaField(String label, String unit) {
        this(label, 6, unit);
    }

    public AreaField(int decimals) {
        this(null, decimals);
    }

    public AreaField(int width, int decimals) {
        this(null, width, decimals);
    }

    public AreaField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public AreaField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public AreaField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public AreaField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public AreaField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public AreaField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Area.class));
    }

    public AreaField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Area.class, unit);
    }
}
