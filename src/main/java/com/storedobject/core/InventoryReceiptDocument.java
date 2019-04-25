package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.storedobject.common.StringList;
import com.storedobject.common.StyledBuilder;
import com.storedobject.core.annotation.Table;

@Table(anchors = "Store")
public abstract class InventoryReceiptDocument extends StoredObject implements OfEntity {

	public enum DocumentType { PURCHASE, TRANSFER, REPAIR, MAINTENANCE, SUBSCRIPTION, SERVICE, LOAN }

    public InventoryReceiptDocument() {
    }

    public static void columns(Columns columns) {
    }

    public StringList readOnlyColumns() {
    	return null;
    }

    public final void setNo(int no) {
    }

    public final int getNo() {
    	return 0;
    }
    
    public final void setDate(Date date) {
    }

    public final Date getDate() {
        return null;
    }

    public final void setStore(Id storeId) {
    }

    public final void setStore(BigDecimal idValue) {
    }

    public final void setStore(InventoryStore store) {
    }

    public Id getStoreId() {
        return null;
    }

    public final InventoryStore getStore() {
        return null;
    }

    public final void setStatus(int status) {
    }

    public final int getStatus() {
        return 0;
    }

    public static String[] getStatusValues() {
        return null;
    }

    public static String getStatusValue(int value) {
        return null;
    }

    public final String getStatusValue() {
        return null;
    }

    public final void setRemarks(String remarks) {
    }

    public final String getRemarks() {
        return null;
    }
    
    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return null;
    }

    public SystemEntity getSystemEntity() {
        return null;
    }

    protected void extraValidateData() throws Exception {
    }

	public final List < InventoryReceiptDocumentItem > getItems() {
        return null;
    }
    
    public abstract Entity getReceivedFrom();
    
    public Entity getStockOwner() {
    	return null;
    }

    public final InventoryReceipt getPendingGRN() {
        return null;
    }
    
    public final InventoryReceipt prepareGRN(Transaction transaction, StyledBuilder message) throws Exception {
        return null;
    }
    
    public final InventoryReceipt prepareGRN(Transaction transaction, InventoryReceipt inventoryReceipt) throws Exception {
        return null;
    }

	public final InventoryDocumentConfiguration getConfiguration() {
        return null;
	}
	
	public final void computeLandedCost(TransactionManager tm) throws Exception {
	}
	
	public Class<? extends InventoryIssueDocument> getExchangeDocumentClass() {
		return null;
	}
	
	public InventoryIssueDocument createExchangeDocument() {
		return null;
	}
	
	public boolean canDeleteGRN(InventoryReceipt grn) {
		return true;
	}
	
	public DocumentType getDocumentType() {
		return null;
	}
	
	public Class<? extends InventoryItemType> getDocumentItemType() {
		return null;
	}
	
    public final InventoryIssue prepareGRN(TransactionManager tm, StyledBuilder message) throws Exception {
        return null;
    }

    public final void accept(TransactionManager tm, StyledBuilder message) throws Exception {
    }
    
    public final void cancel(TransactionManager tm, StyledBuilder message) throws Exception {
    }
    
    public final void close(TransactionManager tm, StyledBuilder message) throws Exception {
    }
    
    public final InventoryIssue preprocessGRN(StyledBuilder message) throws Exception {
    	return null;
    }
    
    public final InventoryIssue processGRN(TransactionManager tm, StyledBuilder message) throws Exception {
    	return null;
    }
    
    public final void deleteGRN(TransactionManager tm, StyledBuilder message) throws Exception {
    }
}
