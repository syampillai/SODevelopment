package com.storedobject.ui.util;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectListProvider<T extends StoredObject, F> extends CallbackDataProvider<T, F> implements ObjectDataProvider<T, F> {

    private final Supplier<T, F> supplier;
    private boolean allowAny = true;
    private Class<T> objectClass;
    private Predicate<T> loadFilter;
    private ObjectSearchFilter filter;
    private final List<ObjectDataLoadedListener> loadedListeners = new ArrayList<>();

    public ObjectListProvider(Class<T> objectClass) {
        this(objectClass, new ArrayList<>());
    }

    public ObjectListProvider(Class<T> objectClass, List<T> list) {
        this(new Supplier<>(list), objectClass);
    }

    private ObjectListProvider(Supplier<T, F> supplier, Class<T> objectClass) {
        super(supplier.new Fetcher(), supplier.new Counter(), StoredObject::getId);
        this.objectClass = objectClass;
        this.supplier = supplier;
        getObjectClass();
    }

    @Override
    public Object getId(T item) {
        Id id = item.getId();
        return id == null ? item : id;
    }

    @Override
    public boolean isAllowAny() {
        return allowAny;
    }

    public void setAllowAny(boolean allowAny) {
        this.allowAny = allowAny;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getObjectClass() {
        if(objectClass == null) {
            if(!supplier.cache.isEmpty()) {
                objectClass = (Class<T>) supplier.cache.get(0).getClass();
            } else {
                throw new SORuntimeException("Unable to determine field type!");
            }
        }
        return objectClass;
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public void setViewFilter(ViewFilter<T> viewFilter) {
        supplier.viewFilter = viewFilter;
    }

    @Override
    public ViewFilter<T> getViewFilter() {
        return supplier.viewFilter;
    }

    @Override
    public void filterView(String filters) {
        supplier.viewFilterString = filters;
        refreshAll();
    }

    @Override
    public void filter(Predicate<T> filter) {
        supplier.cache.removeIf(filter.negate());
        refreshAll();
    }

    @Override
    public final void load(String condition, String orderBy) {
        load(StoredObject.list(getObjectClass(), cond(condition), orderBy, isAllowAny()));
    }

    @Override
    public final void load(int linkType, StoredObject master, String condition, String orderBy) {
        if(master != null) {
            load(master.listLinks(linkType, getObjectClass(), cond(condition), orderBy, isAllowAny()));
        } else {
            load(ObjectIterator.create());
        }
    }

    private String cond(String condition) {
        return filter == null ? condition : filter.getFilter(condition);
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        supplier.cache.clear();
        objects.filter(loadFilter).forEach(o -> {
            if(loadFilter == null || loadFilter.test(o)) {
                append(supplier.cache.size(), o, false);
            }
        });
        supplier.init();
        refreshAll();
        loadedListeners.forEach(ObjectDataLoadedListener::dataLoaded);
    }

    @Override
    public boolean validateFilterCondition(T value) {
        return supplier.cache.contains(value);
    }

    public boolean contains(T item) {
        return supplier.cache.contains(item);
    }

    @Override
    public void filterChanged() {
        refreshAll();
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        this.loadFilter = loadFilter;
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return loadFilter;
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
        this.filter = filter;
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        if(create && filter == null) {
            filter = new ObjectSearchFilter();
        }
        return filter;
    }

    public final boolean add(T item) {
        return add(item, true);
    }

    public final boolean add(T item, boolean refresh) {
        return add(supplier.cache.size(), item, refresh);
    }

    public boolean add(int index, T item) {
        return add(index, item, true);
    }

    public boolean add(int index, T item, boolean refresh) {
        supplier.cache.add(index, item);
        if(refresh) {
            refreshAll();
        }
        return true;
    }

    public final boolean append(T item) {
        return append(item, true);
    }

    public final boolean append(T item, boolean refresh) {
        return append(supplier.cache.size(), item, refresh);
    }

    public boolean append(int index, T item) {
        return append(index, item, true);
    }

    public boolean append(int index, T item, boolean refresh) {
        return add(index, item, refresh);
    }

    public boolean update(T item) {
        int index = indexOf(item);
        if(index < 0) {
            return false;
        }
        supplier.cache.set(index, item);
        super.refreshItem(item);
        return true;
    }

    @Override
    public void edited(T item) {
        update(item);
    }

    public boolean delete(T item) {
        if(supplier.cache.remove(item)) {
            refreshAll();
            return true;
        }
        return false;
    }

    public boolean delete(int index) {
        if(index >= 0 && index < supplier.cache.size()) {
            supplier.cache.remove(index);
            refreshAll();
            return true;
        }
        return false;
    }

    public void deleteCache(T item) {
        supplier.cache.remove(item);
    }

    public void reloadAll() {
        supplier.cache.forEach(StoredObject::reload);
        refreshAll();
    }

    public void reload(T item) {
        item.reload();
        super.refreshItem(item);
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean refresh) {
        supplier.cache.clear();
        supplier.init();
        if(refresh) {
            refreshAll();
        }
    }

    @Override
    public boolean isFullyLoaded() {
        return false;
    }

    @Override
    public ObjectSearchFilter getFilter() {
        return null;
    }

    @Override
    public boolean isFullyCached() {
        return false;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int indexOf(T object) {
        return supplier.cache == null ? -1 : supplier.cache.indexOf(object);
    }

    @Override
    public T getItem(int index) {
        return supplier.cache.get(index);
    }

    @Override
    public int getObjectCount() {
        return supplier.cache.size();
    }

    public int size() {
        return getObjectCount();
    }

    public Stream<T> streamAll() {
        return supplier.cache.stream();
    }

    public Stream<T> streamFiltered() {
        return supplier.matched.stream();
    }

    @Override
    public void added(T item) {
        supplier.cache.add(0, item);
    }

    @Override
    public void deleted(T item) {
        supplier.cache.remove(item);
    }

    @Override
    public Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener) {
        loadedListeners.add(listener);
        return () -> loadedListeners.remove(listener);
    }

    private static class Supplier<T extends StoredObject, Fi> {

        private ViewFilter<T> viewFilter;
        private String viewFilterString;
        private final List<T> cache;
        private List<T> matched;

        private Supplier(List<T> list) {
            this.cache = list;
            init();
        }

        private void init() {
            this.matched = this.cache;
            this.viewFilter = null;
        }

        private void applyMatch(String match) {
            if(viewFilter == null || viewFilter.setMatchTokens(match)) {
                return;
            }
            if(viewFilter.skipMatching()) {
                matched = cache;
                return;
            }
            if(matched == cache) {
                matched = new ArrayList<>();
            } else {
                matched.clear();
            }
            cache.stream().filter(viewFilter::match).forEach(matched::add);
        }

        private class Fetcher implements CallbackDataProvider.FetchCallback<T, Fi> {

            @Override
            public Stream<T> fetch(Query<T, Fi> query) {
                applyMatch(vfs(query));
                int end = query.getLimit();
                if(end < (Integer.MAX_VALUE - query.getOffset())) {
                    end += query.getOffset();
                }
                if(end > matched.size()) {
                    end = matched.size();
                }
                if(query.getOffset() == 0 && end == matched.size()) {
                    return matched.stream();
                }
                return matched.subList(query.getOffset(), end).stream();
            }
        }

        private class Counter implements CallbackDataProvider.CountCallback<T, Fi> {

            @Override
            public int count(Query<T, Fi> query) {
                applyMatch(vfs(query));
                if(query.getOffset() >= matched.size()) {
                    return 0;
                }
                int end = query.getLimit();
                if(end < (Integer.MAX_VALUE - query.getOffset())) {
                    end += query.getOffset();
                }
                if(end > matched.size()) {
                    end = matched.size();
                }
                return end - query.getOffset();
            }
        }

        private String vfs(Query<T, Fi> query) {
            Fi f = query.getFilter().orElse(null);
            if(f == null) {
                return viewFilterString;
            }
            if(viewFilterString == null) {
                return f.toString();
            }
            return viewFilterString + " " + f;
        }
    }

    private final AutoCloseable closeable = new AutoCloseable() {
        @Override
        public void close() {
            supplier.cache.clear();
        }
    };

    @Override
    public final AutoCloseable getResource() {
        return closeable;
    }
}