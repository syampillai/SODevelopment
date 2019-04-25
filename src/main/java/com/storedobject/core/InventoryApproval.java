package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InventoryApproval extends StoredObject {

    public InventoryApproval() {
    }

    public static void columns(Columns columns) {
    }

    public void setReceiptDocument(Id receiptDocumentId) {
    }

    public void setReceiptDocument(BigDecimal idValue) {
    }

    public void setReceiptDocument(InventoryReceiptDocument receiptDocument) {
    }

    public Id getReceiptDocumentId() {
        return null;
    }

    public InventoryReceiptDocument getReceiptDocument() {
        return null;
    }

    public void setApprovedBy(Id approvedById) {
    }

    public void setApprovedBy(BigDecimal idValue) {
    }

    public void setApprovedBy(Person approvedBy) {
    }

    public Id getApprovedById() {
        return null;
    }

    public Person getApprovedBy() {
        return null;
    }

    public void setApprovalSlab(Money approvalSlab) {
    }

    public void setApprovalSlab(Object moneyValue) {
    }

    public Money getApprovalSlab() {
        return null;
    }

    public void setApprovedAt(Timestamp approvedAt) {
    }

    public Timestamp getApprovedAt() {
        return null;
    }
}