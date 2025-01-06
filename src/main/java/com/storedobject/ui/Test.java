package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements Transactional {

    private final WeightOrVolumeField field = new WeightOrVolumeField("Test");

    public Test() {
        super("Test");
        addField(field);
        field.addValueChangeListener(e -> message("Charged from " + e.getOldValue() + " to " + e.getValue()));
    }

    @Override
    protected boolean process() {
        warning( field.getValue());
        return false;
    }
}