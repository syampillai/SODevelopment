package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectLoadFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.DataLoadedListener;
import com.vaadin.flow.shared.Registration;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ObjectLoader<T extends StoredObject> extends com.storedobject.core.ObjectLoader<T> {

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
        setOrderBy(orderBy, false);
    }

    @Override
    default void setOrderBy(String orderBy, boolean load) {
        getObjectLoader().setOrderBy(orderBy, load);
    }

    default void setMaster(StoredObject master) {
        setMaster(master, true);
    }

    @Override
    default void setMaster(StoredObject master, boolean load) {
        getObjectLoader().setMaster(master, load);
    }

    @Override
    default StoredObject getMaster() {
        return getObjectLoader().getMaster();
    }

    default void setLinkType(int linkType) {
        setLinkType(linkType, false);
    }

    @Override
    default void setLinkType(int linkType, boolean load) {
        getObjectLoader().setLinkType(linkType, load);
    }

    @Override
    default int getLinkType() {
        return getObjectLoader().getLinkType();
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
    default void load(ObjectIterator<T> objects) {
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

    /**
     * Clear the entries and refresh it.
     * @param refresh Whether to refresh or not.
     * @deprecated Just use {{@link #clear()}} only, "refresh" flag is not more required.
     */
    @Deprecated
    default void clear(boolean refresh) {
        clear();
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

    @Override
    default void setLoadFilter(Predicate<T> loadFilter) {
        getObjectLoader().setLoadFilter(loadFilter);
    }

    default ObjectLoader<T> getObjectLoader() {
        return (ObjectLoader<T>) com.storedobject.core.ObjectLoader.super.getObjectLoader();
    }
}
