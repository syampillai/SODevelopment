package com.storedobject.ui.util;

import com.storedobject.common.LogicalOperator;
import com.storedobject.core.ObjectToString;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ViewFilterSupport<T> {

    DataProvider<?, ?> getDataProvider();

    Class<? extends T> getObjectClass();

    default ViewFilter<T> getViewFilter() {
        DataProvider<?, ?> dp = getDataProvider();
        if(dp != this && dp instanceof ViewFilterSupport vs) {
            //noinspection unchecked
            return vs.getViewFilter();
        }
        return null;
    }

    default void filterView(String filters) {
        DataProvider<?, ?> dp = getDataProvider();
        if(dp != this && dp instanceof ViewFilterSupport vs) {
            vs.filterView(filters);
        }
    }

    default void configureMatch(BiFunction<T, String[], Boolean> matchFunction) {
        if(getViewFilter().setMatcher(matchFunction)) {
            getDataProvider().refreshAll();
        }
    }

    default void configure(Function<T, String> toString) {
        if(getViewFilter().setObjectConverter(toString)) {
            getDataProvider().refreshAll();
        }
    }

    @SuppressWarnings("unchecked")
    default <O extends StoredObject> void configure(String... attributes) {
        if(!(StoredObject.class.isAssignableFrom(getObjectClass())) || attributes == null || attributes.length == 0) {
            configure((Function<T, String>)null);
            return;
        }
        @SuppressWarnings("unchecked") Class<O> objClass = (Class<O>) getObjectClass();
        configure((Function<T, String>) ObjectToString.create(objClass, attributes));
    }

    default void configure(LogicalOperator logicalOperator) {
        if(logicalOperator != null) {
            getViewFilter().setFilterLogic(logicalOperator);
        }
    }
}
