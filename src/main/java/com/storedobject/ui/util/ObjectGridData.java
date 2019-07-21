package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.HasColumns;
import com.storedobject.vaadin.ItemSelectedListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ObjectGridData<T extends StoredObject> extends HasColumns<T>, ObjectChangedListener<T>, ObjectsSetter, ObjectSearcher<T>, Transactional, ObjectEditorListener, FilterMethods<T> {

    ObjectDataProvider<T> getDataProvider();

    void deselectAll();

    void select(T object);

    default List<ObjectChangedListener<T>> getObjectChangedListeners() {
        return getObjectChangedListeners(false);
    }

    default List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return null;
    }

    void setKeepCache(boolean keepCache);

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
    }

    default void load(int linkType, StoredObject master, String filterClause, String orderBy) {
    }

    default void load(Stream<T> objects) {
    }

    default void load(Iterator<T> objects) {
    }

    default void load(ObjectIterator<T> objects) {
    }

    default void load(Iterable<T> objects) {
    }

    default void clear() {
    }

    default boolean isFullyLoaded() {
        return false;
    }

    @Override
    default Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    default void filter(Predicate<T> filter) {
    }

    @Override
    default void setLoadFilter(Predicate<T> filter) {
    }

    @Override
    default Predicate<T> getLoadFilter() {
        return null;
    }

    @Override
    default void setFilter(String filterClause) {
    }

    @Override
    default void setFilter(FilterProvider filterProvider) {
    }

    @Override
    default void setFilter(FilterProvider filterProvider, String extraFilterClause) {
    }

    @Override
    default ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    default ObjectSearchFilter getFilter() {
        return null;
    }

    @Override
    default void filterChanged() {
    }

    default void scrollTo(T object) {
    }

    default T getItem(int index) {
        return getDataProvider().getItem(index);
    }

    @Override
    default void updated(ObjectMasterData<T> object) {
    }

    @Override
    default void inserted(ObjectMasterData<T> object) {
    }

    @Override
    default void deleted(ObjectMasterData<T> object) {
    }

    @Override
    default void setObject(StoredObject object) {
    }

    @Override
    default void setObjects(Iterable<? extends StoredObject> objects) {
    }

    default T convert(StoredObject so) {
        return null;
    }

    default void addObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    default void removeObjectChangedListener(ObjectChangedListener<T> listener) {
    }

    default boolean isFullyCached() {
        return false;
    }

    default int size() {
        return getDataProvider().getObjectCount();
    }

    @Override
    default void setFilter(ObjectSearchFilter objectSearchFilter) {
    }

    @Override
    default int getObjectCount() {
        return size();
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
    default void search(SystemEntity systemEntity, ObjectSetter setter) {
        search(systemEntity, setter, null);
    }

    default String getEntityFilter(SystemEntity systemEntity, String extraFilter) {
        return null;
    }

    @Override
    default void search(SystemEntity systemEntity, ObjectSetter setter, String extraFilter) {
    }

    default void search(ObjectSetter setter) {
    }

    default boolean isSearchMode() {
        return false;
    }

    GridSelectionModel<T> setSelectionMode(Grid.SelectionMode selectionMode);

    GridSelectionModel<T> getSelectionModel();

    void setObjectSetter(ObjectSetter setter);

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
        return null;
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
