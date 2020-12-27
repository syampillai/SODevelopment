package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.WeightOrVolume;
import com.storedobject.ui.util.AbstractQuantityField;

public class WeightOrVolumeField extends AbstractQuantityField<WeightOrVolume> {

    public WeightOrVolumeField() {
        this(null);
    }

    public WeightOrVolumeField(String label) {
        this(label, null);
    }

    public WeightOrVolumeField(String label, String unit) {
        this(label, 6, unit);
    }

    public WeightOrVolumeField(int decimals) {
        this(null, decimals);
    }

    public WeightOrVolumeField(int width, int decimals) {
        this(null, width, decimals);
    }

    public WeightOrVolumeField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public WeightOrVolumeField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public WeightOrVolumeField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public WeightOrVolumeField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public WeightOrVolumeField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public WeightOrVolumeField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, WeightOrVolume.class));
    }

    public WeightOrVolumeField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, WeightOrVolume.class, unit);
    }
}
