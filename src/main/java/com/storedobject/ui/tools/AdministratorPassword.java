package com.storedobject.ui.tools;

import com.storedobject.core.Database;
import com.storedobject.ui.Application;
import com.storedobject.ui.PasswordField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

import java.util.function.Consumer;

class AdministratorPassword extends DataForm {

    private boolean verified = false;
    private final PasswordField passwordField = new PasswordField("DB Administrator Password");
    private Consumer<char[]> consumer;

    AdministratorPassword() {
        super("DB Administrator Password");
        passwordField.setMaxLength(30);
        addField(passwordField);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        if(!verify(passwordField.getValue(),true)) return false;
        if(verified) {
            close();
            consume();
            return true;
        }
        warning("Invalid administrator password");
        return false;
    }

    private boolean verify(String p, boolean warn) {
        if(verified) return true;
        try {
            verified = Database.get().validateSecurityPassword(p.toCharArray());
        } catch (Exception e) {
            if(warn) warning(e);
            verified = false;
        }
        return verified;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(!verified) {
            String p = System.getenv("PGPW");
            if(p != null && verify(p, false)) {
                passwordField.setValue(p);
                passwordField.setRevealButtonVisible(false);
            }
        }
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
