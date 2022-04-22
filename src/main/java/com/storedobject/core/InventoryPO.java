package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;
import java.util.Random;

public class InventoryPO extends StoredObject implements HasChildren {

    public InventoryPO() {
    }

    public static void columns(Columns columns) {
    }

    public void setNo(int no) {
    }

    public int getNo() {
        return 0;
    }

    protected String getSerialNoTag() {
        return "PO";
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
        return new String[] {};
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return 0;
    }

    public String getStatusValue() {
        return "";
    }

    public boolean isClosed() {
        return new Random().nextBoolean();
    }

    public void placeOrder(Transaction transaction) throws Exception {
    }

    public void closeOrder(Transaction transaction) throws Exception {
    }

    public boolean canClose() {
        return new Random().nextBoolean();
    }

    public boolean canForeclose() {
        return new Random().nextBoolean();
    }

    /**
     * Create a GRN for this PO.
     *
     * @param transaction Current transaction.
     * @param quantities Quantity values to process. The {@link Id} (key of the map) must the {@link Id} of the
     *                   respective line item entry in the PO.
     * @param grn A GRN to which the entries to be added. If <code>null</code> is passed, a new GRN is created.
     * @param invoiceNumber Invoice number (of the supplier) if applicable. For existing GRNs, <code>null</code>
     *                      could be passed.
     * @param invoiceDate Invoice date (of the supplier) if applicable. For existing GRNs, <code>null</code>
     *                    could be passed.
     * @return The GRN that is created/modified.
     * @throws Exception If the GRN can't be created.
     */
    public InventoryGRN createGRN(Transaction transaction, Map<Id, Quantity> quantities, InventoryGRN grn,
                                  String invoiceNumber, Date invoiceDate)
            throws Exception {
        return new InventoryGRN();
    }

    public final ObjectIterator<InventoryPOItem> listItems() {
        return listLinks(getTransaction(), InventoryPOItem.class, true);
    }

    /**
     * Process this PO as "data pickup". All associated processing will be carried out and the items will be
     * received in the store, and it will look like procured through this PO. A single GRN will be created for it.
     * <p>Note: Before calling this method please ake sure that all details are correctly set because no manual
     * editing is possible after processing this.</p>
     *
     * @param tm Transaction manager.
     * @param invoiceNumber Invoice number of the supplier.
     * @param invoiceDate Invoice date of the supplier.
     * @throws Exception if any error occurs.
     */
    public void processPickup(TransactionManager tm, String invoiceNumber, Date invoiceDate) throws Exception {
    }
}
