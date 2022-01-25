package com.storedobject.ui;

import com.storedobject.common.Address;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class Test extends DataForm {

    private final AddressField af;

    public Test() {
        super("Test");
        af = new AddressField("Address");
        TextField tf = new TextField("Test");
        addField(af, tf);
    }

    @Override
    protected boolean process() {
        message(af.getAddress());
        return false;
    }
}
