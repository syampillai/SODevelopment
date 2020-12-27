package com.storedobject.ui.tools;

import com.storedobject.core.SystemUser;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public abstract class AbstractUserForm extends DataForm implements Transactional {

    private final ObjectField<SystemUser> user;

    public AbstractUserForm(String caption, String okLabel) {
        super(caption, okLabel, "Cancel");
        addField(user = new ObjectField<>("Login", SystemUser.class, ObjectField.Type.GET));
    }

    @Override
    protected final boolean process() {
        SystemUser su = user.getObject();
        if(su == null) {
            warning("Please select the login first");
            return false;
        }
        return processUser(su);
    }

    protected abstract boolean processUser(SystemUser user);
}
