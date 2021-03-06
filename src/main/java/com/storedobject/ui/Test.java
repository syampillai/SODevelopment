package com.storedobject.ui;

import com.storedobject.core.Area;
import com.storedobject.core.Temperature;
import com.storedobject.core.Volume;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() {
        super("Test", false);
        MeasurementUnitField muf;
        addField(muf = new MeasurementUnitField("Unit"));
        muf.setValue(Area.defaultUnit);
        MeasurementField<Temperature> tf;
        addField(tf = new MeasurementField<>(new Temperature(26, "F")));
        tf.addValueChangeListener(e -> muf.setValue(Volume.defaultUnit));
    }

    @Override
    protected boolean process() {
        return false;
    }
}
