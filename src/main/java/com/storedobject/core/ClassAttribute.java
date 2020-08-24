package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * For internal use only.
 */
public final class ClassAttribute<T extends StoredObject> {

    protected String moduleName, tableName;
    protected String[] attributes;
    protected StringList displayColumns, protectedColumns, browseColumns, searchColumns, links;
    private StringList extraFields;
    protected String browseOrder;
    protected boolean[] writeAllowed;
    protected Method[] setMethods, getMethods, fileMethods;
    protected int family, hints = -1;
    protected boolean saveAllowed;
    protected Class<T> objectClass;
    protected int statusUI = 0;

    private ClassAttribute() {
    }

    public boolean writeAllowed(String attributeName) {
        return false;
    }

    public UIFieldMetadata getFieldMetadata(String fieldName) {
        return null;
    }

    public UIFieldMetadata getFieldMetadata(String fieldName, boolean external) {
        return null;
    }

    public Method getMethod(String attributeName) {
        return null;
    }

    public Method setMethod(String attributeName) {
        return null;
    }

    public Method[] fileMethods() {
        return null;
    }

    public StringList getAttributes() {
        return null;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getTableName() {
        return tableName;
    }

    public Class<T> getObjectClass() {
        return objectClass;
    }

    public void unload() {
    }

    public ClassAttribute<?> getParent() {
        return new ClassAttribute<>();
    }

    public int getFormStyle() {
        return 0;
    }

    public String getTitle() {
        return "";
    }

    public String getFormLayout() {
        return "";
    }

    public StringList getExtraFields() {
        return StringList.EMPTY;
    }

    public int howBig(boolean any) {
        return 0;
    }

    public StringList getAnchors() {
        return StringList.EMPTY;
    }

    public static ClassAttribute<?> get(int family) {
        return new ClassAttribute<>();
    }

    public static <O extends StoredObject> ClassAttribute<O> get(O object) {
        return new ClassAttribute<>();
    }

    public static <O extends StoredObject> ClassAttribute<O> get(Class<O> objectClass) {
        return new ClassAttribute<>();
    }
}