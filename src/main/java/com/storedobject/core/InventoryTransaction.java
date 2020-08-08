package com.storedobject.core;

import java.sql.Date;
import java.util.stream.Stream;

/**
 * Utility class to create inventory transactions (movement of items from one locations to another).
 *
 * @author Syam
 */
public final class InventoryTransaction {

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

    /**
     * Create "Inventory Transaction" for data pick-up.
     *
     * @param tm Transaction manager.
     * @return Instance of the "Inventory Transaction" suitable for data pick-up.
     */
    public static InventoryTransaction forDataPickup(TransactionManager tm) {
        return new InventoryTransaction(tm, null, null);
    }

    public void dataPickup(InventoryItem item, InventoryLocation to) {
    }

    public void purchase(InventoryItem item, String reference, InventoryLocation to, Entity fromEntity) {
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

    public void sendForRepair(InventoryItem item, String reference, Entity repairEntity) {
    }

    public void loanTo(InventoryItem item, String reference, Entity entity) {
    }

    public void loanFrom(InventoryItem item, String reference, InventoryLocation to, Entity entity) {
    }

    public void loanReturn(InventoryItem item, String reference) {
    }

    public void abandon() {
    }

    public void save() throws Exception {
    }

    public void save(Transaction transaction) throws Exception {
    }

    public Stream<Entry> entries() {
        return entries(false);
    }

    public Stream<Entry> entries(boolean includeAssemblyComponents) {
        return Stream.empty();
    }

    public int getEntryCount() {
        return 0;
    }

    public class Entry {

        private Entry(InventoryItem item, Quantity quantity, String reference, InventoryLocation locationTo) {
            abandon();
        }

        public InventoryItem getItem() {
            return new InventoryItem();
        }

        public Quantity getQuantity() {
            return Count.ONE;
        }

        public void setQuantity(Quantity quantity) {
        }

        public String getReference() {
            return "";
        }

        public void setReference(String reference) {
        }

        public boolean isAssemblyComponent() {
            return false;
        }

        public InventoryLocation getLocationTo() {
            return new InventoryBin();
        }

        public void setLocationTo(InventoryLocation locationTo) {
        }
    }
}
