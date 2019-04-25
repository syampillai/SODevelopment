package com.storedobject.core;

import java.math.BigDecimal;

public final class InventoryReceiptItem extends StoredObject implements Detail {

    public InventoryReceiptItem() {
    }

    public static void columns(Columns columns) {
    }
    
    public void setPartNumber(Id partNumberId) {
    }

    public void setPartNumber(BigDecimal idValue) {
    }

    public void setPartNumber(InventoryItemType partNumber) {
    }

    public Id getPartNumberId() {
        return null;
    }

    public InventoryItemType getPartNumber() {
        return null;
    }

    public void setSerialNumber(String serialNumber) {
    }

    public String getSerialNumber() {
        return null;
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

    public void setUnitCost(Money unitCost) {
    }

    public void setUnitCost(Object moneyValue) {
    }

    public Money getUnitCost() {
        return null;
    }
    
    public Money getCost() {
        return null;
    }

    public void setQuantityExpected(Quantity quantityExpected) {
    }

    public void setQuantityExpected(Object value) {
    }

    public Quantity getQuantityExpected() {
        return null;
    }

    public void setQuantityReceived(Quantity quantityReceived) {
    }

    public void setQuantityReceived(Object value) {
    }

    public Quantity getQuantityReceived() {
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
    
    public void setDocumentItem(Id documentItemId) {
    }

    public void setDocumentItem(BigDecimal idValue) {
    }

    public void setDocumentItem(InventoryReceiptDocumentItem documentItem) {
    }

    public Id getDocumentItemId() {
        return null;
    }

    public InventoryReceiptDocumentItem getDocumentItem() {
        return null;
    }

    @Override
	public Id getUniqueId() {
        return null;
    }

    @Override
	public void copyValuesFrom(Detail detail) {
    }

    @Override
	public boolean isDetailOf(Class <? extends StoredObject > masterClass) {
        return false;
    }

    public static String[] getItemTypeValues() {
        return null;
    }
    
    protected static void validate(InventoryReceiptDocumentItem item) throws Exception {
    }
}