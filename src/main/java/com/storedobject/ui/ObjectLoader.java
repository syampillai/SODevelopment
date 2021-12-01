package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.util.DataLoadedListener;
import com.vaadin.flow.shared.Registration;

import java.util.Objects;
import java.util.stream.Stream;

public interface ObjectLoader<T extends StoredObject> extends com.storedobject.core.ObjectLoader<T>, FilterMethods<T> {

    default Class<T> getObjectClass() {
        return getObjectLoader().getObjectClass();
    }

    default int size() {
        return getObjectCount();
    }

    @Override
    default String getFilterCondition() {
        return getObjectLoader().getFilterCondition();
    }

    @Override
    default String getOrderBy() {
        return getObjectLoader().getOrderBy();
    }

    default void setOrderBy(String orderBy) {
        getObjectLoader().setOrderBy(orderBy);
    }

    @Override
    default boolean isAllowAny() {
        return getObjectLoader().isAllowAny();
    }

    default void load(String condition, String orderBy, boolean any) {
        getObjectLoader().load(condition, orderBy, any);
    }

    @Override
    default void load(int linkType, StoredObject master, String condition, String orderBy, boolean any) {
        getObjectLoader().load(linkType, master, condition, orderBy, any);
    }

    @Override
    default void load(Iterable<Id> idList) {
        getObjectLoader().load(idList);
    }

    @Override
    default void load(ObjectIterator<T> objects) {
        getObjectLoader().load(objects);
    }

    @Override
    default void load(Stream<T> objects) {
        getObjectLoader().load(objects);
    }

    default void reload() {
        load();
    }

    @Override
    default void applyFilter() {
        getObjectLoader().load();
    }

    @Override
    default void applyFilterPredicate() {
        getObjectLoader().applyFilterPredicate();
    }

    default void clear() {
        getObjectLoader().clear();
    }

    default int getObjectCount() {
        return getObjectLoader().getObjectCount();
    }

    default T get(int index) {
        return getObjectLoader().get(index);
    }

    default int indexOf(T object) {
        return getObjectLoader().indexOf(object);
    }

    default Stream<T> streamAll() {
        return getObjectLoader().streamAll();
    }

    default Stream<T> streamFiltered() {
        return getObjectLoader().streamFiltered();
    }

    default int getCacheLevel() {
        return getObjectLoader().getCacheLevel();
    }

    default ObjectLoadFilter<T> getFixedFilter() {
        return getObjectLoader().getFixedFilter();
    }

    /**
     * Set a fixed filter that will be automatically added to any filter that is set later.
     *
     * @param fixedFilterProvider Filter condition to set.
     * @deprecated Please use {@link #setFixedFilter(FilterProvider)} instead.
     */
    @Deprecated
    default void setExtraFilter(FilterProvider fixedFilterProvider) {
        setFixedFilter(fixedFilterProvider);
    }

    /**
     * Set a fixed filter that will be automatically added to any filter that is set later.
     *
     * @param fixedFilterProvider Filter condition to set.
     */
    default void setFixedFilter(FilterProvider fixedFilterProvider) {
        setFixedFilter(fixedFilterProvider, true);
    }

    /**
     * Set a fixed filter that will be automatically added to any filter that is set later.
     *
     * @param fixedFilterProvider Filter condition to set.
     * @param apply Whether to apply immediately or not.
     */
    default void setFixedFilter(FilterProvider fixedFilterProvider, boolean apply) {
        ObjectLoadFilter<T> extraFilter = getObjectLoader().getFixedFilter();
        if(extraFilter.getFilterProvider() == fixedFilterProvider) {
            return;
        }
        extraFilter.setFilterProvider(fixedFilterProvider);
        if(apply) {
            load();
        }
    }

    /**
     * Set a fixed filter that will be automatically added to any filter that is set later.
     *
     * @param fixedFilter Filter condition to set.
     * @deprecated Please use {@link #setFixedFilter(String)} instead.
     */
    @Deprecated
    default void setExtraFilter(String fixedFilter) {
        setFixedFilter(fixedFilter, true);
    }

    /**
     * Set a fixed filter that will be automatically added to any filter that is set later.
     *
     * @param fixedFilter Filter condition to set.
     */
    default void setFixedFilter(String fixedFilter) {
        setFixedFilter(fixedFilter, true);
    }

    /**
     * Set a fixed filter that will be automatically added to any filter that is set later.
     *
     * @param fixedFilter Filter condition to set.
     * @param apply Whether to apply immediately or not.
     */
    default void setFixedFilter(String fixedFilter, boolean apply) {
        ObjectLoadFilter<T> filter = getObjectLoader().getFixedFilter();
        if(Objects.equals(fixedFilter, filter.getCondition())) {
            return;
        }
        filter.setCondition(fixedFilter);
        if(apply) {
            load();
        }
    }

    default Registration addDataLoadedListener(DataLoadedListener listener) {
        return getObjectLoader().addDataLoadedListener(listener);
    }
}
