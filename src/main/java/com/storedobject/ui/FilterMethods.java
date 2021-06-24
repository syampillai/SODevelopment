package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectSearchFilter;

import java.util.function.Predicate;

/**
 * Interface defining filter methods.
 *
 * @param <T> Type of object class.
 * @author Syam
 */
public interface FilterMethods<T> {

    /**
     * Display filter.
     *
     * @param filter Filter.
     */
    void filter(Predicate<T> filter);

    /**
     * Get the display filter.
     *
     * @return Display filter.
     */
    Predicate<T> getFilterPredicate();

    /**
     * Set the load filter.
     *
     * @param filter Load filter.
     */
    void setLoadFilter(Predicate<T> filter);

    /**
     * Get the lad filter.
     *
     * @return Load filter.
     */
    Predicate<T> getLoadFilter();

    /**
     * Set a filter clause.
     *
     * @param filterClause Filter clause to set.
     */
    default void setFilter(String filterClause) {
        setFilter(null, filterClause);
    }

    /**
     * Set a filter provider.
     *
     * @param filterProvider Filter provider.
     */
    default void setFilter(FilterProvider filterProvider) {
        setFilter(filterProvider, null);
    }

    /**
     * Set a filter provider.
     *
     * @param filterProvider Filter provider.
     * @param extraFilterClause Extra condition to set.
     */
    void setFilter(FilterProvider filterProvider, String extraFilterClause);

    /**
     * Set a search filter.
     *
     * @param filter Search filter.
     */
    void setFilter(ObjectSearchFilter filter);

    /**
     * Get the search filter.
     *
     * @param create Whether to create a new if one doesn't exist or not.
     * @return Search filter.
     */
    ObjectSearchFilter getFilter(boolean create);

    /**
     * Get the search filter. (A new one will be created if one doesn't exist).
     *
     * @return Search filter.
     */
    default ObjectSearchFilter getFilter() {
        return getFilter(true);
    }

    /**
     * Filter changed method. This will be invoked whenever a filter is changed. (Or, this method
     * could be programmatically invoked when needed).
     */
    void filterChanged();
}
