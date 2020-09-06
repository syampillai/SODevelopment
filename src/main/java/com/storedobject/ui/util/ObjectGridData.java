package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.FilterMethods;
import com.storedobject.ui.ObjectChangedListener;
import com.storedobject.ui.ObjectEditorListener;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.HasColumns;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ObjectGridData<T extends StoredObject> extends HasColumns<T>, ObjectChangedListener<T>, ObjectsSetter<T>, ObjectSearcher<T>, Transactional, ObjectEditorListener, FilterMethods<T> {

    ObjectDataProvider<T, Void> getDataProvider();

    void deselectAll();

    void select(T object);

    default List<ObjectChangedListener<T>> getObjectChangedListeners() {
        return getObjectChangedListeners(false);
    }

    default List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return null;
    }

    @Override
    default Class<T> getObjectClass() {
        return getDataClass();
    }

    @Override
    default boolean isAllowAny() {
        return getDataProvider().isAllowAny();
    }

    void setOrderBy(String orderBy);

    String getOrderBy();

    default void load() {
        load(null, getOrderBy());
    }

    default void load(String filterClause) {
        load(filterClause, getOrderBy());
    }

    default void load(String filterClause, String orderBy) {
    }

    default void load(StoredObject master) {
        load(0, master, null, getOrderBy());
    }

    default void load(StoredObject master, String filterClause, String orderBy) {
    }

    default void load(int linkType, StoredObject master) {
        load(linkType, master, null, getOrderBy());
    }

    default void load(int linkType, StoredObject master, String filterClause, String orderBy) {
    }

    default void load(Stream<T> objects) {
        load(objects.collect(Collectors.toList()));
    }

    default void load(Iterator<T> objects) {
        load(ObjectIterator.create(objects));
    }

    default void load(ObjectIterator<T> objects) {
    }

    default void load(Iterable<T> objects) {
        load(objects.iterator());
    }

    default void loaded() {
    }

    default void clear() {
        getDataProvider().clear();
    }

    default void clear(boolean refresh) {
        getDataProvider().clear(refresh);
    }

    default boolean isFullyLoaded() {
        return getDataProvider().isFullyLoaded();
    }

    @Override
    default void setLoadFilter(Predicate<T> filter) {
        getDataProvider().setLoadFilter(filter);
    }

    @Override
    default Predicate<T> getLoadFilter() {
        return getDataProvider().getLoadFilter();
    }

    @Override
    default void setFilter(String filterClause) {
        setFilter(null, filterClause);
    }

    @Override
    default void setFilter(FilterProvider filterProvider) {
        setFilter(filterProvider, null);
    }

    @Override
    default void setFilter(FilterProvider filterProvider, String extraFilterClause) {
    }

    @Override
    default void filter(Predicate<T> filter) {
        getDataProvider().filter(filter);
    }

    @Override
    default Predicate<T> getFilterPredicate() {
        return getDataProvider().getFilterPredicate();
    }

    @Override
    default ObjectSearchFilter getFilter(boolean create) {
        return getDataProvider().getFilter(true);
    }

    @Override
    default ObjectSearchFilter getFilter() {
        return getFilter(true);
    }

    @Override
    default void filterChanged() {
        getDataProvider().filterChanged();
    }

    default void scrollTo(T object) {
    }

    default T getItem(int index) {
        return getDataProvider().getItem(index);
    }

    @Override
    default void updated(T object) {
    }

    @Override
    default void inserted(T object) {
    }

    @Override
    default void deleted(T object) {
    }

    @Override
    default void setObject(T object) {
    }

    @Override
    default void setObjects(Iterable<T> objects) {
    }

    default T convert(T so) {
        return so;
    }

    default void addObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    default void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    default boolean isFullyCached() {
        return false;
    }

    default int size() {
        return 0;
    }

    @Override
    default void setFilter(ObjectSearchFilter objectSearchFilter) {
    }

    @Override
    default int getObjectCount() {
        return 0;
    }

    default Stream<T> streamAll() {
        return null;
    }

    default Stream<T> streamFiltered() {
        return null;
    }

    default boolean validateFilterCondition(T value) {
        return false;
    }

    @Override
    default void resetSearch() {
    }

    @Override
    default void search(SystemEntity systemEntity, ObjectSetter<T> setter) {
    }

    default String getEntityFilter(SystemEntity systemEntity, String extraFilter) {
        return null;
    }

    @Override
    default void search(SystemEntity systemEntity, ObjectSetter<T> setter, String extraFilter) {
    }

    default void search(ObjectSetter<T> setter) {
    }

    default boolean isSearchMode() {
        return false;
    }

    GridSelectionModel<T> setSelectionMode(Grid.SelectionMode selectionMode);

    GridSelectionModel<T> getSelectionModel();

    void setObjectSetter(ObjectSetter<T> setter);

    default void setObjectConsumer(ObjectSetter<T> objectConsumer) {
        setObjectSetter(objectConsumer);
    }

    default void setObjectConsumer(Consumer<T> objectConsumer) {
        setObjectSetter(objectConsumer::accept);
    }

    @Override
    default void populate(SystemEntity systemEntity) {
    }

    @Override
    default void populate(SystemEntity systemEntity, String extraFilter) {
    }

    @Override
    default void populate(SystemEntity systemEntity, ObjectIterator<T> objectIterator) {
    }

    @Override
    default ObjectSearchBuilder<T> getSearchBuilder() {
        return null;
    }

    default List<ObjectEditorListener> getObjectEditorListeners() {
        return getObjectEditorListeners(false);
    }

    default List<ObjectEditorListener> getObjectEditorListeners(boolean create) {
        return null;
    }

    default void addObjectEditorListener(ObjectEditorListener listener) {
    }

    default void removeObjectEditorListener(ObjectEditorListener listener) {
    }

    @Override
    default void editingStarted() {
    }

    @Override
    default void editingEnded() {
    }

    @Override
    default void editingCancelled() {
    }
}
