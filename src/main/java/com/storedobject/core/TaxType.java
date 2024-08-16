package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public final class TaxType extends Name implements DisplayOrder {

    private static final Map<Id, TaxType> cache = new HashMap<>();
    private Id regionId, taxMethodId;
    private int displayOrder;
    private final Date applicableFrom = DateUtility.create(2000, 1, 1),
            applicableTo = DateUtility.create(2999, 12, 31);

    public TaxType() {
    }

    public static void columns(Columns columns) {
        columns.add("Region", "id");
        columns.add("TaxMethod", "id");
        columns.add("DisplayOrder", "int");
        columns.add("ApplicableFrom", "date");
        columns.add("ApplicableTo", "date");
    }

    public static void indices(Indices indices) {
        indices.add("Region");
    }

    public static String[] browseColumns() {
        return new String[] { "Region AS Applicable to", "TaxMethod AS Method of Computation", "ApplicablePeriod" };
    }

    public static TaxType get(String name) {
        return StoredObjectUtility.get(TaxType.class, "Name", name, false);
    }

    public static ObjectIterator<TaxType> list(String name) {
        return StoredObjectUtility.list(TaxType.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setRegion(Id regionId) {
        if (!loading() && !Id.equals(this.getRegionId(), regionId)) {
            throw new Set_Not_Allowed("Region");
        }
        this.regionId = regionId;
    }

    public void setRegion(BigDecimal idValue) {
        setRegion(new Id(idValue));
    }

    public void setRegion(TaxRegion region) {
        setRegion(region == null ? null : region.getId());
    }

    @SetNotAllowed
    @Column(order = 200, caption = "Applicable to")
    public Id getRegionId() {
        return regionId;
    }

    public TaxRegion getRegion() {
        return TaxRegion.getFor(regionId);
    }

    public void setTaxMethod(Id taxMethodId) {
        if (!loading() && !Id.equals(this.getTaxMethodId(), taxMethodId)) {
            throw new Set_Not_Allowed("Tax Method");
        }
        this.regionId = taxMethodId;
    }

    public void setTaxMethod(BigDecimal idValue) {
        setTaxMethod(new Id(idValue));
    }

    public void setTaxMethod(TaxMethod taxMethod) {
        setTaxMethod(taxMethod == null ? null : taxMethod.getId());
    }

    @SetNotAllowed
    @Column(order = 400, caption = "Method of Computation")
    public Id getTaxMethodId() {
        return regionId;
    }

    public TaxMethod getTaxMethod() {
        return TaxMethod.getFor(taxMethodId);
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Column(order = 400, required = false)
    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setApplicableFrom(Date applicableFrom) {
        this.applicableFrom.setTime(applicableFrom.getTime());
    }

    public Date getApplicableFrom() {
        return DateUtility.create(applicableFrom);
    }

    public void setApplicableTo(Date applicableTo) {
        this.applicableTo.setTime(applicableTo.getTime());
    }

    public Date getApplicableTo() {
        return DateUtility.create(applicableTo);
    }

    public DatePeriod getApplicablePeriod() {
        return new DatePeriod(applicableFrom, applicableTo);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        regionId = tm.checkType(this, regionId, TaxRegion.class, false);
        if(getTaxMethod() == null) {
            TaxMethod m = get(TaxMethod.class);
            if(m == null) {
                m = new TaxMethod();
                tm.transact(m::save);
            }
            taxMethodId = m.getId();
        }
        taxMethodId = tm.checkTypeAny(this, taxMethodId, TaxMethod.class, false);
        if(applicableFrom.after(applicableTo)) {
            throw new Invalid_Value("Applicable Period");
        }
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        cache.remove(getId());
    }

    public static TaxType getFor(Id id) {
        TaxType tt = cache.get(id);
        if(tt == null) {
            tt = get(TaxType.class, id);
            if(tt != null) {
                cache.put(id, tt);
            }
        }
        return tt;
    }
}
