package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class Test extends DataForm {

    DateField dateField;

    public Test() {
        super("Inventory Stock Report");
        getApplication().setData(String.class, "Hello World");
    }

    @Override
    protected void buildFields() {
        add(dateField = new DateField("Date", DateUtility.today()));
        super.buildFields();
    }

    @Override
    protected boolean process() {
        System.err.println(getApplication().getData(String.class));
        return false;
    }
}
