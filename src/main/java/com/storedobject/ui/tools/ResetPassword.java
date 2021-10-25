package com.storedobject.ui.tools;

import com.storedobject.core.SystemUser;

public class ResetPassword extends AbstractUserForm {

    public ResetPassword() {
        super("Reset Password", "Reset");
    }

    @Override
    protected boolean processUser(SystemUser user) {
        if(transact(t -> {
            user.setTransaction(t);
            user.resetPassword();
        })) {
            if(user.verifyPasswordUpdate()) {
                message("Password reset successfully");
            } else {
                error("Password reset failed, please contact Technical Support!");
            }
        }
        return true;
    }

    @Override
    protected String action() {
        return "reset";
    }
}
