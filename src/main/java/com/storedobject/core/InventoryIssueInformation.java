package com.storedobject.core;

import com.storedobject.common.StringList;

public class InventoryIssueInformation extends com.storedobject.core.StoredObject {

    public InventoryIssueInformation() {
    }

    @com.storedobject.core.annotation.Column(order = 200)
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

    @com.storedobject.core.annotation.Column(order = 100, style = "(any)")
    public com.storedobject.core.Id getItemId() {
        return null;
    }

    public com.storedobject.core.InventoryItem getItem() {
        return null;
    }

    public void setDate(java.sql.Date p1) {
    }

    public void setIssuedTo(com.storedobject.core.Id p1) {
    }

    public void setIssuedTo(java.math.BigDecimal p1) {
    }

    public void setIssuedTo(com.storedobject.core.Entity p1) {
    }

    @com.storedobject.core.annotation.Column(order = 300)
    public com.storedobject.core.Id getIssuedToId() {
        return null;
    }

    public com.storedobject.core.Entity getIssuedTo() {
        return null;
    }

    public void setClosed(boolean p1) {
    }

    @com.storedobject.core.annotation.Column(order = 400)
    public boolean getClosed() {
        return false;
    }

    public void setRemark(java.lang.String p1) {
    }

    @com.storedobject.core.annotation.Column(order = 500, required = false)
    public java.lang.String getRemark() {
        return null;
    }

    public void setGIN(com.storedobject.core.Id p1) {
    }

    public void setGIN(java.math.BigDecimal p1) {
    }

    public void setGIN(com.storedobject.core.InventoryIssueItem p1) {
    }

    @com.storedobject.core.annotation.Column(required = false)
    public com.storedobject.core.Id getGINId() {
        return null;
    }

    public com.storedobject.core.InventoryIssueItem getGIN() {
        return null;
    }

    public void setDocument(InventoryIssueDocument document, InventoryIssue issue, InventoryIssueItem issueItem) {
    }

    public InventoryIssueDocument getDocument() {
        return getGIN().getMaster(InventoryIssue.class).getDocument();
    }

    public InventoryIssueDocumentItem getDocumentItem() {
        return getGIN().getDocumentItem();
    }
}
