package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.VolumeRate;
import com.storedobject.ui.util.AbstractQuantityField;

public class VolumeRateField extends AbstractQuantityField<VolumeRate> {

    public VolumeRateField() {
        this(null);
    }

    public VolumeRateField(String label) {
        this(label, null);
    }

    public VolumeRateField(String label, String unit) {
        this(label, 6, unit);
    }

    public VolumeRateField(int decimals) {
        this(null, decimals);
    }

    public VolumeRateField(int width, int decimals) {
        this(null, width, decimals);
    }

    public VolumeRateField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public VolumeRateField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public VolumeRateField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public VolumeRateField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public VolumeRateField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public VolumeRateField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, VolumeRate.class));
    }

    public VolumeRateField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, VolumeRate.class, unit);
    }
}
