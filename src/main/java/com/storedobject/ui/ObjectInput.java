package com.storedobject.ui;

import com.storedobject.core.ClassAttribute;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.AbstractObjectInput;

/**
 * Fields that can input a given type of {@link StoredObject} value.
 *
 * @param <T> Type of objects that can be inputted.
 * @author Syam
 */
public interface ObjectInput<T extends StoredObject> extends AbstractObjectInput<T> {

    /**
     * Set the value.
     *
     * @param value Value to set.
     */
    void setValue(T value);

    /**
     * Get the current value.
     *
     * @return Value.
     */
    T getValue();

    /**
     * Set the internal label for this field. Internal labels are used by the embedded field within this field.
     *
     * @param label Label.
     */
    void setInternalLabel(String label);

    /**
     * Get the internal label of this field. Internal labels are used by the embedded field within this field.
     *
     * @return Label.
     */
    String getInternalLabel();

    /**
     * Set the value as an {@link Id}. The object representing the {@link Id} value will be set. If the
     * corresponding object value is not compatible, <code>null</code> value will be set.
     *
     * @param id {@link Id} of the object to be set.
     */
    default void setValue(Id id) {
        setValue(getObject(id));
    }

    /**
     * Get the current object. Same as {@link #getValue()}.
     *
     * @return Current object.
     */
    @Override
    default T getObject() {
        return  getValue();
    }

    /**
     * Set the given object as the value.
     *
     * @param object Object to set.
     */
    @Override
    default void setObject(T object) {
        T v = convert(object);
        if(v == null) {
            setValue((T)null);
            return;
        }
        setCached(v);
        setValue(v);
    }

    /**
     * Load allowed values from a list. Once invoked, only this list will be used for showing the allowed objects
     * that can be selected via this field.
     *
     * @param objects Objects to load.
     */
    void load(ObjectIterator<T> objects);

    /**
     * Reload the allowed values by applying newly set filters.
     */
    void reload();

    /**
     * Set one or more (typically more than one) class subtypes that this field should allow.
     *
     * @param classes Classes to allow.
     */
    @SuppressWarnings("unchecked")
    default void setObjectClass(Class<? extends T>... classes) {
        if(isAllowAny()) {
            ObjectBrowser<T> searcher = getSearcher();
            if(searcher != null) {
                StringBuilder s = new StringBuilder();
                for(Class<? extends T> c : classes) {
                    if(s.length() > 0) {
                        s.append(',');
                    }
                    s.append(ClassAttribute.get(c).getFamily());
                }
                String families = "T.T_Family IN(" + s + ")";
                searcher.getDataProvider().getSystemFilter().setFilterProvider(() -> families);
                return;
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Get the searcher for this field. (A searcher is available only in certain implementations).
     *
     * @return Typically, an instance of the {@link ObjectBrowser} that has search capability.
     */
    default ObjectBrowser<T> getSearcher() {
        return null;
    }
}