package com.storedobject.ui.util;

import com.storedobject.core.Id;
import com.storedobject.core.ObjectGetter;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.FilterMethods;
import com.storedobject.ui.ObjectProvider;
import com.storedobject.vaadin.HasElement;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;

import java.util.function.Consumer;

public interface AbstractObjectInput<T extends StoredObject> extends ObjectProvider<T>, ObjectSetter<T>, ObjectGetter<T>, HasElement, FilterMethods<T> {

    @Override
    Class<T> getObjectClass();

    @Override
    T getObject();

    @Override
    void setObject(T object);

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

    void setDetailComponent(Component detailComponent);

    Component getDetailComponent();

    void setDisplayDetail(Consumer<T> displayDetail);

    Consumer<T> getDisplayDetail();

    void setPrefixFieldControl(boolean prefixFieldControl);

    void setLabel(String label);

    String getLabel();

    void setReadOnly(boolean readOnly);

    void setEnabled(boolean enabled);

    default void setPlaceholder(String placeholder) {
    }

    default void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }

    void focus();
}