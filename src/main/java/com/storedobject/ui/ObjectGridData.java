package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.vaadin.HasColumns;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.data.selection.SelectionModel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public interface ObjectGridData<T extends StoredObject, ROOT> extends HasColumns<ROOT>, ObjectChangedListener<T>,
        ObjectsSetter<T>, ObjectSearcher<T>, Transactional, ObjectEditorListener, ObjectLoader<T> {

    @Nonnull
    @Override
    default ObjectLoadFilter<T> getLoadFilter() {
        return ObjectLoader.super.getLoadFilter();
    }

    @Override
    default boolean isAllowAny() {
        return getObjectLoader().isAllowAny();
    }

    default ObjectSearchBuilder<T> createSearchBuilder(StringList searchColumns,
                                                       Consumer<ObjectSearchBuilder<T>> changeConsumer) {
        return new ObjectFilter<>(getObjectClass(), searchColumns, changeConsumer);
    }

    @Override
    default int getObjectCount() {
        return getObjectLoader().getObjectCount();
    }

    @Override
    default Class<T> getObjectClass() {
        return getObjectLoader().getObjectClass();
    }

    default void deselectAll() {
        if(this instanceof Grid) {
            ((Grid<?>)this).deselectAll();
        }
    }

    default void select(T object) {
        if(this instanceof Grid) {
            //noinspection unchecked
            ((Grid<T>)this).select(object);
        }
    }

    default void deselect(T object) {
        if(this instanceof Grid) {
            //noinspection unchecked
            ((Grid<T>)this).deselect(object);
        }
    }

    @Override
    default void setLoadFilter(ObjectLoadFilter<T> objectLoadFilter) {

        setFilter(objectLoadFilter, false);
    }

    default List<ObjectChangedListener<T>> getObjectChangedListeners(boolean create) {
        return null;
    }

    default void scrollTo(T object) {
        if(object != null) {
            ((Grid<?>)this).scrollToIndex(getObjectLoader().indexOf(object));
        }
    }

    default T getItem(int index) {
        return get(index);
    }

    @Override
    default void updated(T object) {
        //noinspection unchecked
        ((ObjectChangedListener<T>)getObjectLoader()).updated(object);
        List<ObjectChangedListener<T>> listeners = getObjectChangedListeners(false);
        if(listeners != null) {
            listeners.forEach(ocl -> ocl.updated(object));
        }
    }

    @Override
    default void inserted(T object) {
        //noinspection unchecked
        ((ObjectChangedListener<T>)getObjectLoader()).inserted(object);
        List<ObjectChangedListener<T>> listeners = getObjectChangedListeners(false);
        if(listeners != null) {
            listeners.forEach(ocl -> ocl.inserted(object));
        }
        select(object);
        scrollTo(object);
    }

    @Override
    default void deleted(T object) {
        deselect(object);
        //noinspection unchecked
        ((ObjectChangedListener<T>)getObjectLoader()).deleted(object);
        List<ObjectChangedListener<T>> listeners = getObjectChangedListeners(false);
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
        load(ObjectIterator.create(objects.iterator()));
    }

    default T convert(StoredObject so) {
        if(so == null || !getObjectClass().isAssignableFrom(so.getClass())) {
            return null;
        }
        if(!isAllowAny() && getObjectClass() != so.getClass()) {
            return null;
        }
        //noinspection unchecked
        return (T)so;
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
        List<ObjectChangedListener<T>> objectChangedListeners = getObjectChangedListeners(false);
        if(objectChangedListeners != null) {
            objectChangedListeners.remove(listener);
        }
    }

    default boolean validateFilterCondition(T value) {
        return getObjectLoader().getLoadFilter().filter(value) != null;
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

    GridSelectionModel<?> setSelectionMode(Grid.SelectionMode selectionMode);

    GridSelectionModel<?> getSelectionModel();

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
            load(objectIterator);
        }
    }

    @Override
    default ObjectSearchBuilder<T> getSearchBuilder() {
        return null;
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
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners(false);
        if(objectEditorListeners != null) {
            objectEditorListeners.remove(listener);
        }
    }

    @Override
    default void editingStarted() {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners(false);
        if(objectEditorListeners != null && !objectEditorListeners.isEmpty()) {
            objectEditorListeners.forEach(ObjectEditorListener::editingStarted);
        }
    }

    @Override
    default void editingEnded() {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners(false);
        if(objectEditorListeners != null && !objectEditorListeners.isEmpty()) {
            objectEditorListeners.forEach(ObjectEditorListener::editingEnded);
        }
    }

    @Override
    default void editingCancelled() {
        List<ObjectEditorListener> objectEditorListeners = getObjectEditorListeners(false);
        if(objectEditorListeners != null && !objectEditorListeners.isEmpty()) {
            objectEditorListeners.forEach(ObjectEditorListener::editingCancelled);
        }
    }
}
