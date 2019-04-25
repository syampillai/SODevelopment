package com.storedobject.core;

import java.math.BigDecimal;

public abstract class InventoryStockLocation extends StoredObject {

    public InventoryStockLocation() {
    }

    public static void columns(Columns columns) {
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

    public void setName(String name) {
    }

    public String getName() {
    	return null;
    }

    public void setParentLocation(Id parentLocationId) {
    }

    public void setParentLocation(BigDecimal idValue) {
    }

    public void setParentLocation(InventoryStockLocation parentLocation) {
    }

    public Id getParentLocationId() {
    	return null;
    }

    public InventoryStockLocation getParentLocation() {
    	return null;
    }

    public void setStockOwner(Id stockOwnerId) {
    }

    public void setStockOwner(BigDecimal idValue) {
    }

    public void setStockOwner(Entity stockOwner) {
    }

    public Id getStockOwnerId() {
    	return null;
    }

    public Entity getStockOwner() {
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

    public void setHandlingAndStorage(int handlingAndStorage) {
    }

    public int getHandlingAndStorage() {
        return 0;
    }

    public static String[] getHandlingAndStorageValues() {
    	return null;
    }

    public static String getHandlingAndStorageValue(int value) {
    	return null;
    }

    public String getHandlingAndStorageValue() {
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

    public boolean checkServiceability(InventoryItem item) {
    	return false;
    }

    public final boolean checkStorage(InventoryItem item) {
    	return false;
    }

    public boolean checkStorage(InventoryItemType itemType, int serviceabilityStatus) {
    	return false;
    }

    public final boolean checkCategory(InventoryItem item) {
    	return false;
    }

    public boolean checkCategory(InventoryItemType itemType) {
    	return true;
    }

    public void setCategory(Id category) {
    }

    public Id getCategoryId() {
    	return null;
    }

    public static InventoryStockLocation get(String name, InventoryStore store) {
    	return null;
    }
    
    public static ObjectIterator<InventoryStockLocation> list(String name, InventoryStore store) {
    	return null;
    }
}