package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

public final class InventoryTransaction extends StoredObject {

    public InventoryTransaction() {
    }

    public static void columns(Columns columns) {
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return null;
    }

    public void setType(int type) {
    }

    public int getType() {
        return 0;
    }

    public static String[] getTypeValues() {
        return null;
    }

    public static String getTypeValue(int value) {
        return null;
    }

    public String getTypeValue() {
        return null;
    }
    
    public boolean isReceipt() {
    	return false;
    }
    
    public boolean isIssue() {
    	return false;
    }

    public void setItem(Id itemId) {
    }

    public void setItem(BigDecimal idValue) {
    }

    public void setItem(InventoryItem item) {
    }

    public Id getItemId() {
        return null;
    }

    public InventoryItem getItem() {
        return null;
    }

    public void setQuantity(Quantity quantity) {
    }

    public void setQuantity(Object value) {
    }

    public Quantity getQuantity() {
        return null;
    }

    public void setCost(Money cost) {
    }

    public void setCost(Object moneyValue) {
    }

    public Money getCost() {
        return null;
    }

    public void setStore(Id storeId) {
    }

    public void setStore(BigDecimal idValue) {
    }

    public void setStore(InventoryStore store) {
    }

    public Id getStoreId() {
        return null;
    }

    public InventoryStore getStore() {
        return null;
    }

    public void setStockLocation(Id stockLocationId) {
    }

    public void setStockLocation(BigDecimal idValue) {
    }

    public void setStockLocation(InventoryStockLocation stockLocation) {
    }

    public Id getStockLocationId() {
        return null;
    }

    public InventoryStockLocation getStockLocation() {
        return null;
    }

    public void setServiceabilityStatus(int serviceabilityStatus) {
    }

    public int getServiceabilityStatus() {
        return 0;
    }

    public static String[] getServiceabilityStatusValues() {
        return null;
    }

    public static String getServiceabilityStatusValue(int value) {
        return null;
    }

    public String getServiceabilityStatusValue() {
        return null;
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return 0;
    }

    public static String[] getStatusValues() {
        return null;
    }

    public static String getStatusValue(int value) {
        return null;
    }

    public String getStatusValue() {
        return null;
    }

    public void setTransactionEntry(Id documentItemId) {
    }

    public void setTransactionEntry(BigDecimal idValue) {
    }

    public void setTransactionEntry(InventoryReceiptItem documentItem) {
    }

    public Id getTransactionEntryId() {
        return null;
    }

    public StoredObject getTransactionEntry() {
        return null;
    }

    public boolean getSubassembly() {
		return false;
	}

	public void setSubassembly(boolean subassembly) {
	}

    public InventoryReceipt getReceipt() {
        return null;
    }

    public InventoryReceiptItem getReceiptItem() {
        return null;
    }
    
    public InventoryIssue getIssue() {
        return null;
    }

    public InventoryIssueItem getIssueItem() {
        return null;
    }

    public InventoryReceiptDocumentItem getReceiptDocumentItem() {
        return null;
    }

    public InventoryIssueDocumentItem getIssueDocumentItem() {
        return null;
    }

    public InventoryReceiptDocument getReceiptDocument() {
        return null;
    }

    public InventoryIssueDocument getIssueDocument() {
        return null;
    }
}