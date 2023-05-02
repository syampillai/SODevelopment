package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public abstract class Document extends FileData {

    private Id typeId;
    private String no;
    private final Date issuedOn = DateUtility.today();
    private final Date expiry = DateUtility.today();

    public Document() {
    }

    public static void columns(Columns columns) {
        columns.add("Type", "id");
        columns.add("No", "text");
        columns.add("IssuedOn", "date");
        columns.add("Expiry", "date");
    }

    public static void indices(Indices indices) {
        indices.add("lower(No),Type");
        indices.add("Type,lower(No)");
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

    public abstract StoredObject getOwner();

    public abstract Id getOwnerId();

    public abstract void setOwner(Id ownerId);
}
