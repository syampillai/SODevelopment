package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() {
        super("Test");
        Quantity q1 = Quantity.create(0, MeasurementUnit.get("°C"));
        Quantity q2 = Quantity.create(0, MeasurementUnit.get("°F"));
        message("q1: " + q1.toString() + " (" + q1.getUnit() + ")" );
        message("q2: " + q2.toString() + " (" + q2.getUnit() + ")" );
        //addField(new MeasurementField<>("Quantity 1", q1));
        MeasurementField<Quantity> mf = new MeasurementField<>("Quantity 1", q2.getUnit());
        addField(mf);
        mf.addValueChangeListener(
                e -> message("Value changed from: " + e.getOldValue() + " to " + e.getValue()));
    }

    @Override
    protected boolean process() {
        return false;
    }
}