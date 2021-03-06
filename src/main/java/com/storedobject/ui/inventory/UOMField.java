package com.storedobject.ui.inventory;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.MeasurementUnitField;
import com.storedobject.vaadin.TranslatedField;

public class UOMField extends TranslatedField<Quantity, MeasurementUnit> {

    public UOMField() {
        this(null);
    }

    public UOMField(String label) {
        super(new MeasurementUnitField(), (f, u) -> Quantity.create(u), (f, q) -> q.getUnit());
        if(label != null) {
            setLabel(label);
        }
    }
}
