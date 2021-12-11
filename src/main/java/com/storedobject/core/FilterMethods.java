package com.storedobject.core;

import com.storedobject.common.FilterProvider;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Interface defining filter methods to be used while loading objects from the DB.
 *
 * @param <T> Type of object class.
 * @author Syam
 */
public interface FilterMethods<T extends StoredObject> {

    /**
     * Set a predicate that will be used for filtering the object after loading is done.
     *
     * @param predicate Filter to apply after loaded.
     */
    default void setFilter(Predicate<T> predicate) {
        setFilter(predicate, true);
    }

    /**
     * Set a predicate that will be used for filtering the object after loading is done.
     *
     * @param predicate Filter to apply after loaded.
     * @param apply Whether to apply it immediately or not.
     */
    default void setFilter(Predicate<T> predicate, boolean apply) {
        ObjectLoadFilter<T> f = getLoadFilter();
        if(apply) {
            boolean changed = predicate != f.getLoadedPredicate();
            f.setLoadedPredicate(predicate);
            if(changed) {
                applyFilterPredicate();
            }
        } else {
            f.setLoadedPredicate(predicate);
        }
    }

    /**
     * Set a filter clause to be used when loading takes place.
     *
     * @param filterClause Filter clause to set.
     */
    default void setFilter(String filterClause) {
        setFilter(filterClause, true);
    }

    /**
     * Set a filter clause to be used when loading takes place.
     *
     * @param filterClause Filter clause to set.
     * @param apply Whether to apply it immediately or not.
     */
    default void setFilter(String filterClause, boolean apply) {
        ObjectLoadFilter<T> f = getLoadFilter();
        if(apply) {
            String c = f.getFilter();
            f.setCondition(filterClause);
            if(!Objects.equals(c, f.getFilter())) {
                applyFilter();
            }
        } else {
            f.setCondition(filterClause);
        }
    }

    /**
     * Set a filter provider to be used when loading takes place.
     *
     * @param filterProvider Filter provider.
     */
    default void setFilter(FilterProvider filterProvider) {
        setFilter(filterProvider, true);
    }

    /**
     * Set a filter provider to be used when loading takes place.
     *
     * @param filterProvider Filter provider.
     * @param apply Whether to apply it immediately or not.
     */
    default void setFilter(FilterProvider filterProvider, boolean apply) {
        ObjectLoadFilter<T> f = getLoadFilter();
        if(apply) {
            String c = f.getFilter();
            f.setFilterProvider(filterProvider);
            if(!Objects.equals(c, f.getFilter())) {
                applyFilter();
            }
        } else {
            f.setFilterProvider(filterProvider);
        }
    }

    /**
     * Set a filter to be applied when loading takes place.
     *
     * @param filter Filter to apply.
     */
    default void setFilter(ObjectLoadFilter<T> filter) {
        setFilter(filter, true);
    }

    /**
     * Set a filter to be applied when loading takes place.
     *
     * @param filter Filter to apply.
     * @param apply Whether to apply it immediately or not.
     */
    default void setFilter(ObjectLoadFilter<T> filter, boolean apply) {
        ObjectLoadFilter<T> f = getLoadFilter();
        if(f == filter) {
            return;
        }
        if(apply) {
            String c = f.getFilter();
            f.set(filter);
            if(!Objects.equals(c, f.getFilter())) {
                applyFilter();
            }
        } else {
            f.set(filter);
        }
    }

    /**
     * This method is called whenever a filter condition is changed. Methods with default implementation already
     * invoke this method. If any of those methods are overridden, make sure that it invokes this method so that logic
     * to handle filter changes can be coded here.
     */
    default void applyFilter() {
    }

    /**
     * This method is called whenever a filter predicate is changed. Methods with default implementation already
     * invoke this method. If any of those methods are overridden, make sure that it invokes this method so that logic
     * to handle filter changes can be coded here.
     * <p>Note: This method is used to show the filtered result of the existing entries</p>
     */
    default void applyFilterPredicate() {
    }

    /**
     * Get the load filter.
     *
     * @return Current load filter.
     */
    default ObjectLoadFilter<T> getLoadFilter() {
        if(this instanceof ObjectLoader ol) {
            //noinspection unchecked
            return ((ObjectLoader<T>) ol).getLoadFilter();
        }
        return null;
    }

    /**
     * Get the object loader that implements these filter methods.
     *
     * @return Object loader.
     */
    default ObjectLoader<T> getObjectLoader() {
        if(this instanceof ObjectLoader ol) {
            //noinspection unchecked
            return (ObjectLoader<T>) ol;
        }
        return null;
    }

    /**
     * Set the load filter. This will be applied whenever loading takes place.
     * @param loadFilter Load filter to be applied while loading.
     */
    default void setLoadFilter(Predicate<T> loadFilter) {
    }
}
