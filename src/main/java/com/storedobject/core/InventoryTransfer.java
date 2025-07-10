package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class InventoryTransfer extends StoredObject implements OfEntity, HasReference {

    private static final ReferencePattern<InventoryTransfer> ref = new ReferencePattern<>();
    private static final String[] statusValues =
            new String[]{
                    "Initiated", "Sent", "Received", "Returned", "Closed"
            };
    private final Date date = DateUtility.today(), invoiceDate = DateUtility.today();
    private String referenceNumber = "";
    private String reference;
    Id fromLocationId;
    Id toLocationId;
    int status = 0;
    int no = 0;
    private String remark = "";
    Id systemEntityId;
    private int amendment = 0;

    public InventoryTransfer() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("ReferenceNumber", "text");
        columns.add("InvoiceDate", "date");
        columns.add("FromLocation", "id");
        columns.add("ToLocation", "id");
        columns.add("Status", "int");
        columns.add("Remark", "text");
        columns.add("Amendment", "int");
    }

    public static void indices(Indices indices) {
        indices.add("FromLocation,No,Date,T_Family");
        indices.add("Date,No");
        indices.add("ToLocation", "Status=1");
        indices.add("ToLocation", "Status=0 AND Amendment>0");
    }

    public static String[] protectedColumns() {
        return new String[]{
                "No",
                "Status",
                "Amendment",
        };
    }

    public static String[] searchColumns() {
        return new String[]{"Date", "No"};
    }

    public void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @SetNotAllowed
    @Column(order = 10, caption = "Of")
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        return SystemEntity.getCached(systemEntityId);
    }

    public void setNo(int no) {
        if(!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    @Override
    @SetNotAllowed
    @Column(style = "(serial)", order = 10)
    public int getNo() {
        if(no == 0) {
            Transaction t = getTransaction();
            no = SerialGenerator.generate(t, SerialConfigurator.getFor(getClass()).getYearPrefix(t)
                    + getTagPrefix() + ref.getTag(this)).intValue();
        }
        return no;
    }

    @Override
    public String getTagPrefix() {
        return switch (this) {
            case InventoryRO ignored -> "RO-";
            case MaterialReturned ignored -> "MR-";
            case MaterialTransferred ignored -> "MT-";
            case InventorySale ignored -> "IS-";
            default -> throw new SORuntimeException("Unknown class " + getClass().getName());
        };
    }

    @Override
    public final String getReference() {
        if(reference == null) {
            reference = ref.get(this);
        }
        return reference == null ? "" : reference;
    }

    @Override
    public final <O extends StoredObject> Amend<O> getAmend() {
        //noinspection unchecked
        return (Amend<O>) new Amend<>(this, amendment);
    }

    public final void setAmendment(int amendment) {
        if(!loading()) {
            throw new Set_Not_Allowed("Amendment");
        }
        this.amendment = amendment;
    }

    @SetNotAllowed
    @Column(order = 1000)
    public final int getAmendment() {
        return amendment;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        reference = null;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Override
    @Column(order = 100)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.referenceNumber = invoiceNumber;
    }

    public String getInvoiceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Column(style = "(code)", order = 200, required = false, caption = "Other Reference")
    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate.setTime(invoiceDate.getTime());
    }

    @SetNotAllowed
    @Column(order = 300)
    public Date getInvoiceDate() {
        return new Date(invoiceDate.getTime());
    }

    public void setFromLocation(Id fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public void setFromLocation(BigDecimal idValue) {
        setFromLocation(new Id(idValue));
    }

    public void setFromLocation(InventoryLocation fromLocation) {
        setFromLocation(fromLocation == null ? null : fromLocation.getId());
    }

    @Column(style = "(any)", order = 400)
    public Id getFromLocationId() {
        return fromLocationId;
    }

    public InventoryLocation getFromLocation() {
        return get(getTransaction(), InventoryLocation.class, fromLocationId, true);
    }

    public void setToLocation(Id toLocationId) {
        this.toLocationId = toLocationId;
    }

    public void setToLocation(BigDecimal idValue) {
        setToLocation(new Id(idValue));
    }

    public void setToLocation(InventoryLocation toLocation) {
        setToLocation(toLocation == null ? null : toLocation.getId());
    }

    @Column(style = "(any)", order = 500)
    public Id getToLocationId() {
        return toLocationId;
    }

    public InventoryLocation getToLocation() {
        return get(getTransaction(), InventoryLocation.class, toLocationId, true);
    }

    public void setStatus(int status) {
        if((status == 4 && !(this instanceof InventoryRO)) || !loading()) {
            throw new Set_Not_Allowed("Status");
        }
        this.status = status;
    }

    @SetNotAllowed
    @Column(order = 600)
    public int getStatus() {
        return status;
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public static String getStatusValue(int value) {
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public String getStatusValue() {
        return getStatusValue(status);
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(order = 700, required = false)
    public String getRemark() {
        return remark;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        fromLocationId = tm.checkTypeAny(this, fromLocationId, InventoryLocation.class, false);
        toLocationId = tm.checkTypeAny(this, toLocationId, InventoryLocation.class, false);
        if(fromLocationId.equals(toLocationId)) {
            throw new Invalid_State("Locations should be different");
        }
        super.validateData(tm);
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(status == 1) {
            throw new Invalid_State("Items are already sent. Please ask someone at '" +
                    getToLocation().toDisplay() + "' to receive it first");
        }
    }

    private void checkItems(Collection<InventoryItem> items) throws Invalid_State {
        InventoryLocation from = getFromLocation();
        Id storeId = from instanceof InventoryStoreBin ? ((InventoryStoreBin) from).getStoreId() : null;
        InventoryLocation loc;
        for(InventoryItem item : items) {
            if(item.getLocationId().equals(from.getId())) {
                continue;
            }
            loc = item.getLocation();
            if(loc instanceof InventoryBin && ((InventoryBin) loc).getStoreId().equals(storeId)) {
                continue;
            }
            throw new Invalid_State("Invalid location for item - " + item.toDisplay() + ", Location - " + loc.toDisplay());
        }
    }

    public void send(Transaction transaction) throws Exception {
        checkStatus(0);
        if(getApprovalRequired()) {
            throw new Invalid_State("Approval required");
        }
        List<InventoryTransferItem> mris = listLinks(transaction, InventoryTransferItem.class,
                "Amendment=" + amendment, true)
                .toList();
        List<InventoryItem> items = mris.stream().map(InventoryTransferItem::getItem).toList();
        InventoryLocation to = getToLocation();
        InventoryLocation from = getFromLocation();
        if(items.isEmpty()) {
            if(!(from.getType() == 0 && to.getType() == 3)) { // Not a RO
                throw new Invalid_State("No items!");
            }
        }
        checkItems(items);
        InventoryItem item;
        InventoryGRN grn = null;
        if(!items.isEmpty() && from.getType() == 3 && to.getType() == 0) { // It's a repair order and items received at a store.
            grn = new InventoryGRN();
            grn.setType(3);
            grn.setInvoiceNumber(referenceNumber);
            grn.setDate(date);
            grn.setInvoiceDate(invoiceDate);
            grn.setStore(((InventoryBin) to).getStoreId());
            grn.setSupplier(from.getEntityId());
            grn.setStatus(2);
            grn.save(transaction);
            List<InventoryRO> ros = list(InventoryRO.class, "FromLocation=" + to.getId()
                    + " AND Status IN (1,2)").toList();
            int n = ros.size();
            InventoryRO amended;
            for(int i = 0; i < n; i++) {
                amended = ros.get(i);
                if(amended.getAmendment() > 0) {
                    while(true) {
                        amended = amended.listLinks(InventoryRO.class).single(false);
                        if(amended == null) {
                            break;
                        }
                        ros.add(amended);
                        if(amended.getAmendment() == 0) {
                            break;
                        }
                    }
                }
            }
            List<InventoryItem> roItems;
            boolean link;
            for(InventoryRO ro : ros) {
                roItems = ro.listLinks(InventoryROItem.class).map(InventoryTransferItem::getItem).toList();
                link = roItems.removeIf(items::contains);
                roItems.removeIf(ii -> !ii.getLocationId().equals(ro.getToLocationId()));
                if(roItems.isEmpty() && ro.status != 3) {
                    ro.status = 3;
                    ro.save(transaction);
                }
                if(link) {
                    ro.addLink(transaction, grn);
                }
            }
        }
        status = 1;
        save(transaction);
        Entity repairEntity = this instanceof InventoryRO ro ? ro.getRepairEntity() : null;
        Entity customerEntity = this instanceof InventorySale is ? is.getCustomerEntity() : null;
        InventoryTransaction it = new InventoryTransaction(transaction.getManager(), date, getReference());
        it.setGRN(grn);
        for(InventoryTransferItem mri : mris) {
            item = mri.getItem();
            item.setInTransit(true);
            if(repairEntity == null) {
                if(customerEntity == null) {
                    it.moveTo(item, mri.getQuantity(), null, to);
                } else {
                    it.sale(item, mri.getQuantity(), null, customerEntity);
                }
            } else {
                it.sendForRepair(item, mri.getQuantity(), null, repairEntity);
            }
        }
        it.save(transaction);
        Map<Id, Id> itemsChanged = it.getItemsChanged();
        for(Id id : itemsChanged.keySet()) {
            for(InventoryTransferItem mri : mris) {
                if(mri.getItemId().equals(id)) {
                    mri.internal = true;
                    mri.setItem(itemsChanged.get(id));
                    mri.save(transaction);
                }
            }
        }
        if(grn == null) {
            return;
        }
        addLink(transaction, grn);
        InventoryGRNItem grnItem;
        InventoryItem ii;
        for(InventoryTransferItem mri : mris) {
            ii = mri.getItem();
            grnItem = new InventoryGRNItem();
            grnItem.setItem(ii.getId());
            grnItem.setPartNumber(ii.getPartNumberId());
            grnItem.setSerialNumber(ii.getSerialNumber());
            grnItem.setQuantity(mri.getQuantity());
            grnItem.setInspected(true);
            grnItem.internal = true;
            grnItem.save(transaction);
            grn.internal = true;
            grn.addLink(transaction, grnItem);
        }
    }

    public void receive(Transaction transaction) throws Exception {
        checkStatus(1);
        InventoryLocation to = getToLocation();
        Id storeId = to instanceof InventoryStoreBin ? ((InventoryStoreBin) to).getStoreId() : null;
        InventoryLocation loc;
        List<InventoryItem> items = listLinks(transaction, InventoryTransferItem.class, true).
                map(mri -> (InventoryItem) get(transaction, InventoryItem.class, mri.getItemId(), true)).
                filter(InventoryItem::getInTransit).toList();
        for(InventoryItem item : items) {
            loc = item.getLocation();
            if(!loc.getId().equals(toLocationId)) {
                if(!(loc instanceof InventoryBin && ((InventoryBin) loc).getStoreId().equals(storeId))) {
                    continue;
                }
            }
            item.setInTransit(false);
            item.save(transaction);
        }
        status = 2;
        save(transaction);
    }

    private void checkStatus(int s) throws Invalid_State {
        if(status != s) {
            throw new Invalid_State("Can't proceed with this status: " + getStatusValue());
        }
    }

    /**
     * Amend this. This entry will be closed (marked with status as "returned"), and another entry will be created with all
     * the items under it. Any new item added to it will be added with a new amendment number.
     *
     * @param transaction Transaction.
     * @return The id of the newly created (and saved) entry.
     * @throws Exception If any exception occurs while carrying out the transaction.
     */
    public Id amend(Transaction transaction) throws Exception {
        switch(status) {
            case 1, 2 -> {
                return amendInt(transaction);
            }
            default -> throw new Invalid_State("Can't amend with Status = " + getStatusValue());
        }
    }

    private Id amendInt(Transaction transaction) throws Exception {
        List<InventoryTransferItem> items = listLinks(InventoryTransferItem.class, true).toList();
        InventoryTransfer it = get(getClass(), getId());
        status = 3;
        save(transaction);
        it.makeNew();
        it.setDate(DateUtility.today());
        it.setApprovalRequired(true);
        it.status = 0;
        it.amendment += 1;
        it.save(transaction);
        it.addLink(transaction, getId());
        for(InventoryTransferItem item : items) {
            item.makeNew();
            item.internal = true;
            item.save(transaction);
            it.addLink(transaction, item);
        }
        return it.getId();
    }

    @Override
    public String toDisplay() {
        return getReference() + " dated " + DateUtility.format(date) + " (From: " + getFromLocation().toDisplay()
                + " to " + getToLocation().toDisplay() + ")";
    }

    public void setApprovalRequired(boolean approvalRequired) {
    }

    public boolean getApprovalRequired() {
        return false;
    }
}