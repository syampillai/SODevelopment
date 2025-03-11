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
    private boolean posted;
    private JournalVoucher voucher;

    public static void columns(Columns columns) {
        columns.add("Voucher", "id");
        columns.add("ForeignSystem", "id");
        columns.add("Posted", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("ForeignSystem,Voucher", true);
    }

    public static String[] browseColumns() {
        return new String[] {
                "Voucher.Reference AS Voucher",
                "Voucher.Date AS Date",
                "Voucher.OriginatedFrom AS Origin",
                "Voucher.GeneratedBy AS Created by",
                "ForeignSystem.Name AS External System",
                "Posted"
        };
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
        if(voucher == null) {
            voucher = getRelated(JournalVoucher.class, voucherId, true);
        }
        return voucher;
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

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    @Column(order = 300)
    public boolean getPosted() {
        return posted;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        voucherId = tm.checkTypeAny(this, voucherId, JournalVoucher.class, false);
        foreignSystemId = tm.checkType(this, foreignSystemId, ForeignFinancialSystem.class, false);
        super.validateData(tm);
    }
}