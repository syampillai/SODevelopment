package com.storedobject.core;

public class PersonalDocument extends Document<Person> {

    public PersonalDocument() {
    }

    public static void columns(Columns columns) {
    }

    @Override
    protected Class<Person> getOwnerClass() {
        return Person.class;
    }
}
