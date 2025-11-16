package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class StockHistoryDate extends StoredObject implements DBTransaction.NoHistory {

    private final Date date = DateUtility.today();
    private Id locationId;

    public StockHistoryDate() {}

    public static void columns(Columns columns) {
        columns.add("Date", "date");
        columns.add("Location", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Date, Location", true);
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Column(order = 100)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setLocation(Id locationId) {
        this.locationId = locationId;
    }

    public void setLocation(BigDecimal idValue) {
        setLocation(new Id(idValue));
    }

    public void setLocation(InventoryLocation location) {
        setLocation(location == null ? null : location.getId());
    }

    @Column(style = "(any)", order = 200)
    public Id getLocationId() {
        return locationId;
    }

    public InventoryLocation getLocation() {
        InventoryLocation location = getRelated(InventoryLocation.class, locationId, true);
        return location == null ? getDeleted(InventoryLocation.class, locationId) : location;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (Utility.isEmpty(date)) {
            throw new Invalid_Value("Date");
        }
        super.validateData(tm);
    }

    public static boolean isComputed(Date date, InventoryStore store) {
        return isComputed(date, store == null ? null : store.getStoreBin());
    }

    public static boolean isComputed(Date date, InventoryLocation location) {
        return isComputed(date, location == null ? null : location.getId());
    }

    public static boolean isComputed(Date date, Id locationId) {
        if(date == null || locationId == null) return true;
        return exists(StockHistoryDate.class, "Date='" + Database.format(date) + "' AND Location=" + locationId);
    }

    public static void compute(TransactionManager tm, Date date, InventoryLocation location) throws Exception {
        if((location.getType() == 0 && !(location instanceof InventoryStoreBin)) || isComputed(date, location)) {
            return;
        }
        Stock stock = new Stock(location, date);
        stock.setTransactionManager(tm);
        stock.setNoZeroSave();
        QueryBuilder<InventoryItemType> qb = QueryBuilder.from(InventoryItemType.class).any().limit(1000).orderBy("Id");
        if(location instanceof InventoryFitmentPosition fp) {
            InventoryItem item = fp.getFittedItem();
            if(item == null) {
                qb.where("Id=" + fp.getAssembly().getItemTypeId());
            } else {
                qb.where("Id=" + item.getPartNumberId());
            }
        }
        List<InventoryItemType> items = new ArrayList<>();
        qb.list().collectAll(items);
        int tries = 3;
        while(!items.isEmpty()) {
            stock.open();
            for(InventoryItemType item: items) {
                stock.setPartNumber(item);
                stock.getStocks();
            }
            stock.close();
            if(stock.isSaveError()) {
                if(--tries <= 0) {
                    throw new SOException("Error computing stock history for " + location.getName() + " on "
                            + DateUtility.format(date));
                }
                continue;
            }
            qb.where("Id>" + items.getLast().getId());
            if(items.size() < 1000) {
                items.clear();
                break;
            }
            items.clear();
            qb.list().collectAll(items);
            tries = 3;
        }
        if(location instanceof InventoryFitmentPosition) return;
        StockHistoryDate d = new StockHistoryDate();
        d.setDate(date);
        d.setLocation(location.getId());
        tm.transact(t -> {
            ((DBTransaction)t).getSQL().executeUpdate("DELETE FROM core.StockHistory WHERE Date='" + Database.format(date) + "' AND (Quantity).Quantity=0");
            d.save(t);
        });
    }
}
