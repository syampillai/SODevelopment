package com.storedobject.core;

public class ObjectPermission extends com.storedobject.core.StoredObject {

    public ObjectPermission() {
    }

    public static com.storedobject.core.ObjectPermission get(com.storedobject.core.SystemUser p1, java.lang.Class <? extends com.storedobject.core.StoredObject > p2) {
        return null;
    }

    public static com.storedobject.core.ObjectPermission getDefault() {
        return null;
    }

    public void setPermission(int p1) {
    }

    @com.storedobject.core.annotation.Column(order = 3, required = false, style = "", caption = "")
    public int getPermission() {
        return 0;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public void setClassFamily(int p1) {
    }

    @com.storedobject.core.annotation.Column(order = 1, required = false, style = "", caption = "")
    public int getClassFamily() {
        return 0;
    }

    public static java.lang.String[] getPermissionBitValues() {
        return null;
    }

    public static java.lang.String getPermissionValue(int p1) {
        return null;
    }

    public java.lang.String getPermissionValue() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }
}
