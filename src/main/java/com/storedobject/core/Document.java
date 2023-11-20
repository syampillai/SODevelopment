package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public abstract class Document<T extends StoredObject> extends FileData {

    private Id typeId, ownerId;
    private String no;
    private final Date issuedOn = DateUtility.today();
    private final Date expiry = DateUtility.today();

    public Document() {
    }

    public static void columns(Columns columns) {
        columns.add("Type", "id");
        columns.add("No", "text");
        columns.add("Owner", "id");
        columns.add("IssuedOn", "date");
        columns.add("Expiry", "date");
    }

    public static void indices(Indices indices) {
        indices.add("lower(No),Type");
        indices.add("Type,lower(No)");
        indices.add("Owner");
    }

    public void setType(Id typeId) {
        this.typeId = typeId;
    }

    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public void setType(DocumentType type) {
        setType(type == null ? null : type.getId());
    }

    @Column(order = 500)
    public Id getTypeId() {
        return typeId;
    }

    public DocumentType getType() {
        return getRelated(DocumentType.class, typeId);
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Column(order = 600)
    public String getNo() {
        return no;
    }

    public void setIssuedOn(Date issuedOn) {
        this.issuedOn.setTime(issuedOn.getTime());
    }

    @Column(caption = "Created/Issued on", order = 700)
    public Date getIssuedOn() {
        return new Date(issuedOn.getTime());
    }

    public void setExpiry(Date expiry) {
        this.expiry.setTime(expiry.getTime());
    }

    @Column(order = 800)
    public Date getExpiry() {
        return new Date(expiry.getTime());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        typeId = tm.checkType(this, typeId, DocumentType.class, false);
        ownerId = allowAny() ? tm.checkTypeAny(this, typeId, getOwnerClass(), false)
                : tm.checkType(this, typeId, getOwnerClass(), false);
        if (StringUtility.isWhite(no)) {
            throw new Invalid_Value("No");
        }
        if (Utility.isEmpty(issuedOn)) {
            throw new Invalid_Value("Issued on");
        }
        if (Utility.isEmpty(expiry)) {
            throw new Invalid_Value("Expiry");
        }
        super.validateData(tm);
    }

    public T getOwner() {
        return getRelated(getOwnerClass(), ownerId);
    }

    public final Id getOwnerId() {
        return ownerId;
    }

    public final void setOwner(BigDecimal idValue) {
        setOwner(new Id(idValue));
    }

    public final void setOwner(T owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    public final void setOwner(Id ownerId) {
        if (!loading() && !Id.equals(this.getOwnerId(), ownerId)) {
            throw new Set_Not_Allowed("Owner");
        }
        this.ownerId = ownerId;
    }

    protected abstract Class<T> getOwnerClass();

    protected boolean allowAny() {
        return false;
    }
}
