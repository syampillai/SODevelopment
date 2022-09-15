package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TokensField;

public class Test extends DataForm {

    private final TokensField<Person> tf;

    public Test() {
        super("Test");
        addField(new WeightField("Weight"), new MoneyField("Amount"));
        tf = new TokensField<>("Persons", StoredObject.list(Person.class).toList());
        addField(tf);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        buttonPanel.add(new Button("Test", e -> tf.setReadOnly(!tf.isReadOnly())));
    }

    @Override
    protected boolean process() {
        return false;
    }
}
