package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.FormLayout;
import com.storedobject.vaadin.util.HasTextValue;
import com.vaadin.flow.component.textfield.TextField;

public class ClassNameField extends CustomTextField<String> {

    public ClassNameField() {
        this(null);
    }

    public ClassNameField(String label) {
        this(label, null);
    }

    public ClassNameField(String label, String value) {
        super("");
        setValue(value);
        setLabel(label);
    }

    @Override
    protected void customizeTextField(HasTextValue textField) {
        super.customizeTextField(textField);
        textField.setMinWidth("30em");
    }

    @Override
    protected String getModelValue(String value) {
        if(value == null) {
            setInvalid(true);
            return "";
        }
        String s = ApplicationServer.guessClass(value);
        if(s != null) {
            setInvalid(false);
            return s;
        }
        setInvalid(true);
        return value;
    }

    public Class<? extends StoredObject> getObjectClass() {
        if(isInvalid()) {
            return null;
        }
        return getObjectClass(getValue());
    }

    @SuppressWarnings("unchecked")
    private Class<? extends StoredObject> getObjectClass(String className) {
        if(className == null || className.isEmpty()) {
            return null;
        }
        try {
            Class<?> c = JavaClassLoader.getLogic(className);
            if(StoredObject.class.isAssignableFrom(c)) {
                return (Class<? extends StoredObject>)c;
            }
        } catch(Exception ignored) {
        }
        return null;
    }

    public int getObjectFamily(String className) {
        Class<? extends StoredObject> c = getObjectClass(className);
        return c == null ? 0 : StoredObjectUtility.family(c);
    }

    public String getObjectClassName(int family) {
        ClassAttribute<?> ca = StoredObjectUtility.classAttribute(family);
        return ca == null ? "" : ca.getObjectClass().getName();
    }

    public String getObjectClassName() {
        return isInvalid() ? null : getValue();
    }
}
