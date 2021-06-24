package com.storedobject.ui.tools;

import com.storedobject.core.SystemUser;

public class UnlockUser extends AbstractUserForm {

    public UnlockUser() {
        super("Unlock User", "Proceed");
    }

    @Override
    protected boolean processUser(SystemUser user) {
        if(user.getStatus() == 0) {
            warning(user + " is not locked currently!");
            return false;
        }
        try {
            user.unlock(getTransactionManager());
            message(user + " unlocked successfully");
        } catch(Exception e) {
            error(e);
        }
        return true;
    }
}
