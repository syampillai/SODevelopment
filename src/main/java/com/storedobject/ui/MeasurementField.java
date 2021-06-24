package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.util.AbstractQuantityField;
import com.storedobject.vaadin.TranslatedField;

/**
 * Create a field to accept a specific measurement {@link Quantity}.
 *
 * @author Syam
 */
public class MeasurementField<T extends Quantity> extends TranslatedField<T, T> {

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param quantity Type of quantity field to be created.
     */
    public MeasurementField(T quantity) {
        this(null, quantity);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param quantity Type of quantity field to be created.
     */
    public MeasurementField(String label, T quantity) {
        this(label, quantity,6);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param quantity Type of quantity field to be created.
     * @param decimals Number of decimal places.
     */
    public MeasurementField(T quantity, int decimals) {
        this(null, quantity, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param quantity Type of quantity field to be created.
     * @param decimals Number of decimal places.
     */
    public MeasurementField(String label, T quantity, int decimals) {
        this(label, quantity, 0, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param quantity Type of quantity field to be created.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public MeasurementField(T quantity, int width, int decimals) {
        this(null, quantity, width, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param quantity Type of quantity field to be created.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public MeasurementField(String label, T quantity, int width, int decimals) {
        //noinspection unchecked
        this(label, (Class<T>) quantity.getClass(), width, decimals, quantity.getUnit());
        setValue(quantity);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param quantityClass Type of quantity field to be created.
     */
    public MeasurementField(Class<T> quantityClass) {
        this(null, quantityClass);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param quantityClass Type of quantity field to be created.
     */
    public MeasurementField(String label, Class<T> quantityClass) {
        this(label, quantityClass, 0, 6);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param quantityClass Type of quantity field to be created.
     * @param decimals Number of decimal places.
     */
    public MeasurementField(Class<T> quantityClass, int decimals) {
        this(null, quantityClass, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param quantityClass Type of quantity field to be created.
     * @param decimals Number of decimal places.
     */
    public MeasurementField(String label, Class<T> quantityClass,
                            int decimals) {
        this(label, quantityClass, 0, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param quantityClass Type of quantity field to be created.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public MeasurementField(Class<T> quantityClass, int width, int decimals) {
        this(null, quantityClass, width, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param quantityClass Type of quantity field to be created.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public MeasurementField(String label, Class<T> quantityClass, int width, int decimals) {
        this(label, quantityClass, width, decimals, null);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param unit Default measurement unit of the quantity.
     */
    public MeasurementField(MeasurementUnit unit) {
        this(null, unit);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param unit Default measurement unit of the quantity.
     */
    public MeasurementField(String label, MeasurementUnit unit) {
        this(label, unit, 0, 6);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param unit Default measurement unit of the quantity.
     * @param decimals Number of decimal places.
     */
    public MeasurementField(MeasurementUnit unit, int decimals) {
        this(null, unit, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param unit Default measurement unit of the quantity.
     * @param decimals Number of decimal places.
     */
    public MeasurementField(String label, MeasurementUnit unit, int decimals) {
        this(label, unit, 0, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param unit Default measurement unit of the quantity.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public MeasurementField(MeasurementUnit unit, int width,
                            int decimals) {
        this(null, unit, width, decimals);
    }

    /**
     * Create a field for accepting a specific type of {@link Quantity}.
     *
     * @param label Label.
     * @param unit Default measurement unit of the quantity.
     * @param width Width of the field (including decimal places).
     * @param decimals Number of decimal places.
     */
    public MeasurementField(String label, MeasurementUnit unit, int width, int decimals) {
        //noinspection unchecked
        this(label, (Class<T>) unit.getQuantityClass(), width, decimals, unit);
    }

    private MeasurementField(String label, Class<T> quantityClass, int width, int decimals, MeasurementUnit unit) {
        this(label, createQField(quantityClass, width, decimals, unit));
    }

    private MeasurementField(String label, AbstractQuantityField<T> field) {
        super(field, (f, q) -> q, (f, q) -> q);
        if(label != null) {
            setLabel(label);
        }
    }

    private static <Q extends Quantity> AbstractQuantityField<Q> createQField(Class<Q> quantityClass, int width,
                                                                              int decimals, MeasurementUnit unit) {
        if(unit == null) {
            try {
                unit = quantityClass.getDeclaredConstructor().newInstance().getUnit();
            } catch(Throwable ignored) {
            }
        }
        String name = quantityClass.getName();
        name = "com.storedobject.ui." + name.substring(name.lastIndexOf('.') + 1) + "Field";
        try {
            //noinspection unchecked
            return (AbstractQuantityField<Q>) Class.forName(name).
                    getDeclaredConstructor(int.class, int.class, MeasurementUnit.class).
                    newInstance(width, decimals, unit);
        } catch(Throwable e) {
            throw new SORuntimeException(e);
        }
    }
}
