package com.storedobject.mail;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

public class SenderGroup extends StoredObject {

    private String name;
    private boolean alert;

    public SenderGroup() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Alert", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    public String getUniqueCondition() {
        return "lower(Name)='" + toCode(name).trim().toLowerCase().replace("'", "''") + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public static SenderGroup get(String name) {
        name = toCode(name);
        return StoredObjectUtility.get(SenderGroup.class, "Name", name, false);
    }

    public static ObjectIterator <SenderGroup> list(String name) {
        name = toCode(name);
        return StoredObjectUtility.list(SenderGroup.class, "Name", name, false);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 1)
    public String getName() {
        return name;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    @Column(order = 2)
    public boolean getAlert() {
        return alert;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        name = toCode(name);
        super.validateData(tm);
    }
    
    @Override
    public String toString() {
    	if(alert) {
    		return name + " (Alert)";
    	}
    	return name;
    }
    
    @Override
    public String toDisplay() {
    	return name;
    }
}
