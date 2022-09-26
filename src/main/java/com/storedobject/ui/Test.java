package com.storedobject.ui;

import com.storedobject.common.Country;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TokensField;

public class Test extends DataForm {

    TokensField<Country> x;

    public Test() {
        super("Chart");
        x = new TokensField<>("Hello", c -> c.getShortName() + " " + c.getFlag() + " " + c.getName());
        addField(x);
        x.setValue(Country.list());
        TokensField<Country> tf = new TokensField<>("Countries", Country.list());
        tf.setValue(Country.list());
        addField(tf);
        tf.setItemLabelGenerator(c -> c.getShortName() + " " + c.getFlag() + " " + c.getName());
    }

    @Override
    public int getMinimumContentWidth() {
        return 80;
    }

    @Override
    protected boolean process() {
        System.err.println(x.getValue());
        return false;
    }
}
