package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public final class Tax extends StoredObject implements Detail {

    private Id typeId;
    private Percentage rate = Quantity.create(Percentage.class);
    private Money tax = new Money();

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
            rate = TaxRate.getRateFor(typeId);
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
        typeId = tm.checkType(this, typeId, TaxType.class, false);
        super.validateData(tm);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return HasTax.class.isAssignableFrom(masterClass);
    }

    @Override
    public Id getUniqueId() {
        return typeId;
    }

    @Override
    public String toString() {
        return getLabel() + " " + tax;
    }

    public String getLabel() {
        return getType().getName() + " @" + rate;
    }
}
