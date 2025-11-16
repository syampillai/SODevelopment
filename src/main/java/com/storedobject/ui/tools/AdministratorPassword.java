package com.storedobject.ui.tools;

import com.storedobject.core.Database;
import com.storedobject.ui.Application;
import com.storedobject.ui.PasswordField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

import java.util.function.Consumer;

class AdministratorPassword extends DataForm {

    private boolean verified = false;
    private final PasswordField passwordField = new PasswordField("Administrator password");
    private Consumer<char[]> consumer;

    AdministratorPassword() {
        super("Administrator Password");
        passwordField.setMaxLength(30);
        addField(passwordField);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        try {
            verified = Database.get().validateSecurityPassword(passwordField.getValue().toCharArray());
        } catch (Exception e) {
            warning(e);
            return false;
        }
        if(verified) {
            close();
            consume();
            return true;
        }
        warning("Invalid administrator password");
        return false;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(verified) {
            consume();
        } else {
            super.execute(parent, doNotLock);
        }
    }

    void execute(Consumer<char[]> consumer) {
        this.consumer = consumer;
        execute();
    }

    private void consume() {
        if(consumer != null) {
            Application.get().access(() -> consumer.accept(password()));
        }
    }

    char[] password() {
        return verified ? passwordField.getValue().toCharArray() : null;
    }
}
