package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MaterialIssued extends StoredObject implements OfEntity, HasReference {

    private static final ReferencePattern<MaterialIssued> ref = new ReferencePattern<>();
    private static final String[] statusValues =
            new String[] {
                    "Initiated", "Sent", "Closed", "Reserved",
            };
    private int no = 0;
    private final Date date = DateUtility.today();
    private String referenceNumber;
    private Id locationId;
    private Id requestId;
    private int status = 0;
    private Timestamp sentAt = DateUtility.now();
    private String reference;
    private Id systemEntityId;

    public MaterialIssued() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("ReferenceNumber", "text");
        columns.add("Location", "id");
        columns.add("Request", "id");
        columns.add("Status", "int");
        columns.add("SentAt", "timestamp");
    }

    public static void indices(Indices indices) {
        indices.add("Location,No");
        indices.add("Request");
        indices.add("Request,Status", "Status=0", true);
    }

    public static String[] protectedColumns() {
        return new String[] {
                "ReferenceNumber", "Status", "SentAt",
        };
    }

    public static String[] searchColumns() {
        return new String[] { "Date", "No" };
    }

    public static String[] browseColumns() {
        return new String[] { "Date", "Reference", "Location", "Request", "Status" };
    }

    public static String[] links() {
        return new String[] {
                "Items|com.storedobject.core.MaterialIssuedItem|||0",
        };
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
        if (!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    @Override
    @SetNotAllowed
    @Column(style = "(serial)", order = 100)
    public int getNo() {
        if (no == 0) {
            Transaction t = getTransaction();
            no = SerialGenerator.generate(t, SerialConfigurator.getFor(getClass()).getYearPrefix(t)
                    + getTagPrefix() + ref.getTag(this)).intValue();
        }
        return no;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Override
    @Column(order = 200)
    public Date getDate() {
        return new Date(date.getTime());
    }

    @Deprecated
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Deprecated
    @Column(style = "(code)", required = false, order = 300)
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public String getTagPrefix() {
        return "MI-";
    }

    @Override
    public String getReference() {
        if(reference == null) {
            reference = ref.getTag(this);
        }
        return reference == null ? "" : reference;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        getReference();
    }

    public String getRequestReference() {
        return getRequest().getReference();
    }

    public void setLocation(Id locationId) {
        this.locationId = locationId;
    }

    public void setLocation(BigDecimal idValue) {
        setLocation(new Id(idValue));
    }

    public void setLocation(InventoryLocation location) {
        setLocation(location == null ? null : location.getId());
    }

    @Column(style = "(any)", order = 400)
    public Id getLocationId() {
        return locationId;
    }

    public InventoryLocation getLocation() {
        return get(InventoryLocation.class, locationId, true);
    }

    public void setRequest(Id requestId) {
        this.requestId = requestId;
    }

    public void setRequest(BigDecimal idValue) {
        setRequest(new Id(idValue));
    }

    public void setRequest(MaterialRequest request) {
        setRequest(request == null ? null : request.getId());
    }

    @Column(style = "(any)", order = 500)
    public Id getRequestId() {
        return requestId;
    }

    public MaterialRequest getRequest() {
        return get(MaterialRequest.class, requestId, true);
    }

    public void setStatus(int status) {
        if (!loading()) {
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

    public void setSentAt(Timestamp sentAt) {
        if (!loading()) {
            throw new Set_Not_Allowed("Sent at");
        }
        this.sentAt = new Timestamp(sentAt.getTime());
        this.sentAt.setNanos(sentAt.getNanos());
    }

    @SetNotAllowed
    @Column(order = 700)
    public Timestamp getSentAt() {
        return new Timestamp(sentAt.getTime());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        referenceNumber = toCode(referenceNumber);
        locationId = tm.checkTypeAny(this, locationId, InventoryLocation.class, false);
        requestId = tm.checkTypeAny(this, requestId, MaterialRequest.class, false);
        MaterialRequest mr = getRequest();
        if(mr.getDate().after(date)) {
            throw new Invalid_State("Request-issue mismatch - check the date");
        }
        if(!deleted() && !locationId.equals(mr.getToLocationId())) {
            throw new Invalid_State("Request-issue mismatch - check the location");
        }
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        referenceNumber = String.valueOf(getNo());
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(status > 0) {
            if(existsLinks(MaterialIssuedItem.class)) {
                throw new SOException("Can't delete with status = '" + getStatusValue() + "'");
            }
        }
    }

    void setCloseStatus() {
        this.status = 2;
    }

    private List<MaterialIssuedItem> items() {
        return listLinks(MaterialIssuedItem.class).toList();
    }

    public void issue(Transaction transaction) throws Exception {
        if(status != 0) {
            throw new Invalid_State("Status is '" + getStatusValue() + "'");
        }
        MaterialRequest mr = getRequest();
        List<MaterialIssuedItem> miis = items();
        InventoryLocation location = getLocation();
        InventoryItem ii;
        boolean any = false;
        for(MaterialIssuedItem mii: miis) {
            if(mii.getQuantity().isZero()) {
                continue;
            }
            any = true;
            ii = mii.getItem();
            if(!ii.isAvailableAt(location)) {
                throw new Invalid_State("Item already moved - " + ii);
            }
            if(ii.getLocation() instanceof InventoryReservedBin) {
                throw new Invalid_State("Item reserved by someone else - " + ii);
            }
            if(ii.getQuantity().isLessThan(mii.getQuantity())) {
                throw new Invalid_State("Quantity not enough - " + ii);
            }
        }
        if(!any) {
            return;
        }
        List<MaterialRequestItem> mris = new ArrayList<>();
        InventoryLocation toLoc = mr.getReserved() ? null : mr.getFromLocation();
        InventoryBin bin;
        MaterialRequestItem mri;
        InventoryTransaction it = new InventoryTransaction(transaction.getManager(), date, referenceNumber);
        for(MaterialIssuedItem mii: miis) {
            if(mii.getQuantity().isZero()) {
                continue;
            }
            if(mris.isEmpty()) {
                mri = null;
            } else {
                mri = mris.stream().filter(i -> i.getId().equals(mii.getRequestId())).findAny().orElse(null);
            }
            if(mri == null) {
                mri = mii.getRequest();
                mris.add(mri);
            }
            mri.setIssued(mri.getIssued().add(mii.getQuantity()).convert(mri.getRequested(), 6));
            if(mri.getIssued().isGreaterThan(mri.getRequested())) {
                throw new Invalid_State("Issued quantity exceeds the requested quantity for item - "
                        + mii.getItem().toDisplay() + " [Requested: " + mri.getRequested() + ", Issued: "
                        + mri.getIssued() + "]" + mri.getId());
            }
            ii = mii.getItem();
            bin = (InventoryBin) ii.getLocation();
            if(mr.getReserved()) {
                toLoc = InventoryReservedBin.createFor(transaction, bin, mr);
            }
            it.moveTo(ii, mii.getQuantity(), referenceNumber, toLoc);
        }
        for(MaterialRequestItem i: mris) {
            i.save(transaction);
        }
        if(mr.listLinks(transaction, MaterialRequestItem.class, true).anyMatch(i -> i.getBalance().isPositive())) {
            mr.status = 2;
        } else {
            mr.status = 3;
        }
        mr.received = false;
        mr.save(transaction);
        it.save(transaction);
        Map<Id, Id> changed = it.getItemsChanged();
        Id newId;
        for(MaterialIssuedItem mii: miis) {
            if(mii.getQuantity().isZero()) {
                continue;
            }
            newId = changed.get(mii.getItemId());
            if(newId != null) {
                mii.setItem(newId);
                mii.save(transaction);
            }
        }
        status = mr.getReserved() ? 3 : 1;
        save(transaction);
    }

    public boolean isReserved() {
        return status == 3;
    }

    public boolean getReserved() {
        return isReserved();
    }

    public void close(Transaction transaction) throws Exception {
        if(isReserved()) {
            throw new Invalid_State("Reserved items exist");
        }
        setTransaction(transaction);
        List<MaterialIssuedItem> miis = items();
        InventoryItem ii;
        for(MaterialIssuedItem mii: miis) {
            ii = get(transaction, InventoryItem.class, mii.getItemId(), true);
            if(ii == null) {
                throw new Invalid_State("Item not found for System Id = " + mii.getItemId() + ", MII System Id = "
                        + mii.getId());
            }
            if(ii.getInTransit() && ii.getLocation().isInspectionRequired()) {
                throw new Invalid_State("Item is in transit (System Id = " + ii.getId() + ", MII System Id = "
                        + mii.getId() + "): " + ii.toDisplay());
            }
        }
        MaterialRequest mr = getRequest();
        if(mr.getReserved() || mr.status > 4) {
            throw new Invalid_State("Reserved items exist");
        }
        status = 2;
        boolean saveMR = false;
        if(list(MaterialIssued.class, "Request=" + mr.getId() + " AND Status !=2")
                .noneMatch(mi -> !mi.getId().equals(getId()))) {
            if(mr.status == 3) {
                mr.status = 4;
                saveMR = true;
            }
            if(!mr.received) {
                mr.received = true;
                saveMR = true;
            }
        }
        if(saveMR) {
            mr.save(transaction);
        }
        save(transaction);
    }

    public void issueReserved(Transaction transaction) throws Exception {
        if(status != 3) {
            return;
        }
        save(transaction);
    }
}
