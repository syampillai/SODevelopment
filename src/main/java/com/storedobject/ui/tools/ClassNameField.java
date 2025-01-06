package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.util.HasTextValue;

public class ClassNameField extends CustomTextField<String> {

    private Class<? extends StoredObject> objectClass;

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
        String className = getValue();
        if(objectClass != null && objectClass.getName().equals(className)) {
            return objectClass;
        }
        objectClass = getObjectClass(className);
        return objectClass;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends StoredObject> getObjectClass(String className) {
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

    public static int getObjectFamily(String className) {
        Class<? extends StoredObject> c = getObjectClass(className);
        return c == null ? 0 : StoredObjectUtility.family(c);
    }

    public static String getObjectClassName(int family) {
        ClassAttribute<?> ca = StoredObjectUtility.classAttribute(family);
        return ca == null ? "" : ca.getObjectClass().getName();
    }

    public String getObjectClassName() {
        return isInvalid() ? null : getValue();
    }
}
