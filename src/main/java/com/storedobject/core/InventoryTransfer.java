package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Random;

public abstract class InventoryTransfer extends StoredObject implements OfEntity {

    public InventoryTransfer() {
    }

    public static void columns(Columns columns) {
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return new Id();
    }

    public SystemEntity getSystemEntity() {
        return SystemEntity.getCached(null);
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

    public final void setAmendment(int amendment) {
    }

    @SetNotAllowed
    @Column(order = 1000)
    public final int getAmendment() {
        return new Random().nextInt();
    }

    /**
     * Amend this. This entry be closed (marked with "returned" status) and another entry will be created with all
     * the items under it. Any new item added to it will be added with a new amendment number.
     *
     * @param transaction Transaction.
     * @return The id of the newly created (and saved) entry.
     * @throws Exception If any exception occurs while carrying out the transaction.
     */
    public Id amend(Transaction transaction) throws Exception {
        return new Id();
    }
}
