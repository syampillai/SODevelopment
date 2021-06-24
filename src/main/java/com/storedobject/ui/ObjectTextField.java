package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ObjectText;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.TextField;

public class ObjectTextField<T extends StoredObject> extends CustomField<ObjectText<T>> {

    private TF textField = new TF();
    private ObjectGetField<T> objectField;
    private final Class<T> objectClass;

    public ObjectTextField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectTextField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectTextField(Class<T> objectClass, boolean any) {
        this(null, objectClass, any);
    }

    public ObjectTextField(String label, Class<T> objectClass, boolean any) {
        super(new ObjectText<>());
        if(!ObjectGetField.canCreate(objectClass)) {
            throw new SORuntimeException("Object Text for " + objectClass.getName() + " is not supported!");
        }
        this.objectClass = objectClass;
        objectField = new ObjectGetField<>(objectClass, any);
        add(new ButtonLayout(textField, objectField));
        setLabel(label);
        objectField.setNotFoundTacker(text -> textField.setValue(text));
        objectField.addValueChangeListener(e -> {
            T o = objectField.getValue();
            if(o == null) {
                textField.restore();
            } else {
                textField.setValue("");
            }
            updateValue();
        });
    }

    public final Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        objectField.setVisible(!readOnly || !textField.isVisible());
        objectField.setReadOnly(readOnly);
    }

    @Override
    protected ObjectText<T> generateModelValue() {
        T object = objectField.getValue();
        if(object != null) {
            return new ObjectText<>(object);
        }
        return new ObjectText<>(textField.getValue());
    }

    @Override
    protected void setPresentationValue(ObjectText<T> objectText) {
        textField.setValue(objectText.getText());
        objectField.setValue(objectText.getObject());
    }

    private class TF extends TextField {

        private String saved;

        private TF() {
            setTabIndex(-1);
            super.setReadOnly(true);
        }

        @Override
        public void setValue(String value) {
            if(value == null) {
                value = "";
            }
            if(!value.isEmpty()) {
                saved = value;
            }
            super.setValue(value);
            setVisible(!value.isEmpty());
        }

        @Override
        public void setReadOnly(boolean readOnly) {
        }

        @Override
        public void setVisible(boolean visible) {
            String value = getValue();
            super.setVisible(value != null && !value.isEmpty());
        }

        private void restore() {
            setValue(saved);
        }
    }
}
