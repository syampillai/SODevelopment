package com.storedobject.ui;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.Clock;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class Test extends DataForm {

    private final TextField textField = new TextField();
    private final Clock clock = new Clock();

    public Test() {
        super("Test");
        setRequired(textField);
        add(clock);
        add(new Button("Local", (String) null, e -> clock.setUTC(false)));
        add(new Button("AM/PM", (String) null, e -> clock.setAMPM(true)));
    }

    @Override
    protected boolean process() {
        message(textField.getValue());
        return false;
    }
}
