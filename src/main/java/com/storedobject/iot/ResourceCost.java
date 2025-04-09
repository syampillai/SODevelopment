package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Table(anchors = "Resource")
public final class ResourceCost extends StoredObject {

    private static final Map<Id, ResourceCost> rates = new HashMap<>(); // Key is Id
    private static final Map<Id, ResourceCost> latestRates = new HashMap<>(); // Key is resourceId
    private Id resourceId;
    private Money rate = new Money();
    private final Date effectiveFrom = DateUtility.today();

    public ResourceCost() {
    }

    public static void columns(Columns columns) {
        columns.add("Resource", "id");
        columns.add("Rate", "money");
        columns.add("EffectiveFrom", "date");
    }

    public static void indices(Indices indices) {
        indices.add("Resource, EffectiveFrom", true);
    }

    public String getUniqueCondition() {
        return "Resource="
                + getResourceId()
                + " AND "
                + "EffectiveFrom='"
                + Database.format(getEffectiveFrom())
                + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setResource(Id resourceId) {
        if (!loading() && !Id.equals(this.getResourceId(), resourceId)) {
            throw new Set_Not_Allowed("Resource");
        }
        this.resourceId = resourceId;
    }

    public void setResource(BigDecimal idValue) {
        setResource(new Id(idValue));
    }

    public void setResource(Resource resource) {
        setResource(resource == null ? null : resource.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getResourceId() {
        return resourceId;
    }

    public Resource getResource() {
        return Resource.getFor(resourceId);
    }

    public void setRate(Money rate) {
        if (!loading()) {
            throw new Set_Not_Allowed("Rate");
        }
        this.rate = rate;
    }

    public void setRate(Object value) {
        setRate(Money.create(value));
    }

    @SetNotAllowed
    @Column(order = 200)
    public Money getRate() {
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
        resourceId = tm.checkType(this, resourceId, Resource.class, false);
        if (Utility.isEmpty(effectiveFrom)) {
            throw new Invalid_Value("Effective from");
        }
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        rates.remove(getId());
        latestRates.remove(resourceId);
    }

    public static ResourceCost getFor(Id id) {
        ResourceCost tr = rates.get(id);
        if(tr == null) {
            tr = get(ResourceCost.class, id);
            if(tr != null) {
                rates.put(id, tr);
            }
        }
        return tr;
    }

    @Override
    public String toString() {
        return getResource().getName() + " @" + rate.toString();
    }

    public static Money getRate(Date date, Resource resource) {
        return getRate(date, resource.getId());
    }

    public static Money getRate(Date date, Id resourceId) {
        ResourceCost cached = latestRates.get(resourceId);
        if(cached != null && !date.before(cached.effectiveFrom)) {
            return cached.rate;
        }
        ResourceCost tr;
        try (Query q = query(ResourceCost.class, "Id", "Resource=" + resourceId, "EffectiveFrom DESC")) {
            for(ResultSet rs : q) {
                tr = getFor(new Id(rs.getBigDecimal(1)));
                if(!date.before(tr.effectiveFrom)) {
                    if(cached == null || cached.effectiveFrom.before(tr.effectiveFrom)) {
                        latestRates.put(resourceId, tr);
                    }
                    return tr.rate;
                }
            }
        } catch (SQLException ignored) {
        }
        return new Money();
    }

    public static Money getRate(Resource resource) {
        return getRate(resource.getId());
    }

    public static Money getRate(Id resourceId) {
        return getRate(DateUtility.today(), resourceId);
    }
}
