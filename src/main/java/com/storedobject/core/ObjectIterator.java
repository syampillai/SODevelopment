package com.storedobject.core;

import java.io.Closeable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class ObjectIterator<O extends StoredObject> implements Iterator<O>, Iterable<O>, Closeable {

	protected ObjectIterator() {
	}

	protected static <O extends StoredObject> ObjectIterator<O> create(Id maxTranId, Transaction transaction, Query sql, ClassAttribute<?> ca) {
		return null;
	}

	protected static <O extends StoredObject> ObjectIterator<O> create(Id maxTranId, Transaction transaction, Query sql, Class<O> objectClass) {
		return null;
	}

	protected static <O extends StoredObject> ObjectIterator<O> createRaw(Id maxTranId, Transaction transaction, Query sql, Class<O> objectClass) {
		return null;
	}

	public ObjectIterator<O> add(ObjectIterator<O> iterator) {
		return null;
	}

	public final ObjectIterator<O> add(O object) {
		return null;
	}

	@Override
	public void close() {
	}
	
	public Stream<O> stream() {
		return null;
	}

	@Override
	public Iterator<O> iterator() {
		return null;
	}

	@Override
	public O next() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public final void remove() {
	}
	
	public <TO extends StoredObject> ObjectIterator<TO> map(Function<O, TO> function) {
		return null;
	}
	
	public <TO extends StoredObject> ObjectIterator<TO> convert(ObjectConverter<O, TO> converter) {
		return null;
	}
	
	public ObjectIterator<O> filter(Predicate<? super O> predicate) {
		return null;
	}

	public ObjectIterator<O> filter(BiPredicate<? super O, ? super O> predicate) {
		return null;
	}
	
	public ObjectIterator<O> deduplicate() {
		return null;
	}

	public O find(Predicate<? super O> predicate) {
		return null;
	}
	
	public O findFirst() {
		return null;
	}
    
    public long count() {
    	return 0;
    }

    public ObjectIterator<O> skip(long count) {
    	return null;
    }
    
    public long count(Predicate<? super O> filter) {
    	return 0;
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
    	return false;
    }
    
    public void forEach(Consumer<? super O> consumer) {
    }
    
    public O max(Comparator<? super O> comparator) {
		return null;
    }
        
    public O min(Comparator<? super O> comparator) {
		return null;
    }
    
    public O select(BiFunction<? super O, ? super O, O> function) {
    	return null;
    }
    
	public Collection<O> collectAll() {
		return null;
	}

	public Collection<O> collectAll(Collection<O> collection) {
		return null;
	}
	
	public <T> Collection<T> collectAll(Function<O, T> convertor) {
		return null;
	}
	
	public <T> Collection<T> collectAll(Collection<T> collection, Function<O, T> convertor) {
		return null;
	}
	
	public O single(Predicate<? super O> filter) {
		return null;
	}

	public O single(Predicate<? super O> filter, boolean showError) {
		return null;
	}

	public O single() {
		return null;
	}

	public O single(boolean showError) {
		return null;
	}

	public static <T extends StoredObject> ObjectIterator<T>create() {
		return null;
	}

	public static <T extends StoredObject> ObjectIterator<T>create(T object) {
		return null;
	}

	public static <T extends StoredObject> ObjectIterator<T>create(List<T> objects) {
		return null;
	}

	public static <T extends StoredObject> ObjectIterator<T>create(Collection<T> objects) {
		return null;
	}

	public static <T extends StoredObject> ObjectIterator<T>create(Iterator<T> objects) {
		return null;
	}

	public static <FROM extends StoredObject, TO extends StoredObject> ObjectIterator<TO>create(ObjectIterator<FROM> iterator,
																								ObjectConverter<FROM, TO> converter) {
		return null;
	}
}
