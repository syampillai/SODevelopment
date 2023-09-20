package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    QuantityField qf;
    WeightField wf;
    VolumeField vf;

    public Test() {
        super("Test");
        addField(qf = new QuantityField("Quantity"));
        addField(wf = new WeightField("Weight"));
        addField(vf = new VolumeField("Volume"));
    }

    @Override
    protected boolean process() {
        message("Quantity = " + qf.getValue());
        message("Weight = " + wf.getValue());
        message("Volume = " + vf.getValue());
        return false;
    }
}