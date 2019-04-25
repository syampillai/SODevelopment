package com.storedobject.core;

public class InventoryAPNReceived extends com.storedobject.core.StoredObject {

    public InventoryAPNReceived() {
    }

    public static java.lang.String[] getDocumentTypeValues() {
        return null;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public java.lang.String getUniqueCondition() {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 2, style = "", required = true, caption = "")
    public int getDocumentNo() {
        return 0;
    }

    @com.storedobject.core.annotation.Column(order = 1, style = "", required = true, caption = "")
    public int getDocumentType() {
        return 0;
    }

    @com.storedobject.core.annotation.Column(order = 3, style = "(any)", required = true, caption = "")
    public com.storedobject.core.Id getItemId() {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 4, style = "(any)", required = true, caption = "")
    public com.storedobject.core.Id getAlternateItemId() {
        return null;
    }

    public void setDocumentType(int p1) {
    }

    public static java.lang.String getDocumentTypeValue(int p1) {
        return null;
    }

    public java.lang.String getDocumentTypeValue() {
        return null;
    }

    public void setDocumentNo(int p1) {
    }

    public void setItem(com.storedobject.core.Id p1) {
    }

    public void setItem(java.math.BigDecimal p1) {
    }

    public void setItem(com.storedobject.core.InventoryItemType p1) {
    }

    public com.storedobject.core.InventoryItemType getItem() {
        return null;
    }

    public void setAlternateItem(com.storedobject.core.Id p1) {
    }

    public void setAlternateItem(java.math.BigDecimal p1) {
    }

    public void setAlternateItem(com.storedobject.core.InventoryItemType p1) {
    }

    public com.storedobject.core.InventoryItemType getAlternateItem() {
        return null;
    }

    public void setQuantity(com.storedobject.core.Quantity p1) {
    }

    public void setQuantity(java.lang.Object p1) {
    }

    @com.storedobject.core.annotation.Column(order = 5, style = "", required = true, caption = "")
    public com.storedobject.core.Quantity getQuantity() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }
}
