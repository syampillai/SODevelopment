package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TokensField;

import java.util.ArrayList;
import java.util.List;

public class Test extends DataForm {

    private final TokensField<String> tf;

    public Test() {
        super("Test");
        List<String> tokens = new ArrayList<>();
        tokens.add("One");
        tokens.add("Two");
        tokens.add("Three");
        tokens.add("Four");
        addField(tf = new TokensField<>("Tokens", tokens));
    }

    @Override
    protected boolean process() {
        message(tf.getValue());
        return false;
    }
}
