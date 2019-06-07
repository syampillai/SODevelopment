package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.vaadin.HasElement;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface AbstractObjectInput<T extends StoredObject> extends ObjectProvider<T>, ObjectSetter, ObjectGetter, HasElement {

    @Override
    Class<T> getObjectClass();

    @Override
    T getObject();

    @Override
    void setObject(StoredObject object);

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

    @Override
    default void setObject(Id objectId) {
        setObject(getObject(objectId));
    }

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

    default void setCached(T cached) {
    }

    T getCached();

    default Id getObjectId(T object) {
        return object == null ? null : object.getId();
    }

    @Override
    default Id getObjectId() {
        return getObjectId(getObject());
    }

    @Override
    default boolean isAllowAny() {
        return false;
    }

    void setFilterProvider(FilterProvider filterProvider);

    void setFilter(Predicate<T> filter);

    void setDetailComponent(Component detailComponent);

    Component getDetailComponent();

    void setDisplayDetail(Consumer<T> displayDetail);

    Consumer<T> getDisplayDetail();

    void setPrefixFieldControl(boolean prefixFieldControl);

    void setLabel(String label);

    String getLabel();

    void setReadOnly(boolean readOnly);

    void setEnabled(boolean enabled);
}