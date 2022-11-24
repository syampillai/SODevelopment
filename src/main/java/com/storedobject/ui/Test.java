package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class Test extends DataForm {

    DateField dateField;

    public Test() {
        super("Test");
    }

    @Override
    protected void buildFields() {
        add(dateField = new DateField("Date", DateUtility.today()));
        add(new AddressField("Address"));
        super.buildFields();
    }

    @Override
    protected boolean process() {
        Quantity q1 = Quantity.create(0, MeasurementUnit.get("NO", Count.class));
        Quantity q2 = Quantity.create(12, MeasurementUnit.get("l", Volume.class));
        System.err.println(q1.isZero());
        System.err.println(q1.add(q2));
        return false;
    }
}
