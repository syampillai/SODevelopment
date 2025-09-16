package com.storedobject.core;

import com.storedobject.accounts.SupplierInvoice;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;
import com.storedobject.core.annotation.Table;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.util.*;

/**
 * GRN class. Used to accept items received from a supplier.
 *
 * @author Syam
 */
@Table(anchors = "Store")
public final class InventoryGRN extends StoredObject implements HasChildren, HasReference, TriggerChangeEvent {

    private static final ReferencePattern<InventoryGRN> ref = new ReferencePattern<>();
    private final static String[] statusValues = new String[] {
            "Initiated", "Processed", "Closed"
    };
    private final static String[] typeValues = new String[] {
            "Purchase/Supplier", "External Owner", "Loaned from", "Items Repaired by", "Sales Return", "Loan Return"
    };
    final Date date = DateUtility.today(), invoiceDate = DateUtility.today();
    private String referenceNumber = "";
    Id storeId;
    Id supplierId;
    int status = 0, type = 0;
    boolean internal = false;
    int no = 0;
    private Money landedCost = new Money();
    private String reference;
    private String currency;
    private Rate exchangeRate = new Rate(6);

    public InventoryGRN() {
    }

    public static void columns(Columns columns) {
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("ReferenceNumber", "text");
        columns.add("InvoiceDate", "date");
        columns.add("Store", "id");
        columns.add("Supplier", "id");
        columns.add("Status", "int");
        columns.add("Type", "int");
        columns.add("LandedCost", "money");
        columns.add("Currency", "currency");
        columns.add("ExchangeRate", "rate");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "No",
                "Status",
                "LandedCost",
        };
    }

    public static String[] browseColumns() {
        return new String[] {
                "Date",
                "Reference",
                "Supplier",
                "InvoiceNumber",
                "InvoiceDate",
                "Status",
        };
    }

    public static void indices(Indices indices) {
        indices.add("Store,No,Date");
        indices.add("Store,lower(ReferenceNumber)");
        indices.add("Store,Date");
    }

    public static String[] searchColumns() {
        return new String[] { "Date", "No" };
    }

    public static String[] links() {
        return new String[]{
                "Items|com.storedobject.core.InventoryGRNItem|PartNumber||0",
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
    public SystemEntity getSystemEntity() {
        return getStore().getSystemEntity();
    }

    @Override
    public Id getSystemEntityId() {
        return getStore().getSystemEntityId();
    }

    @Override
    public String getReference() {
        if(reference == null) {
            reference = ref.get(this);
        }
        return reference == null ? "" : reference;
    }

    @Override
    public String getTagPrefix() {
        return "GRN-";
    }

    @SetNotAllowed
    public void setDate(Date date) {
        if(!loading() && status > 0) {
            throw new Set_Not_Allowed("Date");
        }
        this.date.setTime(date.getTime());
    }

    @Override
    @Column(order = 100, caption = "Receipt Date")
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = toCode(referenceNumber);
    }

    @Column(order = 200, required = false, caption = "Invoice Number")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setInvoiceNumber(String referenceNumber) {
        setReferenceNumber(referenceNumber);
    }

    @Column(order = 200, required = false)
    public String getInvoiceNumber() {
        return referenceNumber;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate.setTime(invoiceDate.getTime());
    }

    @Column(order = 300)
    public Date getInvoiceDate() {
        return new Date(invoiceDate.getTime());
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
    @Column(style = "(any)", order = 400)
    public Id getStoreId() {
        return storeId;
    }

    public InventoryStore getStore() {
        return InventoryStore.getStore(storeId);
    }

    public void setSupplier(Id supplierId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Supplier");
        }
        this.supplierId = supplierId;
    }

    public void setSupplier(BigDecimal idValue) {
        setSupplier(new Id(idValue));
    }

    public void setSupplier(Entity supplier) {
        setSupplier(supplier == null ? null : supplier.getId());
    }

    @SetNotAllowed
    @Column(order = 500)
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
    @Column(order = 600)
    public int getStatus() {
        return status;
    }

    public String getStatusValue() {
        return statusValues[status];
    }

    public static String[] getTypeValues() {
        return typeValues;
    }

    public void setType(int type) {
        if(!loading()) {
            throw new Set_Not_Allowed("Type");
        }
        if(type < 0 || type >= typeValues.length) {
            throw new SORuntimeException();
        }
        this.type = type;
    }

    @SetNotAllowed
    @Column(order = 600)
    public int getType() {
        return type;
    }

    public String getTypeValue() {
        return typeValues[type % typeValues.length];
    }

    public void setLandedCost(Money landedCost) {
        if(!loading()) {
            throw new Set_Not_Allowed("Landed Cost");
        }
        this.landedCost = landedCost;
    }

    public void setLandedCost(Object moneyValue) {
        setLandedCost(Money.create(moneyValue));
    }

    @SetNotAllowed
    @Column(order = 700)
    public Money getLandedCost() {
        return landedCost;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(order = 800, style = "(currency)", caption = "Original Currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrencyObject(Currency currency) {
        if(currency != null) {
            setCurrency(currency.getCurrencyCode());
        }
    }

    public Currency getCurrencyObject() {
        return Currency.getInstance(currency);
    }

    public void setExchangeRate(Rate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public void setExchangeRate(Object value) {
        setExchangeRate(Rate.create(value, 6));
    }

    @Column(order = 900)
    public Rate getExchangeRate() {
        return exchangeRate;
    }

    public boolean isProcessed() {
        return status >= 1;
    }

    public boolean isClosed() {
        return status == 2;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(date.after(DateUtility.today())) {
            throw new Invalid_State("Date cannot be in the future");
        }
        storeId = tm.checkTypeAny(this, storeId, InventoryStore.class, false);
        supplierId = tm.checkType(this, supplierId, Entity.class, false);
        if(currency == null) {
            currency = tm.getCurrency().getCurrencyCode();
        }
        checkCurrency(currency, false);
        if(tm.getCurrency().getCurrencyCode().equals(currency)) {
            exchangeRate = new Rate(1);
        } else {
            exchangeRate.checkLimit("Exchange Rate", 14);
        }
        super.validateData(tm);
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(!isClosed()) {
            try(ObjectIterator<InventoryGRNItem> items = listLinks(InventoryGRNItem.class)) {
                for(InventoryGRNItem item: items) {
                    if(item.getItem() != null || !item.getQuantity().isZero()) {
                        throw new Invalid_State("Only closed and old GRNs can be deleted!");
                    }
                }
            }
        }
    }

    @Override
    public void validateUpdate() throws Exception {
        super.validateUpdate();
        if(status > 0 && !internal) {
            InventoryGRN p = get(InventoryGRN.class, getId());
            if(p == null && status == 2) {
                return;
            }
            if(p != null) {
                if(DateUtility.isSameDate(date, p.date) && status == p.status && type == p.type &&
                        storeId.equals(p.storeId) && supplierId.equals(p.supplierId)) {
                    return;
                }
                throw new Invalid_State("Can't update processed entry");
            }
        }
    }

    @Override
    public void validateChildAttach(StoredObject child, int linkType) throws Exception {
        super.validateChildAttach(child, linkType);
        if(undeleted()) {
            return;
        }
        if(child instanceof InventoryGRNItem item) {
            if(item.internal || internal) {
                return;
            }
            if(!getTransaction().isInvolved(child)) { // Not active in this transaction
                return;
            }
            if(status > 0) {
                throw new Invalid_State("Can't add more items to processed entry");
            }
            if(item.inspected) {
                throw new Invalid_State("Can't add an item that is already inspected");
            }
            checkStore(item);
            return;
        }
        if(child instanceof LandedCost lc && lc.getType().getPartOfInvoice()) {
            if(!(type == 0 || type == 3)) { // Not a PO or RO
                throw new Invalid_State("Can't add landed cost");
            }
            if(status != 2 || !exists(Account.class, "true", true)) {
                return;
            }
            SupplierInvoice si = getMaster(SupplierInvoice.class, true);
            if(si != null && si.isLedgerPosted()) {
                throw new Invalid_State("Can't add invoice item '" + lc.getType().getName()
                        + "' after ledger posted");
            }
        }
    }

    private void checkStore(InventoryGRNItem item) throws SOException {
        checkStore(item.getBin());
    }

    private void checkStore(InventoryBin bin) throws SOException {
        if(bin != null && !bin.getStoreId().equals(storeId)) {
            throw new SOException("Bin '" + bin.toDisplay() + "' does not belong to the store '"
                    + getStore().toDisplay() + "'");
        }
    }

    @Override
    public void validateChildUpdate(StoredObject child, int linkType) throws Exception {
        super.validateChildUpdate(child, linkType);
        if(child instanceof InventoryGRNItem grnItem) {
            if(grnItem.internal) {
                return;
            }
            checkStore(grnItem);
            if(status != 1) {
                if(!grnItem.getInspected()) {
                    InventoryItem item = grnItem.getItem();
                    if(item != null && !item.getOwnerId().equals(supplierId)) {
                        throw new Invalid_State("Item belongs to someone else. Item: " + item.toDisplay() +
                                "\nOwner: " + item.getOwner().toDisplay());
                    }
                }
            }
            if(status == 0) {
                if(grnItem.inspected) {
                    throw new Invalid_State("Can't inspect items before processing");
                }
                return;
            }
            if(getTransaction().isInvolved(grnItem)) {
                InventoryGRNItem old = (InventoryGRNItem) grnItem.previousVersion();
                if(!old.getPartNumberId().equals(grnItem.getPartNumberId()) ||
                        !old.getSerialNumber().equals(grnItem.getSerialNumber()) ||
                        !old.getItemId().equals(grnItem.getItemId()) ||
                        !old.getBinId().equals(grnItem.getBinId()) ||
                        !old.getUnitCost().equals(grnItem.getUnitCost()) ||
                        !old.getQuantity().equals(grnItem.getQuantity()) ||
                        (status == 2 && !grnItem.inspected)) {
                    throw new Invalid_State("Can't update items after processing is already over");
                }
                if(old.inspected && grnItem.inspected && !grnItem.getItem().getInTransit()) {
                    throw new Invalid_State("Can't update inspection status for items that are already in use - " +
                            grnItem.getItem().toDisplay());
                }
            }
            return;
        }
        if(child instanceof LandedCost lc && lc.getType().getPartOfInvoice()) {
            if(status != 2 || !exists(Account.class, "true", true)) {
                return;
            }
            SupplierInvoice si = getSupplierInvoice();
            if(si != null && si.isLedgerPosted()) {
                LandedCost old = get(LandedCost.class, lc.getId());
                if(!old.getAmount().equals(lc.getAmount())) {
                    throw new Invalid_State("Can't update invoice item '" + lc.getType().getName()
                            + "' after ledger posted");
                }
            }
        }
    }

    public SupplierInvoice getSupplierInvoice() {
        return switch (type) {
            case 0, 3 -> getMaster(SupplierInvoice.class, true);
            default -> null;
        };
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        getReference();
        internal = false;
    }

    public void process(Transaction transaction) throws Exception {
        if(status != 0) {
            throw new Invalid_State("Already processed");
        }
        if(type < 0 || type > 5) {
            throw new Invalid_State("Unknown GRN type '" + type + "'");
        }
        List<InventoryGRNItem> grnItems = listLinks(transaction, InventoryGRNItem.class).toList();
        if(grnItems.isEmpty()) {
            throw new Invalid_State("No GRN items found");
        }
        boolean canClose = true;
        InventoryLocation supplierLocation = switch(type) {
            case 0 -> InventoryVirtualLocation.getForSupplier(supplierId);
            case 1 -> InventoryVirtualLocation.getForExternalOwner(supplierId);
            case 2 -> InventoryVirtualLocation.getForLoanFrom(supplierId);
            case 3 -> InventoryVirtualLocation.getForRepairOrganization(supplierId); // Repair return
            case 4 -> InventoryVirtualLocation.getForConsumer(supplierId); // Sales return
            case 5 -> InventoryVirtualLocation.getForLoanTo(supplierId); // Loan return
            default -> null;
        };
        Entity supplier = type != 0 ? getSupplier() : null;
        InventoryItem item;
        InventoryStoreBin storeBin = null;
        InventoryBin bin;
        InventoryTransaction it = null;
        for(InventoryGRNItem grnItem: grnItems) {
            item = grnItem.getItem();
            if(item == null) {
                throw new Invalid_State("Item not inspected! " + grnItem.getPartNumber().toDisplay());
            }
            if(item.isBlocked() && item.isServiceable()) {
                throw new Invalid_State("Blocked item is marked as serviceable! " + item.toDisplay());
            }
            if(!item.getLocation().equals(supplierLocation)) {
                continue;
            }
            if(canClose) {
                canClose = item.getPartNumber().listImmediateAssemblies().findFirst() == null;
            }
            bin = grnItem.getBin();
            if(bin == null) {
                if(storeBin == null) {
                    storeBin = getStore().getStoreBin();
                }
                bin = storeBin;
            } else {
                checkStore(bin);
            }
            item.setInTransit(!canClose);
            if(type == 0) {
                item.setPurchaseDate(date);
            }
            if(it == null) {
                it = new InventoryTransaction(transaction.getManager(), date, getReference());
                it.setGRN(this);
                it.checkTransit(false);
            }
            switch(type) {
                case 0 -> it.moveTo(item, null, bin);
                case 1 -> it.receiveFromExternal(item, null, bin, supplier);
                case 2 -> it.loanFrom(item, null, bin, supplier);
                case 3 -> throw new SORuntimeException("Can't process repair returns");
                case 4 -> throw new SORuntimeException("Can't process sales returns");
                case 5 -> throw new SORuntimeException("Can't process loan returns");
            }
        }
        internal = true;
        status = canClose ? 2 : 1;
        save(transaction);
        if(it != null) {
            it.save(transaction);
        }
        if(status == 2) {
            for(InventoryGRNItem grnItem: grnItems) {
                if(!grnItem.inspected) {
                    grnItem.internal = true;
                    grnItem.inspected = true;
                    grnItem.save(transaction);
                }
            }
        }
    }

    public void close(Transaction transaction) throws Exception {
        if(status == 0) {
            throw new Invalid_State("Not yet processed");
        }
        if(status == 2) {
            throw new Invalid_State("Already closed");
        }
        status = 2;
        List<InventoryGRNItem> grnItems = new ArrayList<>();
        listLinks(transaction, InventoryGRNItem.class).collectAll(grnItems);
        InventoryItem item = grnItems.stream().filter(gi -> !gi.inspected).map(InventoryGRNItem::getItem).
                filter(InventoryItem::isAssemblyIncomplete).findAny().orElse(null);
        if(item != null) {
            throw new Invalid_State("Incomplete assembly found for item - " + item.toDisplay());
        }
        for(InventoryGRNItem gi: grnItems) {
            item = gi.getItem();
            if(item.getInTransit()) {
                item.setInTransit(false);
                item.save(transaction);
            }
        }
        internal = true;
        save(transaction);
        for(InventoryGRNItem grnItem: grnItems) {
            if(!grnItem.inspected) {
                grnItem.internal = true;
                grnItem.inspected = true;
                grnItem.save(transaction);
            }
        }
    }

    @Override
    public String toString() {
        return getReference() + " dated " + DateUtility.format(date)
                + (type == 0 ? "" : " (" + getTypeValue() + ")");
    }

    /**
     * Is a specific type of landed cost applicable to this GRN?
     *
     * @param landedCostType Type of landed cost.
     * @return True/false.
     */
    public boolean isApplicable(LandedCostType landedCostType) {
        return listMasters(InventoryPO.class, true).anyMatch(po -> po.isApplicable(landedCostType, this));
    }

    /**
     * Compute/recompute the landed cost.
     *
     * @param tm Transaction manager.
     * @throws Exception If any error occurs.
     */
    public void computeLandedCost(TransactionManager tm) throws Exception {
        get(getClass(), getId()).computeLandedCostInt(tm, true);
    }

    void computeLandedCostInt(TransactionManager tm, boolean convertCurrency) throws Exception {
        if(status == 0) {
            throw new SOException("Not yet processed");
        }
        List<InventoryGRNItem> grnItems = listLinks(InventoryGRNItem.class).toList();
        Currency locCur = tm.getCurrency();
        InventoryGRNItem foreignGI = grnItems.stream().filter(gi -> gi.getCost().getCurrency() != locCur)
                .findAny().orElse(null);
        if(foreignGI != null) {
            if(!convertCurrency) {
                throw new SOException("Foreign currency found in GRN: " + foreignGI.getCost().getCurrency());
            }
            Currency foreignGICur = foreignGI.getCost().getCurrency();
            Rate rate = Money.getBuyingRate(date, grnItems.getFirst().getUnitCost().getCurrency(), tm.getEntity());
            int tranNo = tm.transact(t -> {
                Currency foreignCur;
                for(InventoryGRNItem gi: grnItems) {
                    gi.internal = true;
                    Money cost = gi.getUnitCost();
                    foreignCur = cost.getCurrency();
                    if(foreignCur == locCur) {
                        gi.save(t);
                        gi.internal = true;
                        continue;
                    }
                    if(foreignCur != foreignGICur) {
                        throw new SOException("Multiple foreign currencies found in GRN: " + foreignCur + ", "
                                + foreignGICur);
                    }
                    gi.setUnitCost(cost.multiply(rate, locCur));
                    gi.save(t);
                    gi.internal = true;
                }
                exchangeRate = rate;
                currency = foreignGICur.getCurrencyCode();
                internal = true;
                save(t);
            });
            if(tranNo > 0) {
                throw new SOException("Please authorize transaction #" + tranNo + " and retry.");
            }
            computeLandedCostInt(tm, false);
            return;
        }
        Money itemTax = new Money();
        for(InventoryGRNItem grnItem: grnItems) {
            itemTax = itemTax.add(grnItem.getTax());
        }
        Money newCost = new Money(), lcTax = new Money();
        for(LandedCost lc: listLinks(LandedCost.class)) {
            LandedCostType lct = lc.getType();
            Money ea = lc.getEffectiveAmount().toLocal(date, tm);
            if(lct.getTax()) {
                lcTax = lcTax.add(ea);
                if (!itemTax.isZero()) {
                    continue;
                }
            }
            newCost = newCost.add(ea);
        }
        if(!itemTax.equals(lcTax) && !lcTax.isZero() && !itemTax.isZero()) {
            throw new Invalid_State("Tax mismatch - Tax from items: " + itemTax + ", Tax from landed cost: " + lcTax);
        }
        boolean skip = true;
        for(InventoryGRNItem grnItem: grnItems) {
            InventoryItem ii = grnItem.getItem();
            if(ii.getCost().getCurrency() != locCur) {
                skip = false;
            }
            if(ii.getPartNumber() instanceof AbstractServiceItemType) {
                newCost = newCost.add(grnItem.getCost()).add(grnItem.getTax());
            }
        }
        if(skip && newCost.equals(landedCost)) {
            return;
        }
        grnItems.removeIf(grnItem -> grnItem.getItem().getPartNumber() instanceof AbstractServiceItemType);
        Money m, total = new Money();
        Map<Id, GI> giMap = new HashMap<>();
        for(InventoryGRNItem grnItem: grnItems) {
            m = grnItem.getCost();
            giMap.put(grnItem.getId(), new GI(m));
            total = total.add(m);
        }
        Money incCost = newCost.subtract(landedCost);
        GI gi;
        BigDecimal ratio = incCost.getValue().divide(total.getValue(), MathContext.DECIMAL128);
        for(InventoryGRNItem grnItem: grnItems) {
            gi = giMap.get(grnItem.getId());
            gi.incUC = gi.amount.multiply(ratio).divide(grnItem.getQuantity());
        }
        DBTransaction t = null;
        try {
            t = tm.createTransaction();
            for(InventoryGRNItem grnItem: grnItems) {
                gi = giMap.get(grnItem.getId());
                if(skip && gi.incUC.isZero()) {
                    continue;
                }
                updateGRNItemCost(gi, grnItem, t);
            }
            internal = true;
            landedCost = newCost;
            save(t);
            t.commit();
        } catch(Throwable e) {
            if(t != null) {
                t.rollback();
            }
            if(e instanceof RuntimeException) {
                throw new SOException("", e);
            } else {
                throw e;
            }
        }
    }

    private void updateGRNItemCost(GI gi, InventoryGRNItem grnItem, DBTransaction t) throws Exception {
        boolean itemFound = false;
        for(InventoryItem ii: list(InventoryItem.class, "PartNumber=" + grnItem.getPartNumberId()
                + " AND GRN=" + getId())) {
            if(ii.getQuantity().isZero()) {
                continue;
            }
            if(!itemFound) {
                itemFound = ii.getId().equals(grnItem.getItemId());
            }
            updateItemCost(gi, t, grnItem, ii);
            for(InventoryLedger movement : list(InventoryLedger.class, "Item=" + ii.getId()
                    + " AND Date>='" + Database.format(date) + "'", "Item,Date")) {
                movement.increaseCost(t, gi.incUC, grnItem.getQuantity(), exchangeRate);
            }
        }
        if(!itemFound) {
            InventoryItem ii = grnItem.getItem();
            if(ii != null && !ii.getQuantity().isZero()) {
                updateItemCost(gi, t, grnItem, ii);
            }
            for(InventoryLedger movement : list(InventoryLedger.class, "Item=" + grnItem.getItemId()
                    + " AND Date>='" + Database.format(date) + "'", "Item,Date")) {
                movement.increaseCost(t, gi.incUC, grnItem.getQuantity(), exchangeRate);
            }
        }
    }

    private void updateItemCost(GI gi, DBTransaction t, InventoryGRNItem grnItem, InventoryItem ii) {
        Money m = ii.getCost();
        Currency localCur = t.getManager().getCurrency();
        if(m.getCurrency() != localCur) {
            ii.illegal = false;
            ii.setCost(m.multiply(exchangeRate, localCur));
        }
        if(ii.getQuantity().isGreaterThanOrEqual(grnItem.getQuantity())) {
            m = ii.getCost().add(gi.incUC.multiply(grnItem.getQuantity()));
        } else {
            m = ii.getCost()
                    .add(gi.incUC.multiply(ii.getQuantity().convert(grnItem.getQuantity().getUnit())));
        }
        t.updateSQL("UPDATE core.InventoryItem SET Cost=" + m.getStorableValue() + " WHERE Id=" + ii.getId());
    }

    private static class GI {

        private final Money amount;
        private Money incUC;

        private GI(Money amount) {
            this.amount = amount;
        }
    }

    public Money getAmount() {
        Money m = new Money();
        for(InventoryGRNItem item: listLinks(InventoryGRNItem.class)) {
            m = m.add(item.getCost());
        }
        return m;
    }

    public Money getTotal() {
        Money m = getAmount();
        for(LandedCost lc: listLinks(LandedCost.class, "Type.PartOfInvoice")) {
            m = m.add(lc.getAmount());
        }
        return m;
    }

    public void computeTax(TransactionManager tm, TaxRegion region) throws Exception {
        List<InventoryGRNItem> items = listLinks(InventoryGRNItem.class).toList();
        DBTransaction t = null;
        Money taxInc;
        GI gi = new GI(new Money());
        try {
            for(InventoryGRNItem item: items) {
                taxInc = new Money();
                for(Tax tax: item.listLinks(Tax.class)) {
                    taxInc = taxInc.subtract(tax.getTax());
                }
                List<Tax> taxes = item.computeTax(date, region, tm.getCurrency());
                for(Tax tax: taxes) {
                    if(tax.status == 0) { // No change
                        taxInc = taxInc.add(tax.getTax());
                        continue;
                    }
                    tax.internal = true;
                    if(t == null) {
                        t = tm.createTransaction();
                    }
                    switch (tax.status) {
                        case 1  -> { // Newly created
                            tax.makeNew();
                            tax.save(t);
                            item.addLink(t, tax);
                            taxInc = taxInc.add(tax.getTax());
                        }
                        case 2 -> { // Recomputed
                            tax.save(t);
                            taxInc = taxInc.add(tax.getTax());
                        }
                        case 3, 4 -> { // Region changed, No more applicable
                            tax.delete(t);
                            taxInc = taxInc.subtract(tax.getTax());
                        }
                    }
                }
                if(taxInc.isZero()) {
                    continue;
                }
                gi.incUC = taxInc.divide(item.getQuantity());
                updateGRNItemCost(gi, item, t);
            }
            if(t != null) {
                t.commit();
            }
        } catch (Exception e) {
            if(t != null) {
                t.rollback();
            }
            throw e;
        }
    }
}
