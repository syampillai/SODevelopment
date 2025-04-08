package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Table(anchors = "Type")
public final class TaxRate extends StoredObject {

    private static final Map<Id, TaxRate> taxRates = new HashMap<>(); // Key is Id
    private static final Map<Id, TaxRate> latestTaxRates = new HashMap<>(); // Key is typeId
    private Id typeId;
    private Percentage rate = Quantity.create(Percentage.class);
    private final Date effectiveFrom = DateUtility.today();

    public TaxRate() {
    }

    public static void columns(Columns columns) {
        columns.add("Type", "id");
        columns.add("Rate", "percentage");
        columns.add("EffectiveFrom", "date");
    }

    public static void indices(Indices indices) {
        indices.add("Type, EffectiveFrom", true);
    }

    public String getUniqueCondition() {
        return "Type="
                + getTypeId()
                + " AND "
                + "EffectiveFrom='"
                + Database.format(getEffectiveFrom())
                + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
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
        if (!loading()) {
            throw new Set_Not_Allowed("Rate");
        }
        this.rate = rate;
    }

    public void setRate(Object value) {
        setRate(Percentage.create(value, Percentage.class));
    }

    @SetNotAllowed
    @Column(order = 200)
    public Percentage getRate() {
        return rate;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom.setTime(effectiveFrom.getTime());
    }

    @Column(order = 300)
    public Date getEffectiveFrom() {
        return new Date(effectiveFrom.getTime());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        typeId = tm.checkType(this, typeId, TaxType.class, false);
        if (Utility.isEmpty(effectiveFrom)) {
            throw new Invalid_Value("Effective from");
        }
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        taxRates.remove(getId());
        latestTaxRates.remove(typeId);
    }

    public static TaxRate getFor(Id id) {
        TaxRate tr = taxRates.get(id);
        if(tr == null) {
            tr = get(TaxRate.class, id);
            if(tr != null) {
                taxRates.put(id, tr);
            }
        }
        return tr;
    }

    @Override
    public String toString() {
        return getType().getName() + " @" + rate.toString();
    }

    public static Percentage getRate(Date date, TaxType type) {
        return getRate(date, type.getId());
    }

    public static Percentage getRate(Date date, Id typeId) {
        TaxRate cached = latestTaxRates.get(typeId);
        if(cached != null && !date.before(cached.effectiveFrom)) {
            return cached.rate;
        }
        TaxRate tr;
        try (Query q = query(TaxRate.class, "Id", "Type=" + typeId, "EffectiveFrom DESC")) {
            for(ResultSet rs : q) {
                tr = getFor(new Id(rs.getBigDecimal(1)));
                if(!date.before(tr.effectiveFrom)) {
                    if(cached == null || cached.effectiveFrom.before(tr.effectiveFrom)) {
                        latestTaxRates.put(typeId, tr);
                    }
                    return tr.rate;
                }
            }
        } catch (SQLException ignored) {
        }
        return new Percentage();
    }
}
