package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.sql.Date;
import java.util.*;

public final class TaxRegion extends Name {

    private static final Map<Id, TaxRegion> cache = new HashMap<>();
    private static TaxRegion DEFAULT;
    private List<TaxType> taxTypes;
    private boolean active = true;

    public TaxRegion() {}

    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    public static TaxRegion get(String name) {
        return StoredObjectUtility.get(TaxRegion.class, "Name", name, false);
    }

    public static ObjectIterator<TaxRegion> list(String name) {
        return StoredObjectUtility.list(TaxRegion.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    @Override
    public void saved() throws Exception {
        cache.remove(getId());
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 200)
    public boolean getActive() {
        return active;
    }

    public static TaxRegion getFor(Id id) {
        TaxRegion tc = cache.get(id);
        if (tc == null) {
            tc = get(TaxRegion.class, id);
            if (tc != null) {
                cache.put(id, tc);
            }
        }
        return tc;
    }

    public static TaxRegion getDefault() {
        if(DEFAULT == null) {
            DEFAULT = list(TaxRegion.class, "Active", "Id").findFirst();
        }
        return DEFAULT;
    }

    public List<TaxType> getTaxTypes() {
        if(taxTypes == null) {
            taxTypes = list(TaxType.class, "Region=" + getId() + " AND Active", "DisplayOrder").toList();
        }
        return taxTypes;
    }

    public List<Tax> computeTax(Date date, StoredObject parent, InventoryItem item, Quantity quantity, Money unitCost,
                                Currency localCurrency) {
        return computeTax(date, parent, item.getPartNumber(), quantity, unitCost, localCurrency);
    }

    public List<Tax> computeTax(Date date, StoredObject parent, InventoryItemType itemType, Quantity quantity,
                                Money unitCost, Currency localCurrency) {
        List<TaxType> taxTypes = TaxDefinition.listTypes(itemType, this, date).toList();
        List<Tax> taxes = parent.listLinks(Tax.class).toList();
        for(Tax tax : taxes) {
            if(!tax.getRegion().getId().equals(getId())) {
                tax.status = 3; // Region changed - to be deleted
            } else if(taxTypes.stream().noneMatch(t -> tax.getTypeId().equals(t.getId()))) {
                tax.status = 4; // No more applicable - to be deleted
            } else {
                TaxType tt = tax.getType();
                Percentage p = TaxRate.getRate(date, tt);
                Money t = tt.getTaxMethod().getTax(itemType, quantity, unitCost, p, localCurrency);
                if(!p.equals(tax.getRate()) || !t.equals(tax.getTax())) {
                    tax.status = 2; // Modified
                    tax.setTax(t);
                    tax.setRate(p);
                }
            }
        }
        List<Tax> result = new ArrayList<>();
        Tax tax;
        for(TaxType taxType : taxTypes) {
            tax = taxes.stream().filter(t -> t.getTypeId().equals(taxType.getId())).findFirst().orElse(null);
            if(tax == null) {
                tax = new Tax();
                tax.status = 1; // Newly created
                tax.setType(taxType);
                Percentage p = TaxRate.getRate(date, taxType);
                tax.setRate(p);
                tax.setTax(taxType.getTaxMethod().getTax(itemType, quantity, unitCost, p, localCurrency));
                tax.makeVirtual();
            }
            result.add(tax);
        }
        return result;
    }
}