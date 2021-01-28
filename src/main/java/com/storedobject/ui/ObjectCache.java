package com.storedobject.ui;

import com.storedobject.common.ResourceDisposal;
import com.storedobject.common.ResourceOwner;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.AbstractDataProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectCache<T extends StoredObject> extends AbstractDataProvider<T, Predicate<T>>
        implements ObjectLoader<T>, AutoCloseable, ResourceOwner {

    private final Class<T> objectClass;
    private com.storedobject.core.ObjectCache<T> cache = null, transformedCache = null;
    private List<T> list =  null, transformedList = null;
    private Comparator<T> sortOrder = null;
    private Predicate<T> filter;

    public ObjectCache(Class<T> objectClass) {
        this.objectClass = objectClass;
        ResourceDisposal.register(this);
    }

    @Override
    public Object getId(T item) {
        return item.getId();
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    public Stream<T> fetch(com.vaadin.flow.data.provider.Query<T, Predicate<T>> query) {
        Stream<T> stream = this.getFilteredStream(query);
        Comparator<T> qComparator = query.getInMemorySorting();
        if (qComparator != null) {
            stream = stream.sorted(qComparator);
        }
        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    public int size(com.vaadin.flow.data.provider.Query<T, Predicate<T>> query) {
        return (int)this.getFilteredStream(query).count();
    }

    private Stream<T> getFilteredStream(com.vaadin.flow.data.provider.Query<T, Predicate<T>> query) {
        Stream<T> stream = stream();
        Predicate<T> qFilter = query.getFilter().orElse(null);
        if(qFilter != null) {
            stream = stream.filter(qFilter);
        }
        return stream;
    }

    private Stream<T> stream() {
        if(transformedCache != null) {
            return transformedCache.list().stream();
        }
        if(transformedList != null) {
            return transformedList.stream();
        }
        return Stream.empty();
    }

    public void setSortComparator(Comparator<T> comparator) {
        if(this.sortOrder == comparator) {
            return;
        }
        this.sortOrder = comparator;
        transform();
        this.refreshAll();
    }

    public T getItem(int index) {
        return cache.get(index);
    }

    public Stream<T> getItems() {
        return stream();
    }

    public void setFilter(Predicate<T> filter) {
        if(this.filter == filter) {
            return;
        }
        this.filter = filter;
        transform();
        this.refreshAll();
    }

    private void reset() {
        list = null;
        transformedList = null;
        if(transformedCache != null) {
            transformedCache.close();
            if(cache == transformedCache) {
                cache = null;
            }
            transformedCache = null;
        }
        if(cache != null) {
            cache.close();
            cache = null;
        }
    }

    @Override
    public void load(String condition, String orderBy) {
        reset();
        cache = new com.storedobject.core.ObjectCache<>(objectClass, condition, orderBy);
        transformedCache = cache;
        refreshAll();
    }

    @Override
    public void load(int linkType, StoredObject master, String condition, String orderBy) {
        reset();
        cache = new com.storedobject.core.ObjectCache<>(objectClass, condition, orderBy);
        transformedCache = cache;
        refreshAll();
    }

    @Override
    public void load(Stream<T> objects) {
        reset();
        if(objects != null) {
            list = new ArrayList<>();
            objects.forEach(list::add);
            transformedList = list;
        }
        refreshAll();
    }

    @Override
    public void clear() {
        reset();
        refreshAll();
    }

    private void transform() {
        if(transformedCache != null) {
            if(transformedCache != cache) {
                transformedCache.close();
            }
            transformedCache = null;
        }
        if(cache != null) {
            if(filter == null && sortOrder == null) {
                transformedCache = cache;
                return;
            }
            if(filter == null) {
                transformedCache = cache;
            } else {
                transformedCache = cache.filter(filter);
            }
            if(sortOrder != null) {
                transformedCache = transformedCache.sort(sortOrder);
            }
            return;
        }
        if(list != null) {
            if(filter == null && sortOrder == null) {
                transformedList = list;
                return;
            }
            if(filter == null) {
                transformedList = list;
            } else {
                transformedList = new ArrayList<>(list);
                transformedList.removeIf(filter);
            }
            if(sortOrder != null) {
                transformedList.sort(sortOrder);
            }
        }
    }

    @Override
    public void close() {
        reset();
    }

    @Override
    public AutoCloseable getResource() {
        return new Cleaner(cache, transformedCache);
    }

    private static class Cleaner implements AutoCloseable {

        private final com.storedobject.core.ObjectCache<?> cache1, cache2;

        private Cleaner(com.storedobject.core.ObjectCache<?> cache1, com.storedobject.core.ObjectCache<?> cache2) {
            this.cache1 = cache1;
            this.cache2 = cache2;
        }

        @Override
        public void close() {
            if(cache1 != null) {
                cache1.close();
            }
            if(cache2 != null) {
                cache2.close();
            }
        }
    }
}
