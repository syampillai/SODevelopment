package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import java.math.BigDecimal;

/**
 * Class to keep track of the JVs to be exchanged with foreign financial systems.
 *
 * @author Syam
 */
public final class JournalVoucherStage extends StoredObject {

    private Id voucherId, foreignSystemId;

    public static void columns(Columns columns) {
        columns.add("Voucher", "id");
        columns.add("ForeignSystem", "id");
    }

    public static void indices(Indices indices) {
        indices.add("ForeignSystem,Voucher", true);
    }

    public void setVoucher(Id voucherId) {
        if(!loading()) {
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

    @Column(style = "(any)", order = 100)
    public Id getVoucherId() {
        return voucherId;
    }

    public JournalVoucher getVoucher() {
        return getRelated(JournalVoucher.class, voucherId, true);
    }

    public void setForeignSystem(Id foreignSystemId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Foreign System");
        }
        this.foreignSystemId = foreignSystemId;
    }

    public void setForeignSystem(BigDecimal idValue) {
        setForeignSystem(new Id(idValue));
    }

    public void setForeignSystem(ForeignFinancialSystem foreignSystem) {
        setForeignSystem(foreignSystem == null ? null : foreignSystem.getId());
    }

    @Column(order = 200)
    public Id getForeignSystemId() {
        return foreignSystemId;
    }

    public ForeignFinancialSystem getForeignSystem() {
        return getRelated(ForeignFinancialSystem.class, foreignSystemId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        voucherId = tm.checkTypeAny(this, voucherId, JournalVoucher.class, false);
        foreignSystemId = tm.checkType(this, foreignSystemId, ForeignFinancialSystem.class, false);
        super.validateData(tm);
    }
}