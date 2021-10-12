package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;

public class Test extends ObjectEditor<Person> {

    public Test() {
        super(Person.class);
        setSearchFilter(StoredObject.getNotExistsCondition(SystemUser.class, "Person"));
    }

    public Test(String className) throws Exception {
        this();
    }
}