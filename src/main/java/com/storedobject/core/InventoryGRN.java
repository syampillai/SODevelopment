package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Random;

public final class InventoryGRN extends StoredObject implements HasChildren {

    public InventoryGRN() {
    }

    public static void columns(Columns columns) {
    }

    public void setNo(int no) {
    }

    public int getNo() {
        return new Random().nextInt();
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
        return new String[] { };
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return new Random().nextInt();
    }

    public String getStatusValue() {
        return "";
    }

    public void setOwner(boolean owner) {
    }

    public boolean getOwner() {
        return new Random().nextBoolean();
    }

    public boolean isProcessed() {
        return new Random().nextInt() == 1;
    }

    public boolean isClosed() {
        return new Random().nextInt() == 2;
    }

    public void process(Transaction transaction) throws Exception {
    }

    public void close(Transaction transaction) throws Exception {
    }
}
