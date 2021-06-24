package com.storedobject.core;

public class ObjectPermissionUser extends com.storedobject.core.ObjectPermission {

    public ObjectPermissionUser() {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public void setLogin(com.storedobject.core.Id p1) {
    }

    public void setLogin(java.math.BigDecimal p1) {
    }

    public void setLogin(com.storedobject.core.SystemUser p1) {
    }

    @com.storedobject.core.annotation.Column(order = 2, required = true, style = "", caption = "")
    public com.storedobject.core.Id getLoginId() {
        return null;
    }

    public com.storedobject.core.SystemUser getLogin() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }
}
