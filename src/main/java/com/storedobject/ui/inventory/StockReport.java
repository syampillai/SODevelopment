package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.report.StockReportExcel;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import java.sql.Date;

public class StockReport extends StockReportForm {

    private DateField dateField;
    private ChoiceField outputField;
    private Report pdf;
    private ExReport excel;

    public StockReport() {
        this(Application.get());
    }

    public StockReport(Application a) {
        super(a.getLogicTitle("Stock Report"));
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        outputField = new ChoiceField("Format", new String[] { "PDF", "Excel" });
        addField(outputField);
    }

    @Override
    protected void buildDateField() {
        dateField = new DateField("As of");
        dateField.setValue(DateUtility.addDay(DateUtility.startOfMonth(), -1));
        addField(dateField);
    }

    @Override
    protected void process(InventoryLocation location) {
        switch(outputField.getValue()) {
            case 0 -> {
                pdf = new Report(getApplication(), location, dateField.getValue());
                locations.forEach(pdf::addLocation);
                pdf.execute();
            }
            case 1 -> {
                excel = new ExReport(getApplication(), location, dateField.getValue());
                locations.forEach(excel::addLocation);
                excel.execute();
            }
        }
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

    public boolean canPrint(StockHistory item) {
        return true;
    }

    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }

    private class Report extends com.storedobject.report.StockReport {

        public Report(Device device, InventoryLocation location, Date date) {
            super(device, location, date);
            printZeros(zerosField.getValue());
            printCostInLocalCurrency(localCurrencyField.getValue());
            InventoryItemType pn = pnField.getObject();
            if(pn != null) {
                setPartNumber(pn);
            }
            setCaption(getCaption());
            setItemFilter(StockReport.this::canPrint);
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

        public ExReport(Device device, InventoryLocation location, Date date) {
            super(device, location, date);
            if(device instanceof Application a) {
                setFeedback(s -> a.access(() -> message("Stock Report - " + s)));
            }
            printZeros(zerosField.getValue());
            printCostInLocalCurrency(localCurrencyField.getValue());
            setCaption(getCaption());
            InventoryItemType pn = pnField.getObject();
            if(pn != null) {
                setPartNumber(pn);
            }
            setItemFilter(StockReport.this::canPrint);
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