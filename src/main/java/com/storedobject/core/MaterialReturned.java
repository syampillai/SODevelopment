package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

public final class MaterialReturned extends StoredObject {

    public MaterialReturned() {
    }

    public static void columns(Columns columns) {
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

    public void setFromLocation(Id fromLocationId) {
    }

    public void setFromLocation(BigDecimal idValue) {
    }

    public void setFromLocation(InventoryLocation fromLocation) {
    }

    public Id getFromLocationId() {
        return new Id();
    }

    public InventoryLocation getFromLocation() {
        return new InventoryBin();
    }

    public void setToLocation(Id toLocationId) {
    }

    public void setToLocation(BigDecimal idValue) {
    }

    public void setToLocation(InventoryLocation toLocation) {
    }

    public Id getToLocationId() {
        return new Id();
    }

    public InventoryLocation getToLocation() {
        return new InventoryBin();
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return 0;
    }

    public static String[] getStatusValues() {
        return new String[] {};
    }

    public static String getStatusValue(int value) {
        return "";
    }

    public String getStatusValue() {
        return getStatusValue(0);
    }

    public void send(Transaction transaction) throws Exception {
    }

    public void receive(Transaction transaction) throws Exception {
    }
}