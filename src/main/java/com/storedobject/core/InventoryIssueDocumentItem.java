package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryIssueDocumentItem extends StoredObject implements Detail {

    public InventoryIssueDocumentItem() {
    }

    public static void columns(Columns columns) {
    }

    public final void setType(int type) {
    }

    public final int getType() {
        return 0;
    }

    public static String[] getTypeValues() {
    	return null;
    }

    public static String getTypeValue(int value) {
    	return null;
    }

    public final String getTypeValue() {
    	return null;
    }

    public final void setItem(Id itemId) {
    }

    public final void setItem(BigDecimal idValue) {
    }

    public final void setItem(InventoryItemType item) {
    }

    public final Id getItemId() {
    	return null;
    }

    public final InventoryItemType getItem() {
    	return null;
    }

    public final void setQuantity(Quantity quantity) {
    }

    public final void setQuantity(Object value) {
    }

    public final Quantity getQuantity() {
    	return null;
    }

    public final void setQuantityIssued(Quantity quantityIssued) {
    }

    public final void setQuantityIssued(Object value) {
    }

    public final Quantity getQuantityIssued() {
    	return null;
    }

    protected void extraValidateData() throws Exception {
    }

    @Override
    public final Id getUniqueId() {
        return getId();
    }

    @Override
    public void copyValuesFrom(Detail detail) {
    }

    @Override
    public final boolean isDetailOf(Class <? extends StoredObject > masterClass) {
    	return false;
    }
    
    protected InventoryIssueInformation createInformation() {
    	return null;
    }

    public Money getUnitCost() {
        return null;
    }

    public void setUnitCost(Money cost) {
    }
    
    public Money getRate() {
    	return null;
    }
    
    public String getSerialNumber() {
    	return null;
    }
    
    public void setSerialNumber(String serialNumber) {
    }
    
    public String getRemark() {
    	return null;
    }
    
    public void setOrderType(int orderType) {
    }
    
    public int getOrderType() {
    	return 0;
    }
    
    public String getOrderTypeValue() {
    	return null;
    }
    
    public boolean forExchange() {
    	return false;
    }
}
