package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.WeightRate;
import com.storedobject.ui.util.AbstractQuantityField;

public class WeightRateField extends AbstractQuantityField<WeightRate> {

    public WeightRateField() {
        this(null);
    }

    public WeightRateField(String label) {
        this(label, null);
    }

    public WeightRateField(String label, String unit) {
        this(label, 6, unit);
    }

    public WeightRateField(int decimals) {
        this(null, decimals);
    }

    public WeightRateField(int width, int decimals) {
        this(null, width, decimals);
    }

    public WeightRateField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public WeightRateField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public WeightRateField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public WeightRateField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public WeightRateField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public WeightRateField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, WeightRate.class));
    }

    public WeightRateField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, WeightRate.class, unit);
    }
}
