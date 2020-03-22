package com.storedobject.ui.util;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;

public interface ObjectInput<T extends StoredObject> extends AbstractObjectInput<T> {

    void setValue(T value);

    T getValue();

    void setInternalLabel(String label);

    String getInternalLabel();

    default void setValue(Id id) {
        setValue(getObject(id));
    }

    @Override
    default T getObject() {
        return  getValue();
    }

    @Override
    default void setObject(T object) {
    }

    default void setPlaceholder(String placeholder) {
    }

    default void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
    }
}