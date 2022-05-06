package com.storedobject.ui;

import com.storedobject.core.ObjectList;
import com.storedobject.core.*;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Grid that can be used show a list of {@link StoredObject} instances. This implements {@link List} and can handle
 * big-sized lists with an internal caching strategy. However, most methods of the {@link List} such as the "add"
 * methods and "set" methods have poor performance. If you really want to use those methods to add rows to the grid
 * rather than using the various "load" methods of {@link ObjectLoader}, it's better to use the in-memory version
 * of this - {@link ObjectMemoryGrid}.
 *
 * @param <T> Type of object instance.
 * @author Syam
 */
public class ObjectListGrid<T extends StoredObject> extends DataGrid<T> implements ObjectLoader<T> {

    List<ObjectChangedListener<T>> objectChangedListeners;

    public ObjectListGrid(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectListGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, columns, false);
    }

    public ObjectListGrid(Class<T> objectClass, boolean any) {
        this(objectClass, null, any);
    }

    public ObjectListGrid(Class<T> objectClass, Iterable<String> columns, boolean any) {
        this(new ObjectCacheList<>(objectClass, any), columns);
    }

    public ObjectListGrid(ObjectList<T> objectList, Iterable<String> columns) {
        super(objectList.getObjectClass(), objectList, columns);
        getDelegatedLoader().addDataLoadedListener(this::loaded);
    }

    @Override
    protected boolean isValid(ListDataProvider<T> dataProvider) {
        return dataProvider instanceof ObjectListProvider;
    }

    @Override
    protected ObjectListProvider<T> createListDataProvider(DataList<T> data) {
        return new ObjectListProvider<>(getObjectClass(), data);
    }

    @Override
    public final ObjectListProvider<T> getDelegatedLoader() {
        return getDataProvider();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Application getApplication() {
        return super.getApplication();
    }

    @Override
    public ObjectListProvider<T> getDataProvider() {
        return (ObjectListProvider<T>) super.getDataProvider();
    }

    public void configure(String... attributes) {
        if(attributes == null || attributes.length == 0) {
            configure((ObjectToString<T>)null);
            return;
        }
        configure(ObjectToString.create(getObjectClass(), attributes));
    }

    public void load(T object) {
        ObjectLoader.super.load(ObjectIterator.create(object));
        select(object);
    }

    @Override
    public void load(Collection<T> items) {
        ObjectLoader.super.load(items);
    }

    /**
     * This method will be invoked whenever data is loaded.
     */
    public void loaded() {
    }

    public Registration addObjectChangedListener(ObjectChangedListener<T> listener) {
        if(objectChangedListeners == null) {
            objectChangedListeners = new ArrayList<>();
        }
        objectChangedListeners.add(listener);
        return () -> objectChangedListeners.remove(listener);
    }

    public void removeObjectChangedListener(ObjectChangedListener<T> listener) {
        if(objectChangedListeners != null) {
            objectChangedListeners.remove(listener);
        }
    }

    @Override
    protected void doInsertAction(T object) {
        super.doInsertAction(object);
        if(objectChangedListeners != null) {
            objectChangedListeners.forEach(l -> l.inserted(object));
        }
    }

    @Override
    protected void doUpdateAction(T object) {
        super.doUpdateAction(object);
        if(objectChangedListeners != null) {
            objectChangedListeners.forEach(l -> l.updated(object));
        }
    }

    @Override
    protected void doDeleteAction(T object) {
        super.doDeleteAction(object);
        if(objectChangedListeners != null) {
            objectChangedListeners.forEach(l -> l.deleted(object));
        }
    }

    @Override
    protected void doUndeleteAction(T object) {
        super.doUndeleteAction(object);
        if(objectChangedListeners != null) {
            objectChangedListeners.forEach(l -> l.undeleted(object));
        }
    }

    @Override
    public void setViewFilter(Predicate<T> filter) {
        ObjectLoader.super.setViewFilter(filter);
    }
}
