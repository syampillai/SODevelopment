package com.storedobject.ui;

import com.storedobject.core.Set_Not_Allowed;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.HasValue;

import java.util.TreeSet;
import java.util.function.Consumer;

public class DataEditor<T> extends com.storedobject.vaadin.DataEditor<T> implements Transactional {

    private final TreeSet<String> setNotAllowed = new TreeSet<>();

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
    public boolean isFieldEditable(String fieldName) {
        if(setNotAllowed.contains(fieldName)) {
            T object = getObject();
            return !(object instanceof StoredObject) || ((StoredObject) object).created();
        }
        return super.isFieldEditable(fieldName);
    }

    @Override
    protected boolean handleValueSetError(String fieldName, HasValue<?, ?> field, Object fieldValue, Object objectValue, Throwable error) {
        Throwable cause = error.getCause();
        if(cause == null) {
            cause = error;
        }
        if(cause instanceof Set_Not_Allowed) {
            if(field.isReadOnly()) {
                return false;
            }
            setNotAllowed.add(fieldName);
            field.setReadOnly(true);
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        return super.getApplication();
    }
}
