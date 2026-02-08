package com.storedobject.core;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public final class InventoryTransaction {

    public final static Date dataPickupDate = DateUtility.today();

    public InventoryTransaction(TransactionManager tm, Date date) {
        this(tm, date, null);
    }

    public InventoryTransaction(TransactionManager tm, Date date, String reference) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public static InventoryTransaction forDataPickup(TransactionManager tm, String reference) {
        return new InventoryTransaction(tm, null, reference);
    }

    /**
     * Set the data pickup mode. If set to true, items transacted will not be "in transit" stage.
     */
    public void setDataPickupMode() {
    }

    public void dataPickup(Id sourceId, InventoryItem item, InventoryLocation to) {
    }

    public void dataPickup(Id sourceId, InventoryItem item, Quantity quantity, InventoryLocation to) {
    }

    public void dataPickup(Id sourceId, InventoryItem item, InventoryLocation locationTo, InventoryFitmentPosition assemblyPosition) {
    }

    public static InventoryLocation createSupplierLocation(TransactionManager tm, Entity supplier) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createExternalOwnerLocation(TransactionManager tm, Entity externalEntity) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createConsumerLocation(TransactionManager tm, Entity consumer) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createConsumptionLocation(TransactionManager tm, Entity entity) {
        return new InventoryVirtualLocation();
    }

    public static InventoryLocation createConsumptionLocation(TransactionManager tm, SystemEntity systemEntity) {
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

    public void scrap(Id sourceId, InventoryItem item, String reference) {
    }

    public void scrap(Id sourceId, InventoryItem item, Quantity quantity, String reference) {
    }

    public void consume(Id sourceId, InventoryItem item, String reference) {
    }

    public void consume(Id sourceId, InventoryItem item, Quantity quantity, String reference) {
    }

    public void bookShortage(Id sourceId, InventoryItem item, Quantity quantity, String reference) {
    }

    public void purchase(Id sourceId, InventoryItem item, String reference, InventoryLocation to, Entity fromEntity) {
    }

    public void purchase(Id sourceId, InventoryItem item, Quantity quantity, String reference, InventoryLocation to,
                         Entity fromEntity) {
    }

    public void sale(Id sourceId, InventoryItem item, Quantity quantity, String reference, Entity toEntity) {
    }

    public void changeOwner(Id sourceId, InventoryItem item, String reference, Entity toEntity) {
    }

    public void splitQuantity(Id sourceId, InventoryItem item, Quantity quantity, String reference) {
    }

    public void moveTo(Id sourceId, InventoryItem item, Quantity quantity, String reference, InventoryLocation to) {
    }

    public void moveTo(Id sourceId, InventoryItem item, String reference, InventoryLocation to) {
    }
    
    public void reverse(Id sourceId, InventoryItem item, String reference) {
    }

    public void sendForRepair(Id sourceId, InventoryItem item, Quantity quantity, String reference, Entity repairEntity) {
    }

    public void loanTo(Id sourceId, InventoryItem item, Quantity quantity, String reference, Entity entity) {
    }

    public void receiveFromExternal(Id sourceId, InventoryItem item, String reference, InventoryLocation to, Entity entity) {
    }

    public void loanFrom(Id sourceId, InventoryItem item, String reference, InventoryLocation to, Entity entity) {
    }

    public void loanReturn(Id sourceId, InventoryItem item, String reference) {
    }

    public void thrash(Id sourceId, InventoryItem item, String reference) {
    }

    public void thrash(Id sourceId, InventoryItem item, Quantity quantity, String reference) {
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

    public void setGRN(InventoryGRN grn) {
    }
}
