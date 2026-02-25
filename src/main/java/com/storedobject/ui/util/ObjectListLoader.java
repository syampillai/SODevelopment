package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectLoader;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ObjectListLoader<T extends StoredObject> implements ObjectLoader<T> {

    private final ObjectList<T> list;
    private final ObjectLoadFilter<T> filter = new ObjectLoadFilter<>();
    private final Consumer<T> loadConsumer;
    private final Runnable cleared, loaded;

    public ObjectListLoader(Class<T> objectClass, Consumer<T> loadConsumer, Runnable cleared, Runnable loaded) {
        this(objectClass, loadConsumer, cleared, loaded, false);
    }

    public ObjectListLoader(Class<T> objectClass, Consumer<T> loadConsumer, Runnable cleared, Runnable loaded, boolean any) {
        this(objectClass, loadConsumer, cleared, loaded, true, any);
    }

    public ObjectListLoader(Class<T> objectClass, Consumer<T> loadConsumer, Runnable cleared, Runnable loaded, boolean inMemory, boolean any) {
        this.loadConsumer = loadConsumer;
        this.cleared = cleared;
        this.loaded = loaded;
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
        list.clear();
        cleared.run();
        list.load(objectIterator);
        for(T object: list) {
            loadConsumer.accept(object);
        }
        loaded.run();
    }

    @Override
    public void clear() {
        load(ObjectIterator.create());
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
