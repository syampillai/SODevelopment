package com.storedobject.ui.tools;

import com.storedobject.core.Secret;
import com.storedobject.ui.PasswordField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

public class Obfuscator extends DataForm {

    private final PasswordField passwordField = new PasswordField("Password");
    private final TextField outputField = new TextField("Obfuscated Value");

    public Obfuscator() {
        super("Obfuscate Passwords");
        addField(passwordField, outputField);
        setFieldReadOnly(outputField);
    }

    @Override
    protected boolean process() {
        outputField.setValue(Secret.obfuscate(passwordField.getValue()));
        return false;
    }
}
