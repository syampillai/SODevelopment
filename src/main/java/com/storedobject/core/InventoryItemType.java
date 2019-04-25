package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryItemType extends StoredObject {

    public InventoryItemType() {
    }

    public static void columns(Columns columns) {
    }
    
    public void setName(Id nameId) {
    }

    public void setName(BigDecimal idValue) {
    }

    public void setName(InventoryItemName name) {
    }

    public Id getNameId() {
        return null;
    }

    public InventoryItemName getName() {
        return null;
    }

    public void setPartNumber(String partNumber) {
    }

    public String getPartNumber() {
        return null;
    }

	public String getAlternateCode() {
        return null;
	}

	public void setAlternateCode(String alternateCode) {
	}
	
	public void setUnitOfMeasurement(MeasurementUnit unitOfMeasurement) {
	}
	
	public void setUnitOfMeasurement(Quantity unitOfMeasurement) {
    }

    public void setUnitOfMeasurement(Object value) {
    }

    public Quantity getUnitOfMeasurement() {
        return null;
    }
    
    public final MeasurementUnit getUnit() {
    	return null;
    }
    
    public void setUnitCost(Money unitCost) {
    }

    public void setUnitCost(Object moneyValue) {
    }

    public Money getUnitCost() {
        return null;
    }
    
    public UnitCost getUnitCost(MeasurementUnit unit) {
        return null;
    }
    
    public Money getCost(Quantity quantity) {
        return null;
    }

    public void setMinimumStockLevel(Quantity minimumStockLevel) {
    }

    public void setMinimumStockLevel(Object value) {
    }

    public Quantity getMinimumStockLevel() {
        return null;
    }

    public void setReorderPoint(Quantity reorderPoint) {
    }

    public void setReorderPoint(Object value) {
    }

    public Quantity getReorderPoint() {
        return null;
    }

    public void setEconomicOrderQuantity(Quantity economicOrderQuantity) {
    }

    public void setEconomicOrderQuantity(Object value) {
    }

    public Quantity getEconomicOrderQuantity() {
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

    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
    }
    
    public void checkUnit(Quantity quantity) throws Invalid_State {
    }

    public boolean isSerialized() {
    	return false;
    }

    public Id getCategoryId() {
    	return null;
    }

	public <T extends InventoryItem> T createItem() {
    	return null;
    }
    
    public static Class<? extends InventoryItem> getItemType() {
        return null;
    }

    public static InventoryItemType get(String partNumber) {
        return null;
    }
    
    public static ObjectIterator<? extends InventoryItemType> list(String partNumber) {
    	return null;
    }

    public static <T extends InventoryItemType> T getByPartNumber(Class<T> itemClass, String partNumber) {
        return null;
    }
    
    public static <T extends InventoryItemType> T getByPartNumber(Class<T> itemClass, String partNumber, boolean any) {
        return null;
    }

    public static <T extends InventoryItemType> ObjectIterator<T> listByPartNumber(Class<T> itemClass, String partNumber) {
        return null;
    }
    
    public static <T extends InventoryItemType> ObjectIterator<T> listByPartNumber(Class<T> itemClass, String partNumber, boolean any) {
        return null;
    }

	public <I extends InventoryItem> ObjectIterator<I> listStock(Id storeId) {
        return null;
	}

	public <I extends InventoryItem> ObjectIterator<I> listStock(String serialNumber, Id storeId) {
        return null;
	}
    
	public <I extends InventoryItem> ObjectIterator<I> listStock(Id storeId, int serviceabilityStatus) {
        return null;
	}
	
	public <I extends InventoryItem> ObjectIterator<I> listStock(Id storeId, int... serviceabilityStatus) {
        return null;
	}
	
	public <I extends InventoryItem> ObjectIterator<I> listStock(String serialNumber, Id storeId, int serviceabilityStatus) {
        return null;
	}

	public <I extends InventoryItem> ObjectIterator<I> listStock(String serialNumber, Id storeId, int... serviceabilityStatus) {
        return null;
	}

	public boolean checkStock(Id storeId, Quantity quantity) {
        return false;
	}
	
	public boolean checkStock(Id storeId, Quantity quantity, int serviceabilityStatus) {
        return false;
	}
	
	public boolean checkStock(Id storeId, Quantity quantity, int... serviceabilityStatus) {
        return false;
	}
	
	public boolean checkStock(Id storeId, Quantity quantity, String serialNumber) {
        return false;
	}
	
	public boolean checkStock(Id storeId, Quantity quantity, String serialNumber, int serviceabilityStatus) {
        return false;
	}

	public boolean checkStock(Id storeId, Quantity quantity, String serialNumber, int... serviceabilityStatus) {
        return false;
	}

	public String getSerialNumberName() {
		return null;
	}
	
	public boolean mainStoreOnly() {
		return false;
	}
}