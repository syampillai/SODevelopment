package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.office.CSVReport;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

import java.io.IOException;
import java.util.Currency;
import java.util.Date;

public class Test extends DataForm {

    DateField dateField;

    public Test() {
        super("Inventory Stock Report");
    }

    @Override
    protected void buildFields() {
        add(dateField = new DateField("Date", DateUtility.today()));
        super.buildFields();
    }

    @Override
    protected boolean process() {
        close();
        //new Report(getApplication(), dateField.getValue()).execute();
        new Temp(getApplication()).execute();
        return true;
    }

    private static class Temp extends CSVReport {
        Temp(Device device) {
            super(device,3);
        }

        @Override
        public void generateContent() throws Exception {
            System.err.println("Here1");
            setValues("One", "Two", "Three");
            System.err.println("Here2");
            writeRow();
            System.err.println("Here3");
            setValues(1, 2, 3);
            writeRow();
            setValues(10, 20, 30);
            writeRow();
        }
    }

    public class Report extends CSVReport {

        private int i = 1;
        private int row;

        private final Date date;

        Currency baseCurrency = Currency.getInstance("INR");

        public Report(Device device, Date date) {
            super(device,26);
            this.date = date;
            row = 1;
        }

        @Override
        public void generateContent() throws Exception {

            final int INITIAL_INVENTORY_VALUE = 12; // Can be found for api docs
            printHeadings();
            try {
                for (InventoryItem item : StoredObject.list(InventoryItem.class, true)) {
                    if (item.getStore() == null
                            || item.getLocation().getType() == INITIAL_INVENTORY_VALUE
                            || item.getQuantity().isZero()) {
                        continue;
                    }
                    if (item.getGRN() == null || item.getGRN().getDate().before(date)) {
                        printItem(item);
                        row++;
                    }
                }
            } catch (Throwable t) {
                log(t);
                error("Unable to download the report. Please contact Technical Support");
            }
        }

        private void printHeadings() throws IOException {
            setValues("SL.No", "Part Number", "Description", "Receipt No.", "Receipt Date", "Number Type",
                    "Serial No./ Batch No./ Lot Number", "Store", "Sub-Store", "Location", "Unit", "Rate",
                    "Inv. Curr.", "Base Curr.", "Inv. Bal. Qty.", "Amount", "Expiry Date", "Source",
                    "Supplier Inv. No.", "Supplier Inv. Date", "Category Name", "IPC Reference",
                    "Release Note No.", "Release Note Date", "Order No.", "Order Date");
            writeRow();
        }

        private void printItem(InventoryItem item) throws IOException {
            int cell = 0;
            InventoryGRN grn = item.getGRN();
            setValues(i++, // 0
                    item.getPartNumber() == null ? " " : item.getPartNumber().getPartNumber(), // 1
                    item.getPartNumber() == null ? " " : item.getPartNumber().getName(), // 2
                    grn == null ? " " : grn.getReference(), // 3
                    grn == null ? " " : dateToString(grn.getDate()), // 4
                    getNumberType(item.getPartNumber()), // 5
                    item.getSerialNumber() == null ? " " : item.getSerialNumber(), // 6
                    item.getStore() == null ? " " : item.getStore(), // 7
                    item.getLocationDisplay(), // 8
                    item.getLocation() == null ? " " : item.getLocation().getName(), // 9
                    item.getPartNumber().getUnitOfMeasurement(), // 10
                    item.getUnitCost().getCost().getValue(), // 11
                    item.getCost() == null ? " " : item.getCost().getCurrency().getSymbol(), // 12
                    baseCurrency, // 13
                    item.getQuantity().getValue()); // 15

            ExchangeRate currencyRate = ExchangeRate.get(baseCurrency, item.getCost().getCurrency());
            if (currencyRate != null) {
                setValue(15, item.getCost() == null ? ""
                                        : item.getCost().multiply(currencyRate.getRate()).getValue());
            } else {
                setValue(15, item.getCost() == null ? "" : item.getCost().getValue());
            }
            setValuesFrom(16,
                    "", // 16
                    grn == null ? "" : grn.getSupplier().getName(), // 17
                    grn == null ? "" : grn.getInvoiceNumber(), // 18
                    grn == null ? "" : dateToString(grn.getInvoiceDate()), // 19
                    getCategory(item), // 20
                    "", "", ""); // 21, 22, 23
            StringBuilder orderString = new StringBuilder();
            if (item.getRO() == null) {
                orderString.append(item.getPO() == null ? " " : item.getPO().getReference());
            } else {
                orderString.append(item.getRO().getReference());
            }
            setValuesFrom(24,
                    orderString, // 24
                    item.getRO() == null ? item.getPO() == null ? " " : dateToString(item.getPO().getDate())
                                    : dateToString(item.getRO().getDate())); // 25
            writeRow();
        }

        private String getNumberType(InventoryItemType type) {
            return "Number Type";
        }

        private String getCategory(InventoryItem item) {
            return "Category";
        }

        private String dateToString(Date date) {
            return DateUtility.format(date);
        }
    }
}
