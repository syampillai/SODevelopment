package com.storedobject.core;

import com.storedobject.common.StyledBuilder;

import java.math.BigDecimal;
import java.sql.Date;

public final class InventoryReceipt extends StoredObject implements OfEntity {

    public InventoryReceipt() {
    }

    public static void columns(Columns columns) {
    }

    public void setGRN(int gRN) {
    }

    public int getGRN() {
        return 0;
    }

    public void setSupplierReference(String supplierReference) {
    }

    public String getSupplierReference() {
    	return null;
    }

    public void setMasterAirWayBillNumber(String masterAirWayBillNumber) {
    }

    public String getMasterAirWayBillNumber() {
    	return null;
    }

    public void setHouseAirWayBillNumber(String houseAirWayBillNumber) {
    }

    public String getHouseAirWayBillNumber() {
    	return null;
    }

    public void setSupplierReferenceDate(Date supplierReferenceDate) {
    }

    public Date getSupplierReferenceDate() {
    	return null;
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
    	return null;
    }

    public void setDocument(Id documentId) {
    }

    public void setDocument(BigDecimal idValue) {
    }

    public void setDocument(InventoryReceiptDocument document) {
    }

    public Id getDocumentId() {
    	return null;
    }

    public InventoryReceiptDocument getDocument() {
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

    public void setRemarks(String remarks) {
    }

    public String getRemarks() {
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

    public static void checkAWB(String awb) throws Exception {
    }

    public Class <? extends StoredObject > getDocumentClass() {
    	return null;
    }

    public Class <? extends StoredObject > getDocumentItemClass() {
    	return null;
    }

    public static Quantity getAlternatePartNumberReceived(InventoryReceiptDocument document, InventoryReceiptDocumentItem item) {
    	return null;
    }

    public static Quantity getQuantityOrdered(InventoryReceiptDocument receipt, InventoryReceiptDocumentItem item) {
    	return null;
    }

    public static Quantity getQuantityExpected(InventoryReceiptDocument document, InventoryReceiptDocumentItem item) {
    	return null;
    }

    public static boolean isReceived(InventoryReceiptDocument document, InventoryReceiptDocumentItem item) {
        return false;
    }

    public static int getReceivedStatus(InventoryReceiptDocument document) {
        return 0;
    }
    
    public void process(TransactionManager tm, StyledBuilder message) throws Exception {
    }
}
