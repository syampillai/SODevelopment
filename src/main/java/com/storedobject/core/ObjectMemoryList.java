package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectMemoryList<T extends StoredObject> extends MemoryCache<T> implements ObjectList<T> {

    private final Class<T> objectClass;
    private Function<Id, T> loader;
    private final ObjectLoadFilter<T> filter = new ObjectLoadFilter<>();
    private Consumer<T> processor;

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
        load(ObjectIterator.create(null, null, query, objectClass, any));
    }

    @Override
    public void load(Iterable<Id> idList) {
        original.clear();
        Predicate<T> loadFilter = filter.getLoadingPredicate();
        idList.forEach(id -> {
            T so = load(id);
            if(so != null) {
                if(loadFilter == null || loadFilter.test(so)) {
                    if(processor != null) {
                        processor.accept(so);
                    }
                    original.add(so);
                }
            }
        });
        rebuild();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        objects = objects.filter(Objects::nonNull);
        original.clear();
        Predicate<T> loadFilter = filter.getLoadingPredicate();
        if(loadFilter != null) {
            objects = objects.filter(loadFilter);
        }
        if(!filter.isAny()) {
            objects = objects.filter(o -> o.getClass() == objectClass);
        }
        for(T object : objects) {
            if(processor != null) {
                processor.accept(object);
            }
            original.add(object);
        }
        rebuild();
    }

    @Override
    public void load(Stream<T> objects) {
        objects = objects.filter(Objects::nonNull);
        original.clear();
        Predicate<T> loadFilter = filter.getLoadingPredicate();
        if(loadFilter != null) {
            objects = objects.filter(loadFilter);
        }
        if(!filter.isAny()) {
            objects = objects.filter(o -> o.getClass() == objectClass);
        }
        objects.forEach(o -> {
            if(processor != null) {
                processor.accept(o);
            }
            original.add(o);
        });
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
        original.set(index, so);
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
        T item = null;
        try {
            item = loader == null ? null : loader.apply(id);
            if (id.isDummy()) {
                return null;
            }
            return item == null ? StoredObject.get(objectClass, id, filter.isAny()) : item;
        } finally {
            if(item != null && processor != null) {
                processor.accept(item);
            }
        }
    }

    public void setLoader(Function<Id, T> loader) {
        this.loader = loader;
    }

    public Function<Id, T> getLoader() {
        return loader;
    }

    @Override
    public boolean add(T object) {
        if(processor != null) {
            processor.accept(object);
        }
        return super.add(object);
    }

    @Override
    public void add(int index, T element) {
        if(processor != null) {
            processor.accept(element);
        }
        super.add(index, element);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection) {
        if(processor != null) {
            collection.forEach(processor);
        }
        return super.addAll(collection);
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends T> collection) {
        if(processor != null) {
            collection.forEach(processor);
        }
        return super.addAll(index, collection);
    }

    @Override
    public void setProcessor(Consumer<T> processor) {
        this.processor = processor;
    }
}
