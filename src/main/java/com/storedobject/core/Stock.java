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

    private final List<InventoryLocation> locations = new ArrayList<>();
    private final List<StockHistory> stocks = new ArrayList<>(), saveStocks = new ArrayList<>();
    private final Date date;
    private InventoryItemType pn;
    private boolean computed = false;
    private final boolean allStores;
    private TransactionManager tm;

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

    public Stock(InventoryLocation location, Date date) {
        if(date != null && !date.before(DateUtility.today())) {
            date= null;
        }
        this.date = date;
        if(location == null) {
            StoredObject.list(InventoryStore.class, true).forEach(s -> locations.add(s.getStoreBin()));
            allStores = true;
        } else {
            addLocation(location);
            allStores = false;
        }
    }

    public void addStore(InventoryStore store) {
        if(store != null) {
            addLocation(store.getStoreBin());
        }
    }

    public void addLocation(InventoryLocation location) {
        location = storeBin(location);
        if(location != null && !locations.contains(location)) {
            locations.add(location);
            computed = false;
        }
    }

    public void setPartNumber(InventoryItemType pn) {
        this.pn = pn;
        computed = false;
    }

    public InventoryItemType getPartNumber() {
        return pn;
    }

    public List<StockHistory> getStocks() {
        synchronized(stocks) {
            if(!computed) {
                compute();
            }
            return stocks;
        }
    }

    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    private void save() {
        try {
            List<StockHistory> toSave;
            synchronized (saveStocks) {
                if (saveStocks.isEmpty()) {
                    return;
                }
                toSave = new ArrayList<>(saveStocks);
                saveStocks.clear();
            }
            tm.transact(t -> {
                for (StockHistory s : toSave) {
                    s.makeNew();
                    s.save(t);
                }
            });
        } catch (Exception e) {
            tm.log(e);
        }
    }

    private void compute() {
        synchronized(stocks) {
            if(computed) {
                return;
            }
            computed = true;
            stocks.clear();
            if(pn == null) {
                return;
            }
            List<InventoryItem> items = null;
            boolean binSave = false;
            for(InventoryLocation location: locations) {
                if(date != null) {
                    binSave = location instanceof InventoryStoreBin || location.getType() != 0;
                    ObjectIterator<StockHistory> ss = location instanceof InventoryStoreBin sb
                            ? StockHistory.listForStore(pn, date, sb.getStoreId())
                            : StockHistory.listForLocation(pn, date, location.getId());
                    boolean found = false;
                    for (StockHistory s : ss) {
                        found = true;
                        if (!s.getQuantity().isZero()) {
                            stocks.add(s);
                        }
                    }
                    if (found) {
                        continue;
                    }
                }
                if(items == null) {
                    items = new ArrayList<>();
                }
                int n = items.size();
                (location instanceof InventoryStoreBin b ? pn.listStock(b.getStore()) : pn.listStock(location))
                        .collectAll(items);
                if(date != null && tm != null && n == items.size() && binSave) { // Nothing new is added
                    createStockHistory(location); // saving zeroed entry for the location
                }
            }
            if(date == null || items == null) {
                return;
            }
            if(items.isEmpty()) {
                if(!saveStocks.isEmpty()) {
                    Thread.startVirtualThread(this::save);
                }
                return;
            }
            List<InventoryLedger> ledger = StoredObject.list(InventoryLedger.class, "ItemType=" + pn.getId()
                    + " AND Date>'" + Database.format(date) + "'", "ItemType,Date,TranId").toList();
            boolean serialized = pn.isSerialized(), qtyMatched;
            InventoryLedger m;
            InventoryLocation from, fromS, toS;
            InventoryItem ii;
            while(!ledger.isEmpty()) {
                m = ledger.removeLast();
                fromS = storeBin(from = m.getLocationFrom());
                toS = storeBin(m.getLocationTo());
                if(!locations.contains(fromS) && !locations.contains(toS)) {
                    continue;
                }
                ii = item(items, m.getItemId());
                if(ii != null) {
                    ii.location(from);
                }
                qtyMatched = serialized || (ii != null && ii.getQuantity().equals(m.getQuantity()));
                if(locations.contains(fromS) && !locations.contains(toS)) {
                    if(ii == null) {
                        ii = m.getItemAnyway();
                        ii.location(from);
                        if(!qtyMatched) {
                            ii.quantity(m.getQuantity());
                            ii.makeNew();
                            ii.makeVirtual();
                        }
                    }
                    items.add(ii);
                } else if(!locations.contains(fromS) && locations.contains(toS)) {
                    if(qtyMatched) {
                        if(ii != null) { // Must be always true for serialized items
                            Id id = ii.getId();
                            items.removeIf(s -> s.getId().equals(id));
                        }
                    } else {
                        if(ii != null) {
                            ii.quantity(ii.getQuantity().subtract(m.getQuantity()));
                            if(!ii.getQuantity().isPositive()) {
                                items.remove(ii);
                            }
                        } else {
                            Quantity q = m.getQuantity();
                            while(q.isPositive()) {
                                for(InventoryItem s : items) {
                                    InventoryItem iim = m.getItemAnyway();
                                    if(s.getPartNumberId().equals(m.getItemTypeId())
                                            && s.getSerialNumber().equals(iim.getSerialNumber())) {
                                        ii = s;
                                        break;
                                    }
                                }
                                if(ii == null) {
                                    for(InventoryItem s : items) {
                                        if(s.getPartNumberId().equals(m.getItemTypeId())
                                                && s.getQuantity().isGreaterThanOrEqual(q)) {
                                            ii = s;
                                            break;
                                        }
                                    }
                                }
                                if(ii == null) {
                                    for(InventoryItem s : items) {
                                        if(s.getPartNumberId().equals(m.getItemTypeId())) {
                                            ii = s;
                                            break;
                                        }
                                    }
                                }
                                if(ii == null) {
                                    break;
                                }
                                if(ii.getQuantity().isGreaterThanOrEqual(q)) {
                                    ii.quantity(ii.getQuantity().subtract(q));
                                    if (!ii.getQuantity().isPositive()) {
                                        items.remove(ii);
                                    }
                                    break;
                                }
                                items.remove(ii);
                                q = q.subtract(ii.getQuantity());
                            }
                        }
                    }
                }
            }
            items.forEach(i -> stocks.add(createStockHistory(i)));
        }
        if(tm != null && !saveStocks.isEmpty()) {
            Thread.startVirtualThread(this::save);
        }
    }

    private StockHistory createStockHistory(InventoryItem i) {
        StockHistory s = new StockHistory();
        s.setDate(date);
        s.setPartNumber(pn);
        s.setSerialNumber(i.getSerialNumber());
        s.setQuantity(i.getQuantity());
        s.setCost(i.getCost());
        InventoryLocation location = i.getLocation();
        s.setLocation(location);
        if(location instanceof InventoryBin b) {
            s.setStore(b.getStore());
        }
        if(i.getInTransit()) {
            s.setPreviousLocation(i.getPreviousLocation());
        }
        s.makeVirtual();
        synchronized (saveStocks) {
            saveStocks.add(s);
        }
        return s;
    }

    private void createStockHistory(InventoryLocation location) {
        StockHistory s = new StockHistory();
        s.setDate(date);
        s.setPartNumber(pn);
        s.setQuantity(Count.ZERO);
        s.setCost(new Money());
        s.setLocation(location);
        if(location instanceof InventoryBin b) {
            s.setStore(b.getStore());
        }
        s.makeVirtual();
        synchronized (saveStocks) {
            saveStocks.add(s);
        }
    }

    private static InventoryItem item(List<InventoryItem> stocks, Id id) {
        return stocks.stream().filter(ii -> ii.getId().equals(id)).findAny().orElse(null);
    }

    private static InventoryLocation storeBin(InventoryLocation location) {
        return location instanceof InventoryBin b && !(location instanceof InventoryStoreBin) ?
                b.getStore().getStoreBin() : location;
    }

    public String getLabel() {
        StringBuilder s = new StringBuilder();
        if(allStores) {
            s.append("All Stores");
            if(locations.stream().anyMatch(l -> !(l instanceof InventoryStoreBin))) {
                s.append(" + Other Locations");
            }
        } else if(locations.size() == 1) {
            InventoryLocation location = locations.getFirst();
            if(location instanceof InventoryStoreBin) {
                s.append("Store");
            } else if(location instanceof InventoryFitmentPosition) {
                s.append("Assembly");
            } else {
                s.append("Location");
            }
            s.append(": ").append(location.toDisplay());
        } else {
            int stores = (int) locations.stream().filter(loc -> loc instanceof InventoryStoreBin).count();
            int locs = (int) locations.stream().filter(loc -> !(loc instanceof InventoryStoreBin)).count();
            if(stores == 0) {
                s.append(locs).append(" locations");
            } else if(locs == 0) {
                s.append(stores).append(" stores");
            } else {
                if(stores == 1) {
                    locations.stream().filter(loc -> loc instanceof InventoryStoreBin)
                            .map(loc -> ((InventoryStoreBin) loc).getStore()).findAny()
                            .ifPresent(store -> s.append(store.toDisplay()));
                } else {
                    s.append(stores).append(" Stores");
                }
                s.append(" + ");
                if(locs == 1) {
                    locations.stream().filter(loc -> !(loc instanceof InventoryStoreBin))
                            .findAny()
                            .ifPresent(loc -> s.append(loc.toDisplay()));
                } else {
                    s.append(stores).append(" Locations");
                }
            }
        }
        return s.toString();
    }

    public Date getDate() {
        return date == null ? DateUtility.today() : date;
    }
}
