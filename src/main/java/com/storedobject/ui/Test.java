package com.storedobject.ui;

import com.storedobject.core.TransactionManager;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.HomeView;

public class Test extends ActionForm implements HomeView, PostLogin {

    public Test() {
        super("No Access",
                "Access restricted!",
                () -> Application.get().close(),
                () -> Application.get().close());
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        cancel.setVisible(false);
        ok.setText("Ok");
    }

    @Override
    public boolean canLogin(TransactionManager tm) {
        return false;
    }

    @Override
    public void informUser() {
        execute();
    }
}
