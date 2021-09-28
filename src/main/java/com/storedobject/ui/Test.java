package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() {
        super("Test");
    }

    @Override
    protected boolean process() {
        return true;
    }
}
