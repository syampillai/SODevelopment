package com.storedobject.core;

public class AuditTrailConfiguration extends com.storedobject.core.StoredObject {

    public AuditTrailConfiguration() {
    }

    public static com.storedobject.core.AuditTrailConfiguration get(java.lang.String p1) {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 1, caption = "", required = false, style = "")
    public java.lang.String getName() {
        return null;
    }

    public void setName(java.lang.String p1) {
    }

    public static com.storedobject.core.ObjectIterator < com.storedobject.core.AuditTrailConfiguration > list(java.lang.String p1) {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 2, caption = "", required = true, style = "")
    public java.lang.String getClassName() {
        return null;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public static java.lang.String[] browseColumns() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }

    public static java.lang.String[] links() {
        return null;
    }

    public java.lang.String getUniqueCondition() {
        return null;
    }

    public static java.lang.String[] searchColumns() {
        return null;
    }

    public void setClassName(java.lang.String p1) {
    }

    public void setDisplayFields(java.lang.String p1) {
    }

    @com.storedobject.core.annotation.Column(order = 3, caption = "", required = false, style = "")
    public java.lang.String getDisplayFields() {
        return null;
    }

    public void setSearchFields(java.lang.String p1) {
    }

    @com.storedobject.core.annotation.Column(order = 4, caption = "", required = false, style = "")
    public java.lang.String getSearchFields() {
        return null;
    }

    public void setLinks(int p1) {
    }

    @com.storedobject.core.annotation.Column(order = 5, caption = "Links Display", required = true, style = "")
    public int getLinks() {
        return 0;
    }

    public static java.lang.String[] getLinksValues() {
        return null;
    }

    public static java.lang.String getLinksValue(int p1) {
        return null;
    }

    public java.lang.String getLinksValue() {
        return null;
    }

    public void setMenu(boolean p1) {
    }

    @com.storedobject.core.annotation.Column(order = 6, caption = "", required = true, style = "")
    public boolean getMenu() {
        return false;
    }

    public java.lang.Class <? extends com.storedobject.core.StoredObject > getObjectClass() throws java.lang.Exception {
        return null;
    }

    public static < O extends com.storedobject.core.StoredObject > com.storedobject.core.AuditTrailConfiguration getByClass(java.lang.Class < O > p1) {
        return null;
    }
}
