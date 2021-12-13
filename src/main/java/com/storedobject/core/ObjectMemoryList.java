package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectMemoryList<T extends StoredObject> extends MemoryCache<T> implements ObjectList<T> {

    private final Class<T> objectClass;
    private Function<Id, T> loader;
    private final ObjectLoadFilter<T> filter = new ObjectLoadFilter<>();

    public ObjectMemoryList(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectMemoryList(Class<T> objectClass, boolean any) {
        this(objectClass, "false", null, any);
    }

    public ObjectMemoryList(Class<T> objectClass, String condition) {
        this(objectClass, condition, null, false);
    }

    public ObjectMemoryList(Class<T> objectClass, String condition, boolean any) {
        this(objectClass, condition, null, any);
    }

    public ObjectMemoryList(Class<T> objectClass, String condition, String orderedBy) {
        this(objectClass, condition, orderedBy, false);
    }

    public ObjectMemoryList(Class<T> objectClass, String condition, String orderedBy, boolean any) {
        this.objectClass = objectClass;
        this.filter.setAny(any);
        load(condition, orderedBy, any);
    }

    public ObjectMemoryList(Class<T> objectClass, Query query) {
        this(objectClass, query, true);
    }

    public ObjectMemoryList(Class<T> objectClass, Query query, boolean any) {
        this.objectClass = objectClass;
        this.filter.setAny(any);
        load(query, any);
    }

    public ObjectMemoryList(Class<T> objectClass, Iterable<Id> idList) {
        this.objectClass = objectClass;
        load(idList);
    }

    public ObjectMemoryList(Class<T> objectClass, ObjectIterator<T> objects) {
        this.objectClass = objectClass;
        load(objects);
    }

    public ObjectMemoryList(Class<T> objectClass, Stream<T> objects) {
        this.objectClass = objectClass;
        load(objects);
    }

    @Override
    public void load(String condition, String orderedBy, boolean any) {
        this.filter.setAny(any);
        ObjectList.super.load(condition, orderedBy, any);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, boolean any) {
        this.filter.setAny(any);
        ObjectList.super.load(linkType, master, condition, any);
    }

    @Override
    public void load(Query query, boolean any) {
        this.filter.setAny(any);
    }

    @Override
    public void load(Iterable<Id> idList) {
        original.clear();
        Predicate<T> loadFilter = filter.getLoadingPredicate();
        idList.forEach(id -> {
            T so = load(id);
            if(so != null) {
                if(loadFilter == null || loadFilter.test(so)) {
                    original.add(so);
                }
            }
        });
        rebuild();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        original.clear();
        Predicate<T> loadFilter = filter.getLoadingPredicate();
        if(loadFilter != null) {
            objects = objects.filter(loadFilter);
        }
        if(!filter.isAny()) {
            objects = objects.filter(o -> o.getClass() == objectClass);
        }
        for(T object : objects) {
            original.add(object);
        }
        rebuild();
    }

    @Override
    public void load(Stream<T> objects) {
        original.clear();
        Predicate<T> loadFilter = filter.getLoadingPredicate();
        if(loadFilter != null) {
            objects = objects.filter(loadFilter);
        }
        if(!filter.isAny()) {
            objects = objects.filter(o -> o.getClass() == objectClass);
        }
        objects.forEach(original::add);
        rebuild();
    }

    @Override
    public void applyFilterPredicate() {
        filter(filter.getViewFilter());
    }

    @Nonnull
    @Override
    public ObjectLoadFilter<T> getLoadFilter() {
        return filter;
    }

    @Override
    public Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public boolean add(Id id) {
        if(!Id.isNull(id)) {
            return add(load(id));
        }
        return false;
    }

    @Override
    public int indexOf(Id id) {
        return indexOf(id, sorted);
    }

    private static <O extends StoredObject> int indexOf(Id id, List<O> list) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void refresh() {
        ArrayList<T> a = new ArrayList<>();
        ObjectIterator.create(original).map(o -> load(o.getId()))
                .filter(Objects::nonNull).collectAll(a);
        original = a;
        rebuild();
    }

    @Override
    public T refresh(Id id) {
        int index = indexOf(id, original);
        if(index < 0) {
            return null;
        }
        T so = original.get(index);
        so = load(so.getId());
        original.set(index, load(so.getId()));
        rebuild();
        return so;
    }

    @Override
    public T refresh(T object) {
        int index = original.indexOf(object);
        if(index < 0) {
            return null;
        }
        object = load(object.getId());
        original.set(index, object);
        rebuild();
        return object;
    }

    @Override
    public int getCacheLevel() {
        return 100;
    }

    private T load(Id id) {
        T item = loader == null ? null : loader.apply(id);
        return item == null ? StoredObject.get(objectClass, id, filter.isAny()) : item;
    }

    public void setLoader(Function<Id, T> loader) {
        this.loader = loader;
    }

    public Function<Id, T> getLoader() {
        return loader;
    }
}
