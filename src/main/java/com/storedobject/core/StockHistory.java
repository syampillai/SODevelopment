package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public class StockHistory extends StoredObject implements DBTransaction.NoHistory {

    private Id partNumberId;
    private final Date date = DateUtility.today();
    private String serialNumber;
    private Quantity quantity = Quantity.create(Quantity.class);
    private Money cost = new Money();
    private Id storeId = Id.ZERO;
    private Id locationId;
    private Id previousLocationId = Id.ZERO;

    public StockHistory() {}

    public static void columns(Columns columns) {
        columns.add("PartNumber", "id");
        columns.add("Date", "date");
        columns.add("SerialNumber", "text");
        columns.add("Quantity", "quantity");
        columns.add("Cost", "money");
        columns.add("Store", "id");
        columns.add("Location", "id");
        columns.add("PreviousLocation", "id");
    }

    public static void indices(Indices indices) {
        indices.add("PartNumber, Date, Location", "Store=0");
        indices.add("PartNumber, Date, Store", "Store<>0");
    }

    public void setPartNumber(Id partNumberId) {
        this.partNumberId = partNumberId;
    }

    public void setPartNumber(BigDecimal idValue) {
        setPartNumber(new Id(idValue));
    }

    public void setPartNumber(InventoryItemType partNumber) {
        setPartNumber(partNumber == null ? null : partNumber.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getPartNumberId() {
        return partNumberId;
    }

    public InventoryItemType getPartNumber() {
        InventoryItemType pn = getRelated(InventoryItemType.class, partNumberId, true);
        return pn == null ? getDeleted(InventoryItemType.class, partNumberId) : pn;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Column(order = 200)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Column(order = 300)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Object value) {
        setQuantity(Quantity.create(value));
    }

    @Column(order = 400)
    public Quantity getQuantity() {
        return quantity;
    }

    public void setCost(Money cost) {
        this.cost = cost;
    }

    public void setCost(Object moneyValue) {
        setCost(Money.create(moneyValue));
    }

    @Column(order = 500)
    public Money getCost() {
        return cost;
    }

    public void setStore(Id storeId) {
        this.storeId = storeId;
    }

    public void setStore(BigDecimal idValue) {
        setStore(new Id(idValue));
    }

    public void setStore(InventoryStore store) {
        setStore(store == null ? null : store.getId());
    }

    @Column(style = "(any)", required = false, order = 600)
    public Id getStoreId() {
        return storeId;
    }

    public InventoryStore getStore() {
        InventoryStore store = getRelated(InventoryStore.class, storeId, true);
        return store == null ? getDeleted(InventoryStore.class, storeId) : store;
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

    @Column(style = "(any)", order = 700)
    public Id getLocationId() {
        return locationId;
    }

    public InventoryLocation getLocation() {
        InventoryLocation location = getRelated(InventoryLocation.class, locationId, true);
        return location == null ? getDeleted(InventoryLocation.class, locationId) : location;
    }

    public void setPreviousLocation(Id previousLocationId) {
        this.previousLocationId = previousLocationId;
    }

    public void setPreviousLocation(BigDecimal idValue) {
        setPreviousLocation(new Id(idValue));
    }

    public void setPreviousLocation(InventoryLocation previousLocation) {
        setPreviousLocation(previousLocation == null ? null : previousLocation.getId());
    }

    @Column(style = "(any)", required = false, order = 800)
    public Id getPreviousLocationId() {
        return previousLocationId;
    }

    public InventoryLocation getPreviousLocation() {
        InventoryLocation location = getRelated(InventoryLocation.class, previousLocationId, true);
        return location == null ? getDeleted(InventoryLocation.class, previousLocationId) : location;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        partNumberId = tm.checkTypeAny(this, partNumberId, InventoryItemType.class, false);
        if (Utility.isEmpty(date)) {
            throw new Invalid_Value("Date");
        }
        if (StringUtility.isWhite(serialNumber)) {
            serialNumber = "N/A";
        }
        super.validateData(tm);
    }

    public boolean inTransit() {
        return !Id.isNull(previousLocationId);
    }

    public static ObjectIterator<StockHistory> listForLocation(InventoryItemType partNumber, Date date, Id locationId) {
        return list(StockHistory.class, "PartNumber=" + partNumber.getId() + " AND Date='" + Database.format(date) + "' AND Location=" + locationId);
    }

    public static ObjectIterator<StockHistory> listForStore(InventoryItemType partNumber, Date date, Id storeId) {
        return list(StockHistory.class, "PartNumber=" + partNumber.getId() + " AND Date='" + Database.format(date) + "' AND Store=" + storeId + " AND Store<>0");
    }
}
