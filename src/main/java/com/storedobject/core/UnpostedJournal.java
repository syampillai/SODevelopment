package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class UnpostedJournal extends StoredObject implements OfEntity {

    private Id systemEntityId;
    private Id ownerId;
    private final Date date = DateUtility.today();
    private String jVClassName;
    private String extraInformation;

    public UnpostedJournal() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Owner", "id");
        columns.add("Date", "date");
        columns.add("JVClassName", "text");
        columns.add("ExtraInformation", "text");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity,Date,No");
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Entries|com.storedobject.core.UnpostedJournalEntry|DisplayOrder||0",
        };
    }

    public void setSystemEntity(Id systemEntityId) {
        if (!loading() && !Id.equals(this.getSystemEntityId(), systemEntityId)) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        return getRelated(SystemEntity.class, systemEntityId);
    }

    public void setOwner(Id ownerId) {
        if (!loading() && !Id.equals(this.getOwnerId(), ownerId)) {
            throw new Set_Not_Allowed("Owner");
        }
        this.ownerId = ownerId;
    }

    public void setOwner(BigDecimal idValue) {
        setOwner(new Id(idValue));
    }

    public void setOwner(StoredObject owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    @SetNotAllowed
    @Column(order = 300)
    public Id getOwnerId() {
        return ownerId;
    }

    public StoredObject getOwner() {
        return getRelated(StoredObject.class, ownerId);
    }

    public void setDate(Date date) {
        if (!loading()) {
            throw new Set_Not_Allowed("Date");
        }
        this.date.setTime(date.getTime());
    }

    @SetNotAllowed
    @Column(order = 400)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setJVClassName(String jVClassName) {
        if (!loading()) {
            throw new Set_Not_Allowed("JV Class Name");
        }
        this.jVClassName = jVClassName;
    }

    @SetNotAllowed
    @Column(order = 500)
    public String getJVClassName() {
        return jVClassName;
    }

    public void setExtraInformation(String extraInformation) {
        if (!loading()) {
            throw new Set_Not_Allowed("Extra Information");
        }
        this.extraInformation = extraInformation;
    }

    @SetNotAllowed
    @Column(required = false, order = 600)
    public String getExtraInformation() {
        return extraInformation;
    }
}