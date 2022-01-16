package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;

import java.util.function.Predicate;

public class Test extends ObjectGrid<Person> {

    public Test() {
        super(Person.class);
        load();
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                new Button("Test1", e -> test1()),
                new Button("Test2", e -> test2())
                );
    }

    private void test1() {
        setLoadFilter(p -> p.getFirstName().startsWith("S"));
        //reload();
    }

    private void test2() {
        setViewFilter(null);
        //reload();
    }
}
