package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements IdentityCheck {

    private SystemUser user;

    public Test() {
        super("Test");
        RateField rf;
        addField(rf = new RateField("Rate"));
        Rate r = new Rate();
        rf.setValue(r);
    }

    @Override
    protected boolean process() {
        close();
        Application.get().forgotPassword(this);
        return true;
    }

    @Override
    public void setUser(SystemUser systemUser) {
        this.user = systemUser;
    }

    @Override
    public SystemUser getUser() {
        return user;
    }
}