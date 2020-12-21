package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Random;

public final class MaterialRequest extends StoredObject {

    public MaterialRequest() {
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
        setFromLocation(new Id(idValue));
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
        setToLocation(new Id(idValue));
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
        return new Random().nextInt();
    }

    public static String[] getStatusValues() {
        return new String[0];
    }

    public static String getStatusValue(int value) {
        return "";
    }

    public String getStatusValue() {
        return "";
    }

    public void setPriority(Id priorityId) {
    }

    public void setPriority(BigDecimal idValue) {
    }

    public void setPriority(MaterialRequestPriority priority) {
    }

    public Id getPriorityId() {
        return new Id();
    }

    public MaterialRequestPriority getPriority() {
        return new MaterialRequestPriority();
    }

    public void setRequiredBefore(Timestamp requiredBefore) {
    }

    public Timestamp getRequiredBefore() {
        return new Timestamp(0);
    }

    public void setRemarks(String remarks) {
    }

    public String getRemarks() {
        return "";
    }

    public void request(Transaction transaction) throws Exception {
    }

    public void foreclose(Transaction transaction) throws Exception {
    }
}
