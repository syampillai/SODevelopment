package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class TaxType extends Name {

    private static final Map<Id, TaxType> taxTypes = new HashMap<>();
    private Id categoryId;
    private boolean active;

    public TaxType() {
    }

    public static void columns(Columns columns) {
        columns.add("Category", "id");
        columns.add("Active", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("Category");
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

    public void setCategory(Id categoryId) {
        if (!loading() && !Id.equals(this.getCategoryId(), categoryId)) {
            throw new Set_Not_Allowed("Category");
        }
        this.categoryId = categoryId;
    }

    public void setCategory(BigDecimal idValue) {
        setCategory(new Id(idValue));
    }

    public void setCategory(TaxCategory category) {
        setCategory(category == null ? null : category.getId());
    }

    @SetNotAllowed
    @Column(order = 200)
    public Id getCategoryId() {
        return categoryId;
    }

    public TaxCategory getCategory() {
        return TaxCategory.getFor(categoryId);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 300)
    public boolean getActive() {
        return active;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        categoryId = tm.checkType(this, categoryId, TaxCategory.class, false);
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        taxTypes.remove(getId());
    }

    public static TaxType getFor(Id id) {
        TaxType tt = taxTypes.get(id);
        if(tt == null) {
            tt = get(TaxType.class, id);
            if(tt != null) {
                taxTypes.put(id, tt);
            }
        }
        return tt;
    }
}
