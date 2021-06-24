package com.storedobject.core;

import com.storedobject.common.HasName;

public final class CheckListTemplateItem extends StoredObject implements HasName, Detail {
	
    public CheckListTemplateItem() {
    }

    public static void columns(Columns columns) {
    }

    public void setDescription(String description) {
    }

    public String getDescription() {
        return null;
    }
    
    public String getName() {
        return null;
    }

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
		return false;
	}
	
    public void setDisplayOrder(int displayOrder) {
    }
    
    public int getDisplayOrder() {
    	return 0;
    }
}