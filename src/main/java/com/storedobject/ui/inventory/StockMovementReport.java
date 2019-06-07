package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.ObjectIterator;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.DataForm;

public class StockMovementReport extends DataForm {

    public StockMovementReport(Application a) {
        super(a.getLogicTitle("Stock Movement"));
    }

    @Override
    protected void buildFields() {
    }

    @Override
    protected boolean process() {
        return true;
    }

    public void generateContent() {
    }

    public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers) {
    }

    public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
    }

    public String getItemTypeTitle(@SuppressWarnings("unused") InventoryItemType itemType) {
        return null;
    }

    public void configure(@SuppressWarnings("unused") com.storedobject.report.StockMovementReport report) {
    }
}
