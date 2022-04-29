package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Person> implements CloseableView {

    public Test() {
        super(Person.class);
        load();
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(getConfigureButton(), new Button("Print", e -> print()));
    }

    @Override
    public String getColumnCaption(String columnName) {
        return super.getColumnCaption(columnName);
    }

    private void print() {
        //noinspection resource
        new ObjectGridReport<>(this).execute();
    }
}
