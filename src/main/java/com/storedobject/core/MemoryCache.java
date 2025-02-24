package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("NullableProblems")
public class MemoryCache<T> implements List<T>, Filtered<T> {

    ArrayList<T> original = new ArrayList<>(), sorted;
    private ArrayList<T> filtered;
    private Predicate<? super T> currentFilter;
    private Comparator<? super T> currentComparator;

    public MemoryCache() {
        sorted = filtered = original;
    }

    public void close() {
        currentFilter = null;
        currentComparator = null;
        original.clear();
        filtered.clear();
        sorted.clear();
        sorted = filtered = original;
    }

    @Override
    public int size() {
        return sorted.size();
    }

    public int size(int startingIndex, int endingIndex) {
        endingIndex = Math.min(endingIndex, size());
        endingIndex = Math.max(0, endingIndex);
        startingIndex = Math.max(startingIndex, 0);
        startingIndex = Math.min(startingIndex, endingIndex);
        return endingIndex - startingIndex;
    }

    public int sizeAll() {
        return original.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return original.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return sorted.iterator();
    }

    @Override
    public Object[] toArray() {
        return sorted.toArray();
    }

    @Override
    public <O> O[] toArray(@Nonnull O[] a) {
        //noinspection unchecked
        return (O[]) sorted.toArray((T[])a);
    }

    @Override
    public boolean add(T object) {
        if(object != null && original.add(object)) {
            rebuild();
        }
        return true;
    }

    @Override
    public boolean remove(Object object) {
        if(original.remove(object)) {
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        return original.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection) {
        if(original.addAll(collection)) {
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends T> collection) {
        if(original.addAll(index, collection)) {
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        if(original.removeAll(collection)) {
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        if(original.removeIf(filter)) {
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        if(original.retainAll(collection)) {
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        close();
    }

    @Override
    public T get(int index) {
        return sorted.get(index);
    }

    @Override
    public T set(int index, T element) {
        T o = original.set(index, element);
        rebuild();
        return o;
    }

    @Override
    public void add(int index, T element) {
        original.add(index, element);
        rebuild();
    }

    @Override
    public T remove(int index) {
        T object = original.remove(index);
        rebuild();
        return object;
    }

    @Override
    public int indexOf(Object o) {
        return sorted.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return sorted.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return sorted.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return sorted.subList(fromIndex, toIndex);
    }

    private static <O> ArrayList<O> sort(Comparator<? super O> comparator, ArrayList<O> list) {
        ArrayList<O> a = new ArrayList<>(list);
        a.sort(comparator);
        return a;
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
        if(comparator == null) {
            return;
        }
        original.sort(comparator);
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
            sorted = filtered;
            return;
        }
        currentComparator = comparator;
        sorted = sort(currentComparator, filtered);
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
            filtered = original;
            sorted = currentComparator == null ? filtered : sort(currentComparator, filtered);
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
            sorted = filtered = original;
            return;
        }
        //noinspection DuplicatedCode
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

    void rebuild() {
        if(currentFilter == null) {
            filtered = original;
        } else {
            filtered = new ArrayList<>();
            original.stream().filter(currentFilter).forEach(filtered::add);
        }
        sorted = currentComparator == null ? filtered : sort(currentComparator, filtered);
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
        return Utility.stream(sorted, startingIndex, endingIndex);
    }

    @Override
    public Stream<T> streamAll(int startingIndex, int endingIndex) {
        return Utility.stream(original, startingIndex, endingIndex);
    }
}
