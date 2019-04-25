package com.storedobject.core;

import java.util.function.Predicate;

public interface ObjectSearchBuilder<T extends StoredObject> {

	Class<T> getObjectClass();
	
	boolean addSearchField(String fieldName);
    
	boolean removeSearchField(String fieldName);
	
	String getFilterText();

	default int getSearchFieldCount() {
		return 0;
	}
	
	default Predicate<T> getFilterPredicate() {
		return null;
	}
}