package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataProvider;

import java.io.Closeable;
import java.util.function.Predicate;

public interface ObjectDataProvider<T extends StoredObject> extends DataProvider<T, String>, Closeable {

    @Override
    default Object getId(T item) {
        return item.getId();
    }

    boolean isAllowAny();

    Class<T> getObjectClass();

    @Override
    void close();

    void setFilter(Predicate<T> filter);

    @SuppressWarnings("unchecked")
    default void setFilter(ObjectSearchFilter filter) {
        setFilter((Predicate<T>) filter.getPredicate());
    }

    default void setFilter(FilterProvider filter) {
    }

    default void setFilter(String filter) {
    }

    default int getObjectCount() {
        return -1;
    }

    int indexOf(T object);

    T getItem(int index);

    void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator);

    default void load() {
        load(null, null);
    }

    default void load(String condition) {
        load(condition, null);
    }

    void load(String condition, String orderBy);

    default void load(int linkType, StoredObject master) {
        load(linkType, master, null, null);
    }

    default void load(int linkType, StoredObject master, String condition) {
        load(linkType, master, condition, null);
    }

    void load(int linkType, StoredObject master, String condition, String orderBy);

    void load(ObjectIterator<T> objects);

    boolean isFullyLoaded();

    ObjectSearchFilter getFilter();

    boolean isFullyCached();
}