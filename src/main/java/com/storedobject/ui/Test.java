package com.storedobject.ui;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.inventory.LocationField;
import com.storedobject.vaadin.DataForm;

public class Test extends ObjectListEditor<Person> {

    public Test() {
        super(Person.class);
        Person p;
        for(int i = 0; i < 5; i++) {
            p = new Person("Name " + i, "", "Test " + 0);
            itemInserted(p);
        }
    }

    @Override
    protected ObjectEditor<Person> createObjectEditor() {
        ObjectEditor<Person> pe = ObjectEditor.create(Person.class);
        pe.setCaption("Hello");
        return pe;
    }
}
