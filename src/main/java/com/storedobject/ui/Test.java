package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.Rate;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() {
        super("Test");
        RateField rf;
        addField(rf = new RateField("Rate"));
        Rate r = new Rate();
        rf.setValue(r);
    }

    @Override
    protected boolean process() {
        return false;
    }
}