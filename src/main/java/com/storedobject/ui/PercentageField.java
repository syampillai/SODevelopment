package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Percentage;
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
}
