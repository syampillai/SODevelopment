package com.storedobject.core;

import com.storedobject.common.StringList;

public class InventoryReceiptInformation extends com.storedobject.core.StoredObject {

    public InventoryReceiptInformation() {
    }

    @com.storedobject.core.annotation.Column(order = 200, style = "", required = true, caption = "")
    public java.sql.Date getDate() {
        return null;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public static java.lang.String[] protectedColumns() {
        return null;
    }

    public StringList readOnlyColumns() {
        return null;
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

    public void setDate(java.sql.Date p1) {
    }

    public void setReceivedFrom(com.storedobject.core.Id p1) {
    }

    public void setReceivedFrom(java.math.BigDecimal p1) {
    }

    public void setReceivedFrom(com.storedobject.core.Entity p1) {
    }

    @com.storedobject.core.annotation.Column(order = 300, style = "", required = true, caption = "")
    public com.storedobject.core.Id getReceivedFromId() {
        return null;
    }

    public com.storedobject.core.Entity getReceivedFrom() {
        return null;
    }

    public void setClosed(boolean p1) {
    }

    @com.storedobject.core.annotation.Column(order = 400, style = "", required = true, caption = "")
    public boolean getClosed() {
        return false;
    }

    public void setRemark(java.lang.String p1) {
    }

    @com.storedobject.core.annotation.Column(order = 500, style = "", required = false, caption = "")
    public java.lang.String getRemark() {
        return null;
    }

    public void setGRN(com.storedobject.core.Id p1) {
    }

    public void setGRN(java.math.BigDecimal p1) {
    }

    public void setGRN(com.storedobject.core.InventoryReceiptItem p1) {
    }

    @com.storedobject.core.annotation.Column(order = 0, style = "", required = false, caption = "")
    public com.storedobject.core.Id getGRNId() {
        return null;
    }

    public com.storedobject.core.InventoryReceiptItem getGRN() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }
}
