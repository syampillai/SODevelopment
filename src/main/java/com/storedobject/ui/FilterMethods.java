package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectSearchFilter;

import java.util.function.Predicate;

public interface FilterMethods<T> {

    void filter(Predicate<T> filter);

    Predicate<T> getFilterPredicate();

    void setLoadFilter(Predicate<T> filter);

    Predicate<T> getLoadFilter();

    default void setFilter(String filterClause) {
        setFilter(null, filterClause);
    }

    default void setFilter(FilterProvider filterProvider) {
        setFilter(filterProvider, null);
    }

    void setFilter(FilterProvider filterProvider, String extraFilterClause);

    void setFilter(ObjectSearchFilter filter);

    ObjectSearchFilter getFilter(boolean create);

    default ObjectSearchFilter getFilter() {
        return getFilter(true);
    }

    void filterChanged();
}