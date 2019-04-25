package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InventoryActionLog extends StoredObject {

    public InventoryActionLog() {
    }
    
    public static void columns(Columns columns) {
    }

    public String getUniqueCondition() {
    	return null;
    }

    public void setDocument(Id documentId) {
    }

    public void setDocument(BigDecimal idValue) {
    }

    public void setDocument(StoredObject document) {
    }

    public Id getDocumentId() {
    	return null;
    }

    public StoredObject getDocument() {
    	return null;
    }

    public void setAction(int action) {
    }

    public int getAction() {
        return 0;
    }
    
    public String getActionValue() {
    	return null;
    }

    public void setActedBy(Id actedById) {
    }

    public void setActedBy(BigDecimal idValue) {
    }

    public void setActedBy(Person actedBy) {
    }

    public Id getActedById() {
    	return null;
    }

    public Person getActedBy() {
    	return null;
    }
    
    public void setActedAt(Timestamp actedAt) {
    }

    public Timestamp getActedAt() {
    	return null;
    }

    public static InventoryActionLog get(Id documentId, int action) {
    	return null;
    }
}