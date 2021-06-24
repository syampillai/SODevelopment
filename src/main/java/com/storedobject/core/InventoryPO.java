package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;
import java.util.Random;

public class InventoryPO extends StoredObject implements HasChildren {

    public InventoryPO() {
    }

    public static void columns(Columns columns) {
    }

    public void setNo(int no) {
    }

    public int getNo() {
        return 0;
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public void setReferenceNumber(String referenceNumber) {
    }

    public String getReferenceNumber() {
        return "";
    }

    public void setStore(Id storeId) {
    }

    public void setStore(BigDecimal idValue) {
    }

    public void setStore(InventoryStore store) {
    }

    public Id getStoreId() {
        return new Id();
    }

    public InventoryStore getStore() {
        return new InventoryStore();
    }

    public void setSupplier(Id supplierId) {
    }

    public void setSupplier(BigDecimal idValue) {
    }

    public void setSupplier(Entity supplier) {
    }

    public Id getSupplierId() {
        return new Id();
    }

    public Entity getSupplier() {
        return new Entity();
    }

    public static String[] getStatusValues() {
        return new String[] {};
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return 0;
    }

    public String getStatusValue() {
        return "";
    }

    public boolean isClosed() {
        return new Random().nextBoolean();
    }

    public void placeOrder(Transaction transaction) throws Exception {
    }

    public void closeOrder(Transaction transaction) throws Exception {
    }

    public boolean canClose() {
        return new Random().nextBoolean();
    }

    public boolean canForeclose() {
        return new Random().nextBoolean();
    }

    public InventoryGRN createGRN(Transaction transaction, Map<Id, Quantity> quantities, InventoryGRN grn) throws Exception {
        return new InventoryGRN();
    }

    public final ObjectIterator<InventoryPOItem> listItems() {
        return listLinks(getTransaction(), InventoryPOItem.class, true);
    }
}
