package com.storedobject.ui;

import com.storedobject.core.Count;
import com.storedobject.core.Distance;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class Test extends DataForm {

    private final DistanceField qField;

    public Test() {
        super("Test");
        MoneyField mf = new MoneyField("Amount");
        mf.setAllowedCurrencies("USD", "PKR");
        addField(mf);
        qField = new DistanceField("Distance");
        qField.addValueChangeListener(e -> message("Changed to: " + e.getValue()));
        addField(qField, new TextField("Test"));
        setRequired(qField);
        qField.setValue(new Distance(0, "cm"));
    }

    @Override
    protected boolean process() {
        message(qField.getValue());
        return false;
    }
}
