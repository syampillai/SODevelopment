package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Volume;
import com.storedobject.ui.util.AbstractQuantityField;

public class VolumeField extends AbstractQuantityField<Volume> {

    public VolumeField() {
        this(null);
    }

    public VolumeField(String label) {
        this(label, null);
    }

    public VolumeField(String label, String unit) {
        this(label, 6, unit);
    }

    public VolumeField(int decimals) {
        this(null, decimals);
    }

    public VolumeField(int width, int decimals) {
        this(null, width, decimals);
    }

    public VolumeField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public VolumeField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public VolumeField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public VolumeField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public VolumeField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public VolumeField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Volume.class));
    }

    public VolumeField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Volume.class, unit);
    }
}
