package com.storedobject.core;

import java.sql.Date;
import java.util.stream.Stream;

/**
 * Utility class to create inventory transactions (movement of items from one locations to another).
 *
 * @author Syam
 */
public final class InventoryTransaction {

    public InventoryTransaction(Date date) {
    }

    public InventoryTransaction(Date date, String reference) {
    }

    /**
     * Create a "data pick-up" location for a given location.
     *
     * @param forLocation Location for which "data pick-up" location to be created.
     * @param tm Transaction manager.
     * @return "Data pickup" location (it may be already existing or created now).
     */
    public static InventoryLocation createDataPickupLocation(InventoryLocation forLocation, TransactionManager tm) {
        return new InventoryBin();
    }

    public void moveTo(InventoryItem item, Quantity quantity, String reference, InventoryLocation to) {
    }

    public void abandon() {
    }

    public void save(TransactionManager tm) throws Exception {
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
