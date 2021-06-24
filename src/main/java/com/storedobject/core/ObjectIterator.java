package com.storedobject.core;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ObjectIterator<O extends StoredObject> implements Iterator<O>, Iterable<O>, Closeable {

    private final static ObjectIterator<? extends StoredObject> empty = new ObjectIterator<>();

    ObjectIterator() {
    }

    static <O extends StoredObject> ObjectIterator<O> create(Id maxTranId, Transaction transaction, Query sql, ClassAttribute<?> ca) {
        return ObjectIterator.create();
    }

    static <O extends StoredObject> ObjectIterator<O> create(Id maxTranId, Transaction transaction, Query sql, Class<O> objectClass) {
        return ObjectIterator.create();
    }

    static <O extends StoredObject> ObjectIterator<O> createRaw(Id maxTranId, Transaction transaction, Query sql, Class<O> objectClass) {
        return ObjectIterator.create();
    }

    static <O extends StoredObject> ObjectIterator<O> createTree(Class<O> oClass, StoredObject root, Function<StoredObject, ObjectIterator<O>> childrenFunction) {
        return ObjectIterator.create();
    }

    public ObjectIterator<O> add(ObjectIterator<O> iterator) {
        return ObjectIterator.create();
    }

    public final ObjectIterator<O> add(O object) {
        return ObjectIterator.create();
    }

    @Override
    public void close() {
    }

    public Stream<O> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    @Nonnull
    public Iterator<O> iterator() {
        return this;
    }

    @Override
    public O next() {
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    public <TO extends StoredObject> ObjectIterator<TO> map(Function<O, TO> function) {
        return ObjectIterator.create();
    }

    public <TO extends StoredObject> ObjectIterator<TO> convert(ObjectConverter<O, TO> converter) {
        return ObjectIterator.create();
    }

    public <TO extends StoredObject> ObjectIterator<TO> expand(Function<O, ObjectIterator<TO>> function) {
        return ObjectIterator.create();
    }

    public ObjectIterator<O> filter(Predicate<? super O> predicate) {
        return ObjectIterator.create();
    }

    public ObjectIterator<O> filter(BiPredicate<? super O, ? super O> predicate) {
        return ObjectIterator.create();
    }

    public ObjectIterator<O> deduplicate() {
        return filter((o1, o2) -> o1.getId().equals(o2.getId()));
    }

    public O find(Predicate<? super O> predicate) {
        return null;
    }

    public O findFirst() {
        return find(o -> true);
    }

    public ObjectIterator<O> skip(long count) {
        return this;
    }

    public ObjectIterator<O> limit(long count) {
        return this;
    }

    public long count() {
        return 0;
    }

    public long count(Predicate<? super O> filter) {
        return filter(filter).count();
    }

    public double sum(ToDoubleFunction<? super O> function) {
        return 0;
    }

    public double average(ToDoubleFunction<? super O> function) {
        return 0;
    }

    public boolean allMatch(Predicate<? super O> predicate) {
        return true;
    }

    public boolean anyMatch(Predicate<? super O> predicate) {
        return false;
    }

    public boolean noneMatch(Predicate<? super O> predicate) {
        return !anyMatch(predicate);
    }

    @Override
    public void forEach(Consumer<? super O> consumer) {
    }

    public O max(Comparator<? super O> comparator) {
        return null;
    }

    public O min(Comparator<? super O> comparator) {
        return max(comparator.reversed());
    }

    public O select(BiFunction<? super O, ? super O, O> function) {
        return null;
    }

    public Collection<O> collectAll() {
        return collectAll((Collection<O>) null);
    }

    public Collection<O> collectAll(Collection<O> collection) {
        return new ArrayList<>();
    }

    public <T> Collection<T> collectAll(Function<O, T> convertor) {
        return collectAll(null, convertor);
    }

    public <T> Collection<T> collectAll(Collection<T> collection, Function<O, T> convertor) {
        return new ArrayList<>();
    }

    public List<O> toList() {
        return (List<O>)collectAll((Collection<O>)null);
    }

    public <T> List<T> toList(Function<O, T> convertor) {
        return (List<T>)collectAll(null, convertor);
    }

    public O single(Predicate<? super O> filter) {
        return single(filter, true);
    }

    public O single(Predicate<? super O> filter, boolean showError) {
        return filter(filter).single(showError);
    }

    public O single() {
        return single(true);
    }

    public O single(boolean showError) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends StoredObject> ObjectIterator<T> create() {
        return (ObjectIterator<T>) empty;
    }

    public static <T extends StoredObject> ObjectIterator<T> create(T object) {
        return ObjectIterator.create();
    }

    public static <T extends StoredObject> ObjectIterator<T> create(List<T> objects) {
        return ObjectIterator.create();
    }

    public static <T extends StoredObject> ObjectIterator<T> create(Collection<T> objects) {
        return ObjectIterator.create();
    }

    public static <T extends StoredObject> ObjectIterator<T> create(Iterator<T> objects) {
        return ObjectIterator.create();
    }

    public static <FROM extends StoredObject, TO extends StoredObject> ObjectIterator<TO> create(ObjectIterator<FROM> iterator,
                                                                                                 ObjectConverter<FROM, TO> converter) {
        return ObjectIterator.create();
    }
}
