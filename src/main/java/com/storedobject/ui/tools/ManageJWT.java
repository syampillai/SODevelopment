package com.storedobject.ui.tools;

import com.storedobject.core.AutoLogin;
import com.storedobject.core.SystemUser;
import com.storedobject.core.TransactionManager;
import com.storedobject.ui.ELabelField;
import com.storedobject.vaadin.*;

public class ManageJWT extends AbstractUserForm {

    private final RadioChoiceField selection = new RadioChoiceField("Select", new String[] {
            "Generate",
            "Re-issue"
    });

    public ManageJWT() {
        super("Manage JWT", "Ok");
        addField(selection);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(!getTransactionManager().getUser().isSS()) {
            message("Not allowed, you don't belong to system security group!");
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected String action() {
        return "jwt";
    }

    @Override
    protected boolean processUser(SystemUser user) {
        TransactionManager tm = getTransactionManager();
        boolean gen = selection.getValue() == 0;
        try {
            AutoLogin login = AutoLogin.listLogin4JWT(user).findFirst();
            if (gen) {
                if (login == null) {
                    login = AutoLogin.createLogin4JWT(tm, user, null, true);
                }
                ViewJWT viewJWT = new ViewJWT();
                viewJWT.user.clearContent().append(user.toDisplay()).update();
                viewJWT.clientID.setValue(login.getVia());
                viewJWT.clientSecret.setValue(login.generateJWT());
                viewJWT.execute();
            } else {
                if (login == null) {
                    AutoLogin.createLogin4JWT(tm, user, null, true);
                    message("JWT issued successfully!");
                } else {
                    login.reissueJWT(tm);
                    message("JWT re-issued successfully!");
                }
            }
        } catch (Throwable e) {
            error(e);
        }
        return false;
    }

    private static class ViewJWT extends DataForm {

        private final ELabelField user = new ELabelField("User");
        private final TextField clientID = new TextField("Client ID");
        private final TextArea clientSecret = new TextArea("Client Secret");

        public ViewJWT() {
            super("View JWT");
            addField(user);
            addField(clientID);
            addField(clientSecret);
            setFieldReadOnly(clientID, clientSecret);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            cancel.setVisible(false);
        }

        @Override
        protected boolean process() {
            return true;
        }
    }
}
