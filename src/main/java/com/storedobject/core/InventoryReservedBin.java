package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public final class InventoryReservedBin extends InventoryBin {

    private Id binId;
    private InventoryBin bin;
    private Id reservedById = Id.ZERO;
    boolean illegal = true;

    public InventoryReservedBin() {
    }

    public static void columns(Columns columns) {
        columns.add("Bin", "id");
        columns.add("ReservedBy", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Bin, ReservedBy", true);
        indices.add("ReservedBy");
    }

    public String getUniqueCondition() {
        return "Bin=" + binId + " AND ReservedBy=" + reservedById;
    }

    public void setBin(Id binId) {
        this.binId = binId;
    }

    public void setBin(BigDecimal idValue) {
        setBin(new Id(idValue));
    }

    public void setBin(InventoryBin bin) {
        setBin(bin == null ? null : bin.getId());
    }

    @Column(style = "(any)", order = 100)
    public Id getBinId() {
        return binId;
    }

    public InventoryBin getBin() {
        if(bin == null) {
            bin = get(getTransaction(), InventoryBin.class, binId, true);
        }
        return bin;
    }

    public void setReservedBy(Id reservedById) {
        this.reservedById = reservedById;
    }

    public void setReservedBy(BigDecimal idValue) {
        setReservedBy(new Id(idValue));
    }

    public void setReservedBy(MaterialRequest reservedBy) {
        setReservedBy(reservedBy == null ? null : reservedBy.getId());
    }

    @Column(required = false, style = "(any)", order = 200)
    public Id getReservedById() {
        return reservedById;
    }

    public MaterialRequest getReservedBy() {
        return get(getTransaction(), MaterialRequest.class, reservedById, true);
    }

    @Override
    void loadedCore() {
        super.loadedCore();
        illegal = true;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        bin = null;
        illegal = true;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(illegal) {
            throw new Invalid_State("Illegal");
        }
        binId = tm.checkTypeAny(this, binId, InventoryBin.class, false);
        InventoryBin bin = getBin();
        if(bin instanceof InventoryReservedBin) {
            throw new Invalid_State("Bin");
        }
        reservedById = tm.checkTypeAny(this, reservedById, MaterialRequest.class, true);
        MaterialRequest mr = getReservedBy();
        if(mr != null) {
            String ref = String.valueOf(mr.getNo());
            ref += "/" + DateUtility.formatDate(mr.getDate())
                    + (ref.equals(mr.getReference()) ? " " : (" Ref:" + mr.getReference()));
            setName(ref + " (" + DateUtility.formatDate(mr.getRequiredBefore()) + ")*" + bin.getId());
        }
        super.validateData(tm);
    }

    @Override
    public String toDisplay() {
        String n = getName();
        int i = n.lastIndexOf('*');
        if(i > 0) {
            n = n.substring(0, i);
        }
        return getBin().getName() + " " + n;
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        getBin();
        if(bin != null && !bin.deleted()) {
            throw new Invalid_State("Deletion not allowed");
        }
    }

    @Override
    boolean checkStorage(InventoryItemType partNumber) {
        return getBin().canBin(partNumber);
    }

    private static InventoryReservedBin getFor(Transaction transaction, InventoryBin bin,
                                               MaterialRequest materialRequest) {
        return get(transaction, InventoryReservedBin.class, "Bin=" + bin.getId() + " AND ReservedBy="
                + materialRequest.getId());
    }

    public static InventoryReservedBin createFor(Transaction transaction, InventoryBin bin,
                                                 MaterialRequest materialRequest) throws Exception {
        InventoryReservedBin rb = getFor(transaction, bin, materialRequest);
        if(rb != null) {
            return rb;
        }
        createInt(transaction, bin, materialRequest);
        return getFor(transaction, bin, materialRequest);
    }

    private static void createInt(Transaction transaction, InventoryBin bin,
                                                 MaterialRequest materialRequest) throws Exception {
        InventoryReservedBin rb = list(transaction, InventoryReservedBin.class, "Bin=" + bin.getId()
                + " AND ReservedBy=0").filter(b -> !transaction.isInvolved(b.getId())).findFirst();
        if(rb == null) {
            rb = new InventoryReservedBin();
            rb.binId = bin.getId();
            rb.setStore(bin.getStoreId());
            rb.setPickingOrder(bin.getPickingOrder());
        }
        rb.reservedById = materialRequest.getId();
        rb.illegal = false;
        rb.save(transaction);
    }

    @Override
    public String getTypeDescription() {
        return "Reserved";
    }
}
