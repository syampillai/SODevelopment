package com.storedobject.core;

public abstract class AbstractCheckListItem extends AbstractCheckList implements Detail {

	public AbstractCheckListItem() {
	}

	public static void columns(Columns columns) {
	}

	public String getName() {
		return null;
	}

	public void setDescription(String description) {
	}

	public String getDescription() {
		return null;
	}

	public Id getUniqueId() {
		return null;
	}

	public void copyValuesFrom(Detail detail) {
	}

	public final boolean isDetailOf(Class <? extends StoredObject > masterClass) {
		return false;
	}

	public void setDisplayOrder(int displayOrder) {
	}

	public int getDisplayOrder() {
		return 0;
	}
}