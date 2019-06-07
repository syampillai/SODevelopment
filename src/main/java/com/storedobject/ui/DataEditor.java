package com.storedobject.ui;

import java.util.TreeSet;
import java.util.function.Consumer;

public class DataEditor<T> extends com.storedobject.vaadin.DataEditor<T> implements Transactional {

    private TreeSet<String> setNotAllowed = new TreeSet<>();

    public DataEditor(Class<T> objectClass) {
        super(objectClass);
    }

    public DataEditor(Class<T> objectClass, String caption) {
        super(objectClass, caption);
    }

    public DataEditor(Class<T> objectClass, String labelSave, String labelCancel) {
        super(objectClass, labelSave, labelCancel);
    }

    public DataEditor(Class<T> objectClass, String caption, String labelSave, String labelCancel) {
        super(objectClass, caption, labelSave, labelCancel);
    }

    public DataEditor(Class<T> objectClass, Consumer<T> saveAction) {
        super(objectClass, saveAction);
    }

    public DataEditor(Class<T> objectClass, String caption, Consumer<T> saveAction) {
        super(objectClass, caption, saveAction);
    }

    public DataEditor(Class<T> objectClass, String labelSave, String labelCancel, Consumer<T> saveAction) {
        super(objectClass, labelSave, labelCancel, saveAction);
    }

    public DataEditor(Class<T> objectClass, String caption, String labelSave, String labelCancel, Consumer<T> saveAction) {
        super(objectClass, caption, labelSave, labelCancel, saveAction);
    }

    public DataEditor(Class<T> objectClass, Consumer<T> saveAction, Consumer<T> cancelAction) {
        super(objectClass, saveAction, cancelAction);
    }

    public DataEditor(Class<T> objectClass, String caption, Consumer<T> saveAction, Consumer<T> cancelAction) {
        super(objectClass, caption, saveAction, cancelAction);
    }

    public DataEditor(Class<T> objectClass, String labelSave, String labelCancel, Consumer<T> saveAction, Consumer<T> cancelAction) {
        super(objectClass, labelSave, labelCancel, saveAction, cancelAction);
    }

    public DataEditor(Class<T> objectClass, String caption, String labelSave, String labelCancel, Consumer<T> saveAction, Consumer<T> cancelAction) {
        super(objectClass, caption, labelSave, labelCancel, saveAction, cancelAction);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        return (Application)super.getApplication();
    }
}
