package com.storedobject.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

public class InventoryApprovalConfiguration extends StoredObject implements Comparable<InventoryApprovalConfiguration> {

    public InventoryApprovalConfiguration() {
    }

    public static void columns(Columns columns) {
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

    public void setApprovalSlab(Money approvalSlab) {
    }

    public void setApprovalSlab(Object moneyValue) {
    }

    public Money getApprovalSlab() {
        return null;
    }

    public void setRequiredApprovalCount(int requiredApprovalCount) {
    }

    public int getRequiredApprovalCount() {
        return 0;
    }

    public void setApprovalsFromThisSlab(int approvalsFromThisSlab) {
    }

    public int getApprovalsFromThisSlab() {
        return 0;
    }

    @Override
	public int compareTo(InventoryApprovalConfiguration o) {
    	return 0;
	}
	
	public static ArrayList<InventoryApprovalConfiguration> listSlabs(Id documentId, Currency currency) {
        return null;
	}
}