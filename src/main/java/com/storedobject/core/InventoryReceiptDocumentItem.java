package com.storedobject.core;

import java.math.BigDecimal;

public abstract class InventoryReceiptDocumentItem extends StoredObject implements Detail {

    public InventoryReceiptDocumentItem() {
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

    public final void setQuantityReceived(Quantity quantityReceived) {
    }

    public final void setQuantityReceived(Object value) {
    }

    public final Quantity getQuantityReceived() {
        return null;
    }

    protected void extraValidateData() throws Exception {
    }

    @Override
    public final Id getUniqueId() {
        return null;
    }

    @Override
    public void copyValuesFrom(Detail detail) {
    }

    @Override
    public final boolean isDetailOf(Class <? extends StoredObject > masterClass) {
    	return false;
    }
    
    protected InventoryReceiptInformation createInformation() {
    	return null;
    }
    
    public Money getUnitPrice() {
    	return null;
    }
    
    public void setUnitPrice(Money price) {
    }
    
    public Money getLandedCost() {
    	return null;
    }

    public String getSerialNumber() {
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
