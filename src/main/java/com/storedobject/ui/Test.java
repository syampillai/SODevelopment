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
        tf = new TokensField<>("Persons", StoredObject.list(Person.class).toList());
        addField(tf);
        tf.setPlaceholder("Select persons");
        setRequired(tf);
        tf.setItemLabelGenerator(Person::getFirstName);
        add(new Button("Test", e -> tf.setReadOnly(!tf.isReadOnly())));
    }

    @Override
    protected boolean process() {
        message(tf.getValue());
        message(tf.isEmpty());
        return false;
    }
}