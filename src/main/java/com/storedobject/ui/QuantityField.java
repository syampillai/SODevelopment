package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.util.AbstractQuantityField;

public class QuantityField extends AbstractQuantityField<Quantity> {

    public QuantityField() {
        this(null);
    }

    public QuantityField(String label) {
        this(label, null);
    }

    public QuantityField(String label, String unit) {
        this(label, 6, unit);
    }

    public QuantityField(int decimals) {
        this(null, decimals);
    }

    public QuantityField(int width, int decimals) {
        this(null, width, decimals);
    }

    public QuantityField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public QuantityField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public QuantityField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public QuantityField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public QuantityField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public QuantityField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Quantity.class));
    }

    public QuantityField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Quantity.class, unit);
    }
}
