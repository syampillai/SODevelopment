package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.HasColumns;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.data.selection.SelectionModel;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ObjectGridData<T extends StoredObject> extends HasColumns<T>, ObjectChangedListener<T>,
        ObjectsSetter<T>, ObjectSearcher<T>, Transactional, ObjectEditorListener, FilterMethods<T> {

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
        deselectAll();
        getDataProvider().load(filterClause, orderBy);
    }

    default void load(StoredObject master) {
        load(0, master, null, getOrderBy());
    }

    default void load(StoredObject master, String filterClause, String orderBy) {
        load(0, master, filterClause, orderBy);
    }

    default void load(int linkType, StoredObject master) {
        load(linkType, master, null, getOrderBy());
    }

    default void load(int linkType, StoredObject master, String filterClause, String orderBy) {
        deselectAll();
        getDataProvider().load(linkType, master, filterClause, orderBy);
    }

    default void load(Stream<T> objects) {
        load(objects.collect(Collectors.toList()));
    }

    default void load(Iterator<T> objects) {
        load(ObjectIterator.create(objects));
    }

    default void load(ObjectIterator<T> objects) {
        deselectAll();
        getDataProvider().load(objects);
    }

    default void load(Iterable<T> objects) {
        load(objects.iterator());
    }

    /**
     * This will be invoked whenever a new set of rows are loaded.
     */
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
        getDataProvider().setFilter(filterProvider, extraFilterClause);
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
        if(object != null) {
            ((Grid<?>)this).scrollToIndex(getDataProvider().indexOf(object));
        }
    }

    default T getItem(int index) {
        return getDataProvider().getItem(index);
    }

    @Override
    default void updated(T object) {
        refresh(object);
        List<ObjectChangedListener<T>> listeners = getObjectChangedListeners();
        if(listeners != null) {
            listeners.forEach(ocl -> ocl.updated(object));
        }
    }

    @Override
    default void inserted(T object) {
        getDataProvider().added(object);
        refresh();
        List<ObjectChangedListener<T>> listeners = getObjectChangedListeners();
        if(listeners != null) {
            listeners.forEach(ocl -> ocl.inserted(object));
        }
        select(object);
        scrollTo(object);
    }

    @Override
    default void deleted(T object) {
        if(this instanceof Grid) {
            //noinspection unchecked
            ((Grid<T>)this).deselect(object);
        }
        getDataProvider().deleted(object);
        refresh();
        List<ObjectChangedListener<T>> listeners = getObjectChangedListeners();
        if(listeners != null) {
            listeners.forEach(ocl -> ocl.deleted(object));
        }
    }

    @Override
    default void setObject(T object) {
        deselectAll();
        if(object == null || !getObjectClass().isAssignableFrom(object.getClass())) {
            return;
        }
        select(object);
        scrollTo(object);
    }

    @Override
    default void setObjects(Iterable<T> objects) {
        deselectAll();
        ObjectIterator<T> oi = ObjectIterator.create(objects.iterator()).filter(Objects::nonNull);
        getDataProvider().load(oi.map(this::convert).filter(Objects::nonNull));
    }

    default T convert(T so) {
        if(so == null || !getObjectClass().isAssignableFrom(so.getClass())) {
            return null;
        }
        if(!isAllowAny() && getObjectClass() != so.getClass()) {
            return null;
        }
        return so;
    }

    default void addObjectChangedListener(ObjectChangedListener<T> listener) {
        if(listener != null && listener != this) {
            List<ObjectChangedListener<T>> objectChangedListeners = getObjectChangedListeners(true);
            if(objectChangedListeners != null) {
                objectChangedListeners.add(listener);
            }
        }
    }

    default void removeObjectChangedListener(ObjectChangedListener<T> listener) {
        List<ObjectChangedListener<T>> objectChangedListeners = getObjectChangedListeners();
        if(objectChangedListeners != null) {
            getObjectChangedListeners().remove(listener);
        }
    }

    default boolean isFullyCached() {
        return getDataProvider().isFullyCached();
    }

    default int size() {
        return getDataProvider().getObjectCount();
    }

    @Override
    default void setFilter(ObjectSearchFilter objectSearchFilter) {
        getDataProvider().setFilter(objectSearchFilter);
    }

    @Override
    default int getObjectCount() {
        return size();
    }

    default Stream<T> streamAll() {
        return getDataProvider().streamAll();
    }

    default Stream<T> streamFiltered() {
        return getDataProvider().streamFiltered();
    }

    default boolean validateFilterCondition(T value) {
        return getDataProvider().validateFilterCondition(value);
    }

    @Override
    default void resetSearch() {
    }

    @Override
    default void search(SystemEntity systemEntity, ObjectSetter<T> setter) {
        search(systemEntity, setter, null);
    }

    default String getEntityFilter(SystemEntity systemEntity, String extraFilter) {
        if(systemEntity == null) {
            return extraFilter;
        }
        Class<T> c = getObjectClass();
        if(OfEntity.class.isAssignableFrom(c)) {
            if(extraFilter == null) {
                extraFilter = "";
            } else {
                extraFilter = "(" + extraFilter + ") AND ";
            }
            extraFilter += "SystemEntity=" + systemEntity.getId();
        }
        return extraFilter;
    }

    @Override
    default void search(SystemEntity systemEntity, ObjectSetter<T> setter, String extraFilter) {
        extraFilter = getEntityFilter(systemEntity, extraFilter);
        if(extraFilter != null) {
            load(extraFilter);
        }
        search(setter);
    }

    default void search(ObjectSetter<T> setter) {
        if(!isSearchMode()) {
            warning("Not in search mode!");
            return;
        }
        if(setter instanceof ObjectsSetter) {
            if (!(getSelectionModel() instanceof SelectionModel.Multi)) {
                setSelectionMode(Grid.SelectionMode.MULTI);
            }
        } else {
            if (!(getSelectionModel() instanceof SelectionModel.Single)) {
                setSelectionMode(Grid.SelectionMode.SINGLE);
            }
        }
        setObjectSetter(setter);
        View v = null;
        if(setter instanceof View) {
            v = (View) setter;
        } else {
            Application a = Application.get();
            if(a != null) {
                v = a.getActiveView();
            }
        }
        if(v == null) {
            execute();
        } else {
            getView(true).execute(v);
        }
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
        if(objectConsumer instanceof ObjectSetter) {
            setObjectSetter((ObjectSetter<T>)objectConsumer);
        } else {
            setObjectSetter(objectConsumer::accept);
        }
    }

    @Override
    default void populate(SystemEntity systemEntity) {
        populate(systemEntity, (String)null);
    }

    @Override
    default void populate(SystemEntity systemEntity, String extraFilter) {
        load(getEntityFilter(systemEntity, extraFilter));
    }

    @Override
    default void populate(SystemEntity systemEntity, ObjectIterator<T> objectIterator) {
        if(systemEntity != null && OfEntity.class.isAssignableFrom(getObjectClass())) {
            Id sid = systemEntity.getId();
            load(objectIterator.filter(obj -> ((OfEntity)obj).getSystemEntityId().equals(sid)));
        } else {
            load(objectIterator.iterator());
        }
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
        if (listener != null && listener != this) {
            List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners(true);
            if(objectEditorListeners != null) {
                objectEditorListeners.add(listener);
            }
        }
    }

    default void removeObjectEditorListener(ObjectEditorListener listener) {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners();
        if(objectEditorListeners != null) {
            objectEditorListeners.remove(listener);
        }
    }

    @Override
    default void editingStarted() {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners();
        if(objectEditorListeners != null && !objectEditorListeners.isEmpty()) {
            objectEditorListeners.forEach(ObjectEditorListener::editingStarted);
        }
    }

    @Override
    default void editingEnded() {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners();
        if(objectEditorListeners != null && !objectEditorListeners.isEmpty()) {
            objectEditorListeners.forEach(ObjectEditorListener::editingEnded);
        }
    }

    @Override
    default void editingCancelled() {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners();
        if(objectEditorListeners != null && !objectEditorListeners.isEmpty()) {
            objectEditorListeners.forEach(ObjectEditorListener::editingCancelled);
        }
    }
}
