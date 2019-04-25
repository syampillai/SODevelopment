package com.storedobject.core;

import java.sql.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractCheckList extends StoredObject implements HasChildren {
	
    public AbstractCheckList() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setCompleteBefore(ComputedDate completeBefore) {
    }

    public void setCompleteBefore(Object value) {
    }

    public ComputedDate getCompleteBefore() {
        return null;
    }

    public void setCompletedOn(Date completedOn) {
    }

    public Date getCompletedOn() {
        return null;
    }

    public void setCompleted(boolean completed) {
    }

    public boolean getCompleted() {
        return false;
    }
    
    public boolean checkCompleteness() {
        return false;
    }

    public ObjectIterator<AbstractCheckListItem> listItems() {
    	return null;
    }
    	
    public static <T extends AbstractCheckList>
    void populate(T checkList, CheckListTemplate template, TransactionManager tm) throws Exception {
    }
    
    
    public static <T extends AbstractCheckList>
    void populate(T checkList, CheckListTemplate template, TransactionManager tm, Consumer<T> setValues) throws Exception {
    }
    
    public static <T extends AbstractCheckList, IT extends AbstractCheckListItem>
    void populate(T checkList, CheckListTemplate template, TransactionManager tm, Consumer<T> setValues, BiConsumer<IT, T> setItemValues)
    		throws Exception {
    }
    
    public AbstractCheckList getCheckList() {
    	return null;
    }
    
    public AbstractCheckList getCheckList(Transaction transaction) {
    	return null;
    }
}