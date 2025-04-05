package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

@Table(anchors = "Unit")
public abstract class UnitItem extends AbstractUnit {

    private Id unitId;
    private boolean independent = true;

    public UnitItem() {
    }

    public static void columns(Columns columns) {
        columns.add("Unit", "id");
        columns.add("Independent", "boolean");
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setUnit(Id unitId) {
        if (!loading() && !Id.equals(this.getUnitId(), unitId)) {
            throw new Set_Not_Allowed("Unit");
        }
        this.unitId = unitId;
    }

    public void setUnit(BigDecimal idValue) {
        setUnit(new Id(idValue));
    }

    public void setUnit(Unit unit) {
        setUnit(unit == null ? null : unit.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", order = 200, caption = "Unit")
    public Id getUnitId() {
        return unitId;
    }

    public Unit getUnit() {
        return getRelated(Unit.class, unitId, true);
    }

    public void setIndependent(boolean independent) {
        this.independent = independent;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Column(order = 250)
    public boolean getIndependent() {
        return independent;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        unitId = tm.checkTypeAny(this, unitId, Unit.class, false);
        super.validateData(tm);
    }

    @Override
    public final Site getSite() {
        return getUnit().getSite();
    }

    public final Id getSiteId() {
        return getUnit().getSiteId();
    }

    @Override
    public String toString() {
        return name + " (" + StringUtility.makeLabel(getClass()) + ")";
    }

    public final Block getBlock() {
        return getUnit().getBlock();
    }

    public final Id getBlockId() {
        return getUnit().getBlockId();
    }

    @Override
    final Id unitId() {
        return unitId;
    }
}
