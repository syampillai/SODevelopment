package com.storedobject.ui;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectInput;
import com.storedobject.vaadin.HasElement;
import com.vaadin.flow.component.HasValue;

/**
 * Fields that can input {@link Id} values.
 *
 * @param <T> Type of objects for which {@link Id} values can be inputted.
 * @author Syam
 */
public interface IdInput<T extends StoredObject> extends AbstractObjectInput<T> {

    /**
     * Get the value.
     *
     * @return Value.
     */
    Id getValue();

    /**
     * Set the value.
     *
     * @param id Value to set.
     */
    void setValue(Id id);

    /**
     * Set the value for the given object. The default implementation tries to cache the object.
     *
     * @param object Object for which value needs to be set.
     */
    default void setValue(T object) {
        if(object == null) {
            setValue((Id)null);
            return;
        }
        setCached(object);
        setValue(object.getId());
    }

    /**
     * Get the current object value.
     *
     * @return Object value.
     */
    @Override
    default T getObject() {
        return getObject(getValue());
    }

    /**
     * Get the current value. (Same as {@link #getValue()}.
     *
     * @return Value.
     */
    @Override
    default Id getObjectId() {
        return getValue();
    }

    /**
     * Set a raw object as the value. If the object is not convertible to the supported type,
     * it will set <code>null</code> value.
     *
     * @param object Object to set.
     */
    @Override
    default void setObject(StoredObject object) {
        T v = convert(object);
        if(v != null) {
            setCached(v);
        }
        setValue(v == null ? null : v.getId());
    }
}
