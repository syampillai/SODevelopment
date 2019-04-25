package com.storedobject.core;

public interface DisplayOrder extends Comparable<DisplayOrder> {

	public int getDisplayOrder();
	
	public default void setDisplayOrder(int displayOrder) {
	}
	
	public default int getDisplayOrderGap() {
		return 1;
	}
	
	public default String getTitle() {
		return null;
	}
	
	public default void setTitle(String title) {
	}
	
	public default int compareTo(DisplayOrder o) {
		return 0;
	}
}
