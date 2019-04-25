package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryDocumentConfiguration extends StoredObject implements OfEntity {

    public InventoryDocumentConfiguration() {
    }

    public static void columns(Columns columns) {
    }

    public void setType(int type) {
    }

    public int getType() {
        return 0;
    }

    public void setCategory(int category) {
    }

    public int getCategory() {
        return 0;
    }

    public static String[] getCategoryValues() {
        return null;
    }

    public static String getCategoryValue(int value) {
        return null;
    }

    public String getCategoryValue() {
        return null;
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setClassName(String className) {
    }

    public String getClassName() {
        return null;
    }

	public void setSerialPrefix(String serialPrefix) {
	}

    public String getSerialPrefix() {
        return null;
	}

	public void setHandleExcess(boolean handleExcess) {
    }

    public boolean getHandleExcess() {
        return false;
    }

    public void setHandleAPN(boolean handleAPN) {
    }

    public boolean getHandleAPN() {
        return false;
    }

    public void setActionNameForActivation(String actionNameForActivation) {
    }

    public String getActionNameForActivation() {
        return null;
    }

    public void setActionNameForActivated(String actionNameForActivated) {
    }

    public String getActionNameForActivated() {
        return null;
    }

    public void setActionNameForCancellation(String actionNameForCancellation) {
    }

    public String getActionNameForCancellation() {
        return null;
    }

    public void setActionNameForCancelled(String actionNameForCancelled) {
    }

    public String getActionNameForCancelled() {
        return null;
    }

    public void setShortNameWithAction(String shortNameWithAction) {
    }

    public String getShortNameWithAction() {
        return null;
    }
    
	public void setLabelForGRN(String labelForGRN) {
	}

	public String getLabelForGRN() {
		return null;
	}

    public void setLabelForDetail(String labelForDetail) {
	}

	public String getLabelForDetail() {
        return null;
	}

	public void setLabelForOther(String labelForOther) {
	}

	public String getLabelForOther() {
        return null;
	}

    public void setDescription(String description) {
    }

    public String getDescription() {
        return null;
    }

    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return null;
    }

    public SystemEntity getSystemEntity() {
        return null;
    }

	public static InventoryDocumentConfiguration get(Id systemEntityId, int type, int category) {
        return null;
	}

	public static InventoryDocumentConfiguration get(Id systemEntityId, Class<?> documentClass, int category) {
        return null;
	}

	public static InventoryDocumentConfiguration get(Id systemEntityId, String className, int category) {
        return null;
	}

	public static InventoryDocumentConfiguration get(Class<?> documentClass, int category, TransactionManager tm) {
        return null;
	}
	public static String[] names(Id systemEntityId, int category) {
        return null;
	}
	
	public String toString() {
        return null;
	}
}
