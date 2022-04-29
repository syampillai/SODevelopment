package com.storedobject.core;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public final class InventoryTransaction {

    public final static Date dataPickupDate = DateUtility.today();

    public InventoryTransaction(TransactionManager tm) {
        this(tm, "");
    }

    public InventoryTransaction(TransactionManager tm, String reference) {
        this(tm, null, reference);
    }

    public InventoryTransaction(TransactionManager tm, Date date) {
        this(tm, date, null);
    }

    public InventoryTransaction(TransactionManager tm, Date date, String reference) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public static InventoryTransaction forDataPickup(TransactionManager tm) {
        return new InventoryTransaction(tm, null, null);
    }

    /**
     * Set the data pickup mode. If set to true, items transacted will not be "in transit" stage.
     *
     * @param dataPickupMode True/false.
     */
    public void setDataPickupMode(boolean dataPickupMode) {
    }

    public void dataPickup(InventoryItem item, InventoryLocation to) {
    }

    public void dataPickup(InventoryItem item, Quantity quantity, InventoryLocation to) {
    }

    public void dataPickup(InventoryItem item, InventoryLocation locationTo, InventoryFitmentPosition assemblyPosition) {
    }

    public static InventoryLocation createSupplierLocation(TransactionManager tm, Entity supplier) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createExternalOwnerLocation(TransactionManager tm, Entity externalOwner) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createConsumerLocation(TransactionManager tm, Entity consumer) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createRepairLocation(TransactionManager tm, Entity repairEntity) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createLoanToLocation(TransactionManager tm, Entity entity) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createLoanFromLocation(TransactionManager tm, Entity entity) {
        return new InventoryVirtualLocation();
    }

    public void scrap(InventoryItem item, String reference) {
    }

    public void scrap(InventoryItem item, Quantity quantity, String reference) {
    }

    public void consume(InventoryItem item, String reference) {
    }

    public void consume(InventoryItem item, Quantity quantity, String reference) {
    }

    public void bookShortage(InventoryItem item, Quantity quantity, String reference) {
    }

    public void purchase(InventoryItem item, String reference, InventoryLocation to, Entity fromEntity) {
    }

    public void purchase(InventoryItem item, Quantity quantity, String reference, InventoryLocation to,
                         Entity fromEntity) {
    }

    public void purchaseReturn(InventoryItem item, Quantity quantity, String reference, Entity toEntity) {
    }

    public void sale(InventoryItem item, Quantity quantity, String reference, Entity toEntity) {
    }

    public void saleReturn(InventoryItem item, String reference, Entity fromEntity) {
    }

    public void moveTo(InventoryItem item, Quantity quantity, String reference, InventoryLocation to) {
    }

    public void moveTo(InventoryItem item, String reference, InventoryLocation to) {
    }
    
    public void reverse(InventoryItem item, String reference) {
    }

    public void sendForRepair(InventoryItem item, String reference, Entity repairEntity) {
    }

    public void loanTo(InventoryItem item, String reference, Entity entity) {
    }

    public void receiveFrom(InventoryItem item, String reference, InventoryLocation to, Entity entity) {
    }

    public void loanFrom(InventoryItem item, String reference, InventoryLocation to, Entity entity) {
    }

    public void loanReturn(InventoryItem item, String reference) {
    }

    public void thrash(InventoryItem item, String reference) {
    }

    public void thrash(InventoryItem item, Quantity quantity, String reference) {
    }

    public void abandon() {
    }

    public void save() throws Exception {
    }

    public void save(Transaction transaction) throws Exception {
    }

    public Map<Id, Id> getItemsChanged() {
        return new HashMap<>();
    }

    public void checkTransit(boolean checkTransit) {
    }
}
