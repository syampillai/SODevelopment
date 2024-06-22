package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;
import com.storedobject.core.annotation.Table;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * GRN class. Used to accept items received from a supplier.
 *
 * @author Syam
 */
@Table(anchors = "Store")
public final class InventoryGRN extends StoredObject implements HasChildren, HasReference {

    private static final ReferencePattern<InventoryGRN> ref = new ReferencePattern<>();
    private final static String[] statusValues = new String[] {
            "Initiated", "Processed", "Closed"
    };
    private final static String[] typeValues = new String[] {
            "Purchase/Supplier", "External Owner", "Loaned from", "Items Repaired by", "Sales Return"
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
                    + getTagPrefix() + ref.get(this)).intValue();
        }
        return no;
    }

    @Override
    public SystemEntity getSystemEntity() {
        return getStore().getSystemEntity();
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
        this.referenceNumber = toCode(referenceNumber);
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

    public boolean isProcessed() {
        return status == 1;
    }

    public boolean isClosed() {
        return status == 2;
    }

    public void process(Transaction transaction) throws Exception {
        if(status != 0) {
            throw new Invalid_State("Already processed");
        }
    }

    public void close(Transaction transaction) throws Exception {
        if(status == 0) {
            throw new Invalid_State("Not yet processed");
        }
    }

    @Override
    public String toString() {
        return getReference() + " dated " + DateUtility.format(date)
                + (type == 0 ? "" : " (" + getTypeValue() + ")");
    }

    /**
     * Is a specific type of landed cost is applicable to this GRN?
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
        if(status == 0) {
            throw new SOException("Not yet processed");
        }
    }
}
