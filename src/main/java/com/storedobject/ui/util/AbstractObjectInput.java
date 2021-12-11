package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectProvider;
import com.storedobject.vaadin.HasElement;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;

import java.util.function.Consumer;

/**
 * Methods used by fields for inputting {@link StoredObject}s or {@link Id}s.
 *
 * @param <T> Type of input.
 * @author Syam
 */
public interface AbstractObjectInput<T extends StoredObject>
        extends ObjectProvider<T>, ObjectSetter<T>, ObjectGetter<T>, HasElement, FilterMethods<T> {

    /**
     * Class of the object supported in this input field.
     *
     * @return Class of the supported object.
     */
    @Override
    Class<T> getObjectClass();

    /**
     * Get the object that is currently set.
     *
     * @return Object that is currently set. It could be <code>null</code>.
     */
    @Override
    T getObject();

    /**
     * Set the object.
     *
     * @param object Object to set.
     */
    @Override
    void setObject(T object);

    /**
     * Convert a raw object to the type that is accepted. (This is a helper method).
     *
     * @param object Object to convert.
     * @return Converted object or null if not convertible.
     */
    @SuppressWarnings("unchecked")
    default T convert(StoredObject object) {
        if(object == null) {
            return null;
        }
        if (this.getObjectClass().isAssignableFrom(object.getClass())) {
            if(!isAllowAny() && this.getObjectClass() != object.getClass()) {
                return null;
            } else {
                setCached((T)object);
                return (T) object;
            }
        }
        return null;
    }

    /**
     * Set object for the given {@link Id}.
     *
     * @param objectId {@link Id} of the object to be set.
     */
    @Override
    default void setObject(Id objectId) {
        setObject(getObject(objectId));
    }

    /**
     * Get the object for the given {@link Id}. (This is a helper method.)
     *
     * @param objectId {@link Id} for which object needs to be returned.
     * @return Object retrieved from the database or cache. It returns <code>null</code> if not found.
     */
    default T getObject(Id objectId) {
        if (Id.isNull(objectId)) {
            return null;
        }
        T cache = getCached();
        if(cache != null && cache.getId().equals(objectId)) {
            return cache;
        }
        cache = StoredObject.get(this.getObjectClass(), objectId, this.isAllowAny());
        if(cache != null) {
            setCached(cache);
        }
        return cache;
    }

    /**
     * Cache an object instance. Default implementation doesn't do anything but a field may cache it and re-used
     * for eliminating unwanted database access.
     *
     * @param cached Object to be cached.
     */
    default void setCached(T cached) {
    }

    /**
     * Get the currently cached object.
     *
     * @return Object that is currently cached. It may return <code>null</code>.
     */
    T getCached();

    /**
     * Get the {@link Id} of the given object. (This is a helper method.)
     *
     * @param object Object for which {@link Id} is required.
     * @return {@link Id} of the object.
     */
    default Id getObjectId(T object) {
        return object == null ? null : object.getId();
    }

    /**
     * Get the {@link Id} of the current object.
     *
     * @return {@link Id} or <code>null</code> if no object is currently set.
     */
    @Override
    default Id getObjectId() {
        return getObjectId(getObject());
    }

    /**
     * Whether this field allows any derived object values or not.
     *
     * @return True or false.
     */
    @Override
    default boolean isAllowAny() {
        return false;
    }

    /**
     * Set a component that will display details of the object value.
     *
     * @param detailComponent A component that can display information regarding the object.
     */
    void setDetailComponent(Component detailComponent);

    /**
     * Get the component that is currently displaying details of the object value.
     *
     * @return Component that is displaying the details.
     */
    Component getDetailComponent();

    /**
     * Set a consumer that can display/consume the details of the object value.
     *
     * @param displayDetail A consumer that can accept the object value.
     */
    void setDisplayDetail(Consumer<T> displayDetail);

    /**
     * Get the consumer that is currently consuming details of the object value.
     *
     * @return Consumer that is currently set.
     */
    Consumer<T> getDisplayDetail();

    /**
     * Decide whether "prefix components" needs to be controlled by this field or not. "Prefix components" are
     * parts of the field that display extra information, and it needs to be turned on or off
     * when the status of the field changes via methods such as {@link #setEnabled(boolean)},
     * {@link #setReadOnly(boolean)} etc. In some cases, prefix components may be displayed elsewhere and needs to
     * be controlled outside the field.
     *
     * @param prefixFieldControl True if it needs to be controlled by this field.
     */
    void setPrefixFieldControl(boolean prefixFieldControl);

    /**
     * Set the label for this field.
     *
     * @param label Label to set.
     */
    void setLabel(String label);

    /**
     * Get the label of this field.
     *
     * @return Current label.
     */
    String getLabel();

    /**
     * Make this field read-only.
     *
     * @param readOnly True to set as read-only.
     */
    void setReadOnly(boolean readOnly);

    /**
     * Enable/disable this field.
     *
     * @param enabled True for enabling.
     */
    void setEnabled(boolean enabled);

    /**
     * Set a placeholder value for the field when it is empty.
     *
     * @param placeholder Placeholder to set.
     */
    default void setPlaceholder(String placeholder) {
    }

    /**
     * Set an "item label generator" that returns the string value of the object value of this field. (It is mainly
     * used to display the object).
     *
     * @param itemLabelGenerator Item value generator.
     */
    default void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    /**
     * Focus this field.
     */
    void focus();
}