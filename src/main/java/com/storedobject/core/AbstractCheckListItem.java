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

	@Override
	public final boolean isDetailOf(Class <? extends StoredObject > masterClass) {
		return false;
	}

	public void setDisplayOrder(int displayOrder) {
	}

	public int getDisplayOrder() {
		return 0;
	}
}