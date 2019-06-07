package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Temperature;
import com.storedobject.ui.util.AbstractQuantityField;

public class TemperatureField extends AbstractQuantityField<Temperature> {

    public TemperatureField() {
        this(null);
    }

    public TemperatureField(String label) {
        this(label, null);
    }

    public TemperatureField(String label, String unit) {
        this(label, 6, unit);
    }

    public TemperatureField(int decimals) {
        this(null, decimals);
    }

    public TemperatureField(int width, int decimals) {
        this(null, width, decimals);
    }

    public TemperatureField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    public TemperatureField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    public TemperatureField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    public TemperatureField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    public TemperatureField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    public TemperatureField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Temperature.class));
    }

    public TemperatureField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Temperature.class, unit);
    }
}
