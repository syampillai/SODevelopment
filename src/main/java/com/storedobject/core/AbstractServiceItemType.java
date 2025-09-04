package com.storedobject.core;

import com.storedobject.core.annotation.Column;

public abstract class AbstractServiceItemType extends InventoryItemType {

    private String description;

    public AbstractServiceItemType() {
        setUnitOfMeasurement(Count.ZERO);
        setMinimumStockLevel(Count.ONE);
        setReorderPoint(Count.ONE);
        setEconomicOrderQuantity(Count.ONE);
    }

    public static void columns(Columns columns) {
        columns.add("Description", "text");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "UnitOfMeasurement",
                "MinimumStockLevel",
                "ReorderPoint",
                "EconomicOrderQuantity",
                "HandlingAndStorage",
                "HSNCode",
                "UNNumber",
        };
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(style = "(large)", required = false, order = 2000)
    public String getDescription() {
        return description;
    }

    @Override
    public final boolean isExpendable() {
        return false;
    }

    @Override
    public final boolean isConsumable() {
        return false;
    }

    @Override
    public final boolean isTool() {
        return false;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(description)) {
            description = getName();
        }
        serviceCodeFromPN();
        super.validateData(tm);
    }

    @Override
    public String getPartNumberName() {
        return "SAC-";
    }

    @Override
    public String getPartNumberShortName() {
        return "SAC-";
    }

    protected void serviceCodeFromPN() {
        String pn = getPartNumber();
        if(pn.length() >= 6) {
            setHSNCode(pn.substring(0, 6));
        }
    }
}