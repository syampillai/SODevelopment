package com.storedobject.core;

public class LogicGroup extends StoredObject implements DisplayOrder {

	public LogicGroup() {
	}

	public static void columns(Columns columns) {
	}

	public static LogicGroup get(String name) {
		return null;
	}

	public static ObjectIterator<LogicGroup> list(String name) {
		return null;
	}

	public String getName() {
		return null;
	}

	public void setName(String name) {
	}

	public String getTitle() {
		return null;
	}

	public void setTitle(String title) {
	}

	public void setDisplayOrder(int displayOrder) {
	}

	public int getDisplayOrder() {
		return 0;
	}

	protected ObjectIterator<Logic> list(int deviceId, int deviceWidth, int deviceHeight) {
		return null;
	}
}
