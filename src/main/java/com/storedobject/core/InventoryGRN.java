package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Random;

public final class InventoryGRN extends StoredObject implements HasChildren {

    public InventoryGRN() {
    }

    public static void columns(Columns columns) {
    }

    public void setNo(int no) {
    }

    public int getNo() {
        return new Random().nextInt();
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return new Date(0);
    }

    public void setReferenceNumber(String referenceNumber) {
    }

    public String getReferenceNumber() {
        return "";
    }

    public void setInvoiceDate(Date invoiceDate) {
    }

    public Date getInvoiceDate() {
        return new Date(0);
    }

    public void setStore(Id storeId) {
    }

    public void setStore(BigDecimal idValue) {
    }

    public void setStore(InventoryStore store) {
    }

    public Id getStoreId() {
        return new Id();
    }

    public InventoryStore getStore() {
        return new InventoryStore();
    }

    public void setSupplier(Id supplierId) {
    }

    public void setSupplier(BigDecimal idValue) {
    }

    public void setSupplier(Entity supplier) {
    }

    public Id getSupplierId() {
        return new Id();
    }

    public Entity getSupplier() {
        return new Entity();
    }

    public static String[] getStatusValues() {
        return new String[] { };
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return new Random().nextInt();
    }

    public String getStatusValue() {
        return "";
    }

    public static String[] getTypeValues() {
        return new String[] { };
    }

    public void setType(int type) {
    }

    public int getType() {
        return 0;
    }

    public String getTypeValue() {
        return "";
    }

    public void setLandedCost(Money landedCost) {
    }

    public void setLandedCost(Object moneyValue) {
        setLandedCost(Money.create(moneyValue));
    }

    public Money getLandedCost() {
        return new Money();
    }

    public boolean isProcessed() {
        return new Random().nextInt() == 1;
    }

    public boolean isClosed() {
        return new Random().nextInt() == 2;
    }

    public void process(Transaction transaction) throws Exception {
    }

    public String getReference() {
        return "GRN";
    }

    public void close(Transaction transaction) throws Exception {
    }

    public void updateInvoiceDetails(TransactionManager tm, String invoiceNumber, Date invoiceDate) throws  Exception {
    }

    /**
     * Get the MT of this GRN. The GRN may have created for a Purchase Order and in that case, this method returns null.
     *
     * @return An instance of {@link MaterialReturned} if found, otherwise null.
     */
    public MaterialReturned getMR() {
        return listMasters(MaterialReturned.class).single(false);
    }

    /**
     * Get the PO of this GRN. There could be multiple POs and in that case it will always return null.
     * Also, the GRN may have created for a {@link MaterialReturned} and in that case also, this method returns null.
     *
     * @return PO if found, otherwise null.
     */
    public InventoryPO getPO() {
        return Math.random() > 0.5 ? null : new InventoryPO();
    }

    /**
     * Get all the POs of this GRN.
     *
     * @return List of POs.
     */
    public List<InventoryPO> listPOs() {
        return listMasters(InventoryPO.class, true).toList();
    }

    /**
     * Is a specific type of landed cost is applicable to this GRN?
     *
     * @param landedCostType Type of landed cost.
     * @return True/false.
     */
    public boolean isApplicable(LandedCostType landedCostType) {
        InventoryPO po = getPO();
        return po != null && po.isApplicable(landedCostType, this);
    }

    /**
     * Compute/recompute the landed cost.
     *
     * @param tm Transaction manager.
     * @throws Exception If any error occurs.
     */
    public void computeLandedCost(TransactionManager tm) throws Exception {
    }
}
