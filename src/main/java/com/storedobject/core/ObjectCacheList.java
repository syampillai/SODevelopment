package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectCacheList<T extends StoredObject> implements ObjectList<T>, AutoCloseable {

    private ObjectCache<T> original, sorted, filtered;
    private Predicate<? super T> currentFilter;
    private Comparator<? super T> currentComparator;

    public ObjectCacheList(Class<T> objectClass) {
        this(objectClass, false);
    }

    public ObjectCacheList(Class<T> objectClass, boolean any) {
        this(objectClass, "false", null, any);
    }

    public ObjectCacheList(Class<T> objectClass, String condition) {
        this(objectClass, condition, null, false);
    }

    public ObjectCacheList(Class<T> objectClass, String condition, boolean any) {
        this(objectClass, condition, null, any);
    }

    public ObjectCacheList(Class<T> objectClass, String condition, String orderedBy) {
        this(objectClass, condition, orderedBy, false);
    }

    public ObjectCacheList(Class<T> objectClass, String condition, String orderedBy, boolean any) {
        this(objectClass, StoredObject.query(objectClass, "T.Id", condition, orderedBy, any), any);
    }

    public ObjectCacheList(Class<T> objectClass, Query query) {
        this(objectClass, query, true);
    }

    public ObjectCacheList(Class<T> objectClass, Query query, boolean any) {
        this(new ObjectCache<>(objectClass, query, any));
    }

    public ObjectCacheList(Class<T> objectClass, Iterable<Id> idList) {
        this(new ObjectCache<>(objectClass, idList));
    }

    public ObjectCacheList(Class<T> objectClass, ObjectIterator<T> objects) {
        this(new ObjectCache<>(objectClass, objects));
    }

    public ObjectCacheList(Class<T> objectClass, Stream<T> objects) {
        this(new ObjectCache<>(objectClass, objects));
    }

    private ObjectCacheList(ObjectCache<T> cache) {
        this.original = this.sorted = this.filtered = cache;
    }

    private static <O extends StoredObject> O get(int index, ObjectCache<O> cache) {
        return index >= 0 && index < cache.size() ? cache.get(index) : null;
    }

    @Override
    public void setLoadFilter(Predicate<T> loadFilter) {
        original.setLoadFilter(loadFilter);
    }

    @Override
    public void load(String condition, String orderedBy, boolean any) {
        original.load(condition, orderedBy, any);
        rebuild();
    }

    @Override
    public void load(Query query, boolean any) {
        original.load(query, any);
        rebuild();
    }

    @Override
    public void load(Iterable<Id> idList) {
        original.load(idList);
        rebuild();
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        original.load(objects);
        rebuild();
    }

    @Override
    public void load(Stream<T> objects) {
        original.load(objects);
        rebuild();
    }

    @Override
    public void close() {
        currentFilter = null;
        currentComparator = null;
        if(sorted != original) {
            sorted.close();
        }
        if(filtered != original) {
            filtered.close();
        }
        load(ObjectIterator.create());
    }

    @Override
    public boolean isAllowAny() {
        return original.isAllowAny();
    }

    @Override
    public Class<T> getObjectClass() {
        return original.getObjectClass();
    }

    @Override
    public int size() {
        return sorted.size();
    }

    @Override
    public int size(int startingIndex, int endingIndex) {
        return sorted.size(startingIndex, endingIndex);
    }

    @Override
    public int sizeAll() {
        return original.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        //noinspection unchecked
        return getObjectClass().isAssignableFrom(o.getClass()) && original.contains((T) o);
    }

    @Override
    public Iterator<T> iterator() {
        return sorted.loop();
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[size()];
        int i = 0;
        for(T object: sorted) {
            a[i++] = object;
        }
        return a;
    }

    @Override
    public <O> O[] toArray(O[] a) {
        int size = size();
        if (a.length < size) {
            //noinspection unchecked
            return (O[]) Arrays.copyOf(toArray(), size, a.getClass());
        }
        for(int i = 0; i < a.length; i++) {
            //noinspection unchecked
            a[i] = (O)get(i);
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(T object) {
        return add(object.getId());
    }

    @Override
    public boolean add(Id id) {
        if(!Id.isNull(id)) {
            ArrayList<Id> a = new ArrayList<>();
            a.add(id);
            ObjectCache<T> c = new ObjectCache<>(getObjectClass(), ObjectIterator.create());
            IdIterable ids = new IdIterable();
            ids.add(original.loopIds());
            ids.add(a);
            c.load(ids);
            original.close();
            original = c;
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object object) {
        if(getObjectClass().isAssignableFrom(object.getClass())) {
            //noinspection unchecked
            return remove(((T)object).getId());
        }
        return false;
    }

    private boolean remove(Id id) {
        if(!Id.isNull(id)) {
            if(original.contains(id)) {
                ObjectCache<T> c = original.delete(id);
                if(c != original) {
                    original.close();
                    original = c;
                    rebuild();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o: c) {
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection) {
        if(collection.isEmpty()) {
            return false;
        }
        ObjectCache<T> c = new ObjectCache<>(getObjectClass(), ObjectIterator.create());
        IdIterable ids = new IdIterable();
        ids.add(original.loopIds());
        ids.addObjects(collection);
        c.load(ids);
        original.close();
        original = c;
        rebuild();
        return true;
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends T> collection) {
        if(index >= size()) {
            return addAll(collection);
        }
        if(collection.isEmpty()) {
            return false;
        }
        ObjectCache<T> c = new ObjectCache<>(getObjectClass(), ObjectIterator.create());
        IdIterable ids = new IdIterable();
        ids.add(original.loopIds(0, index));
        ids.addObjects(collection);
        ids.add(original.loopIds(index));
        c.load(ids);
        original.close();
        original = c;
        rebuild();
        return true;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        if(collection.isEmpty()) {
            return true;
        }
        ObjectCache<T> c = original.filter(o -> !collection.contains(o));
        if(c != original) {
            original.close();
            original = c;
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        ObjectCache<T> c = original.filter(filter);
        if(c != original) {
            original.close();
            original = c;
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        if(collection.isEmpty()) {
            clear();
            return true;
        }
        boolean all = containsAll(collection);
        ObjectCache<T> c = original.filter(collection::contains);
        if(c != original) {
            original.close();
            original = c;
            rebuild();
        }
        return all;
    }

    @Override
    public void clear() {
        load(ObjectIterator.create());
    }

    @Override
    public T get(int index) {
        return get(index, sorted);
    }

    @Override
    public T set(int index, T element) {
        T set = get(index);
        ObjectCache<T> c = new ObjectCache<>(getObjectClass(), ObjectIterator.create());
        IdIterable ids = new IdIterable();
        ids.add(original.loopIds(0, index));
        List<Id> id = new ArrayList<>();
        id.add(element.getId());
        ids.add(id);
        ids.add(original.loopIds(index + 1));
        c.load(ids);
        original.close();
        original = c;
        rebuild();
        return set;
    }

    @Override
    public void add(int index, T element) {
        List<T> a = new ArrayList<>();
        a.add(element);
        addAll(index, a);
    }

    @Override
    public T remove(int index) {
        T object = get(index, original);
        if(object == null) {
            return null;
        }
        remove(object);
        return object;
    }

    @Override
    public int indexOf(Id id) {
        return sorted.indexOf(id);
    }

    @Override
    public int indexOf(Object o) {
        //noinspection unchecked
        return getObjectClass().isAssignableFrom(o.getClass()) ? sorted.indexOf((T)o) : -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return sorted.loop();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return sorted.loop(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return sorted.list(fromIndex, toIndex);
    }

    @Override
    public void refresh() {
        original.refresh();
    }

    @Override
    public T refresh(Id id) {
        original.refresh(id);
        return original.get(id);
    }

    @Override
    public T refresh(T object) {
        Id id = object.getId();
        original.refresh(object);
        return original.get(id);
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
        if(comparator == null) {
            return;
        }
        ObjectCache<T> c = original.sort(comparator);
        if(c == original) {
            return;
        }
        original.close();
        original = c;
        rebuild();
    }

    @Override
    public void order(Comparator<? super T> comparator) {
        if(comparator == currentComparator) {
            return;
        }
        if(comparator == null) {
            currentComparator = null;
            if(sorted == filtered) {
                return;
            }
            sorted.close();
            sorted = filtered;
            return;
        }
        if(sorted != filtered) {
            sorted.close();
        }
        currentComparator = comparator;
        sorted = filtered.sort(currentComparator);
    }

    @Override
    public void filter(Predicate<? super T> filter) {
        if(filter == currentFilter) {
            return;
        }
        if(filter == null) {
            currentFilter = null;
            if(filtered == original) {
                return;
            }
            if(sorted != filtered) {
                sorted.close();
            }
            filtered.close();
            filtered = original;
            sorted = currentComparator == null ? filtered : filtered.sort(currentComparator);
            return;
        }
        currentFilter = filter;
        rebuild();
    }

    @Override
    public void filter(Predicate<? super T> filter, Comparator<? super T> comparator) {
        if(filter == currentFilter && comparator == currentComparator) {
            return;
        }
        if(filter == null && comparator == null) {
            if(sorted != filtered) {
                sorted.close();
            }
            if(filtered != original) {
                filtered.close();
            }
            sorted = filtered = original;
            return;
        }
        if(filter == currentFilter) { // Filter not changed
            order(comparator);
            return;
        }
        if(comparator == currentComparator) { // Sorter not changed
            filter(filter);
            return;
        }
        currentComparator = comparator;
        currentFilter = filter;
        rebuild();
    }

    private void rebuild() {
        if(sorted != filtered) {
            sorted.close();
        }
        if(filtered != original) {
            filtered.close();
        }
        filtered = currentFilter == null ? original : original.filter(currentFilter);
        sorted = currentComparator == null ? filtered : filtered.sort(currentComparator);
    }

    @Override
    public final Predicate<? super T> getFilter() {
        return currentFilter;
    }

    @Override
    public final Comparator<? super T> getComparator() {
        return currentComparator;
    }

    @Override
    public Stream<T> stream(int startingIndex, int endingIndex) {
        return sorted.stream(startingIndex, endingIndex);
    }

    @Override
    public Stream<T> streamAll(int startingIndex, int endingIndex) {
        return original.stream(startingIndex, endingIndex);
    }

    @Override
    public int getCacheLevel() {
        return original.getCacheLevel();
    }
}
