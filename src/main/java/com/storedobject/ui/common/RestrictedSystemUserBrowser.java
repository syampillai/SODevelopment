package com.storedobject.ui.common;

import com.storedobject.core.SystemUser;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;

public class RestrictedSystemUserBrowser extends ObjectBrowser<SystemUser> {
    
    public RestrictedSystemUserBrowser() {
        super(SystemUser.class);
    }

    public RestrictedSystemUserBrowser(String className) {
        this();
    }

    @Override
    protected ObjectEditor<SystemUser> createObjectEditor() {
        return new SystemUserEditor(0, null, true);
    }
}
