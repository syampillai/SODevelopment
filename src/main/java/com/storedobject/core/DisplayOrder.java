package com.storedobject.core;

public interface DisplayOrder extends Comparable<DisplayOrder> {

	int getDisplayOrder();
	
	default void setDisplayOrder(int displayOrder) {
	}
	
	default int getDisplayOrderGap() {
		return 1;
	}
	
	default String getTitle() {
		return null;
	}
	
	default void setTitle(String title) {
	}
	
	default int compareTo(@SuppressWarnings("NullableProblems") DisplayOrder o) {
		return 0;
	}
}
