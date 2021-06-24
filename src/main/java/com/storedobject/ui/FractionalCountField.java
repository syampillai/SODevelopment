package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.FractionalCount;
import com.storedobject.ui.util.AbstractQuantityField;

public class FractionalCountField extends AbstractQuantityField<FractionalCount> {

    public FractionalCountField() {
        this(null);
    }

    public FractionalCountField(String label) {
        this(label, null);
    }

    public FractionalCountField(String label, String unit) {
        this(label, 6, unit);
    }

    public FractionalCountField(int decimals) {
        this(null, decimals);
    }

    public FractionalCountField(int width, int decimals) {
        this(null, width, decimals);
    }

    public FractionalCountField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public FractionalCountField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public FractionalCountField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public FractionalCountField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public FractionalCountField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public FractionalCountField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, FractionalCount.class));
    }

    public FractionalCountField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, FractionalCount.class, unit);
    }
}
