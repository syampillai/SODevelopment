package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Distance;
import com.storedobject.ui.util.AbstractQuantityField;

public class DistanceField extends AbstractQuantityField<Distance> {

    public DistanceField() {
        this(null);
    }

    public DistanceField(String label) {
        this(label, null);
    }

    public DistanceField(String label, String unit) {
        this(label, 6, unit);
    }

    public DistanceField(int decimals) {
        this(null, decimals);
    }

    public DistanceField(int width, int decimals) {
        this(null, width, decimals);
    }

    public DistanceField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public DistanceField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public DistanceField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public DistanceField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public DistanceField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public DistanceField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Distance.class));
    }

    public DistanceField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Distance.class, unit);
    }
}
