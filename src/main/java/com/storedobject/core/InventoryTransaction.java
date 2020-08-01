package com.storedobject.core;

import java.sql.Date;
import java.util.stream.Stream;

/**
 * Utility class to create inventory transactions (movement of items from one locations to another).
 *
 * @author Syam
 */
public final class InventoryTransaction {

    public InventoryTransaction(Date date, InventoryStore from, InventoryLocation to, String reference) {
        this(date, from.getStoreBin(), to, reference);
    }

    public InventoryTransaction(Date date, InventoryStore from, InventoryStore to, String reference) {
        this(date, from.getStoreBin(), to.getStoreBin(), reference);
    }

    public InventoryTransaction(Date date, InventoryLocation from, InventoryStore to, String reference) {
        this(date, from, to.getStoreBin(), reference);
    }

    public InventoryTransaction(Date date, InventoryLocation from, InventoryLocation to, String reference) {
    }

    public void add(InventoryItemType partNumber, Quantity quantity, Money cost) {
        add(partNumber, null, quantity, cost);
    }

    public void add(InventoryItemType partNumber, String serialNumber, Quantity quantity) {
    }

    public void add(InventoryItemType partNumber, String serialNumber, Quantity quantity, Money cost) {
    }

    public void add(InventoryItemType partNumber, String serialNumber, Quantity quantity, Money cost, String reference) {
    }

    public void remove(Entry entry) {
    }

    public void save(TransactionManager tm) throws Exception {
    }

    public void save(Transaction transaction) throws Exception {
    }

    public InventoryLocation getLocationFrom() {
        return new InventoryBin();
    }

    public InventoryLocation getLocationTo() {
        return new InventoryStoreBin();
    }

    public Stream<Entry> entries() {
        return entries(false);
    }

    public Stream<Entry> entries(boolean includeAssemblyComponents) {
        return Stream.empty();
    }

    public class Entry {

        private Entry() {
            InventoryTransaction.this.getLocationTo();
        }

        public InventoryItem getItem() {
            return new InventoryItem();
        }

        public InventoryItemType getItemType() {
            return new InventoryItemType();
        }

        public String getSerialNumber() {
            return "";
        }

        public void setSerialNumber(String serialNumber) {
        }

        public Quantity getQuantity() {
            return Count.ZERO;
        }

        public void setQuantity(Quantity quantity) {
        }

        public Money getCost() {
            return new Money();
        }

        public void setCost(Money cost) {
        }

        public String getReference() {
            return "";
        }

        public void setReference(String reference) {
        }

        public boolean isAssemblyComponent() {
            return false;
        }
    }
}
