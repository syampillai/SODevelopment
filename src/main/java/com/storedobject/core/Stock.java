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

    private final List<Location> locations = new ArrayList<>();
    private final List<StockHistory> stocks = new ArrayList<>();
    private final List<StoredObject> saveObjects = new ArrayList<>();
    private final Date date;
    private InventoryItemType pn;
    private boolean computed = false, saveRemaining = false;
    private final boolean allStores;
    private TransactionManager tm;
    private Thread saverThread;
    private boolean saveError = false;
    private final boolean historyAvailable;
    private boolean noZeroSave = false;

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
        historyAvailable = date != null && StockHistoryDate.isComputed(date, Id.ZERO);
        if(location == null) {
            StoredObject.list(InventoryStore.class, true).forEach(s -> locations.add(new Location(s.getStoreBin(), this.date, historyAvailable)));
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
        if(location != null && !contains(location)) {
            locations.add(new Location(location, this.date, historyAvailable));
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
        computeHistory();
        return stocks;
    }

    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    private void save() {
        List<StoredObject> toSave = new ArrayList<>();
        try {
            if(!saveRemaining && saveObjects.size() < 1000) {
                return;
            }
            while (!saveObjects.isEmpty()) {
                toSave.add(saveObjects.removeFirst());
            }
            if(toSave.isEmpty()) {
                return;
            }
            tm.transact(t -> {
                for (StoredObject s : toSave) {
                    s.makeNew();
                    s.save(t);
                }
            });
        } catch (Exception e) {
            tm.log(e);
            saveError = true;
        }
    }

    public void computeHistory() {
        synchronized(stocks) {
            if(!computed) {
                computeInt();
                computed = true;
            }
        }
    }

    private void computeInt() {
        stocks.clear();
        if(pn == null) {
            return;
        }
        List<InventoryItem> items = null;
        boolean binSave = false;
        InventoryLocation location;
        for(Location loc: locations) {
            location = loc.location();
            if(date != null) {
                binSave = location instanceof InventoryStoreBin || location.getType() != 0;
                ObjectIterator<StockHistory> ss = location instanceof InventoryStoreBin sb
                        ? StockHistory.listForStore(pn, date, sb.getStoreId())
                        : StockHistory.listForLocation(pn, date, location.getId());
                boolean found = loc.computed;
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
            if(location instanceof InventoryFitmentPosition f) {
                InventoryItem ii = f.getFittedItem();
                if(ii != null) {
                    items.add(ii);
                }
            } else {
                (location instanceof InventoryStoreBin b ? pn.listStock(b.getStore()) : pn.listStock(location))
                        .collectAll(items);
            }
            if(date != null && tm != null && !noZeroSave && n == items.size() && binSave) { // Nothing new is added
                createStockHistory(location); // saving zero value
            }
        }
        if(date == null || items == null) {
            return;
        }
        if(items.isEmpty()) {
            if (!saveObjects.isEmpty()) {
                kickSaver();
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
            if(!contains(fromS) && !contains(toS)) {
                continue;
            }
            ii = item(items, m.getItemId());
            if(ii != null) {
                ii.location(from);
            }
            qtyMatched = serialized || (ii != null && ii.getQuantity().equals(m.getQuantity()));
            if(contains(fromS) && !contains(toS)) {
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
            } else if(!contains(fromS) && contains(toS)) {
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
        if(tm != null && !saveObjects.isEmpty()) {
            kickSaver();
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
        if(tm != null) {
            saveObjects.add(s);
            if(location instanceof InventoryFitmentPosition) {
                createStockHistoryDate(location);
            }
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
        saveObjects.add(s);
        if(location instanceof InventoryFitmentPosition) {
            createStockHistoryDate(location);
        }
    }

    private void createStockHistoryDate(InventoryLocation location) {
        StockHistoryDate d = new StockHistoryDate();
        d.setDate(date);
        d.setLocation(location);
        d.makeVirtual();
        saveObjects.add(d);
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
            if(locations.stream().anyMatch(l -> !(l.location instanceof InventoryStoreBin))) {
                s.append(" + Other Locations");
            }
        } else if(locations.size() == 1) {
            InventoryLocation location = locations.getFirst().location;
            if(location instanceof InventoryStoreBin) {
                s.append("Store");
            } else if(location instanceof InventoryFitmentPosition) {
                s.append("Assembly");
            } else {
                s.append("Location");
            }
            s.append(": ").append(location.toDisplay());
        } else {
            int stores = (int) locations.stream().map(l -> l.location)
                    .filter(loc -> loc instanceof InventoryStoreBin).count();
            int locs = (int) locations.stream().map(l -> l.location)
                    .filter(loc -> !(loc instanceof InventoryStoreBin)).count();
            if(stores == 0) {
                s.append(locs).append(" locations");
            } else if(locs == 0) {
                s.append(stores).append(" stores");
            } else {
                if(stores == 1) {
                    locations.stream().map(l -> l.location)
                            .filter(loc -> loc instanceof InventoryStoreBin)
                            .map(loc -> ((InventoryStoreBin) loc).getStore()).findAny()
                            .ifPresent(store -> s.append(store.toDisplay()));
                } else {
                    s.append(stores).append(" Stores");
                }
                s.append(" + ");
                if(locs == 1) {
                    locations.stream().map(l -> l.location)
                            .filter(loc -> !(loc instanceof InventoryStoreBin))
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

    private void kickSaver() {
        if(tm != null && !saveObjects.isEmpty() && (saverThread == null || !saverThread.isAlive())) {
            saverThread = Thread.startVirtualThread(this::save);
        }
    }

    public boolean isSaveError() {
        return saveError;
    }

    public void setNoZeroSave() {
        this.noZeroSave = true;
    }

    public void open() {
        close();
        computed = false;
        saveRemaining = false;
        saveError = false;
    }

    public void close() {
        saveRemaining = true;
        int tries = 3;
        while(tries-- > 0) {
            kickSaver();
            if (saverThread != null) {
                try {
                    saverThread.join();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private boolean contains(InventoryLocation location) {
        return locations.stream().anyMatch(loc -> loc.location().equals(location));
    }

    private record Location(InventoryLocation location, boolean computed) {

        private Location(InventoryLocation location, Date date, boolean computed) {
            this(location, computed || StockHistoryDate.isComputed(date, location));
        }
    }
}
