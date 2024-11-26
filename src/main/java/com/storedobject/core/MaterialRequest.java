package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class MaterialRequest extends StoredObject implements OfEntity, HasReference {

    private static final ReferencePattern<MaterialRequest> ref = new ReferencePattern<>();
    private static final String[] statusValues =
            new String[] {
                    "Initiated", "Requested", "Partially issued", "Issued", "Closed", "To issue", "To issue",
            };
    private final Date date = DateUtility.today();
    private String referenceNumber;
    Id fromLocationId; // Requesting from
    private Id toLocationId; // Issuing from
    int status = 0;
    private Id priorityId;
    private Timestamp requiredBefore = DateUtility.now();
    private String remarks;
    private Id personId;
    private boolean reserved;
    private int no = 0;
    boolean received;
    private String reference;
    Id systemEntityId;

    public MaterialRequest() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("ReferenceNumber", "text");
        columns.add("FromLocation", "id");
        columns.add("ToLocation", "id");
        columns.add("Status", "int");
        columns.add("Priority", "id");
        columns.add("RequiredBefore", "timestamp");
        columns.add("Remarks", "text");
        columns.add("Person", "id");
        columns.add("Reserved", "boolean");
        columns.add("Received", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("FromLocation,No");
        indices.add("FromLocation,lower(ReferenceNumber)");
        indices.add("Date,lower(ReferenceNumber)");
        indices.add("ToLocation,Status", "(Status>0 AND Status<3) OR Reserved");
    }

    public static String[] browseColumns() {
        return new String[] {
                "Date",
                "Reference",
                "ToLocation",
                "Status",
                "Priority",
                "RequiredBefore",
                "Received",
        };
    }

    public static String[] protectedColumns() {
        return new String[] {
                "No",
                "ReferenceNumber",
                "Status",
                "Person",
                "Reserved",
                "Received",
        };
    }

    public static String[] searchColumns() {
        return new String[] { "Date", "No" };
    }

    public static String[] links() {
        return new String[] {
                "Items|com.storedobject.core.MaterialRequestItem|||0",
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

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Column(order = 100)
    public Date getDate() {
        return new Date(date.getTime());
    }

    @Deprecated
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Deprecated
    @Column(style = "(code)", required = false, order = 200)
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public String getTagPrefix() {
        return "MRQ-";
    }

    public String getReference() {
        if(reference == null) {
            reference = ref.get(this);
        }
        return reference == null ? "" : reference;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        getReference();
    }

    public String getIssueReference() {
        switch(status) {
            case 2, 3, 4 -> {
            }
            default -> {
                return "";
            }
        }
        StringBuilder s = new StringBuilder();
        StoredObject.list(MaterialIssued.class, "Request=" + getId())
                .forEach(mi -> {
                    if(!s.isEmpty()) {
                        s.append('\n');
                    }
                    s.append(mi.getReference());
                });
        return s.toString();
    }

    public void setFromLocation(Id fromLocationId) {
        if (!loading() && status >= 2) {
            throw new Set_Not_Allowed("From Location");
        }
        this.fromLocationId = fromLocationId;
    }

    public void setFromLocation(BigDecimal idValue) {
        setFromLocation(new Id(idValue));
    }

    public void setFromLocation(InventoryLocation fromLocation) {
        setFromLocation(fromLocation == null ? null : fromLocation.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 300)
    public Id getFromLocationId() {
        return fromLocationId;
    }

    public InventoryLocation getFromLocation() {
        return get(InventoryLocation.class, fromLocationId, true);
    }

    public void setToLocation(Id toLocationId) {
        if (!loading() && status >= 2) {
            throw new Set_Not_Allowed("To Location");
        }
        this.toLocationId = toLocationId;
    }

    public void setToLocation(BigDecimal idValue) {
        setToLocation(new Id(idValue));
    }

    public void setToLocation(InventoryLocation toLocation) {
        setToLocation(toLocation == null ? null : toLocation.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 400)
    public Id getToLocationId() {
        return toLocationId;
    }

    public InventoryLocation getToLocation() {
        return get(InventoryLocation.class, toLocationId, true);
    }

    public void setStatus(int status) {
        if (!loading()) {
            throw new Set_Not_Allowed("Status");
        }
        this.status = status;
    }

    @SetNotAllowed
    @Column(order = 500)
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
        if(reserved) {
            return switch(status) {
                case 1 -> "To reserve";
                case 2 -> "Partially reserved";
                case 3 -> "Reserved";
                default -> getStatusValue(status);
            };
        }
        return getStatusValue(status);
    }

    public void setPriority(Id priorityId) {
        this.priorityId = priorityId;
    }

    public void setPriority(BigDecimal idValue) {
        setPriority(new Id(idValue));
    }

    public void setPriority(MaterialRequestPriority priority) {
        setPriority(priority == null ? null : priority.getId());
    }

    @Column(order = 600)
    public Id getPriorityId() {
        return priorityId;
    }

    public MaterialRequestPriority getPriority() {
        return get(MaterialRequestPriority.class, priorityId);
    }

    public void setRequiredBefore(Timestamp requiredBefore) {
        this.requiredBefore = new Timestamp(DateUtility.trimSeconds(requiredBefore).getTime());
    }

    @Column(order = 700)
    public Timestamp getRequiredBefore() {
        return new Timestamp(requiredBefore.getTime());
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Column(style = "(large)", required = false, order = 800)
    public String getRemarks() {
        return remarks;
    }

    public void setPerson(Id personId) {
        if (!loading()) {
            throw new Set_Not_Allowed("Person");
        }
        this.personId = personId;
    }

    public void setPerson(BigDecimal idValue) {
        setPerson(new Id(idValue));
    }

    public void setPerson(Person person) {
        setPerson(person == null ? null : person.getId());
    }

    @SetNotAllowed
    @Column(caption = "Requested by", order = 900)
    public Id getPersonId() {
        return personId;
    }

    public Person getPerson() {
        return getRelated(Person.class, personId);
    }

    public void setReserved(boolean reserved) {
        if (!loading()) {
            throw new Set_Not_Allowed("Reserved");
        }
        this.reserved = reserved;
    }

    @SetNotAllowed
    @Column(order = 1000)
    public boolean getReserved() {
        return reserved;
    }

    public void setReceived(boolean received) {
        if (!loading()) {
            throw new Set_Not_Allowed("Received");
        }
        this.received = received;
    }

    @SetNotAllowed
    @Column(order = 1100)
    public boolean getReceived() {
        return received;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        fromLocationId = tm.checkTypeAny(this, fromLocationId, InventoryLocation.class, false);
        toLocationId = tm.checkTypeAny(this, toLocationId, InventoryLocation.class, false);
        if(fromLocationId.equals(toLocationId)) {
            throw new Invalid_State("Locations should be different");
        }
        priorityId = tm.checkType(this, priorityId, MaterialRequestPriority.class, false);
        if(date.after(requiredBefore)) {
            throw new Invalid_Value("Required Before");
        }
        if(status == 0 || status == 1) {
            personId = tm.getUser().getPersonId();
        }
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        referenceNumber = String.valueOf(getNo());
    }

    @Override
    public void validateUpdate() throws Exception {
        super.validateUpdate();
        if(status == 1 && updated()) {
            MaterialRequest old = get(getClass(), getId());
            if(old.status == 1 && !old.toLocationId.equals(toLocationId)) { // Store changed
                if(exists(MaterialIssued.class, "Request=" + getId() + " AND Status>0")) {
                    throw new Invalid_State("Can't change location, items were issued!");
                }
                MaterialIssued mi = get(MaterialIssued.class, "Request=" + getId() + " AND Status=0");
                if(mi != null) {
                    mi.delete(getTransaction());
                }
                status = 0; // Initiated stage
            }
        }
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(status == 0 || status == 4) {
            return;
        }
        throw new Invalid_State("Can't delete when status is '" + getStatusValue() + "'");
    }

    private static boolean issued(MaterialIssued mi) {
        MaterialIssuedItem mii = mi.listLinks(MaterialIssuedItem.class).findFirst();
        return mii != null && !(mii.getItem().getLocation() instanceof InventoryReservedBin);
    }

    /**
     * Release all reserved items.
     *
     * @param transaction Transaction.
     * @throws Exception if any error occurs.
     */
    public void releaseReservation(Transaction transaction) throws Exception {
        if(!reserved || status == 4) {
            return;
        }
        List<MaterialIssued> issuedList = list(MaterialIssued.class, "Request=" + getId()).toList();
        boolean partial = issuedList.removeIf(MaterialRequest::issued);
        if(status > 4) {
            status = status == 5 ? 2 : 1;
        } else {
            status = partial ? 2 : 1;
        }
        reserved = false;
        if(issuedList.isEmpty()) {
            save(transaction);
            return;
        }
        relReservation(transaction, issuedList);
    }

    private void relReservation(Transaction tran, List<MaterialIssued> issuedList) throws Exception {
        String ref = "Released by " + no + "/" + DateUtility.formatDate(date);
        if(!referenceNumber.isBlank()) {
            ref += " Ref: " + referenceNumber;
        }
        InventoryTransaction iTran = new InventoryTransaction(tran.getManager(), null, ref);
        List<MaterialIssuedItem> miis;
        MaterialRequestItem mri;
        InventoryItem item;
        InventoryReservedBin rbin;
        for(MaterialIssued mi: issuedList) {
            miis = mi.listLinks(MaterialIssuedItem.class).toList();
            for(MaterialIssuedItem mii: miis) {
                item = mii.getItem();
                mri = mii.getRequest();
                rbin = (InventoryReservedBin)item.getLocation();
                rbin.illegal = false;
                iTran.moveTo(item, null, rbin.getBin());
                mri.setIssued(mri.getIssued().subtract(mii.getQuantity()));
                mri.save(tran);
                rbin.setReservedBy(Id.ZERO);
                rbin.illegal = false;
                rbin.save(tran);
            }
            mi.setCloseStatus();
            mi.save(tran);
        }
        save(tran);
        iTran.save(tran);
    }

    public void request(Transaction transaction) throws Exception {
        if(status > 0) {
            throw new Invalid_State("Status is already '" + getStatusValue() + "'");
        }
        if(!existsLinks(MaterialRequestItem.class, true)) {
            throw new Invalid_State("No items");
        }
        status = 1;
        save(transaction);
    }

    public void reserve(Transaction transaction) throws Exception {
        if(reserved) {
            throw new Invalid_State("Already reserved");
        }
        reserved = true;
        try {
            request(transaction);
        } catch(Exception e) {
            reserved = false;
            throw e;
        }
    }

    public void requestForIssuance(Transaction transaction) throws Exception {
        if(!reserved) {
            throw new Invalid_State("No reservation");
        }
        if(status == 1) {
            reserved = false;
        } else if(status == 2 || status == 3) {
            status += 3;
        } else if(status == 5 || status == 6) {
            if(!StoredObject.exists(MaterialIssued.class, "Request=" + getId() + " AND Status=3")) {
                status -= 3;
                reserved = false;
            }
        } else {
            return;
        }
        save(transaction);
    }

    public void foreclose(Transaction transaction) throws Exception {
        switch(status) {
            case 3 -> throw new Invalid_State("All items are already issued");
            case 4 -> throw new Invalid_State("Already closed");
        }
        if(reserved && status > 1) {
            throw new Invalid_State("Items are reserved, release them before closing");
        }
        MaterialIssued mi = get(MaterialIssued.class, "Request=" + getId() + " AND Status=0");
        if(mi != null) {
            mi.delete(transaction);
        }
        status = 4;
        save(transaction);
    }

    @Override
    public String toDisplay() {
        return "From: " + getFromLocation().toDisplay() +
                ", To: " + getToLocation().toDisplay() +
                ", Date: " + DateUtility.format(date) +
                ", Reference: " + getReference() +
                ", Status: " + getStatusValue();
    }

    void checkStatus() throws SOException {
        switch(status) {
            case 3, 4 -> throw new SOException("Status is " + getStatusValue());
        }
    }

    public void reduceRequestedQuantity(Transaction transaction, InventoryItemType partNumber, Quantity reduceBy)
            throws Exception {
        checkStatus();
        List<MaterialRequestItem> mris = listLinks(MaterialRequestItem.class,
                "PartNumber=" + partNumber.getId(), true).filter(i -> i.getBalance().isPositive()).toList();
        MaterialRequestItem mri = mris.stream().filter(i -> i.getBalance().equals(reduceBy))
                .findAny().orElse(null);
        if(mri != null) {
            mri.reduceRequestedQuantity(transaction, reduceBy, false);
            return;
        }
        mri = mris.stream().filter(i -> i.getBalance().isGreaterThan(reduceBy))
                .findAny().orElse(null);
        if(mri != null) {
            mri.reduceRequestedQuantity(transaction, reduceBy, false);
            return;
        }
        Quantity r = reduceBy, b;
        while(r.isPositive() && !mris.isEmpty()) {
            mri = mris.remove(0);
            b = mri.getBalance();
            r = r.subtract(b);
            mri.reduceRequestedQuantity(transaction, b, false);
        }
        if(r.isPositive()) {
            transaction.rollback();
            throw new SOException("Unable to find " + partNumber.toDisplay() + " for quantity " + r);
        }
    }

    public int getType() {
        return 0;
    }
}
