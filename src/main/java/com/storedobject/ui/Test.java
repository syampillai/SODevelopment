package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.DataForm;

public class Test implements FallbackAuthenticator {

    @Override
    public boolean login(Id id, char[] chars) throws Exception {
        System.err.println("Id: " + id);
        if(id.toString().equals("87726")) {
            System.err.println("Here");
            return true;
        }
        Thread.dumpStack();
        return false;
    }

    @Override
    public boolean login(Id passwordOwner, char[] password, int authenticatorCode) throws Exception {
        if(authenticatorCode == -1 && password == null) {
            return true;
        }
        return login(passwordOwner, password);
    }

    @Override
    public boolean exists(Id passwordOwner) {
        return true;
    }
}