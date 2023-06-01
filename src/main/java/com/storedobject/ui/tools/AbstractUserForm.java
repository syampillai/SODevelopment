package com.storedobject.ui.tools;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.*;
import com.storedobject.vaadin.DataForm;

public abstract class AbstractUserForm extends DataForm implements Transactional {

    private final ObjectField<SystemUser> user;
    private final ELabel warning = new ELabel();

    public AbstractUserForm(String caption, String okLabel) {
        super(caption, okLabel, "Cancel");
        ObjectGetField<SystemUser> field = new ObjectGetField<>(SystemUser.class);
        FilterProvider filter = Application.getUserVisibility(action());
        if(filter != null) {
            field.setFilter(filter);
        }
        addField(user = new ObjectField<>("Username", field));
        user.addValueChangeListener(e -> userChanged());
        warning.setVisible(false);
        add(warning);
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

    private void userChanged() {
        SystemUser su = user.getObject();
        if(su == null) {
            warning.setVisible(false);
        } else {
            String aw = actionWarning(su);
            if(aw == null) {
                warning.setVisible(false);
            } else {
                warning.setVisible(true);
                warning.clearContent().append(aw, "red").update();
            }
        }
    }

    protected String actionWarning(SystemUser su) {
        return null;
    }

    protected abstract boolean processUser(SystemUser user);

    protected abstract String action();
}
