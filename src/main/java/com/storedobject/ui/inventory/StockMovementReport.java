package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;

public class StockMovementReport extends DataForm {

    private ObjectField<InventoryStore> storeField;
    private DatePeriodField periodField;
    private BooleanField summaryField, zerosField, localCurrencyField;
    private boolean customized = true;
    private Report report;
    private ObjectField<InventoryItemType> pnField;

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
        zerosField = new BooleanField("Print Zero-Quantity Items");
        addField(zerosField);
        localCurrencyField = new BooleanField("Print Cost in Accounting Currency", true);
        addField(localCurrencyField);
        pnField = new ObjectField<>("Part Number", InventoryItemType.class, true);
        pnField.setHelperText("Leave it blank for printing all items");
        addField(pnField);
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
