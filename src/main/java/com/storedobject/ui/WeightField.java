package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Weight;
import com.storedobject.ui.util.AbstractQuantityField;

public class WeightField extends AbstractQuantityField<Weight> {

    public WeightField() {
        this(null);
    }

    public WeightField(String label) {
        this(label, null);
    }

    public WeightField(String label, String unit) {
        this(label, 6, unit);
    }

    public WeightField(int decimals) {
        this(null, decimals);
    }

    public WeightField(int width, int decimals) {
        this(null, width, decimals);
    }

    public WeightField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public WeightField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public WeightField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public WeightField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public WeightField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }
    
    public WeightField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Weight.class));
    }

    public WeightField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Weight.class, unit);
    }
}
