package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.TokensField;

public class Test extends DataForm {

    public Test() {
        super("Test");TextField tf;
        addField(tf = new TextField("Hello"));
        setRequired(tf);
        //tf.setRequired(true);
        TokensField<Person> pf = new TokensField<>("Person",
                StoredObject.list(Person.class, "FirstName LIKE '%s%'").toList());
        addField(pf);
        pf.setRequired(true);
        //setRequired(pf);
    }

    @Override
    protected boolean process() {
        return true;
    }
}
