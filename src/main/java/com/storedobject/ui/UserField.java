package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.SystemUser;

import java.util.List;

public class UserField extends ObjectField<SystemUser> {

    public UserField(Class<SystemUser> objectClass) {
        super(objectClass);
        setup();
    }

    public UserField(Type type) {
        super(SystemUser.class, type);
        setup();
    }

    public UserField(String label, Class<SystemUser> objectClass) {
        super(label, SystemUser.class);
        setup();
    }

    public UserField(String label, Class<SystemUser> objectClass, Type type) {
        super(label, SystemUser.class, type);
        setup();
    }

    public UserField(List<SystemUser> list) {
        super(list);
        setup();
    }

    public UserField(Iterable<SystemUser> list) {
        super(list);
        setup();
    }

    public UserField(String label, Iterable<SystemUser> list) {
        super(label, SystemUser.class, list(list), false);
        setup();
    }

    public UserField(String label, List<SystemUser> list) {
        super(label, SystemUser.class, list, false);
        setup();
    }

    public UserField(ObjectInput<SystemUser> field) {
        super(field);
        setup();
    }

    public UserField(String label, ObjectInput<SystemUser> field) {
        super(label, field);
        setup();
    }

    private void setup() {
        setItemLabelGenerator(SystemUser::getName);
        var field = getField();
        if(field instanceof AbstractObjectField<SystemUser> of) {
            StringList columns = StringList.create("Person.FirstName as First Name", "Person.LastName as Last Name");
            of.setBrowseColumns(columns);
            of.setSearchColumns(columns);
        }
    }
}
