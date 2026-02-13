package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class TransactionDate extends StoredObject {

    private Id voucherId;
    private final Date currentDate = DateUtility.today();
    private final Date newDate = DateUtility.today();
    private String remarks;

    public TransactionDate() {
    }

    TransactionDate(Id voucherId, Date currentDate, Date newDate, String remarks) {
        this.voucherId = voucherId;
        this.currentDate.setTime(currentDate.getTime());
        this.newDate.setTime(newDate.getTime());
        this.remarks = remarks;
    }

    public static void columns(Columns columns) {
        columns.add("Voucher", "id");
        columns.add("CurrentDate", "date");
        columns.add("NewDate", "date");
        columns.add("Remarks", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Voucher,TranId", true);
    }

    public String getUniqueCondition() {
        return "Voucher=" + getVoucherId() + " AND TranId=" + getTransactionId();
    }

    private boolean notLoading() {
        if(getId() == null) {
            return true;
        }
        if(isVirtual()) {
            return true;
        }
        return !loading();
    }

    public void setVoucher(Id voucherId) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Illegal call");
        }
        this.voucherId = voucherId;
    }

    public void setVoucher(BigDecimal idValue) {
        setVoucher(new Id(idValue));
    }

    public void setVoucher(JournalVoucher voucher) {
        setVoucher(voucher == null ? null : voucher.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 100)
    public Id getVoucherId() {
        return voucherId;
    }

    public JournalVoucher getVoucher() {
        return getRelated(JournalVoucher.class, voucherId, true);
    }

    public void setCurrentDate(Date currentDate) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Illegal call");
        }
        this.currentDate.setTime(currentDate.getTime());
    }

    @Column(order = 200)
    public Date getCurrentDate() {
        return new Date(currentDate.getTime());
    }

    public void setNewDate(Date newDate) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Illegal call");
        }
        this.newDate.setTime(newDate.getTime());
    }

    @Column(order = 300)
    public Date getNewDate() {
        return new Date(newDate.getTime());
    }

    public void setRemarks(String remarks) {
        if (notLoading()) {
            throw new Set_Not_Allowed("Illegal call");
        }
        this.remarks = remarks;
    }

    @Column(order = 400)
    public String getRemarks() {
        return remarks;
    }

    @Override
    public void validate() throws Exception {
        if(!inserted()) {
            throw new Invalid_State("Not allowed");
        }
        super.validate();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        voucherId = tm.checkTypeAny(this, voucherId, JournalVoucher.class, false);
        if (Utility.isEmpty(currentDate)) {
            throw new Invalid_Value("Current Date");
        }
        if (Utility.isEmpty(newDate)) {
            throw new Invalid_Value("New Date");
        }
        if (StringUtility.isWhite(remarks)) {
            throw new Invalid_Value("Remarks");
        }
        super.validateData(tm);
    }
}