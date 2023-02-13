package com.storedobject.ui.inventory;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.MeasurementUnitField;
import com.storedobject.vaadin.TranslatedField;

/**
 * A field for inputting {@link MeasurementUnit}s. However, the corresponding {@link Quantity}
 * is returned as the value of the field. This is useful when the value has to be stored in the database.
 *
 * @author Syam
 */
public class UOMField extends TranslatedField<Quantity, MeasurementUnit> {

    /**
     * Constructor. By default, only units corresponding to real quantities are allowed.
     */
    public UOMField() {
        this(null);
    }

    /**
     * Constructor. By default, only units corresponding to real quantities are allowed.
     *
     * @param label Label.
     */
    public UOMField(String label) {
        this(label, false);
    }

    /**
     * Constructor.
     *
     * @param all Whether all types should be allowed or not. If <code>false</code> is passed,
     *            units corresponding to only real quantities are allowed.
     */
    public UOMField(boolean all) {
        this(null, all);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param all Whether all types should be allowed or not. If <code>false</code> is passed,
     *            units corresponding to only real quantities are allowed.
     */
    public UOMField(String label, boolean all) {
        super(new MeasurementUnitField(all), (f, u) -> Quantity.create(u), (f, q) -> q.getUnit());
        if(label != null) {
            setLabel(label);
        }
    }

    @Override
    protected boolean valueEquals(Quantity value1, Quantity value2) {
        return value1.equals(value2) && value1.getUnit() == value2.getUnit();
    }

    @Override
    public boolean isEmpty() {
        return getValue() == null;
    }
}
