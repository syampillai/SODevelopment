package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public final class MaterialIssued extends StoredObject {

    public MaterialIssued() {
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

    public void setLocation(Id locationId) {
    }

    public void setLocation(BigDecimal idValue) {
    }

    public void setLocation(InventoryLocation location) {
    }

    public Id getLocationId() {
        return new Id();
    }

    public InventoryLocation getLocation() {
        return new InventoryBin();
    }

    public void setRequest(Id requestId) {
    }

    public void setRequest(BigDecimal idValue) {
    }

    public void setRequest(MaterialRequest request) {
    }

    public Id getRequestId() {
        return new Id();
    }

    public MaterialRequest getRequest() {
        return new MaterialRequest();
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
        return "";
    }

    public void setSentAt(Timestamp sentAt) {
    }

    public Timestamp getSentAt() {
        return new Timestamp(0);
    }

    public void issue(Transaction transaction) throws Exception {
    }

    public void close(Transaction transaction) throws Exception {
    }

    public void issueReserved(Transaction transaction) throws Exception {
    }

    public boolean isReserved() {
        return Math.random() > 0.5;
    }

    public boolean getReserved() {
        return isReserved();
    }
}
