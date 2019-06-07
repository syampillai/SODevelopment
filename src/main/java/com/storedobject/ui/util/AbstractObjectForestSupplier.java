package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;

import java.util.List;
import java.util.function.Predicate;

public interface AbstractObjectForestSupplier<T extends StoredObject> extends HierarchicalDataProvider<Object, String> {
    void close();

    boolean isAllowAny();

    void load(String filterClause, String orderBy);

    void load(int linkType, StoredObject master, String filterClause, String orderBy);

    void load(ObjectIterator<T> objects);

    boolean isFullyLoaded();

    void setFilter(FilterProvider filterProvider);

    void setFilter(String extraFilterClause);

    void setFilter(Predicate<T> filter);

    ObjectSearchFilter getFilter();

    int indexOf(T object);

    List<T> listRoots();

    T getItem(int index);

    boolean isFullyCached();

    int getObjectCount();
}
