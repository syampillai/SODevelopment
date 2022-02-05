package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.RawSQL;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.FormLayout;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Person> implements CloseableView {

    private final TextField nameField = new TextField("Search Name");
    private final ObjectField<Person> personField = new ObjectField<>("Filter Person", Person.class);

    public Test() {
        super(Person.class);
        setCaption("Test");
        setFilter(() -> "FirstName LIKE '" + personField.getObject().getFirstName().charAt(0) + "%'", false);
    }

    @Override
    public Component createHeader() {
        personField.setFilter(() -> "FirstName LIKE '" + nameField.getValue() + "%'");
        nameField.addValueChangeListener(e -> personField.reload());
        personField.addValueChangeListener(e -> {
           Person p = personField.getObject();
           if(p != null) {
               load();
           } else {
               clear();
           }
        });
        return new FormLayout(nameField, personField);
    }
}
