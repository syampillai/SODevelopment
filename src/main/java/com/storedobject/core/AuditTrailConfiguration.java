package com.storedobject.core;

public class AuditTrailConfiguration extends StoredObject {

    public static void columns(Columns columns) {
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

    public void setDisplayFields(String displayFields) {
    }

    public String getDisplayFields() {
        return null;
    }

    public void setSearchFields(String searchFields) {
    }

    public String getSearchFields() {
        return null;
    }

    public void setLinks(int links) {
    }

    public int getLinks() {
        return 0;
    }

    public static String[] getLinksValues() {
        return null;
    }

    public static String getLinksValue(int value) {
        return null;
    }

    public String getLinksValue() {
        return null;
    }

    public void setMenu(boolean menu) {
    }

    public boolean getMenu() {
        return false;
    }

    public Class<? extends StoredObject> getObjectClass() throws Exception {
        return null;
    }

    public static <O extends StoredObject> AuditTrailConfiguration getByClass(Class<O> objectClass) {
        return null;
    }

    public static AuditTrailConfiguration get(String name) {
        return null;
    }

    public static ObjectIterator<AuditTrailConfiguration> list(String name) {
        return null;
    }
}
