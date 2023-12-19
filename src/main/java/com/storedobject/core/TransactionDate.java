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

    public static void columns(Columns columns) {
    }

    public void setVoucher(Id voucherId) {
        if (!loading() && !Id.equals(this.getVoucherId(), voucherId)) {
            throw new Set_Not_Allowed("Voucher");
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
        this.currentDate.setTime(currentDate.getTime());
    }

    @Column(order = 200)
    public Date getCurrentDate() {
        return new Date(currentDate.getTime());
    }

    public void setNewDate(Date newDate) {
        this.newDate.setTime(newDate.getTime());
    }

    @Column(order = 300)
    public Date getNewDate() {
        return new Date(newDate.getTime());
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Column(order = 400)
    public String getRemarks() {
        return remarks;
    }
}
