package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Person;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.DateField;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Person> implements CloseableView {

    public Test() {
        super(Person.class, StringList.create("FirstName", "LastName", "DateOfBirth"));
        createComponentColumn("DateOfBirth", this::birthDateField);
        load();
    }

    private Component birthDateField(Person p) {
        DateField df = new DateField();
        df.setValue(p.getDateOfBirth());
        df.addValueChangeListener(e -> p.setDateOfBirth(e.getValue()));
        return df;
    }
}
