package com.storedobject.ui.util;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.HasElement;
import com.vaadin.flow.component.HasValue;

public interface IdInput<T extends StoredObject> extends AbstractObjectInput<T> {

    Id getValue();

    void setValue(Id id);

    default void setValue(T object) {
        if(object == null) {
            setValue((Id)null);
            return;
        }
        setCached(object);
        setValue(object.getId());
    }

    @Override
    default T getObject() {
        return getObject(getValue());
    }

    @Override
    default Id getObjectId() {
        return getValue();
    }

    @Override
    default void setObject(StoredObject object) {
        T v = convert(object);
        if(v != null) {
            setCached(v);
        }
        setValue(v == null ? null : v.getId());
    }
}
