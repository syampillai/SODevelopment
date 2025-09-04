package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;
import com.storedobject.core.annotation.Table;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.List;
import java.util.Map;

/**
 * Base class for creating Purchase Orders (POs). This class may be used as such, or it can be extended to add more
 * fields if required. It may also be used for another type of orders such as "Loan Orders" etc. and in such cases,
 * the appropriate GRN type ({@link #getGRNType()}) should be returned.
 *
 * @author Syam
 */
@Table(anchors = "Store")
public class InventoryPO extends StoredObject implements HasChildren, HasReference, TradeType {

    private static final ReferencePattern<InventoryPO> ref = new ReferencePattern<>();
    private final static String[] statusValues = {
            "Initiated", "Ordered", "Partially Received", "Received", "Closed"
    };
    private final Date date = DateUtility.today();
    private String referenceNumber = "";
    private String reference;
    Id storeId;
    Id supplierId;
    int status = 0;
    int no = 0;
    private boolean approvalRequired = true;
    private boolean internal = false;

    public InventoryPO() {
    }

    public static void columns(Columns columns) {
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("ReferenceNumber", "text");
        columns.add("Store", "id");
        columns.add("Supplier", "id");
        columns.add("Status", "int");
        columns.add("ApprovalRequired", "boolean");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "No",
                "ReferenceNumber",
                "Status",
                "ApprovalRequired",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "Date",
                "Reference",
                "ReferenceNumber AS Other Reference",
                "Supplier",
                "Status",
        };
    }

    public static void indices(Indices indices) {
        indices.add("Store,Date");
        indices.add("Store,No");
        indices.add("Store", "Status<4");
    }

    public static String[] searchColumns() {
        return new String[] { "Date", "No" };
    }

    public static String[] links() {
        return new String[]{
                "Items|com.storedobject.core.InventoryPOItem|||0",
        };
    }

    public void setNo(int no) {
        if (!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    @Override
    @SetNotAllowed
    @Column(style = "(serial)", order = 10)
    public int getNo() {
        if (no == 0) {
            Transaction t = getTransaction();
            no = SerialGenerator.generate(t, SerialConfigurator.getFor(getClass()).getYearPrefix(t)
                    + getTagPrefix() + ref.getTag(this)).intValue();
        }
        return no;
    }

    @Override
    public String getTagPrefix() {
        return "PO-";
    }

    @Override
    public SystemEntity getSystemEntity() {
        return getStore().getSystemEntity();
    }

    @Override
    public final String getReference() {
        if (reference == null) {
            reference = ref.get(this);
        }
        return reference == null ? "" : reference;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Override
    @Column(order = 100)
    public Date getDate() {
        return new Date(date.getTime());
    }

    @Deprecated
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = toCode(referenceNumber);
    }

    @Deprecated
    @Column(order = 200, required = false, caption = "Other Reference")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setStore(Id storeId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Store");
        }
        this.storeId = storeId;
    }

    public void setStore(BigDecimal idValue) {
        setStore(new Id(idValue));
    }

    public void setStore(InventoryStore store) {
        setStore(store == null ? null : store.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 300)
    public Id getStoreId() {
        return storeId;
    }

    public InventoryStore getStore() {
        return InventoryStore.getStore(storeId);
    }

    public void setSupplier(Id supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplier(BigDecimal idValue) {
        setSupplier(new Id(idValue));
    }

    public void setSupplier(Entity supplier) {
        setSupplier(supplier == null ? null : supplier.getId());
    }

    @Column(order = 400)
    public Id getSupplierId() {
        return supplierId;
    }

    public Entity getSupplier() {
        return get(Entity.class, supplierId);
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public void setStatus(int status) {
        if(!loading()) {
            throw new Set_Not_Allowed("Status");
        }
        if(status < 0 || status >= statusValues.length) {
            throw new SORuntimeException();
        }
        this.status = status;
    }

    @SetNotAllowed
    @Column(order = 500)
    public int getStatus() {
        return status;
    }

    public String getStatusValue() {
        if(status == 0) {
            return approvalRequired ? statusValues[0] : "Approved";
        }
        return getStatusValue(status);
    }

    public static String getStatusValue(int value) {
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    @Column(order = 800, required = false)
    public boolean getApprovalRequired() {
        return approvalRequired;
    }

    public boolean isClosed() {
        return status == 4;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(date.after(DateUtility.today())) {
            throw new Invalid_State("Date cannot be in the future");
        }
        if(getType() >= 1000) {
            throw new Invalid_State("Invalid type");
        }
        storeId = tm.checkTypeAny(this, storeId, InventoryStore.class, false);
        supplierId = tm.checkType(this, supplierId, Entity.class, false);
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if(referenceNumber.isBlank()) {
            SerialPattern pattern = SerialPattern.get("PO" + getGRNType() + "-" + getType());
            if(pattern == null) {
                referenceNumber = "PO/" + getNo();
            } else {
                referenceNumber = pattern.getNumber(tran.getManager(), getNo(), date);
            }
        }
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(status == 0 || status == 4) {
            return;
        }
        throw new Invalid_State("Can't delete processed entry");
    }

    @Override
    public void validateUpdate() throws Exception {
        super.validateUpdate();
        if(status > 0 && !internal) {
            throw new Invalid_State("Can't update processed entry");
        }
    }

    @Override
    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
        super.validateChildAttach(child, linkType);
        if(child instanceof InventoryPOItem item) {
            if(internal || item.internal) {
                return;
            }
            if(!getTransaction().isInvolved(child)) { // Not active in this transaction
                return;
            }
            if(status > 0 && item.getType() == 0 && !item.getReceived().isZero()) {
                throw new Invalid_State("Can't add more items to processed entry");
            }
        }
    }

    @Override
    public void validateChildUpdate(StoredObject child, int linkType) throws Exception {
        super.validateChildUpdate(child, linkType);
        if(child instanceof InventoryPOItem poItem) {
            if(internal || poItem.internal || status == 0 || poItem.getReceived().isZero()) {
                return;
            }
            throw new Invalid_State("Can't update with status = '" + getStatusValue() + "'");
        }
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        getReference();
        internal = false;
    }

    void setInternalStatusTo2() {
        internal = true;
        this.status = 2;
    }

    /**
     * Amend this PO. This PO will be foreclosed, and another PO will be created with the balance items to receive.
     *
     * @param transaction Transaction.
     * @return The id of the newly created (and saved) PO with balance items to receive.
     * @throws Exception If any exception occurs while carrying out the transaction.
     */
    public Id amendOrder(Transaction transaction) throws Exception {
        switch(status) {
            case 1, 2 -> {
                return amend(transaction);
            }
            default -> throw new Invalid_State("Can't amend with Status = " + getStatusValue());
        }
    }

    private Id amend(Transaction transaction) throws Exception {
        List<InventoryPOItem> items = listItems().filter(i -> i.getBalance().isPositive()).toList();
        InventoryPO po = get(getClass(), getId());
        closeOrder(transaction);
        po.makeNew();
        po.internal = true;
        po.setDate(DateUtility.today());
        po.status = 0;
        po.save(transaction);
        po.addLink(transaction, getId());
        Quantity q;
        for(InventoryPOItem item: items) {
            item.makeNew();
            item.internal = true;
            q = item.getBalance();
            item.received = q.zero();
            item.setQuantity(q);
            item.save(transaction);
            po.addLink(transaction, item);
        }
        return po.getId();
    }

    public void recallOrder(Transaction transaction) throws Exception {
        if(status != 1) {
            throw new Invalid_State("Can't proceed with Status = " + getStatusValue());
        }
        internal = true;
        status = 0;
        save(transaction);
    }

    public void placeOrder(Transaction transaction) throws Exception {
        if(status != 0) {
            throw new Invalid_State("Can't proceed with Status = " + getStatusValue());
        }
        if(getApprovalRequired()) {
            throw new Invalid_State("Approval required");
        }
        List<InventoryPOItem> items = listItems().toList();
        if(items.isEmpty()) {
            throw new Invalid_State("Item list is empty");
        }
        Currency currency = items.getFirst().getUnitPrice().getCurrency();
        if(items.stream().skip(1).anyMatch(i -> !i.getUnitPrice().getCurrency().equals(currency))) {
            throw new Invalid_State("All items must have the same currency");
        }
        internal = true;
        status = 1;
        save(transaction);
    }

    public void closeOrder(Transaction transaction) throws Exception {
        InventoryGRN grn = listLinks(InventoryGRN.class).find(g -> !g.isClosed());
        if(grn != null) {
            throw new Invalid_State("Please process the GRN - " + grn.getReference() + " before closing this");
        }
        internal = true;
        status = 4;
        save(transaction);
    }

    public boolean canClose() {
        return status == 3 && canForeclose();
    }

    public boolean canForeclose() {
        return status != 4 && listLinks(InventoryGRN.class).allMatch(InventoryGRN::isClosed);
    }

    /**
     * Create a GRN for this PO.
     *
     * @param transaction Current transaction.
     * @param quantities Received quantity values to process. The {@link Id} (key of the map) must the {@link Id} of the
     *                   respective line item entry in the PO.
     * @param grn A GRN to which the entries to be added. If <code>null</code> is passed, a new GRN is created.
     * @param invoiceNumber Invoice number (of the supplier) if applicable. For existing GRNs, <code>null</code>
     *                      could be passed.
     * @param invoiceDate Invoice date (of the supplier) if applicable. For existing GRNs, <code>null</code>
     *                    could be passed.
     * @param exchangeRate Exchange rate to be used for the GRN. For existing GRNs, <code>null</code> could be passed.
     * @return The GRN that is created/modified.
     * @throws Exception If the GRN can't be created.
     */
    public InventoryGRN createGRN(Transaction transaction, Map<Id, Quantity> quantities, InventoryGRN grn,
                                  String invoiceNumber, Date invoiceDate, Rate exchangeRate)
            throws Exception {
        switch(status) {
            case 1, 2 -> {
            }
            default -> throw new Invalid_State("Can't proceed with Status = " + getStatusValue());
        }
        List<InventoryPOItem> items = listItems().toList();
        Currency localCurrency = transaction.getManager().getCurrency(),
                currency = items.getFirst().getUnitPrice().getCurrency();
        if(grn != null) {
            if(grn.getType() != getGRNType()) {
                throw new Invalid_State(grn.getReference() + " is of different type");
            }
            if(!grn.getSupplierId().equals(supplierId)) {
                throw new Invalid_State(grn.getReference() + " belongs to another supplier - "
                        + grn.getSupplier().toDisplay());
            }
            if(!grn.getStoreId().equals(storeId)) {
                throw new Invalid_State(grn.getReference() + " belongs to another store - "
                        + grn.getStore().toDisplay());
            }
            if(grn.isClosed()) {
                throw new Invalid_State(grn.getReference() + " is already closed");
            }
            if(grn.getCurrencyObject() != currency) {
                throw new Invalid_State(grn.getReference() + " has different currency: " + grn.getCurrency() + " vs "
                        + currency.getCurrencyCode());
            }
            boolean saveGRN = false;
            if(invoiceNumber != null && !grn.getInvoiceNumber().equals(invoiceNumber)) {
                grn.setInvoiceNumber(invoiceNumber);
                saveGRN = true;
            }
            if(invoiceDate != null && DateUtility.isSameDate(grn.getInvoiceDate(), invoiceDate)) {
                grn.setInvoiceDate(invoiceDate);
                saveGRN = true;
            }
            if(exchangeRate == null) {
                exchangeRate = grn.getExchangeRate();
            } else if(!grn.getExchangeRate().isSameValue(exchangeRate)) {
                throw new Invalid_State("Exchange rate passed is different from the existing one: "
                        + grn.getExchangeRate() + " vs " + exchangeRate);
            }
            if(saveGRN) {
                grn.save(transaction);
            }
        }
        Quantity b, q;
        boolean partial = false;
        for(InventoryPOItem item: items) {
            b = item.getBalance();
            if(b.isZero()) {
                continue;
            }
            q = quantities.get(item.getId());
            if(q == null || q.isLessThan(b)) {
                partial = true;
                break;
            }
        }
        items.removeIf(i -> quantities.get(i.getId()) == null);
        if(items.size() != quantities.size()) {
            throw new Invalid_State("Found extra items: " + (quantities.size() - items.size()));
        }
        int s = partial ? 2 : 3;
        if(status != s) {
            internal = true;
            status = s;
            save(transaction);
        }
        InventoryGRNItem grnItem;
        if(grn == null) {
            if(exchangeRate == null) {
                if(currency == localCurrency) {
                    exchangeRate = Rate.ONE;
                } else {
                    exchangeRate = Money.getBuyingRate(date, currency, transaction.getManager().getEntity());
                }
            } else if(currency == localCurrency && !exchangeRate.isOne()) {
                throw new Invalid_State("Invalid exchange rate passed: " + exchangeRate );
            }
            grn = new InventoryGRN();
            grn.setType(getGRNType());
            grn.setStore(storeId);
            grn.setSupplier(supplierId);
            if(invoiceDate != null) {
                grn.setInvoiceDate(invoiceDate);
            }
            if(invoiceNumber != null) {
                grn.setInvoiceNumber(invoiceNumber);
            }
            grn.setCurrencyObject(currency);
            grn.setExchangeRate(exchangeRate);
            grn.save(transaction);
        }
        String sn;
        int n, entries = 0;
        for(InventoryPOItem item: items) {
            q = quantities.get(item.getId());
            if(!q.isPositive()) {
                throw new Invalid_State("Invalid quantity for: " + item.toDisplay() + ", " + q);
            }
            item.internal = true;
            b = item.getBalance();
            if(b.isLessThan(q)) { // Excess
                InventoryPOItem i = item.getClass().getConstructor().newInstance();
                i.internal = true;
                i.setPartNumber(item.getPartNumberId());
                i.received = q.subtract(b);
                i.setQuantity(i.received);
                i.setUnitPrice(item.getUnitPrice());
                i.setType(2);
                i.save(transaction);
                addLink(transaction, i);
                item.received = item.getQuantity();
            } else {
                try {
                    item.received = Quantity.sum(item.received, q);
                } catch(SORuntimeException e) {
                    throw new SORuntimeException(e.getMessage() + ", PO Item - " + item.toDisplay());
                }
            }
            item.save(transaction);
            sn = item.getSerialNumber();
            if(item.getPartNumber().isSerialized()) {
                n = q.getValue().intValue();
                if(n > 1000) {
                    throw new Invalid_State("Too many items specified: " + n);
                }
                q = Count.ONE;
            } else {
                n = 1;
            }
            while(n-- > 0) {
                grnItem = new InventoryGRNItem();
                ++entries;
                grnItem.internal = true;
                grnItem.setPartNumber(item.getPartNumberId());
                grnItem.setSerialNumber(sn);
                grnItem.setQuantity(q);
                grnItem.setUnitCost(item.getUnitPrice().multiply(exchangeRate, localCurrency));
                grnItem.save(transaction);
                grn.addLink(transaction, grnItem);
                sn = "";
            }
        }
        if(entries == 0) {
            throw new Invalid_State("No items received");
        }
        addLink(transaction, grn);
        for(Consignment c: listLinks(Consignment.class, true)) {
            grn.addLink(transaction, c);
        }
        return grn;
    }

    public final ObjectIterator<InventoryPOItem> listItems() {
        return listLinks(getTransaction(), InventoryPOItem.class, true);
    }

    /**
     * Get the GRN type. One of the GRN type values: 0, 1 or 2.
     *
     * @return Type of GRN. Default is 0.
     */
    public int getGRNType() {
        return 0;
    }

    /**
     * Is a specific type of landed cost is applicable to this PO?
     *
     * @param landedCostType Type of landed cost.
     * @param grn Associated GRN.
     * @return True/false.
     */
    public boolean isApplicable(LandedCostType landedCostType, InventoryGRN grn) {
        return true;
    }

    @Override
    public String toDisplay() {
        return getReference() + " dated " + DateUtility.format(date);
    }

    @Override
    public final <O extends StoredObject> Amend<O> getAmend() {
        //noinspection unchecked
        return amendment(new Amend<>((O)this, 0));
    }

    private static <T extends  StoredObject> Amend<T> amendment(Amend<T> amend) {
        @SuppressWarnings("unchecked")
        T old = (T) amend.object().listLinks(amend.object().getClass(), "No="
                + ((HasReference)amend.object()).getNo()).findFirst();
        if(old == null) {
            return amend;
        }
        return amendment(new Amend<>(old, amend.amendment() + 1));
    }

    public static String actionPrefixForUI() {
        return "PO";
    }
}
