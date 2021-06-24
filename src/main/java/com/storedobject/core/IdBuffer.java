package com.storedobject.core;

import com.storedobject.common.ListLoop;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class IdBuffer implements Iterable<Id> {
	
	protected final static int MAX_CACHE = 10;
	
	public IdBuffer(Query query) {
	}

	public IdBuffer(Iterable<Id> idList) {
	}

	public void close() {
	}
	
	public int size() {
		return 0;
	}
	
	public void swap(int firstIndex, int secondIndex) {
	}
	
	public boolean contains(Id id) {
		return false;
	}
	
	public int indexOf(Id id) {
		return -1;
	}
	
	public Id get(int index) {
		return null;
	}

	public ListLoop<Id> loop() {
		return null;
	}

	public ListLoop<Id> loop(int startingIndex) {
		return null;
	}

	public ListLoop<Id> loop(int startingIndex, int endingIndex) {
		return null;
	}

	public List<Id> list() {
		return null;
	}

	public List<Id> list(int startingIndex) {
		return null;
	}

	public List<Id> list(int startingIndex, int endingIndex) {
		return null;
	}
	
	public IdBuffer filter(Predicate<Id> filter) {
		return null;
	}
	
	public IdBuffer sort(Comparator<Id> comparator) {
		return null;
	}
	
	@Override
	public Iterator<Id> iterator() {
		return null;
	}
}
