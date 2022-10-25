package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.office.CSVReport;
import com.storedobject.office.ExcelReport;
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
        new Report1(getApplication(), dateField.getValue()).execute();
        return true;
    }

    public static class Report extends CSVReport {

        private int i = 1;

        private final Date date;

        Currency baseCurrency = Currency.getInstance("INR");

        public Report(Device device, Date date) {
            super(device,26);
            this.date = date;
        }

        @Override
        public void generateContent() throws Exception {

            final int INITIAL_INVENTORY_VALUE = 12; // Can be found for api docs
            printHeadings();
                for (InventoryItem item : StoredObject.list(InventoryItem.class, true)) {
                    if (item.getStore() == null
                            || item.getLocation().getType() == INITIAL_INVENTORY_VALUE
                            || item.getQuantity().isZero()) {
                        continue;
                    }
                    if (item.getGRN() == null || item.getGRN().getDate().before(date)) {
                        printItem(item);
                    }
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
            InventoryGRN grn = item.getGRN();
            setValues(i++, // 0
                    item.getPartNumber() == null ? " " : item.getPartNumber().getPartNumber(), // 1
                    item.getPartNumber() == null ? " " : item.getPartNumber().getName(), // 2
                    grn == null ? " " : grn.getReference(), // 3
                    grn == null ? " " : dateToString(grn.getDate()), // 4
                    getNumberType(), // 5
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
                    getCategory(), // 20
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

        private String getNumberType() {
            return "Number Type";
        }

        private String getCategory() {
            return "Category";
        }

        private String dateToString(Date date) {
            return DateUtility.format(date);
        }
    }

    public static class Report1 extends ExcelReport {

        private int i = 1;
        private int row;

        private final Date date;

        Currency baseCurrency = Currency.getInstance("INR");

        public Report1(Device device, Date date) {
            super(device);
            this.date = date;
            row = 1;
        }

        @Override
        public void generateContent() throws Exception {

            final int INITIAL_INVENTORY_VALUE = 12; // Can be found for api docs
            printHeadings();
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
        }

        private void printHeadings() {
            getCell("A1").setCellValue("SL.No");
            getCell("B1").setCellValue("Part Number");
            getCell("C1").setCellValue("Description");
            getCell("D1").setCellValue("Receipt No.");
            getCell("E1").setCellValue("Receipt Date");
            getCell("F1").setCellValue("Number Type");
            getCell("G1").setCellValue("Serial No./ Batch No./ Lot Number");
            getCell("H1").setCellValue("Store");
            getCell("I1").setCellValue("Sub-Store");
            getCell("J1").setCellValue("Location");
            getCell("K1").setCellValue("Unit");
            getCell("L1").setCellValue("Rate");
            getCell("M1").setCellValue("Inv. Curr.");
            getCell("N1").setCellValue("Base Curr.");
            getCell("O1").setCellValue("Inv. Bal. Qty.");
            getCell("P1").setCellValue("Amount");
            getCell("Q1").setCellValue("ExpiryDate");
            getCell("R1").setCellValue("Source");
            getCell("S1").setCellValue("Supplier Inv. No.");
            getCell("T1").setCellValue("Supplier Inv. Date");
            getCell("U1").setCellValue("Category Name");
            getCell("V1").setCellValue("IPC Reference");
            getCell("W1").setCellValue("Release Note No.");
            getCell("X1").setCellValue("Release Note Date");
            getCell("Y1").setCellValue("Order No.");
            getCell("Z1").setCellValue("Order Date");
        }

        private void printItem(InventoryItem item) {
            int cell = 0;
            InventoryGRN grn = item.getGRN();
            getCell(cell++, row).setCellValue(i++);
            getCell(cell++, row)
                    .setCellValue(item.getPartNumber() == null ? " " : item.getPartNumber().getPartNumber());
            getCell(cell++, row)
                    .setCellValue(item.getPartNumber() == null ? " " : item.getPartNumber().getName());
            getCell(cell++, row).setCellValue(grn == null ? " " : grn.getReference());
            getCell(cell++, row).setCellValue(grn == null ? " " : dateToString(grn.getDate()));
            getCell(cell++, row).setCellValue("");
            getCell(cell++, row)
                    .setCellValue(item.getSerialNumber() == null ? " " : item.getSerialNumber());
            getCell(cell++, row).setCellValue(item.getStore() == null ? " " : item.getStore().toString());
            getCell(cell++, row)
                    .setCellValue("");
            getCell(cell++, row)
                    .setCellValue(item.getLocation() == null ? " " : item.getLocation().getName());
            getCell(cell++, row)
                    .setCellValue(item.getPartNumber().getUnitOfMeasurement().getUnit().toString());
            getCell(cell++, row).setCellValue(item.getUnitCost().getCost().getValue().doubleValue());
            getCell(cell++, row)
                    .setCellValue(item.getCost() == null ? " " : item.getCost().getCurrency().getSymbol());
            getCell(cell++, row).setCellValue(baseCurrency.toString());
            getCell(cell++, row).setCellValue(item.getQuantity().getValue().doubleValue());

            ExchangeRate currencyRate = ExchangeRate.get(baseCurrency, item.getCost().getCurrency());
            if (currencyRate != null) {
                getCell(cell++, row)
                        .setCellValue(
                                item.getCost() == null
                                        ? " "
                                        : (item.getCost().getValue().doubleValue()
                                        * currencyRate.getRate().getValue().doubleValue())
                                        + "");
            } else {
                getCell(cell++, row)
                        .setCellValue(
                                item.getCost() == null ? " " : (item.getCost().getValue().doubleValue()) + "");
            }

            getCell(cell++, row)
                    .setCellValue("");
            getCell(cell++, row).setCellValue(grn == null ? " " : grn.getSupplier().getName());
            getCell(cell++, row).setCellValue(grn == null ? " " : grn.getInvoiceNumber());
            getCell(cell++, row).setCellValue(grn == null ? " " : dateToString(grn.getInvoiceDate()));
            getCell(cell++, row).setCellValue("");
            getCell(cell++, row)
                    .setCellValue("");
            getCell(cell++, row)
                    .setCellValue("");
            getCell(cell++, row)
                    .setCellValue("");
            StringBuilder orderString = new StringBuilder();
            if (item.getRO() == null) {
                orderString.append(item.getPO() == null ? " " : item.getPO().getReference());
            } else {
                orderString.append(item.getRO().getReference());
            }
            getCell(cell++, row).setCellValue(orderString.toString());
            getCell(cell, row)
                    .setCellValue(
                            item.getRO() == null
                                    ? item.getPO() == null ? " " : dateToString(item.getPO().getDate())
                                    : dateToString(item.getRO().getDate()));
        }

        private String dateToString(Date date) {

            return DateUtility.format(date);
        }
    }

}
