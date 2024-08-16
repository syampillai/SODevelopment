package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;

public final class TaxDefinition extends StoredObject {

    private int category;
    private Id typeId;

    public TaxDefinition() {
    }

    public static void columns(Columns columns) {
        columns.add("Category", "int");
        columns.add("Type", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Category,Type", true);
    }

    public String getUniqueCondition() {
        return "Category=" + category + " AND Type=" + typeId;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Column(order = 100, required = false)
    public int getCategory() {
        return category;
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
    @Column(order = 300)
    public Id getTypeId() {
        return typeId;
    }

    public TaxType getType() {
        return TaxType.getFor(typeId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(getType() == null) {
            throw new Invalid_Value("Type");
        }
        checkForDuplicate("Category", "Type");
        super.validateData(tm);
    }

    public static ObjectIterator<TaxType> listTypes(InventoryItem item, TaxRegion region, Date date) {
        return listTypes(item.getPartNumber(), region, date);
    }

    public static ObjectIterator<TaxType> listTypes(InventoryItemType itemType, TaxRegion region, Date date) {
        return list(TaxDefinition.class, "Category=" + itemType.getTaxCategory(region)
                        + " AND Type.Region=" + region
                        + " AND '" + Database.format(date) + "' BETWEEN Type.ApplicableFrom AND Type.ApplicableTo",
                "Type.DisplayOrder").map(TaxDefinition::getType);
    }
}
