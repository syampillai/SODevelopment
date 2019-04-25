package com.storedobject.core;

public class ObjectPermissionGroup extends com.storedobject.core.ObjectPermission {

    public ObjectPermissionGroup() {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public void setUserGroup(com.storedobject.core.Id p1) {
    }

    public void setUserGroup(java.math.BigDecimal p1) {
    }

    public void setUserGroup(com.storedobject.core.SystemUserGroup p1) {
    }

    @com.storedobject.core.annotation.Column(order = 2, required = true, style = "", caption = "")
    public com.storedobject.core.Id getUserGroupId() {
        return null;
    }

    public com.storedobject.core.SystemUserGroup getUserGroup() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }
}
