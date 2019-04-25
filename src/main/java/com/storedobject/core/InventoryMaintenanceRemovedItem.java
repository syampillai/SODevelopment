package com.storedobject.core;

public class InventoryMaintenanceRemovedItem extends com.storedobject.core.StoredObject {

    public InventoryMaintenanceRemovedItem() {
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public void setItem(com.storedobject.core.Id p1) {
    }

    public void setItem(java.math.BigDecimal p1) {
    }

    public void setItem(com.storedobject.core.InventoryItem p1) {
    }

    @com.storedobject.core.annotation.Column(order = 100, style = "(any)", required = true, caption = "")
    public com.storedobject.core.Id getItemId() {
        return null;
    }

    public com.storedobject.core.InventoryItem getItem() {
        return null;
    }

    public void setClosed(boolean p1) {
    }

    @com.storedobject.core.annotation.Column(order = 400, style = "", required = true, caption = "")
    public boolean getClosed() {
        return false;
    }

    public void validateData() throws java.lang.Exception {
    }
}
