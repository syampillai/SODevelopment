package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Person> {

    public Test() {
        super(Person.class);
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                new Button("Load", e -> myLoad())
        );
    }

    private void myLoad() {
        loadOne(StoredObject.get(Person.class));
    }
}
