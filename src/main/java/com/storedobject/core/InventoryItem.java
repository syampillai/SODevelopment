package com.storedobject.core;

import com.storedobject.common.SOException;
import com.storedobject.common.StringList;

import java.math.BigDecimal;
import java.sql.Date;

public class InventoryItem extends StoredObject implements HasParents {

    public InventoryItem() {
    }

    public static void columns(Columns columns) {
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

    public void setAssembly(Id assemblyId) {
    }

    public void setAssembly(BigDecimal idValue) {
    }

    public void setAssembly(InventoryAssembly assembly) {
    }

    public Id getAssemblyId() {
        return null;
    }

    public InventoryAssembly getAssembly() {
        return null;
    }

    public final boolean isAssembly() {
        return false;
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
    
    public UnitCost getUnitCost() {
    	return null;
    }

    public UnitCost getUnitCost(MeasurementUnit unit) {
    	return null;
    }
    
    public Money getCost(Quantity quantity) {
    	return null;
    }
    
	public void setInTransit(boolean inTransit) {
	}

	public boolean getInTransit() {
		return false;
	}

	public void setGRN(String grn) {
	}

	public String getGRN() {
    	return null;
	}

    public void addGRN(String grn) {
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
    
    public static InventoryItem get(String serialNumber, String partNumber) {
    	return null;
    }
    
	public static <T extends InventoryItem> InventoryItem get(String serialNumber, InventoryItemType partNumber) {
    	return null;
    }
    
    public static InventoryItem getByPartNumberId(String serialNumber, Id partNumber) {
    	return null;
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber, String partNumber) {
    	return null;
    }
    
    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber) {
    	return null;
    }

    public static <T extends InventoryItem> T getByPartNumber(Class<T> itemClass, String serialNumber, String partNumber, boolean any) {
    	return null;
    }
    
    public static <T extends InventoryItem> T get(Class<T> itemClass, String serialNumber, InventoryItemType partNumber, boolean any) {
    	return null;
    }
    
    public static <T extends InventoryItem> T getByPartNumberId(Class<T> itemClass, String serialNumber, Id partNumber) {
    	return null;
    }
    
    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType partNumber) {
    	return null;
    }
    
    public static <T extends InventoryItem> ObjectIterator<T> list(Class<T> itemClass, String serialNumber, InventoryItemType partNumber, boolean any) {
    	return null;
    }

    public final boolean isSerialized() {
    	return false;
    }

    public static Class<? extends InventoryItemType> getPartNumberType() {
    	return null;
    }
    
    protected String extraConditionForSimilarItem() {
    	return null;
    }
    
    public boolean getShelfLifeApplicable() {
    	return false;
    }
    
    public Date getShelfLife() {
    	return null;
    }

    public final InventoryItem club(Id storeId) {
    	return null;
    }
    
    public final InventoryItem[] split(Id storeId) throws SOException {
    	return null;
    }

    public final void add(InventoryItem item) {
    }
    
    public static <I extends InventoryItem> ObjectIterator<I> listStock(String partNumber, Id storeId) {
    	return listStock(partNumber, storeId, 0);
    }
    
    public static <I extends InventoryItem> ObjectIterator<I> listStock(String partNumber, String serialNumber, Id storeId) {
    	return null;
    }
    
	public static <T extends InventoryItemType, I extends InventoryItem> ObjectIterator<I> listStock(T itemType, Id storeId) {
    	return null;
	}
    
	public static <T extends InventoryItemType, I extends InventoryItem> ObjectIterator<I> listStock(T itemType, String serialNumber,
			Id storeId) {
    	return null;
	}
    
    public static <I extends InventoryItem> ObjectIterator<I> listStock(String partNumber, Id storeId, int serviceabilityStatus) {
    	return null;
    }
    
    public static <I extends InventoryItem> ObjectIterator<I> listStock(String partNumber, Id storeId, int... serviceabilityStatus) {
    	return null;
    }
    
    public static <I extends InventoryItem> ObjectIterator<I> listStock(String partNumber, String serialNumber, Id storeId,
    		int serviceabilityStatus) {
    	return null;
    }
    
	public static <T extends InventoryItemType, I extends InventoryItem> ObjectIterator<I> listStock(T itemType, Id storeId, int serviceabilityStatus) {
    	return null;
	}
    
	public static <T extends InventoryItemType, I extends InventoryItem> ObjectIterator<I> listStock(T itemType, Id storeId, int... serviceabilityStatus) {
    	return null;
	}
    
	public static <T extends InventoryItemType, I extends InventoryItem> ObjectIterator<I> listStock(T itemType, String serialNumber,
			Id storeId, int serviceabilityStatus) {
    	return null;
    }
    
	public static <T extends InventoryItemType, I extends InventoryItem> ObjectIterator<I> listStock(T itemType, String serialNumber,
			Id storeId, int... serviceabilityStatus) {
    	return null;
    }
    
    public void checkUnit(Quantity quantity, String name) throws Invalid_State {
    }
    
    public void checkUnit(Quantity quantity) throws Invalid_State {
    }
    
    public InventoryReceiptInformation createReceiptInformation(TransactionManager tm, InventoryReceiptDocument document,
    		InventoryReceiptItem item) throws Exception {
    	return null;
    }
    
    public void updateReceiptInformation(TransactionManager tm, InventoryReceiptInformation info, InventoryReceiptDocument document,
    		InventoryReceiptItem item) throws Exception {
    }
    
    public InventoryIssueInformation createIssueInformation(Transaction transaction, InventoryIssueDocument document,
    		InventoryIssueItem item) throws Exception {
    	return null;
    }
    
    public void updateIssueInformation(Transaction transaction, InventoryIssueInformation info, InventoryIssueDocument document,
    		InventoryIssueItem item) throws Exception {
    }
    
    public StringList readOnlyColumns() {
    	return null;
    }
}