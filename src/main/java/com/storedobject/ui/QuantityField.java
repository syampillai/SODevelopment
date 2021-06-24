package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.util.AbstractQuantityField;

/**
 * Generic field to accept {@link Quantity} values.
 *
 * @author Syam
 */
public class QuantityField extends AbstractQuantityField<Quantity> {

    /**
     * Constructor.
     */
    public QuantityField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     */
    public QuantityField(String label) {
        this(label, null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param unit Default unit of measurement.
     */
    public QuantityField(String label, String unit) {
        this(label, 6, unit);
    }

    /**
     * Constructor.
     *
     * @param decimals Number of decimal places.
     */
    public QuantityField(int decimals) {
        this(null, decimals);
    }

    /**
     * Constructor.
     *
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public QuantityField(int width, int decimals) {
        this(null, width, decimals);
    }

    /**
     * Constructor.
     *
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     * @param unit Default unit of measurement.
     */
    public QuantityField(int width, int decimals, MeasurementUnit unit) {
        this(null, width, decimals, unit);
    }

    /**
     * Constructor.
     *
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     * @param unit Default unit of measurement.
     */
    public QuantityField(int width, int decimals, String unit) {
        this(null, width, decimals, unit);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param decimals Number of decimal places.
     */
    public QuantityField(String label, int decimals) {
        this(label, 0, decimals, (MeasurementUnit)null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param decimals Number of decimal places.
     * @param unit Default unit of measurement.
     */
    public QuantityField(String label, int decimals, String unit) {
        this(label, 0, decimals, unit);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public QuantityField(String label, int width, int decimals) {
        this(label, width, decimals, (MeasurementUnit)null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     * @param unit Default unit of measurement.
     */
    public QuantityField(String label, int width, int decimals, String unit) {
        this(label, width, decimals, MeasurementUnit.get(unit, Quantity.class));
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     * @param unit Default unit of measurement.
     */
    public QuantityField(String label, int width, int decimals, MeasurementUnit unit) {
        super(label, width, decimals, Quantity.class, unit);
    }
}
