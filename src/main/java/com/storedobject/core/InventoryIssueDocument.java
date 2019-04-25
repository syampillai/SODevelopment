package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.storedobject.common.StringList;
import com.storedobject.common.StyledBuilder;
import com.storedobject.core.InventoryReceiptDocument.DocumentType;

public abstract class InventoryIssueDocument extends StoredObject implements OfEntity {

    public InventoryIssueDocument() {
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

    public final static String[] getStatusValues() {
        return null;
    }

    public static String getStatusValue(int value) {
        return null;
    }

    public final String getStatusValue() {
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

	public final List < InventoryIssueDocumentItem > getItems() {
        return null;
    }

    public abstract Entity getIssuedTo();
    
    public boolean isPartialIssueAllowed() {
    	return true;
    }

    public final InventoryIssue getPendingGIN() {
        return null;
    }
    
	public final InventoryDocumentConfiguration getConfiguration() {
        return null;
	}
	
    public final int[] getDefaultServiceabilityStatus() {
        return null;
    }

    public final InventoryIssue prepareGIN(TransactionManager tm, StyledBuilder message) throws Exception {
    	return null;
    }

    public final boolean accept(TransactionManager tm, StyledBuilder message) throws Exception {
    	return false;
    }
    
    public final boolean close(TransactionManager tm, StyledBuilder message) throws Exception {
		return true;
    }
    
	public final boolean cancel(TransactionManager tm, StyledBuilder message) throws Exception {
		return true;
	}

    public final InventoryIssue preprocessGIN(StyledBuilder message) {
		return null;
    }
    
    public final InventoryIssue processGIN(TransactionManager tm, StyledBuilder message) throws Exception {
    	return null;
    }
    
    public final boolean deleteGIN(TransactionManager tm, StyledBuilder message) throws Exception {
		return true;
    }
    
	public DocumentType getDocumentType() {
		return null;
	}
}