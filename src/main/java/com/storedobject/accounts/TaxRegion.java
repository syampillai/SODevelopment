package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TaxRegion extends StoredObject {

    private static final Map<Id, TaxRegion> regions = new HashMap<>();
    private String country;
    private Id destinationId = Id.ZERO;

    public TaxRegion() {
    }

    public static void columns(Columns columns) {
        columns.add("Country", "country");
        columns.add("Destination", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Country");
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setCountry(String country) {
        if (!loading()) {
            throw new Set_Not_Allowed("Country");
        }
        this.country = country;
    }

    @SetNotAllowed
    @Column(style = "(country)", order = 100)
    public String getCountry() {
        return country;
    }

    public void setDestination(Id destinationId) {
        if (!loading() && !Id.equals(this.getDestinationId(), destinationId)) {
            throw new Set_Not_Allowed("Destination");
        }
        this.destinationId = destinationId;
    }

    public void setDestination(BigDecimal idValue) {
        setDestination(new Id(idValue));
    }

    public void setDestination(TaxRegion destination) {
        setDestination(destination == null ? null : destination.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", required = false, order = 200)
    public Id getDestinationId() {
        return destinationId;
    }

    public TaxRegion getDestination() {
        return getRelated(TaxRegion.class, destinationId, true);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        country = StringUtility.pack(country);
        if (!country.isEmpty()) {
            throw new Invalid_Value("Country");
        }
        country = com.storedobject.common.Country.check(country);
        destinationId = tm.checkTypeAny(this, destinationId, TaxRegion.class, true);
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        regions.remove(getId());
    }

    public static TaxRegion getFor(Id id) {
        TaxRegion region = regions.get(id);
        if (region == null) {
            region = get(TaxRegion.class, id, true);
            if(region != null) {
                regions.put(region.getId(), region);
            }
        }
        return region;
    }
}
