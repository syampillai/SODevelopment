package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class Test extends DataForm {

    DateField dateField;

    public Test() {
        super("Inventory Stock Report");
    }

    @Override
    protected void buildFields() {
        add(dateField = new DateField("Date", DateUtility.today()));
        super.buildFields();
    }

    @Override
    protected boolean process() {
        throw new NullPointerException("Hello world!");
    }
}
