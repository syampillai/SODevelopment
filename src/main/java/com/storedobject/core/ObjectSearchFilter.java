package com.storedobject.core;

import com.storedobject.common.FilterProvider;

import java.util.function.Predicate;

public class ObjectSearchFilter {

	public String getFilter() {
		return null;
	}

	public String getFilter(String extraCondition) {
		return null;
	}

	public void set(ObjectSearchFilter filter) {
	}

	public String getCondition() {
		return null;
	}

	public void setCondition(String condition) {
	}

	public FilterProvider getFilterProvider() {
		return null;
	}

	public void setFilterProvider(FilterProvider filterProvider) {
	}

	public Predicate<StoredObject> getPredicate() {
		return null;
	}
	
	public Predicate<StoredObject> getPredicate(String extraCondition) {
		return null;
	}

	public <T extends StoredObject> T filter(T object) {
		return null;
	}

	public <T extends StoredObject> T filter(T object, String extraCondition) {
		return null;
	}
}
