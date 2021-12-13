package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectLoadFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.DataLoadedListener;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ObjectLoader<T extends StoredObject> extends com.storedobject.core.ObjectLoader<T> {

    /**
     * Clear the entries and refresh it.
     * @param refresh Whether to refresh or not.
     * @deprecated Just use {{@link #clear()}} only, "refresh" flag is not more required.
     */
    @Deprecated
    default void clear(boolean refresh) {
        clear();
    }

    @Override
    default int size() {
        return delegate().size();
    }

    default int getObjectCount() {
        return size();
    }

    default T get(int index) {
        return delegate().get(index);
    }

    default int indexOf(T object) {
        return delegate().indexOf(object);
    }

    default Stream<T> streamAll() {
        return delegate().streamAll();
    }

    default Stream<T> streamFiltered() {
        return delegate().streamFiltered();
    }

    default int getCacheLevel() {
        return delegate().getCacheLevel();
    }

    default ObjectLoadFilter<T> getFixedFilter() {
        return delegate().getFixedFilter();
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
        ObjectLoadFilter<T> filter = getFixedFilter();
        if(filter.getFilterProvider() == fixedFilterProvider) {
            return;
        }
        filter.setFilterProvider(fixedFilterProvider);
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
        ObjectLoadFilter<T> filter = getFixedFilter();
        if(Objects.equals(fixedFilter, filter.getCondition())) {
            return;
        }
        filter.setCondition(fixedFilter);
        if(apply) {
            load();
        }
    }

    @Override
    default void load(ObjectIterator<T> objectIterator) {
        delegate().load(objectIterator);
    }

    @Nonnull
    @Override
    default ObjectLoadFilter<T> getLoadFilter() {
        return delegate().getLoadFilter();
    }

    @Override
    default void applyFilterPredicate() {
        delegate().applyFilterPredicate();
    }

    /**
     * Set view filter.
     *
     * @param predicate Predicate to set.
     * @deprecated Use {@link #setViewFilter(Predicate)} instead.
     */
    @Deprecated
    default void setFilter(Predicate<T> predicate) {
        setViewFilter(predicate);
    }

    /**
     * Set view filter.
     *
     * @param predicate Predicate to set.
     * @param refresh Whether to refresh the view immediately or not.
     * @deprecated Use {@link #setViewFilter(Predicate, boolean)} instead.
     */
    @Deprecated
    default void setFilter(Predicate<T> predicate, boolean refresh) {
        setViewFilter(predicate, refresh);
    }

    default Registration addDataLoadedListener(DataLoadedListener listener) {
        return delegate().addDataLoadedListener(listener);
    }

    private ObjectLoader<T> delegate() {
        ObjectLoader<T> delegate = getDelegatedLoader();
        if(delegate == this) {
            throw new SORuntimeException("Implement OL - " + getClass());
        }
        return delegate;
    }

    default ObjectLoader<T> getDelegatedLoader() {
        return this;
    }
}
