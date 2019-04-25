package com.storedobject.core;

public class InventoryItemServiceability extends com.storedobject.core.StoredObject {

    public InventoryItemServiceability() {
    }

    public static java.lang.String[] values() {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 2, required = true, style = "", caption = "")
    public java.lang.String getName() {
        return null;
    }

    public void setName(java.lang.String p1) {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public java.lang.String getUniqueCondition() {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 1, required = true, style = "", caption = "")
    public int getSerial() {
        return 0;
    }

    public static int hints() {
        return 0;
    }

    public void setSerial(int p1) {
    }

    public void validateData() throws java.lang.Exception {
    }
}
