package com.storedobject.ui;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    private MeasurementUnitField unitField;

    public Test() {
        super("Test Percentage Field");
    }

    @Override
    protected void buildFields() {
        add(unitField = new MeasurementUnitField("Measurement Field"));
        super.buildFields();
    }

    @Override
    protected boolean process() {
        MeasurementUnit mu = unitField.getValue();

        add(new MeasurementField<>("Measurement Field", mu.getQuantityClass()));

        return false;
    }
}