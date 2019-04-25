package com.storedobject.core;

import com.storedobject.common.StringList;

public class ClassAttribute < T extends com.storedobject.core.StoredObject > {

    protected java.lang.String moduleName;
    protected java.lang.String tableName;
    protected java.lang.String[] attributes;
    protected StringList displayColumns;
    protected StringList protectedColumns;
    protected StringList browseColumns;
    protected StringList searchColumns;
    protected StringList links;
    protected java.lang.String browseOrder;
    protected boolean[] writeAllowed;
    protected java.lang.reflect.Method[] setMethods;
    protected java.lang.reflect.Method[] getMethods;
    protected int family;
    protected int hints;
    protected boolean saveAllowed;
    protected java.lang.Class < T > objectClass;
    protected int statusUI;

    protected ClassAttribute() {
    }

    public java.lang.reflect.Method getMethod(java.lang.String p1) {
        return null;
    }

    public com.storedobject.core.ClassAttribute <?> getParent() {
        return null;
    }

    public void unload() {
    }

    public StringList getAttributes() {
        return null;
    }

    public java.lang.reflect.Method setMethod(java.lang.String p1) {
        return null;
    }

    public java.lang.String getTitle() {
        return null;
    }

    public boolean writeAllowed(java.lang.String p1) {
        return false;
    }

    public java.lang.Class < T > getObjectClass() {
        return null;
    }

    protected void loadMetaData() {
    }

    public int getFormStyle() {
        return 0;
    }

    public java.lang.String getFormLayout() {
        return null;
    }

    public com.storedobject.core.UIFieldMetadata getFieldMetadata(java.lang.String p1) {
        return null;
    }

    public com.storedobject.core.UIFieldMetadata getFieldMetadata(java.lang.String p1, boolean p2) {
        return null;
    }

    public java.lang.String getModuleName() {
        return null;
    }

    public java.lang.String getTableName() {
        return null;
    }

    public StringList getExtraFields() {
        return null;
    }
    
	public int howBig(boolean any) {
		return 0;
	}
	
	public StringList getAnchors() {
		return null;
	}
}
