package com.storedobject.ui.util;

import com.storedobject.core.ObjectCache;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectsCached<T extends StoredObject, M, F> {

    boolean init = false;
    ViewFilter<T> viewFilter;
    String viewFilterString;
    StoredObject master;
    final Class<T> objectClass;
    String condition, orderBy;
    final boolean any;
    ObjectCache<T> cache;
    private ObjectCache<T> sortedCache, filteredCache, matchedCache;
    private final boolean allowSorting;
    private Comparator<T> sorted, sorter;
    int linkType;
    Predicate<T> filter;
    private Predicate<T> loadFilter;
    boolean fullyLoaded = false;
    ObjectSearchFilter filterDB;
    boolean tree;
    private final List<T> added = new ArrayList<>();
    private ObjectDataLoadedListener dataLoadedListener;

    ObjectsCached(int linkType, StoredObject master, Class<T> objectClass, String condition, String orderBy, boolean any, boolean allowSorting) {
        this.allowSorting = allowSorting;
        this.linkType = linkType;
        this.master = master;
        this.objectClass = objectClass;
        this.condition = condition;
        this.orderBy = orderBy;
        this.any = any;
    }

    void setDataLoadedListener(ObjectDataLoadedListener dataLoadedListener) {
        this.dataLoadedListener = dataLoadedListener;
    }

    void setLoadFilter(Predicate<T> loadFilter) {
        this.loadFilter = loadFilter;
        unload();
    }

    Predicate<T> getLoadFilter() {
        return loadFilter;
    }

    private void fireLoaded() {
        if(dataLoadedListener != null) {
            dataLoadedListener.dataLoaded();
        }
    }

    void load(ObjectIterator<T> objects) {
        added.clear();
        fullyLoaded = false;
        if(objects != null) {
            if(loadFilter != null) {
                objects = objects.filter(loadFilter);
            }
            if(cache == null) {
                cache = new ObjectCache<>(objectClass, objects);
            } else {
                if(matchedCache != filteredCache) {
                    close(matchedCache);
                }
                if(filteredCache != sortedCache) {
                    close(filteredCache);
                }
                if(sortedCache != cache) {
                    close(sortedCache);
                }
                cache.load(objects);
            }
        } else {
            if(cache != null) {
                reload();
                return;
            }
            if(master == null) {
                cache = new ObjectCache<>(objectClass, cond(), orderBy, any);
                if(tree) {
                    loadFilter(o -> o.getMaster(objectClass) == null);
                }
                fullyLoaded = nullCond(condition);
            } else {
                com.storedobject.core.Query q = master.queryLinks(linkType, objectClass, "T.Id", cond(), orderBy, any);
                cache = new ObjectCache<>(objectClass, q);
            }
            if(loadFilter != null) {
                loadFilter(loadFilter);
            }
        }
        applyFilter();
    }

    void reload() {
        if(cache == null) {
            return;
        }
        added.clear();
        fullyLoaded = false;
        if(matchedCache != filteredCache) {
            close(matchedCache);
        }
        if(filteredCache != sortedCache) {
            close(filteredCache);
        }
        if(sortedCache != cache) {
            close(sortedCache);
        }
        if(master == null) {
            cache.load(cond(), orderBy, any);
            if(tree) {
                loadFilter(o -> o.getMaster(objectClass) == null);
            }
            fullyLoaded = nullCond(condition);
        } else {
            cache.load(master.queryLinks(linkType, objectClass, "T.Id", cond(), orderBy, any));
        }
        if(loadFilter != null) {
            loadFilter(loadFilter);
        }
        applyFilter();
        fireLoaded();
    }

    private void loadFilter(Predicate<T> filter) {
        ObjectCache<T> c = cache.filter(filter);
        if(c != cache) {
            cache.close();
            cache = c;
        }
    }

    private void close(ObjectCache<T> c) {
        if(c == cache) {
            cache = null;
        }
        if(sortedCache == c) {
            sortedCache = null;
            sorted = null;
        }
        if(filteredCache == c) {
            filteredCache = null;
        }
        if(matchedCache == c) {
            matchedCache = null;
        }
        c.close();
    }

    boolean nullCond(String condition) {
        if(!(condition == null || condition.trim().isEmpty())) {
            return false;
        }
        return filterDB == null || filterDB.getFilterProvider() == null;
    }

    private String cond() {
        return filterDB == null ? condition : filterDB.getFilter(condition);
    }

    private void applyFilter() {
        if(sorter == null) {
            if(sortedCache != null && sortedCache != cache) {
                close(sortedCache);
            }
            sortedCache = cache;
        } else {
            if(sorted != sorter) {
                if(sortedCache != null && sortedCache != cache) {
                    close(sortedCache);
                }
                sortedCache = cache.sort(sorter);
            }
        }
        sorted = sorter;
        if(filteredCache != null && filteredCache != sortedCache) {
            close(filteredCache);
        }
        if(filter == null) {
            filteredCache = sortedCache;
        } else {
            filteredCache = sortedCache.filter(filter);
        }
        applyMatch();
        fireLoaded();
    }

    private void applyChangedMatch(boolean changed) {
        if(sorter != sorted) {
            applyFilter();
            return;
        }
        if(!changed) {
            return;
        }
        if(matchedCache != filteredCache) {
            close(matchedCache);
        }
        applyMatch();
    }

    private void applyMatch() {
        if(viewFilter == null || viewFilter.skipMatching()) {
            matchedCache = filteredCache;
        } else {
            matchedCache = filteredCache.filter(viewFilter::match);
        }
    }

    void unload() {
        init = false;
        if(cache != null) {
            added.clear();
            if(matchedCache != filteredCache) {
                close(matchedCache);
            }
            if(filteredCache != sortedCache) {
                close(filteredCache);
            }
            if(sortedCache != cache) {
                close(sortedCache);
            }
            close(cache);
            filter = null;
            filteredCache = null;
            matchedCache = null;
            sortedCache = null;
            fullyLoaded = false;
        }
    }

    boolean filter(Predicate<T> filter) {
        if(!added.isEmpty()) {
            load(null);
        }
        if(this.filter == null && filter == null) {
            return false;
        }
        this.filter = filter;
        if(cache == null) {
            return false;
        }
        if(matchedCache != filteredCache) {
            close(matchedCache);
        }
        if(filteredCache != sortedCache) {
            close(filteredCache);
        }
        if(sortedCache != cache) {
            close(sortedCache);
        }
        if(filter == null) {
            filteredCache = cache;
        } else {
            filteredCache = cache.filter(filter);
        }
        applyMatch();
        return true;
    }

    T getItem(int index) {
        if(index < 0) {
            return null;
        }
        if(!added.isEmpty()) {
            if (index < added.size()) {
                return added.get(index);
            }
            index -= added.size();
        }
        if(cache == null) {
            return null;
        }
        return matchedCache.get(index);
    }

    int size() {
        if(cache == null) {
            if(!added.isEmpty()) {
                return added.size();
            }
            return -1;
        }
        return matchedCache.size() + added.size();
    }

    void added(T item) {
        added.add(0, item);
    }

    void deleted(T item) {
        Optional<T> a = added.stream().filter(i -> i.getId().equals(item.getId())).findAny();
        if(a.isPresent()) {
            added.remove(a.get());
            return;
        }
        load(null);
    }

    class Fetcher implements CallbackDataProvider.FetchCallback<M, F> {

        @SuppressWarnings("unchecked")
        @Override
        public Stream<M> fetch(Query<M, F> query) {
            if(allowSorting) {
                sorter = (Comparator<T>) query.getInMemorySorting();
            }
            boolean viewFilterChanged = viewFilter != null && !viewFilter.setMatchTokens(vfs(query));
            if(added.isEmpty()) {
                return fetch(query.getOffset(), query.getLimit(), viewFilterChanged);
            }
            if(viewFilterChanged) {
                load(null);
                return fetch(query.getOffset(), query.getLimit(), true);
            }
            int start = query.getOffset(), limit = query.getLimit();
            int end = limit;
            if(end < (Integer.MAX_VALUE - start)) {
                end += start;
            }
            if(end <= added.size()) {
                return (Stream<M>)added.subList(start, end).stream();
            }
            if(start >= added.size()) {
                start -= added.size();
                return fetch(start, limit, false);
            }
            end = added.size();
            limit -= (end - start);
            return Stream.concat((Stream<M>)added.subList(start, end).stream(), fetch(0, limit, false));
        }

        private Stream<M> fetch(int offset, int limit, boolean viewFilterChanged) {
            if(!init) {
                return Stream.of();
            }
            applyChangedMatch(viewFilterChanged);
            int end = limit;
            if(end < (Integer.MAX_VALUE - offset)) {
                end += offset;
            }
            if(cache == null) {
                load(null);
            }
            if(end > matchedCache.size()) {
                end = matchedCache.size();
            }
            //noinspection unchecked
            return (Stream<M>)matchedCache.list(offset, end).stream();
        }
    }

    class Counter implements CallbackDataProvider.CountCallback<M, F> {

        @Override
        public int count(Query<M, F> query) {
            boolean viewFilterChanged = viewFilter != null && !viewFilter.setMatchTokens(vfs(query));
            if(added.isEmpty()) {
                return count(query.getOffset(), query.getLimit(), viewFilterChanged);
            }
            if(viewFilterChanged) {
                load(null);
                return count(query.getOffset(), query.getLimit(), true);
            }
            int start = query.getOffset(), limit = query.getLimit();
            int end = limit;
            if(end < (Integer.MAX_VALUE - start)) {
                end += start;
            }
            if(end <= added.size()) {
                return end - start;
            }
            if(start >= added.size()) {
                start -= added.size();
                return count(start, limit, false);
            }
            end = added.size();
            limit -= (end - start);
            return end - start + count(0, limit, false);
        }

        private int count(int offset, int limit, boolean viewFilterChanged) {
            if(!init) {
                return 0;
            }
            if(cache == null) {
                load(null);
            }
            applyChangedMatch(viewFilterChanged);
            if(offset >= matchedCache.size()) {
                return 0;
            }
            int end = limit;
            if(end < (Integer.MAX_VALUE - offset)) {
                end += offset;
            }
            if(end > matchedCache.size()) {
                end = matchedCache.size();
            }
            return end - offset;
        }
    }

    private String vfs(Query<?, F> query) {
        F f = query.getFilter().orElse(null);
        if(f == null) {
            return viewFilterString;
        }
        if(viewFilterString == null) {
            return f.toString();
        }
        return viewFilterString + " " + f;
    }
}