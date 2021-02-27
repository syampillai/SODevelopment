package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.FreeFormatField;

public class Test extends DataForm implements Transactional {

    public Test() {
        super("Test", false);
        addField(new F("Person 1"), new F("Person 2"));
    }

    @Override
    protected boolean process() {
        return false;
    }

    public static class F extends FreeFormatField<Person> {

        protected F(String label) {
            super(label, null);
        }

        @Override
        protected Person getModelValue(String string) {
            return Person.get(string);
        }
    }
}
