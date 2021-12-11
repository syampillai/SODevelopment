package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.util.*;
import java.util.function.*;

/**
 * Helper class that allows you to iterate through objects retrieved from the DB. Several methods of
 * {@link StoredObject} returns an instance of this class.
 * <p>Note: Please make sure that you call the {@link #close()} method to release DB resources held by this instance
 * once the usage over. However, if you iterate through the whole list or if you use a terminal operation, it will be
 * closed automatically.</p>
 *
 * @param <O> Type of objects of the iterator.
 * @author Syam
 */
public abstract class ObjectIterator<O extends StoredObject> implements Iterator<O>, Iterable<O>, Closeable {

    ObjectIterator() {
    }

    /**
     * Concatenate another instance.
     * @param iterator Another instance.
     * @return A new instance that contains objects from this instance followed by objects from the added instance.
     * There is no need to maintain any references to this instance or to the added one for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> add(ObjectIterator<O> iterator) {
        return this;
    }

    /**
     * Concatenate another instance by adding to the head of it.
     * @param iterator Another instance.
     * @return A new instance that contains objects from the added instance followed by objects from this instance.
     * There is no need to maintain any references to this instance or to the added one for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> addToHead(ObjectIterator<O> iterator) {
        return this;
    }

    /**
     * Add one object to the tail of this instance.
     *
     * @param object Object to add.
     * @return Resulting instance.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public final ObjectIterator<O> add(O object) {
        return this;
    }

    /**
     * Add one object to the head of this instance so that it will be next entry returned.
     *
     * @param object Object to add.
     * @return Resulting instance.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public final ObjectIterator<O> addToHead(O object) {
        return this;
    }

    /**
     * Close this instance and release all associated resources.
     */
    @Override
    public void close() {
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

    /**
     * Convert this iterator to another type.
     *
     * @param function Function to map.
     * @param <TO> To type.
     * @return Converted iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public <TO extends StoredObject> ObjectIterator<TO> map(Function<O, TO> function) {
        //noinspection unchecked
        return (ObjectIterator<TO>) this;
    }

    /**
     * Convert this iterator to another type.
     *
     * @param converter Converter.
     * @param <TO> To type.
     * @return Converted iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public <TO extends StoredObject> ObjectIterator<TO> convert(ObjectConverter<O, TO> converter) {
        //noinspection unchecked
        return (ObjectIterator<TO>) this;
    }

    /**
     * Expand this iterator using the given function. For each item from this iterator, an iterator is derived via
     * the given function and the resulting iterator is a concatenated version of all those iterators streamed
     * in sequence.
     *
     * @param function Function to map.
     * @param <TO> To type.
     * @return Converted iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public <TO extends StoredObject> ObjectIterator<TO> expand(Function<O, ObjectIterator<TO>> function) {
        //noinspection unchecked
        return (ObjectIterator<TO>) this;
    }

    /**
     * Apply a filter.
     *
     * @param predicate Filter to apply.
     * @return Filtered iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> filter(Predicate<? super O> predicate) {
        return this;
    }

    /**
     * Apply a bi-filter.
     *
     * @param predicate Filter to apply.
     * @return Filtered iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> filter(BiPredicate<? super O, ? super O> predicate) {
        return this;
    }

    /**
     * Convert the iterator to a new one by eliminating duplicate consecutive entries if any from it.
     *
     * @return Converted iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> deduplicate() {
        return this;
    }

    /**
     * Find the object matching the given filter.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param predicate Filter.
     * @return The object found or null if nothing matches.
     */
    public O find(Predicate<? super O> predicate) {
        for(O object: this) {
            if(predicate.test(object)) {
                close();
                return object;
            }
        }
        return null;
    }

    /**
     * Find the first object.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @return The object found or null if nothing found.
     */
    public O findFirst() {
        return find(o -> true);
    }

    /**
     * Skip a specified number of entries.
     *
     * @param count Number of entries to drop.
     * @return Self.
     */
    public ObjectIterator<O> skip(long count) {
        while(count > 0) {
            if(!hasNext()) {
                return this;
            }
            next();
            --count;
        }
        return this;
    }

    /**
     * Limit the number of entries. (Rest will be dropped).
     *
     * @param limit Limit.
     * @return Modified iterator.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> limit(long limit) {
        return this;
    }

    /**
     * Get the count.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @return Count.
     */
    public long count() {
        long count = 0;
        while(hasNext()) {
            ++count;
            next();
        }
        return count;
    }

    /**
     * Get the count after applying a filter.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param filter Filter.
     * @return Count.
     */
    public long count(Predicate<? super O> filter) {
        return filter(filter).count();
    }

    /**
     * Get the sum.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param function Function to extract the value from the object.
     * @return Sum.
     */
    public double sum(ToDoubleFunction<? super O> function) {
        double sum = 0;
        for(O object: this) {
            sum += function.applyAsDouble(object);
        }
        return sum;
    }

    /**
     * Get the average.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param function Function to extract the value from the object.
     * @return Average.
     */
    public double average(ToDoubleFunction<? super O> function) {
        double sum = 0;
        int count = 0;
        for(O object: this) {
            ++count;
            sum += function.applyAsDouble(object);
        }
        if(count > 0) {
            sum /= count;
        }
        return sum;
    }

    /**
     * Check whether all objects are matching the given filter or not.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param predicate Filter.
     * @return True/false.
     */
    public boolean allMatch(Predicate<? super O> predicate) {
        for(O object: this) {
            if(!predicate.test(object)) {
                close();
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether any object is matching the given filter or not.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param predicate Filter.
     * @return True/false.
     */
    public boolean anyMatch(Predicate<? super O> predicate) {
        for(O object: this) {
            if(predicate.test(object)) {
                close();
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether no object is matching the given filter or not.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param predicate Filter.
     * @return True/false.
     */
    public boolean noneMatch(Predicate<? super O> predicate) {
        return !anyMatch(predicate);
    }

    /**
     * Consume objects.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param consumer Consumer.
     */
    @Override
    public void forEach(Consumer<? super O> consumer) {
    }

    /**
     * Consume objects while a given predicate is true.
     *
     * @param consumer Consumer.
     * @return An iterator containing the remaining entries. Please note that this could be a different instance.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> consumeWhile(Predicate<O> predicate, Consumer<O> consumer) {
        return this;
    }

    /**
     * Skip objects while a given predicate is true.
     *
     * @return An iterator containing the remaining entries. Please note that this could be a different instance.
     * There is no need to maintain any references to this instance after this operation for the purpose of closing etc.
     * and you need to take care of the resulting instance only.
     */
    public ObjectIterator<O> skipWhile(Predicate<O> predicate) {
        return this;
    }

    /**
     * Find the object that satisfies the "max" condition that specified by the given comparator.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param comparator Comparator.
     * @return Object instance if any found (or null).
     */
    public O max(Comparator<? super O> comparator) {
        O initial = null;
        for(O object: this) {
            if(initial == null) {
                initial = object;
            } else {
                if(comparator.compare(object, initial) > 0) {
                    initial = object;
                }
            }
        }
        return initial;
    }

    /**
     * Find the object that satisfies the "min" condition that specified by the given comparator.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param comparator Comparator.
     * @return Object instance if any found (or null).
     */
    public O min(Comparator<? super O> comparator) {
        return max(comparator.reversed());
    }

    /**
     * Select an object based on the given bi-function (to compare with other entries).
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param function Function to apply.
     * @return Selected object. Could be null if the iterator os already empty.
     */
    public O select(BiFunction<? super O, ? super O, O> function) {
        O initial = null;
        for(O object: this) {
            if(initial == null) {
                initial = object;
            } else {
                initial = function.apply(initial, object);
            }
        }
        return initial;
    }

    /**
     * Select an object randomly. An object is guaranteed to be selected without any bias.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @return Selected object. Could be null if the iterator os already empty.
     */
    public O random() {
        Random r = new Random();
        return select((o1, o2) -> r.nextBoolean() ? o1 : o2);
    }

    /**
     * Collect all entries to a collection.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @return Collection.
     */
    public Collection<O> collectAll() {
        return collectAll((Collection<O>)null);
    }

    /**
     * Collect all entries to a given collection.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param collection Collection to which entries to be added.
     * @return Collection.
     */
    public Collection<O> collectAll(Collection<O> collection) {
        if(collection == null) {
            collection = new ArrayList<>();
        }
        for(O o: this) {
            collection.add(o);
        }
        return collection;
    }

    /**
     * Collect all entries to a collection after converting it to another type.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param convertor Converter.
     * @return Collection.
     */
    public <T> Collection<T> collectAll(Function<O, T> convertor) {
        return collectAll(null, convertor);
    }

    /**
     * Collect all entries to a given collection after converting it to another type.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param collection Collection to which entries to be added.
     * @param convertor Converter.
     * @return Collection.
     */
    public <T> Collection<T> collectAll(Collection<T> collection, Function<O, T> convertor) {
        if(collection == null) {
            collection = new ArrayList<>();
        }
        T object;
        for(O o: this) {
            object = convertor.apply(o);
            if(object != null) {
                collection.add(object);
            }
        }
        return collection;
    }

    /**
     * Collect all entries to a list.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @return List.
     */
    public List<O> toList() {
        return (List<O>)collectAll((Collection<O>)null);
    }

    /**
     * Collect all entries to a list after converting it to another type.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param convertor Converter.
     * @return List.
     */
    public <T> List<T> toList(Function<O, T> convertor) {
        return (List<T>)collectAll(null, convertor);
    }

    /**
     * Get a single entry from this iterator (after applying the filter). Only a single entry is expected and if no
     * entry is found or multiple entries are found, a run-time exception is thrown.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param filter Filter to apply.
     * @return The object.
     */
    public O single(Predicate<? super O> filter) {
        return single(filter, true);
    }

    /**
     * Get a single entry from this iterator (after applying the filter). Only a single entry is expected and if no
     * entry is found or multiple entries are found, <code>null</code> will be returned (based on the "show error"
     * parameter).
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param filter Filter to apply.
     * @param showError Whether to throw a run-time exception or not if no entry is found or multiple entries are found.
     * @return The object.
     */
    public O single(Predicate<? super O> filter, boolean showError) {
        return filter(filter).single(showError);
    }

    /**
     * Get a single entry from this iterator. Only a single entry is expected and if no
     * entry is found or multiple entries are found, a run-time exception is thrown.
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @return The object.
     */
    public O single() {
        return single(true);
    }

    /**
     * Get a single entry from this iterator. Only a single entry is expected and if no
     * entry is found or multiple entries are found, <code>null</code> will be returned (based on the "show error"
     * parameter).
     * <p>Note: This is a terminal operation and the iterator is closed after this operation.</p>
     *
     * @param showError Whether to throw a run-time exception or not if no entry is found or multiple entries are found.
     * @return The object.
     */
    public O single(boolean showError) {
        O single = null;
        for(O o: this) {
            if(single != null) {
                if(single.getId().equals(o.getId())) {
                    continue;
                }
                close();
                if(showError) {
                    throw new SORuntimeException("Multiple objects found in iterator");
                }
                return null;
            }
            single = o;
        }
        return single;
    }

    /**
     * Create an empty iterator.
     *
     * @param <T> Type of object of the iterator to create.
     * @return Iterator created.
     */
    public static <T extends StoredObject> ObjectIterator<T>create() {
        return new ObjectIterator<>() {
        };
    }

    /**
     * Create an iterator from a single object.
     *
     * @param object Object to be included.
     * @param <T> Type of object of the iterator to create.
     * @return Iterator created.
     */
    public static <T extends StoredObject> ObjectIterator<T>create(T object) {
        return create();
    }

    /**
     * Create an iterator from the given objects.
     *
     * @param objects Objects to be included.
     * @param <T> Type of object of the iterator to create.
     * @return Iterator created.
     */
    public static <T extends StoredObject> ObjectIterator<T>create(List<T> objects) {
        return create();
    }

    /**
     * Create an iterator from the given objects.
     *
     * @param objects Objects to be included.
     * @param <T> Type of object of the iterator to create.
     * @return Iterator created.
     */
    public static <T extends StoredObject> ObjectIterator<T>create(Collection<T> objects) {
        return create();
    }

    /**
     * Create an iterator from the given objects.
     *
     * @param objects Objects to be included.
     * @param <T> Type of object of the iterator to create.
     * @return Iterator created.
     */
    public static <T extends StoredObject> ObjectIterator<T>create(Iterator<T> objects) {
        return create();
    }

    /**
     * Create an iterator from the given objects.
     *
     * @param objects Objects to be included.
     * @param <T> Type of object of the iterator to create.
     * @return Iterator created.
     */
    @SafeVarargs
    public static <T extends StoredObject> ObjectIterator<T>create(T... objects) {
        return create(List.of(objects));
    }

    /**
     * Create an iterator from the given iterator with conversion.
     *
     * @param iterator Objects from the iterator to be included.
     * @param converter Converter.
     * @param <FROM> Type if object of the source.
     * @param <TO> Type of object of the iterator to create.
     * @return Iterator created.
     */
    public static <FROM extends StoredObject, TO extends StoredObject> ObjectIterator<TO>create(
            ObjectIterator<FROM> iterator, ObjectConverter<FROM, TO> converter) {
        return create();
    }

    /**
     * Create an iterator from some source.
     *
     * @param from Source.
     * @param converter Converter to convert the source to an instance of TO.
     * @param <TO> Type of object of the iterator to create.
     * @param <FROM> Type of the source.
     * @return Iterator created.
     */
    public static <TO extends StoredObject, FROM> ObjectIterator<TO> create(Iterable<FROM> from,
                                                                            Function<FROM, TO> converter) {
        return create();
    }

    /**
     * Create an iterator from some source.
     *
     * @param from Source.
     * @param converter Converter to convert the source to an instance of TO.
     * @param <TO> Type of object of the iterator to create.
     * @param <FROM> Type of the source.
     * @return Iterator created.
     */
    public static <TO extends StoredObject, FROM> ObjectIterator<TO> create(Iterator<FROM> from,
                                                                            Function<FROM, TO> converter) {
        return create();
    }
}
