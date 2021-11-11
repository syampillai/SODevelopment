package com.storedobject.ui;

import com.storedobject.core.FallbackAuthenticator;
import com.storedobject.core.Id;
import com.storedobject.core.IdentityCheck;
import com.storedobject.core.SystemUser;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm implements IdentityCheck {

    private SystemUser su;

    public Test() {
        super("New Pass");
    }

    @Override
    protected boolean process() {
        close();
        Application.get().forgotPassword(this);
        return true;
    }

    @Override
    public void setUser(SystemUser systemUser) {
        su = systemUser;
    }

    @Override
    public SystemUser getUser() {
        return su;
    }
}