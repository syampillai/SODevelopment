package com.storedobject.ui;

import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioChoiceField;

public class Test extends DataForm {

    private final PhoneField pf;
    public Test() {
        super("Test");
        addField(new QuantityField("Quantity"));
        addField(pf = new PhoneField("Phone"));
        RadioChoiceField cf = new RadioChoiceField("Radio", new String[] { "One", "Two", "Three" });
        cf.setVertical();
        addField(cf);
    }

    @Override
    protected boolean process() {
        message(pf.getValue());
        return false;
    }
}
