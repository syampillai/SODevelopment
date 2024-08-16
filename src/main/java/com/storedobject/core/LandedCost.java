package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

public final class LandedCost extends StoredObject implements Detail {

    private Id typeId;
    private Money amount = new Money();

    public LandedCost() {
    }

    public static void columns(Columns columns) {
        columns.add("Type", "id");
        columns.add("Amount", "money");
    }

    public void setType(Id typeId) {
        if (!loading() && !Id.equals(this.getTypeId(), typeId)) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
    }

    public void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public void setType(LandedCostType type) {
        setType(type == null ? null : type.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getTypeId() {
        return typeId;
    }

    public LandedCostType getType() {
        return LandedCostType.getFor(typeId);
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public void setAmount(Object moneyValue) {
        setAmount(Money.create(moneyValue));
    }

    @Column(required = false, order = 200)
    public Money getAmount() {
        return amount;
    }

    public Money getEffectiveAmount() {
        return getType().getDeduct() ? amount.negate() : amount;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        typeId = tm.checkType(this, typeId, LandedCostType.class, false);
        super.validateData(tm);
    }

    @Override
    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == InventoryGRN.class;
    }

    @Override
    public Id getUniqueId() {
        return typeId;
    }

    public static ObjectIterator<LandedCost> listCost(StoredObject forObject) {
        return forObject.listLinks(LandedCost.class, null, "Type.DisplayOrder");
    }

    public static Money getCost(StoredObject forObject) {
        Money cost = new Money();
        for(LandedCost lc: listCost(forObject)) {
            cost = lc.getType().getDeduct() ? cost.subtract(lc.getAmount()) : cost.add(lc.getAmount());
        }
        return cost;
    }
}
