package com.storedobject.core;

import com.storedobject.accounts.FixedTax;
import com.storedobject.accounts.NoTax;
import com.storedobject.core.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class TaxMethod extends Name {

    private static final BigDecimal BD_100 = BigDecimal.valueOf(100);
    private static final Map<Id, TaxMethod> cache = new HashMap<>();
    private boolean active = true;

    public TaxMethod() {
        name = "Percentage";
    }

    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("T_Family", true);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 300)
    public boolean getActive() {
        return active;
    }

    @Override
    public void saved() throws Exception {
        cache.remove(getId());
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        if(inserted()) {
            TaxMethod m = get(TaxMethod.class, "T_Family=" + ClassAttribute.get(getClass()));
            if(m != null) {
                throw new Invalid_State("Duplicate entry");
            }
        }
        if(!(this instanceof NoTax)) {
            if(!exists(NoTax.class, "T_Family=" + ClassAttribute.get(NoTax.class).getFamily())) {
                tm.transact(new NoTax()::save);
            }
        }
        if(!(this instanceof FixedTax)) {
            if(!exists(FixedTax.class, "T_Family=" + ClassAttribute.get(FixedTax.class).getFamily())) {
                tm.transact(new FixedTax()::save);
            }
        }
        if(getClass() != TaxMethod.class) {
            if(!exists(TaxMethod.class, "T_Family=" + ClassAttribute.get(TaxMethod.class).getFamily())) {
                tm.transact(new TaxMethod()::save);
            }
        }
    }

    public static TaxMethod getFor(Id id) {
        TaxMethod tm = cache.get(id);
        if(tm == null) {
            tm = get(TaxMethod.class, id, true);
            if(tm != null) {
                cache.put(id, tm);
            }
        }
        return tm;
    }

    public Money getTax(InventoryItemType itemType, Quantity quantity,
                        Money unitCost, Percentage taxRate, Currency localCurrency) {
        return round(unitCost.multiply(quantity).multiply(taxRate).divide(BD_100).convert(localCurrency));
    }

    public Money round(Money taxAmount) {
        return taxAmount;
    }
}
