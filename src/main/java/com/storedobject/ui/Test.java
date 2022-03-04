package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    private final PhoneField phoneField = new PhoneField("Phone");

    public Test() {
        super("Chart Example 2");
        addField(phoneField);
        setRequired(phoneField);
    }

    @Override
    protected boolean process() {
        message(phoneField.getValue());
        return false;
    }
}
