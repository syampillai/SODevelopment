package com.storedobject.ui.inventory;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Device;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectIterator;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;

public class StockMovementReport extends DataForm {

    private ObjectField<InventoryStore> storeField;
    private DatePeriodField periodField;
    private BooleanField summaryField, zerosField;
    private boolean customized = true;
    private Report report;

    public StockMovementReport(Application a) {
        super(a.getLogicTitle("Stock Movement"));
    }

    @Override
    protected void buildFields() {
        storeField = new ObjectField<>("Store", InventoryStore.class, true);
        addField(storeField);
        setRequired(storeField);
        periodField = new DatePeriodField("Period", new DatePeriod(DateUtility.startOfMonth(), DateUtility.today()));
        addField(periodField);
        summaryField = new BooleanField("Print Summary Only");
        addField(summaryField);
        zerosField = new BooleanField("Print Zero Quantity Items");
        addField(zerosField);
    }

    @Override
    protected boolean process() {
        close();
        report = new Report(getApplication(), storeField.getObject(), periodField.getValue());
        report.execute();
        return true;
    }

    public void generateContent() {
        customized = false;
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

        public Report(Device device, InventoryStore store, DatePeriod period) {
            super(device, store, period);
            printZeros(zerosField.getValue());
            printSummary(summaryField.getValue());
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
