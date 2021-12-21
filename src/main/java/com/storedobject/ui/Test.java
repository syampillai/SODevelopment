package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.CloseableView;

public class Test extends ListEditor<Person> implements CloseableView {

    public Test() {
        super(Person.class);
        StoredObject.list(Person.class).forEach(this::append);
    }
}
