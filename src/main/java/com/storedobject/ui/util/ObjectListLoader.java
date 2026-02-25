package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectLoader;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ObjectListLoader<T extends StoredObject> implements ObjectLoader<T> {

    private final ObjectList<T> list;
    private final ObjectLoadFilter<T> filter = new ObjectLoadFilter<>();
    private final Consumer<T> loadConsumer;

    public ObjectListLoader(Class<T> objectClass, Consumer<T> loadConsumer) {
        this(objectClass, loadConsumer, false);
    }

    public ObjectListLoader(Class<T> objectClass, Consumer<T> loadConsumer, boolean any) {
        this(objectClass, loadConsumer, any,true);
    }

    public ObjectListLoader(Class<T> objectClass, Consumer<T> loadConsumer, boolean any, boolean inMemory) {
        this.loadConsumer = loadConsumer;
        list = inMemory ? new ObjectMemoryList<>(objectClass, any) : new ObjectCacheList<>(objectClass, any);
    }

    @Override
    public Class<T> getObjectClass() {
        return list.getObjectClass();
    }

    public ObjectList<T> getList() {
        return list;
    }

    @Override
    public final int size() {
        return list.size();
    }

    @Override
    public void load(ObjectIterator<T> objectIterator) {
        list.load(objectIterator);
        for(T object: list) {
            loadConsumer.accept(object);
        }
        loadConsumer.accept(null);
    }

    @Override
    public @Nonnull ObjectLoadFilter<T> getLoadFilter() {
        return filter;
    }

    @Override
    public void applyFilterPredicate() {
    }

    @Override
    public void applyFilter() {
    }

    @Override
    public String getEffectiveCondition(String condition) {
        return filter.getFilter(condition);
    }
}
