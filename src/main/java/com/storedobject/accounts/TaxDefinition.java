package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public final class TaxDefinition extends StoredObject {

    private Id itemId = Id.ZERO;
    private Id regionId;
    private Id categoryId;

    public TaxDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("Item", "id");
        columns.add("Region", "id");
        columns.add("Category", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Item, Region, Category", true);
    }

    public String getUniqueCondition() {
        return "Item="
                + getItemId()
                + " AND "
                + "Region="
                + getRegionId()
                + " AND "
                + "Category="
                + getCategoryId();
    }

    public void setItem(Id itemId) {
        if (!loading() && !Id.equals(this.getItemId(), itemId)) {
            throw new Set_Not_Allowed("Item");
        }
        this.itemId = itemId;
    }

    public void setItem(BigDecimal idValue) {
        setItem(new Id(idValue));
    }

    public void setItem(InventoryItemType item) {
        setItem(item == null ? null : item.getId());
    }

    @SetNotAllowed
    @Column(style = "(any)", required = false, order = 100)
    public Id getItemId() {
        return itemId;
    }

    public InventoryItemType getItem() {
        return getRelated(InventoryItemType.class, itemId, true);
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
    @Column(style = "(any)", order = 200)
    public Id getRegionId() {
        return regionId;
    }

    public TaxRegion getRegion() {
        return TaxRegion.getFor(regionId);
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
    @Column(order = 300)
    public Id getCategoryId() {
        return categoryId;
    }

    public TaxCategory getCategory() {
        return TaxCategory.getFor(categoryId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        itemId = tm.checkTypeAny(this, itemId, InventoryItemType.class, true);
        regionId = tm.checkTypeAny(this, regionId, TaxRegion.class, false);
        categoryId = tm.checkType(this, categoryId, TaxCategory.class, false);
        super.validateData(tm);
    }
}
