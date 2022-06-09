package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public final class MaterialRequest extends StoredObject implements OfEntity {

    public MaterialRequest() {
    }

    public static void columns(Columns columns) {
    }

    public void setSystemEntity(Id systemEntityId) {
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
        return 0;
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public String getReference() {
        return "";
    }

    public String getIssueReference() {
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

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
        setPerson(new Id(idValue));
    }

    public void setPerson(Person person) {
        setPerson(person == null ? null : person.getId());
    }

    public Id getPersonId() {
        return new Id();
    }

    public Person getPerson() {
        return new Person();
    }

    public void setReserved(boolean reserved) {
    }

    public boolean getReserved() {
        return false;
    }

    public void setReceived(boolean received) {
    }

    public boolean getReceived() {
        return false;
    }

    public void releaseReservation(Transaction transaction) throws Exception {
    }

    public void request(Transaction transaction) throws Exception {
    }

    public void reserve(Transaction transaction) throws Exception {
    }

    public void requestForIssuance(Transaction transaction) throws Exception {
    }

    public void foreclose(Transaction transaction) throws Exception {
    }
}