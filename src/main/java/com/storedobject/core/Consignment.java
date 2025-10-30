package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;

public class Consignment extends StoredObject implements HasReference {

    private static final ReferencePattern<Consignment> ref = new ReferencePattern<>();
    private String reference;
    private final static String[] typeValues = new String[] {
            "Material Return", "Repair", "Transfer", "GRN", "Loan out", "Sale", "Material Issued",
    };
    private final Date date = DateUtility.today();
    private int no = 0, type = 0;
    private String portOfLoading = "", portOfDischarge = "", airwayBillNumber = "", remark = "";
    private Id buyerId = Id.ZERO;
    private OfEntity parent;

    public Consignment() {
    }

    public Consignment(OfEntity parent) {
        this(parent.getClass());
        this.parent = parent;
    }

    public <T extends OfEntity> Consignment(Class<T> parentClass) {
        type = findTypeFor(parentClass);
    }

    public static int findTypeFor(Class<? extends OfEntity> parentClass) {
        int type;
        if(parentClass == null) {
            throw new SORuntimeException("Parent class must not be null");
        }
        if(InventorySale.class.isAssignableFrom(parentClass)) {
            type = 5;
        } else if(InventoryLoanOut.class.isAssignableFrom(parentClass)) {
            type = 4;
        } else if(MaterialReturned.class.isAssignableFrom(parentClass)) {
            type = 0;
        } else if(InventoryRO.class.isAssignableFrom(parentClass)) {
            type = 1;
        } else if(InventoryTransfer.class.isAssignableFrom(parentClass)) {
            type = 2;
        } else if(InventoryGRN.class.isAssignableFrom(parentClass)) {
            type = 3;
        } else if(MaterialIssued.class.isAssignableFrom(parentClass)) {
            type = 6;
        } else {
            throw new SORuntimeException("Consignment not configured for: " + StringUtility.makeLabel(parentClass));
        }
        return type;
    }

    public static void columns(Columns columns) {
        columns.add("Type", "int");
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("PortOfLoading", "text");
        columns.add("PortOfDischarge", "text");
        columns.add("Buyer", "id");
        columns.add("AirwayBillNumber", "text");
        columns.add("Remark", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Type,Date,No");
    }

    public static String[] links() {
        return new String[]{
                "Packets|com.storedobject.core.ConsignmentPacket|Number||0",
                "Items|com.storedobject.core.ConsignmentItem|||0",
        };
    }

    public static String[] browseColumns() {
        return new String[] { "Reference", "Type", "Date", "PortOfLoading", "PortOfDischarge", "AirwayBillNumber", "Remark" };
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

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Override
    @Column(order = 20)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setPortOfLoading(String portOfLoading) {
        this.portOfLoading = portOfLoading;
    }

    @Column(order = 30, required = false)
    public String getPortOfLoading() {
        return portOfLoading;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    @Column(order = 40, required = false)
    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setBuyer(Id buyerId) {
        this.buyerId = buyerId;
    }

    public void setBuyer(BigDecimal idValue) {
        setBuyer(new Id(idValue));
    }

    public void setBuyer(Entity buyer) {
        setBuyer(buyer == null ? Id.ZERO : buyer.getId());
    }

    @Column(order = 50, required = false, caption = "Buyer (If other than as consignee)")
    public Id getBuyerId() {
        return buyerId;
    }

    public Entity getBuyer() {
        return get(Entity.class, buyerId);
    }

    public void setAirwayBillNumber(String airwayBillNumber) {
        this.airwayBillNumber = airwayBillNumber == null ? "" : airwayBillNumber;
    }

    @Column(order = 60, style = "(upper)", required = false)
    public String getAirwayBillNumber() {
        return airwayBillNumber;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(order = 70, style = "(large)", required = false)
    public String getRemark() {
        return remark;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        airwayBillNumber = toCode(airwayBillNumber);
        buyerId = tm.checkType(this, buyerId, Entity.class, true);
    }

    @Override
    public String getTagPrefix() {
        return "CONSIGNMENT-";
    }

    Id orgId() {
        SystemEntity se = getSystemEntity();
        return se == null ? Id.ZERO : se.getId();
    }

    public SystemEntity getSystemEntity() {
        if(parent != null) {
            return parent.getSystemEntity();
        }
        if(type == 0) {
            parent = listMasters(MaterialReturned.class, true).findFirst();
        } else if(type == 3) {
            parent = listMasters(InventoryGRN.class).findFirst();
        }
        parent = listMasters(InventoryTransfer.class, true).findFirst();
        return parent == null ? null : parent.getSystemEntity();
    }

    public void setParent(OfEntity parent) {
        if(findTypeFor(parent.getClass()) == type) {
            this.parent = parent;
        }
    }

    Id loc() {
        switch (type) {
            case 0 -> {
                MaterialReturned mt;
                if(parent == null) {
                    mt = listMasters(MaterialReturned.class, true).findFirst();
                } else {
                    mt = (MaterialReturned) parent;
                }
                return mt == null ? Id.ZERO : mt.fromLocationId;
            }
            case 1 -> {
                InventoryRO ro;
                if(parent == null) {
                    ro = listMasters(InventoryRO.class).findFirst();
                } else {
                    ro = (InventoryRO) parent;
                }
                return ro == null ? Id.ZERO : ro.fromLocationId;
            }
            case 2 -> {
                InventoryTransfer it;
                if(parent == null) {
                    it = listMasters(InventoryTransfer.class, true).findFirst();
                } else {
                    it = (InventoryTransfer) parent;
                }
                return it == null ? Id.ZERO : it.fromLocationId;
            }
            case 3 -> {
                InventoryGRN grn;
                if(parent == null) {
                    grn = listMasters(InventoryGRN.class).findFirst();
                } else {
                    grn = (InventoryGRN) parent;
                }
                return grn == null ? Id.ZERO : grn.getStore().getStoreBin().getId();
            }
        }
        return Id.ZERO;
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
}
