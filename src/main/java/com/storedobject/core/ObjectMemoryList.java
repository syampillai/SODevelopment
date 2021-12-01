package com.storedobject.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ObjectMemoryList<T extends StoredObject> extends MemoryCache<T> implements ObjectList<T> {

    private final Class<T> objectClass;
    private boolean any;
    private Function<Id, T> loader;

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
        this.any = any;
        load(condition, orderedBy, any);
    }

    public ObjectMemoryList(Class<T> objectClass, Query query) {
        this(objectClass, query, true);
    }

    public ObjectMemoryList(Class<T> objectClass, Query query, boolean any) {
        this.objectClass = objectClass;
        this.any = any;
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
        this.any = any;
        ObjectList.super.load(condition, orderedBy, any);
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, boolean any) {
        this.any = any;
        ObjectList.super.load(linkType, master, condition, any);
    }

    @Override
    public void load(Query query, boolean any) {
        this.any = any;
        original.clear();
        query.forEach(rs -> {
            try {
                T so = load(new Id(rs.getBigDecimal(1)));
                if(so != null) {
                    original.add(so);
                }
            } catch(Exception ignored) {
            }
        });
        rebuild();
    }

    @Override
    public void load(Iterable<Id> idList) {
        original.clear();
        idList.forEach(id -> {
            T so = load(id);
            if(so != null) {
                original.add(so);
            }
        });
        rebuild();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        original.clear();
        for(T object : objects) {
            original.add(object);
        }
        rebuild();
    }

    @Override
    public void load(Stream<T> objects) {
        original.clear();
        objects.forEach(original::add);
        rebuild();
    }

    @Override
    public boolean isAllowAny() {
        return any;
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
                .filter(Objects::nonNull).toList();
        original = a;
        rebuild();
    }

    @Override
    public void refresh(Id id) {
        int index = indexOf(id, original);
        if(index < 0) {
            return;
        }
        T so = original.get(index);
        original.set(index, load(so.getId()));
        rebuild();
    }

    @Override
    public void refresh(T object) {
        int index = original.indexOf(object);
        if(index < 0) {
            return;
        }
        original.set(index, load(object.getId()));
        rebuild();
    }

    @Override
    public int getCacheLevel() {
        return 100;
    }

    private T load(Id id) {
        T item = loader == null ? null : loader.apply(id);
        return item == null ? StoredObject.get(objectClass, id, any) : item;
    }

    public void setLoader(Function<Id, T> loader) {
        this.loader = loader;
    }

    public Function<Id, T> getLoader() {
        return loader;
    }
}
