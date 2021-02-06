package com.storedobject.ui;

import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    private final ComboField<String> productField = new ComboField<>("Product", new String[] { "CD", "SB", "TD"});
    private final ComboField<String> currencyField = new ComboField<>("Currency", new String[] { });

    public Test() {
        super("Test");
        addField(productField, currencyField);
        productField.addValueChangeListener(e -> productChanged(e.getValue()));
        productChanged("CD");
    }

    private void productChanged(String p) {
        switch(p) {
            case "CD":
                currencyField.setItems("AED", "USD", "EUR");
                break;
            case "SB":
                currencyField.setItems("AED");
                break;
            case "TD":
                currencyField.setItems("AED", "USD", "EUR", "JPY");
                break;
        }
    }

    @Override
    protected boolean process() {
        return false;
    }
}
