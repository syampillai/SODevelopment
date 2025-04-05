package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.inventory.UOMField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    UOMField uomField = new UOMField("UOM");

    public Test() {
        super("Test");
        addField(uomField);
    }

    @Override
    protected boolean process() {
        message(uomField.getValue());
        message(MeasurementUnit.get("USG"));
        uomField.setValue(Quantity.create("(0.00,\"gal(US)\")"));
        return false;
    }
}