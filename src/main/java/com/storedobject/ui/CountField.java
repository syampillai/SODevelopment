package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Count;
import com.storedobject.ui.util.AbstractQuantityField;

public class CountField extends AbstractQuantityField<Count> {

    public CountField() {
        this(null);
    }

    public CountField(String label) {
        this(label, null);
    }

    public CountField(String label, String unit) {
        this(label, 6, unit);
    }

    public CountField(int decimals) {
        this(null, decimals);
    }

    public CountField(int width, int decimals) {
        this(null, width, decimals, (MeasurementUnit)null);
    }

    public CountField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public CountField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public CountField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public CountField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public CountField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Count.class));
    }

    public CountField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Count.class, unit);
    }
}
