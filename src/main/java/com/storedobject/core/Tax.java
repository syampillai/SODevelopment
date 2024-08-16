package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public final class Tax extends StoredObject {

    private Id typeId;
    private Percentage rate = Quantity.create(Percentage.class);
    private Money tax = new Money();
    int status = 0;
    boolean internal = false;

    public Tax() {
    }

    public static void columns(Columns columns) {
        columns.add("Type", "id");
        columns.add("Rate", "percentage");
        columns.add("Tax", "money");
    }

    public void setType(Id typeId) {
        if (!loading() && !Id.equals(this.typeId, typeId)) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
        if(!Id.isNull(typeId)) {
            rate = TaxRate.getRate(DateUtility.today(), typeId);
        }
    }

    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public void setType(TaxType type) {
        setType(type == null ? null : type.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getTypeId() {
        return typeId;
    }

    public TaxType getType() {
        return TaxType.getFor(typeId);
    }

    public void setRate(Percentage rate) {
        this.rate = rate;
    }

    public void setRate(Object value) {
        setRate(Percentage.create(value, Percentage.class));
    }

    @Column(order = 200)
    public Percentage getRate() {
        return rate;
    }

    public void setTax(Money tax) {
        this.tax = tax;
    }

    public void setTax(Object moneyValue) {
        setTax(Money.create(moneyValue));
    }

    @Column(order = 200)
    public Money getTax() {
        return tax;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(getType() == null) {
            throw new Invalid_Value("Tax Type");
        }
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        if(!internal) {
            throw new Invalid_State("Illegal access");
        }
        super.validate();
    }

    @Override
    void savedCore() throws Exception {
        internal = false;
    }

    @Override
    public String toString() {
        return getLabel() + " " + tax;
    }

    public String getLabel() {
        return getType().getName() + " @" + rate;
    }

    public TaxRegion getRegion() {
        return getType().getRegion();
    }

    /**
     * Status - 0: Normal / No change, 1: Newly computed, 2: Recomputed, 3: Region changed (to deleted), 4: No more applicable (to delete)
     * @return Status value
     */
    public int getStatus() {
        return status;
    }
}
