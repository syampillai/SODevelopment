package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent stock of an item.
 *
 * @author Syam
 */
public final class Stock {

    private final List<InventoryItem> stocks = new ArrayList<>();
    private InventoryItemType pn;

    public Stock() {
        this((InventoryLocation) null, null);
    }

    public Stock(InventoryStore store) {
        this(store, null);
    }

    public Stock(InventoryLocation location) {
        this(location, null);
    }

    public Stock(Date date) {
        this((InventoryLocation) null, date);
    }

    public Stock(InventoryStore store, Date date) {
        this(store == null ? null : store.getStoreBin(), date);
    }

    @SuppressWarnings("unused")
    public Stock(InventoryLocation location, Date date) {
    }

    public void addStore(InventoryStore store) {
        if(store != null) {
            addLocation(store.getStoreBin());
        }
    }

    public void addLocation(InventoryLocation location) {
    }

    public void setPartNumber(InventoryItemType pn) {
        this.pn = pn;
    }

    public InventoryItemType getPartNumber() {
        return pn;
    }

    public List<InventoryItem> getStocks() {
        return stocks;
    }

    public String getLabel() {
        return "";
    }

    public Date getDate() {
        return DateUtility.today();
    }
}
