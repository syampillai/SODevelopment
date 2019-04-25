package com.storedobject.core;

import java.math.BigDecimal;

public class InventoryAccess extends StoredObject {

	public static final int
	CREATE_EDIT_DOC = 1,
	CANCEL_DOC = 2,
	ALTERNATE_PN = 4,
	EXCESS = 8,
	PRINT_DOC = 16,
	PRINT = 32,
	ACCEPT = 64,
	PREPARE = 128,
	PROCESS = 256,
	DELETE = 512,
	CLOSE_DOC = 1024,
	INSPECT = 2048,
	NEW_PN = 4096,
	ACCEPT_ITEM_DETAILS = 8192,
	BINNING = 16384;

    public InventoryAccess() {
    }

    public static void columns(Columns columns) {
    }

    public void setUserGroup(Id userGroupId) {
    }

    public void setUserGroup(BigDecimal idValue) {
    }

    public void setUserGroup(SystemUserGroup userGroup) {
    }

    public Id getUserGroupId() {
        return null;
    }

    public SystemUserGroup getUserGroup() {
        return null;
    }

    public void setDocument(Id documentId) {
    }

    public void setDocument(BigDecimal idValue) {
    }

    public void setDocument(InventoryDocumentConfiguration document) {
    }

    public Id getDocumentId() {
        return null;
    }

    public InventoryDocumentConfiguration getDocument() {
        return null;
    }

    public void setAccess(int access) {
    }

    public int getAccess() {
        return 0;
    }

    public static String[] getAccessBitValues() {
        return null;
    }

    public static String getAccessValue(int value) {
        return null;
    }

    public String getAccessValue() {
        return null;
    }

    public static int get(TransactionManager tm, InventoryDocumentConfiguration document) {
        return 0;
    }
    
    public static int get(SystemUser su, InventoryDocumentConfiguration document) {
        return 0;
    }
    
    public void setApprovalLimit(Money approvalLimit) {
    }

    public void setApprovalLimit(Object moneyValue) {
    }

    public Money getApprovalLimit() {
        return null;
    }
}
