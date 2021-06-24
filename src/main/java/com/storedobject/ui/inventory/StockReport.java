package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.report.StockReportExcel;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;

public class StockReport extends DataForm {

    private ObjectField<InventoryStore> storeField;
    private BooleanField zerosField;
    private ChoiceField outputField;
    private boolean customized = true;
    private Report pdf;
    private ExReport excel;

    public StockReport(Application a) {
        super(a.getLogicTitle("Stock Report"));
    }

    @Override
    protected void buildFields() {
        storeField = new ObjectField<>("Store", InventoryStore.class, true);
        storeField.setPlaceholder("All");
        addField(storeField);
        zerosField = new BooleanField("Print Zero Quantity Items");
        addField(zerosField);
        outputField = new ChoiceField("Format", new String[] { "PDF", "Excel" });
        addField(outputField);
    }

    @Override
    protected boolean process() {
        close();
        switch (outputField.getValue()) {
            case 0:
                pdf = new Report(getApplication(), storeField.getObject());
                pdf.execute();
                break;
            case 1:
                excel = new ExReport(getApplication(), storeField.getObject());
                excel.execute();
                break;
            default:
                return false;
        }
        return true;
    }

    public void generateContent() throws Exception {
        customized = false;
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers) {
        printStock(partNumbers, null);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
        printStock(partNumbers, categoryHeading, false);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, boolean newPage) {
        printStock(partNumbers, null, newPage);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading, boolean newPage) {
        if(pdf != null) {
            pdf.printStock(partNumbers, categoryHeading, newPage);
        } else if(excel != null) {
            excel.printStock(partNumbers, categoryHeading);
        }
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return null;
    }

    public void configure(com.storedobject.report.StockReport report) {
    }

    public void configure(com.storedobject.report.StockReportExcel report) {
    }

    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }

    private class Report extends com.storedobject.report.StockReport {

        public Report(Device device, InventoryStore store) {
            super(device, store);
            printZeros(zerosField.getValue());
            setCaption(getCaption());
            configure(this);
        }

        @Override
        public void generateContent() throws Exception {
            StockReport.this.generateContent();
            if(customized) {
                return;
            }
            super.generateContent();
        }

        @Override
        public String getItemTypeTitle(InventoryItemType itemType) {
            String title = StockReport.this.getItemTypeTitle(itemType);
            return title == null ? super.getItemTypeTitle(itemType) : title;
        }

        public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
            return StockReport.this.printZeroQuantity(store, itemType);
        }
    }

    private class ExReport extends StockReportExcel {

        public ExReport(Device device, InventoryStore store) {
            super(device, store);
            printZeros(zerosField.getValue());
            setCaption(getCaption());
            configure(this);
        }

        @Override
        public void generateContent() throws Exception {
            StockReport.this.generateContent();
            if(customized) {
                return;
            }
            super.generateContent();
        }

        @Override
        public String getItemTypeTitle(InventoryItemType itemType) {
            String title = StockReport.this.getItemTypeTitle(itemType);
            return title == null ? super.getItemTypeTitle(itemType) : title;
        }

        public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
            return StockReport.this.printZeroQuantity(store, itemType);
        }
    }
}