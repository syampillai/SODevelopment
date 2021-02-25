package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements Transactional {

    public Test() {
        super("Test", false);
    }

    @Override
    protected boolean process() {
        return false;
    }
}
