package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Random;

public abstract class InventoryTransfer extends StoredObject {

    public InventoryTransfer() {
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

    public void setInvoiceNumber(String invoiceNumber) {
    }

    public String getInvoiceNumber() {
        return "";
    }

    public void setInvoiceDate(Date invoiceDate) {
    }

    public Date getInvoiceDate() {
        return new Date(0);
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

    public void setRemark(String remark) {
    }

    public String getRemark() {
        return "";
    }

    public void send(Transaction transaction) throws Exception {
    }

    public void receive(Transaction transaction) throws Exception {
    }
    public final String getReference() {
        return "RO";
    }
}
