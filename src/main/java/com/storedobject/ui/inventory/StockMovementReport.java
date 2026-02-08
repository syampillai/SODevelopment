package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.vaadin.BooleanField;

public class StockMovementReport extends StockReportForm {

    private DatePeriodField periodField;
    private BooleanField summaryField;
    private Report report;

    public StockMovementReport(Application a) {
        super(a.getLogicTitle("Stock Movement"));
    }

    @Override
    protected void buildDateField() {
        periodField = new DatePeriodField("Period", new DatePeriod(DateUtility.startOfMonth(), DateUtility.today()));
        addField(periodField);
        summaryField = new BooleanField("Print Summary Only");
        addField(summaryField);
    }

    @Override
    protected void process(InventoryLocation location) {
        report = new Report(getApplication(), location, periodField.getValue());
        report.execute();
    }

    public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers) {
        printStockMovement(partNumbers, null);
    }

    public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
        if(report != null) {
            report.printStockMovement(partNumbers, categoryHeading);
        }
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return null;
    }

    public void configure(com.storedobject.report.StockMovementReport report) {
    }

    private class Report extends com.storedobject.report.StockMovementReport {

        public Report(Device device, InventoryLocation location, DatePeriod period) {
            super(device, location, period);
            printZeros(zerosField.getValue());
            printSummary(summaryField.getValue());
            printCostInLocalCurrency(localCurrencyField.getValue());
            InventoryItemType pn = pnField.getObject();
            if(pn != null) {
                setPartNumber(pn);
            }
            setCaption(getCaption());
            configure(this);
        }

        @Override
        public void generateContent() throws Exception {
            StockMovementReport.this.generateContent();
            if(customized) {
                return;
            }
            super.generateContent();
        }

        @Override
        public String getItemTypeTitle(InventoryItemType itemType) {
            String title = StockMovementReport.this.getItemTypeTitle(itemType);
            return title == null ? super.getItemTypeTitle(itemType) : title;
        }
    }
}
