package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.FilterMethods;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataProvider;

import java.io.Closeable;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ObjectDataProvider<T extends StoredObject> extends DataProvider<T, String>, FilterMethods<T>, Closeable {

    @Override
    default Object getId(T item) {
        return item.getId();
    }

    boolean isAllowAny();

    Class<T> getObjectClass();

    @Override
    void close();

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     */
    @Override
    void setFilter(ObjectSearchFilter filter);

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     */
    @Override
    default void setFilter(FilterProvider filter) {
    }

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     * @param extraFilterClause Extra filter clause
     */
    @Override
    default void setFilter(FilterProvider filter, String extraFilterClause) {
    }

    /**
     * Set a DB filter.
     *
     * @param filter Filter
     */
    @Override
    default void setFilter(String filter) {
    }

    Stream<T> streamAll();

    default Stream<T> streamFiltered() {
        return null;
    }

    boolean validateFilterCondition(T value);

    default int getObjectCount() {
        return -1;
    }

    int indexOf(T object);

    T getItem(int index);

    void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator);

    default void load() {
    }

    default void load(String condition) {
    }

    void load(String condition, String orderBy);

    default void load(int linkType, StoredObject master) {
    }

    default void load(int linkType, StoredObject master, String condition) {
    }

    void load(int linkType, StoredObject master, String condition, String orderBy);

    void load(ObjectIterator<T> objects);

    default void clear() {
    }

    boolean isFullyLoaded();

    boolean isFullyCached();

    void filterChanged();
}