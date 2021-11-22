package com.storedobject.core;

import com.storedobject.common.ListLoop;
import com.storedobject.common.Loop;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectCache<T extends StoredObject> implements Iterable<T> {

	public ObjectCache(Class<T> objectClass) {
	}
	
	public ObjectCache(Class<T> objectClass, boolean any) {
	}

	public ObjectCache(Class<T> objectClass, String condition) {
	}
	
	public ObjectCache(Class<T> objectClass, String condition, boolean any) {
	}
	
	public ObjectCache(Class<T> objectClass, String condition, String orderedBy) {
	}
	
	public ObjectCache(Class<T> objectClass, String condition, String orderedBy, boolean any) {
	}

	public ObjectCache(Class<T> objectClass, Query query) {
	}
	
	public ObjectCache(Class<T> objectClass, Query query, boolean any) {
	}

	public ObjectCache(Class<T> objectClass, Iterable<Id> idList) {
	}

	public ObjectCache(Class<T> objectClass, ObjectIterator<T> objects) {
	}

	public ObjectCache(Class<T> objectClass, Stream<T> objects) {
	}

	public void load() {
	}
	
	public void load(boolean any) {
	}
	
	public void load(String condition) {
	}
	
	public void load(String condition, boolean any) {
	}
	
	public void load(String condition, String orderedBy) {
	}
	
	public void load(String condition, String orderedBy, boolean any) {
	}

	public void load(Query query) {
	}
	
	public void load(Query query, boolean any) {
	}
	
	public void load(Iterable<Id> idList) {
	}

	public void load(ObjectIterator<T> objects) {
	}

	public void load(Stream<T> objects) {
	}

	public Class<T> getObjectClass() {
		return null;
	}
	
	public final void setAllowAny(boolean any) {
	}
	
	public boolean isAllowAny() {
		return false;
	}

	public boolean getAllowAny() {
		return false;
	}

	public void setCacheSize(int size) {
	}
	
	public int getCacheSize() {
		return 0;
	}
	
	public void close() {
	}
	
	public void refresh() {
	}
	
	public void refresh(Id id) {
	}
	
	public void refresh(T object) {
	}
	
	public int size() {
		return 0;
	}

	public int size(int startingIndex, int endingIndex) {
		return 0;
	}

	public Id getId(int index) {
		return null;
	}
	
	public boolean contains(Id id) {
		return false;
	}
	
	public boolean contains(T object) {
		return false;
	}
	
	public int indexOf(Id id) {
		return 0;
	}
		
	public int indexOf(T object) {
		return 0;
	}
		
	public T get(int index) {
		return null;
	}
	
	public T get(Id id) {
		return null;
	}
	
	public void put(T object) {
	}
	
	public ListLoop<Id> loopIds() {
		return null;
	}

	public ListLoop<Id> loopIds(int startingIndex) {
		return null;
	}

	public ListLoop<Id> loopIds(int startingIndex, int endingIndex) {
		return null;
	}

	public List<Id> listIds() {
		return null;
	}

	public List<Id> listIds(int startingIndex) {
		return null;
	}

	public List<Id> listIds(int startingIndex, int endingIndex) {
		return null;
	}

	public Loop<Id> getIdLoop() {
		return null;
	}
	
	public IdBuffer getIdBuffer() {
		return null;
	}

	public ObjectCache<T> delete(T object) {
		return null;
	}

	public ObjectCache<T> delete(Id id) {
		return null;
	}

	public ObjectCache<T> filter(Predicate<? super T> filter) {
		return null;
	}
	
	public ObjectCache<T> sort(Comparator<? super T> comparator) {
		return null;
	}
	
	public ListLoop<T> loop() {
		return null;
	}

	public ListLoop<T> loop(int startingIndex) {
		return null;
	}

	public ListLoop<T> loop(int startingIndex, int endingIndex) {
		return null;
	}

	public List<T> list() {
		return null;
	}

	public List<T> list(int startingIndex) {
		return null;
	}

	public List<T> list(int startingIndex, int endingIndex) {
		return null;
	}
	
	@Override
	public Iterator<T> iterator() {
		return loop();
	}

	public Stream<T> stream(int startingIndex, int endingIndex) {
		return Stream.empty();
	}
}
