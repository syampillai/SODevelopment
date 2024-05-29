package com.storedobject.ui.support;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.AlertHandler;
import com.storedobjects.support.Issue;

public class SupportAlertHandler implements AlertHandler {

    @Override
    public void handleAlert(StoredObject so) {
        if(so instanceof Issue issue) {
            new SupportSystem(issue.getType()).handleAlert(issue);
        }
    }
}
