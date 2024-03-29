package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Percentage;
import com.storedobject.core.Weight;
import com.storedobject.ui.util.AbstractQuantityField;

public class PercentageField extends AbstractQuantityField<Percentage> {

    public PercentageField() {
        this(null);
    }

    public PercentageField(String label) {
        this(label, 2);
    }

    public PercentageField(int decimals) {
        this(null, decimals);
    }

    public PercentageField(int width, int decimals) {
        this(null, width, decimals);
    }

    public PercentageField(String label, int decimals) {
        this(label, 0, decimals);
    }

    public PercentageField(String label, int width, int decimals) {
        super(label, width, decimals, Percentage.class, MeasurementUnit.get("%", Percentage.class));
    }

    public PercentageField(int width, int decimals, String unit) {
        this(null, width, decimals, MeasurementUnit.get(unit, Weight.class));
    }

    public PercentageField(int width, int decimals, MeasurementUnit unit) {
        super(null, width, decimals, Percentage.class, unit);
    }

    public PercentageField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Weight.class));
    }

    public PercentageField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Percentage.class, unit);
    }
}
