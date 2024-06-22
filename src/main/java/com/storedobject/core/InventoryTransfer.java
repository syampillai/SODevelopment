package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public abstract class InventoryTransfer extends StoredObject implements OfEntity, HasReference {

    private static final ReferencePattern<InventoryTransfer> ref = new ReferencePattern<>();
    private static final String[] statusValues =
            new String[]{
                    "Initiated", "Sent", "Received", "Returned"
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
            if(t == null) {
                return 0;
            }
            no = SerialGenerator.generate(t, SerialConfigurator.getFor(getClass()).getYearPrefix(t)
                    + getTagPrefix() + ref.getTag(this)).intValue();
        }
        return no;
    }

    @Override
    public String getTagPrefix() {
        if(this instanceof InventoryRO) {
            return "RO-";
        } else if(this instanceof InventorySale) {
            return "S-";
        } else {
            return "M" + (this instanceof MaterialReturned ? "R" : "") + "T" + "-";
        }
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
        InventoryTransfer it = this;
        int a = amendment;
        while(a > 0) {
            --a;
            it = it.listLinks(it.getClass(), "No=" + no + " AND Amendment=" + a).findFirst();
        }
        //noinspection unchecked
        return (Amend<O>) new Amend<>(it, it.amendment);
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
        if(!loading()) {
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

    public void send(Transaction transaction) throws Exception {
        checkStatus(0);
        if(getApprovalRequired()) {
            throw new Invalid_State("Approval required");
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
     * Amend this. This entry be closed (marked with "returned" status) and another entry will be created with all
     * the items under it. Any new item added to it will be added with a new amendment number.
     *
     * @param transaction Transaction.
     * @return The id of the newly created (and saved) entry.
     * @throws Exception If any exception occurs while carrying out the transaction.
     */
    public Id amend(Transaction transaction) throws Exception {
        switch(status) {
            case 1, 2 -> {
                return Id.ZERO;
            }
            default -> throw new Invalid_State("Can't amend with Status = " + getStatusValue());
        }
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