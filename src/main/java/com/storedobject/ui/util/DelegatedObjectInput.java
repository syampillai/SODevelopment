package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectInput;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface DelegatedObjectInput<T extends StoredObject> extends ObjectInput<T> {
    
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
    default void filter(Predicate<T> filter) {
        getObjectInput().filter(filter);
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
}
