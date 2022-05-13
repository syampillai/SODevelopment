package com.storedobject.ui;

import com.storedobject.core.Distance;
import com.storedobject.core.Money;
import com.storedobject.core.Quantity;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    private final MoneyField mf;
    private final QuantityField qf;

    public Test() {
        super("Test");
        mf = new MoneyField("Amount");
        mf.setAllowedCurrencies("PKR");
        addField(mf);
        setRequired(mf);
        add(new Button("Test Money", e -> mf.setValue(new Money(0, "GBP"))));
        qf = new QuantityField("Quantity");
        addField(qf);
        add(new Button("Test Quantity", e -> qf.setValue(new Distance(0, "km"))));
        setRequired(qf);
    }

    @Override
    protected boolean process() {
        message(mf.getValue());
        message(qf.getValue());
        return false;
    }
}