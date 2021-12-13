package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Person> {

    public Test() {
        super(Person.class);
        load();
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                new Button("Filter", e -> myLoad())
        );
    }

    private void myLoad() {
        setViewFilter(p -> p.getFirstName().startsWith("S"), true);
    }
}
