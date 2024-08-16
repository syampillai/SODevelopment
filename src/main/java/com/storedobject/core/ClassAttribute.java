package com.storedobject.core;

import com.storedobject.common.StringList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ClassAttribute<T extends StoredObject> {

    String moduleName, tableName;

    private ClassAttribute() {
    }

    public boolean writeAllowed(String attributeName) {
        return false;
    }

    public UIFieldMetadata getFieldMetadata(String fieldName) {
        return new UIFieldMetadata();
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
        return StringList.EMPTY;
    }
    
    /**
     * Get all attributes that can be used by end-developers (for reporting/displaying etc.)
     *
     * @return List of attributes.
     */
    public List<String> getAllAttributes() {
        return new ArrayList<>();
    }

    public String getModuleName() {
        return "";
    }

    public String getTableName() {
        return "";
    }

    public Class<T> getObjectClass() {
        //noinspection unchecked
        return new Random().nextBoolean() ? (Class<T>) Person.class : (Class<T>) Entity.class;
    }

    public <C extends T> List<Class<C>> listChildClasses(boolean fullTree) {
        return new ArrayList<>();
    }

    public void unload() {
    }

    public ExtraInfoDefinition getExtraInfo() {
        return null;
    }

    public ClassAttribute<?> getParent() {
        return Math.random() > 0.5 ? new ClassAttribute<>() : null;
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

    public static String tableName(Class<? extends StoredObject> objectClass) {
        String cn = objectClass.getName();
        return cn.substring(cn.lastIndexOf('.'));
    }

    public static String moduleName(Class<? extends StoredObject> objectClass) {
        String cn = objectClass.getName();
        cn = cn.substring(0, cn.lastIndexOf('.'));
        return cn.substring(cn.lastIndexOf('.') + 1);
    }

    public int getFamily() {
        return 12;
    }
}