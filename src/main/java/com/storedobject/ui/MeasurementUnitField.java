package com.storedobject.ui;

import com.storedobject.core.Count;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.vaadin.FreeFormatField;

public class MeasurementUnitField extends FreeFormatField<MeasurementUnit> {

    /**
     * Constructor.
     */
    protected MeasurementUnitField() {
        this(null);
    }

    /**
     * Constructor.
     */
    protected MeasurementUnitField(String label) {
        super(label, Count.defaultUnit);
    }

    @Override
    protected MeasurementUnit getModelValue(String string) {
        MeasurementUnit mu;
        try {
            mu = Quantity.create("1 " + string).getUnit();
        } catch(Throwable e) {
            mu = getValue();
        }
        return mu == null ? getEmptyValue() : mu;
    }
}
