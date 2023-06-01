package com.storedobject.ui.tools;

import com.storedobject.core.SystemUser;

public class LockUser extends AbstractUserForm {

    public LockUser() {
        super("Lock User", "Proceed");
    }

    @Override
    protected boolean processUser(SystemUser user) {
        if((user.getStatus() & 1) == 1) {
            warning(user + " - Access is already in the locked state!");
            return false;
        }
        try {
            user.lock(getTransactionManager());
            message(user + " - Access locked successfully");
        } catch(Exception e) {
            error(e);
        }
        return true;
    }

    @Override
    protected String actionWarning(SystemUser su) {
        if((su.getStatus() & 2) == 2) {
            return "This is an system-level user!";
        }
        if((su.getStatus() & 4) == 4) {
            return "This is a process-level user!";
        }
        return null;
    }

    @Override
    protected String action() {
        return "lock";
    }
}
