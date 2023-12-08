package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Internal class to keep track of the JVs to be exchanged with external systems.
 *
 * @author Syam
 */
public final class JournalVoucherStage extends StoredObject {

    private static final String[] stageValues =
            new String[]{
                    "Opened", "In Progress", "Closed",
            };
    private Id voucherId;
    private int bitPosition;
    private int stage = 0;
    private int processor;

    public static void columns(Columns columns) {
        columns.add("Voucher", "id");
        columns.add("BitPosition", "int");
        columns.add("Stage", "int");
        columns.add("Processor", "int");
    }

    public static void indices(Indices indices) {
        indices.add("BitPosition,Voucher", true);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
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

    public void setBitPosition(int bitPosition) {
        this.bitPosition = bitPosition;
    }

    @Column(required = false, order = 200)
    public int getBitPosition() {
        return bitPosition;
    }

    public void setStage(int stage) {
        if(!loading()) {
            throw new Set_Not_Allowed("Stage");
        }
        this.stage = stage;
    }

    @Column(order = 300)
    public int getStage() {
        return stage;
    }

    public static String[] getStageValues() {
        return stageValues;
    }

    public static String getStageValue(int value) {
        String[] s = getStageValues();
        return s[value % s.length];
    }

    public String getStageValue() {
        return getStageValue(stage);
    }

    public void setProcessor(int processor) {
        if(!loading()) {
            throw new Set_Not_Allowed("Processor");
        }
        this.processor = processor;
    }

    @Column(required = false, order = 400)
    public int getProcessor() {
        return processor;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        voucherId = tm.checkTypeAny(this, voucherId, JournalVoucher.class, false);
        super.validateData(tm);
    }

    public static JournalVoucherStage getNext(TransactionManager transactionManager, int processor, int bitPosition)
            throws Exception {
        if(new Random().nextBoolean()) {
            throw new Invalid_State("");
        }
        return new Random().nextBoolean() ? null : new JournalVoucherStage();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void close(TransactionManager transactionManager) throws Exception {
        if(stage != 1) {
            throw new Invalid_State("Already closed");
        }
        JournalVoucher jv = getVoucher();
        jv.setStage(jv.getStage() | (1 << bitPosition));
        if(exists(JournalVoucherStage.class, "BitPosition=" + bitPosition + " AND Voucher>" + voucherId)) {
            stage = 100;
            transactionManager.transact(t -> {
                delete(t);
                jv.save(t);
            });
        } else {
            stage = 2;
            transactionManager.transact(t -> {
                save(t);
                jv.save(t);
            });
        }
    }

    public JournalVoucherStage mark(TransactionManager transactionManager) throws Exception {
        if(stage == 1) {
            return this;
        }
        if(stage != 0) {
            throw new Invalid_State("Illegal");
        }
        JournalVoucherStage jvs = get(JournalVoucherStage.class, getId());
        if(stage == 1) {
            return jvs;
        }
        stage = 1;
        //noinspection ResultOfMethodCallIgnored
        transactionManager.transact(this::save);
        jvs = get(JournalVoucherStage.class, getId());
        if(jvs == null || jvs.stage != 1) {
            throw new Invalid_State("Illegal");
        }
        return jvs;
    }

    @Override
    public void validateDelete() throws Exception {
        if(stage != 100) {
            throw new Invalid_State("Illegal");
        }
        stage = 2;
        super.validateDelete();
    }
}