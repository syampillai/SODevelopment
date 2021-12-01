package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.IdInput;
import com.storedobject.ui.ObjectInput;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public interface DelegatedIdInput<T extends StoredObject> extends IdInput<T> {

    ObjectInput<T> getObjectInput();

    @Override
    default Class<T> getObjectClass() {
        return getObjectInput().getObjectClass();
    }

    @Override
    default boolean isAllowAny() {
        return getObjectInput().isAllowAny();
    }

    @Override
    default void setFilter(FilterProvider filterProvider) {
        getObjectInput().setFilter(filterProvider);
    }

    @Override
    default void setDetailComponent(Component detailComponent) {
        getObjectInput().setDetailComponent(detailComponent);
    }

    @Override
    default Component getDetailComponent() {
        return getObjectInput().getDetailComponent();
    }

    @Override
    default void setDisplayDetail(Consumer<T> displayDetail) {
        getObjectInput().setDisplayDetail(displayDetail);
    }

    @Override
    default Consumer<T> getDisplayDetail() {
        return getObjectInput().getDisplayDetail();
    }

    @Override
    default void setPrefixFieldControl(boolean searchFieldControl) {
        getObjectInput().setPrefixFieldControl(searchFieldControl);
    }

    @Override
    default T getObject() {
        return getObject(getValue());
    }

    @Override
    default Id getObjectId() {
        return getValue();
    }
}
