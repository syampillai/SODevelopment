package com.storedobject.ui.tools;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public abstract class AbstractUserForm extends DataForm implements Transactional {

    private final ObjectField<SystemUser> user;

    public AbstractUserForm(String caption, String okLabel) {
        super(caption, okLabel, "Cancel");
        ObjectGetField<SystemUser> field = new ObjectGetField<>(SystemUser.class);
        FilterProvider filter = Application.getUserVisibility(action());
        if(filter != null) {
            field.setFilter(filter);
        }
        addField(user = new ObjectField<>("Login", field));
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

    protected abstract String action();
}
