package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.report.ObjectList;
import com.storedobject.ui.accounts.AccountField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements Transactional {

    public Test() {
        super("Test");
    }

    @Override
    protected boolean process() {
        message(new Money(12334456.46, "INR").words());
        return false;
    }
}