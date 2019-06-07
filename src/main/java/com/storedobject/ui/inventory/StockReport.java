package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.ObjectIterator;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.DataForm;

public class StockReport extends DataForm {

    public StockReport(Application a) {
        super(a.getLogicTitle("Stock Report"));
    }

    @Override
    protected void buildFields() {
    }

    @Override
    protected boolean process() {
        return true;
    }

    @SuppressWarnings("RedundantThrows")
    public void generateContent() throws Exception {
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers) {
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
    }

    public void printHeading(String heading) {
    }

    public String getItemTypeTitle(@SuppressWarnings("unused") InventoryItemType itemType) {
        return null;
    }

    public void configure(@SuppressWarnings("unused") com.storedobject.report.StockReport report) {
    }

    public void configure(@SuppressWarnings("unused") com.storedobject.report.StockReportExcel report) {
    }
}