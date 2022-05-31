package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

public class Test extends DataForm {

    private final DateField dateField = new DateField("Date");

    public Test() {
        super("Test");
        addField(dateField);
        ELabel test = new ELabel("Hello", "red");
        add(test);
        test.appendHTML("<style></style>");
    }

    @Override
    protected boolean process() {
        message("Value: " + DateUtility.format(dateField.getValue()));
        return false;
    }
}
