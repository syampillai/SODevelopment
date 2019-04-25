package com.storedobject.core;

import java.math.BigDecimal;

public abstract class AbstractTask extends Name {

	protected int periodicity = 0;

    public AbstractTask() {
    }

    public static void columns(Columns columns) {
    }

    public void setTaskGroup(Id taskGroupId) {
    }

    public void setTaskGroup(BigDecimal idValue) {
    }

    public void setTaskGroup(TaskGroup taskGroup) {
    }

    public Id getTaskGroupId() {
        return null;
    }

    public TaskGroup getTaskGroup() {
        return null;
    }

    public void setPeriodicity(int periodicity) {
	}

	public int getPeriodicity() {
		return 0;
	}

    public void setRemarks(String remarks) {
    }

    public String getRemarks() {
        return null;
    }

    public void setInactive(boolean inactive) {
    }

    public boolean getInactive() {
        return false;
    }
    
    public abstract boolean isDue();
    
    public abstract void computeNextDue();
    
    public abstract String getNextDueValue();

    public abstract String formatNextDue();
}