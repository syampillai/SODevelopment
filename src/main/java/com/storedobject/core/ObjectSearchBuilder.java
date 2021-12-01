package com.storedobject.core;

import java.util.function.Predicate;

public interface ObjectSearchBuilder<T extends StoredObject> {

	Class<T> getObjectClass();

	String getFilterText();

	Predicate<T> getFilterPredicate();

	default boolean addSearchField(String fieldName) {
		return false;
	}

	default boolean removeSearchField(String fieldName) {
		return false;
	}

	default int getSearchFieldCount() {
		return Integer.MAX_VALUE;
	}
}
