package com.storedobject.core;

public final class InventoryStock extends com.storedobject.core.StoredObject {

    static java.sql.Date OPENING_DATE;

    public InventoryStock() {
    }

    public static com.storedobject.core.InventoryStock get(com.storedobject.core.Transaction p1, com.storedobject.core.Id p2, com.storedobject.core.Id p3, java.sql.Date p4) {
        return null;
    }

    public static com.storedobject.core.InventoryStock get(com.storedobject.core.Id p1, com.storedobject.core.Id p2, java.sql.Date p3) {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 3, style = "", required = true, caption = "")
    public java.sql.Date getDate() {
        return null;
    }

    public static void columns(com.storedobject.core.Columns p1) {
    }

    public static void indices(com.storedobject.core.Indices p1) {
    }

    public java.lang.String getUniqueCondition() {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 1, style = "(any)", required = true, caption = "")
    public com.storedobject.core.Id getStoreId() {
        return null;
    }

    @com.storedobject.core.annotation.Column(order = 2, style = "(any)", required = true, caption = "")
    public com.storedobject.core.Id getPartNumberId() {
        return null;
    }

    public void setStore(com.storedobject.core.Id p1) {
    }

    public void setStore(java.math.BigDecimal p1) {
    }

    public void setStore(com.storedobject.core.InventoryStore p1) {
    }

    public com.storedobject.core.InventoryStore getStore() {
        return null;
    }

    public void setPartNumber(com.storedobject.core.Id p1) {
    }

    public void setPartNumber(java.math.BigDecimal p1) {
    }

    public void setPartNumber(com.storedobject.core.InventoryItemType p1) {
    }

    public com.storedobject.core.InventoryItemType getPartNumber() {
        return null;
    }

    public void setDate(java.sql.Date p1) {
    }

    public void setQuantity(com.storedobject.core.Quantity p1) {
    }

    public void setQuantity(java.lang.Object p1) {
    }

    @com.storedobject.core.annotation.Column(order = 4, style = "", required = false, caption = "")
    public com.storedobject.core.Quantity getQuantity() {
        return null;
    }

    public void setCost(com.storedobject.core.Money p1) {
    }

    public void setCost(java.lang.Object p1) {
    }

    @com.storedobject.core.annotation.Column(order = 5, style = "", required = false, caption = "")
    public com.storedobject.core.Money getCost() {
        return null;
    }

    public void validateData() throws java.lang.Exception {
    }

    public static com.storedobject.core.InventoryStock getClosingStock(com.storedobject.core.Transaction p1, com.storedobject.core.Id p2, com.storedobject.core.Id p3, java.sql.Date p4) {
        return null;
    }

    public static com.storedobject.core.InventoryStock getClosingStock(com.storedobject.core.Id p1, com.storedobject.core.Id p2, java.sql.Date p3) {
        return null;
    }

    public static com.storedobject.core.InventoryStock getOpeningStock(com.storedobject.core.Transaction p1, com.storedobject.core.Id p2, com.storedobject.core.Id p3, java.sql.Date p4) {
        return null;
    }

    public static com.storedobject.core.InventoryStock getOpeningStock(com.storedobject.core.Id p1, com.storedobject.core.Id p2, java.sql.Date p3) {
        return null;
    }

    public static void createOpeningStock(com.storedobject.core.Transaction p1, com.storedobject.core.Id p2, com.storedobject.core.Id p3) throws java.lang.Exception {
    }

    public static void adjustOpeningStock(com.storedobject.core.InventoryItem p1) throws java.lang.Exception {
    }
}
