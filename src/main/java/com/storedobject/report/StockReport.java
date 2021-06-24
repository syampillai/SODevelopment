package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectIterator;
import com.storedobject.pdf.PDFReport;

public class StockReport extends PDFReport {

    public StockReport(Device device) {
        this(device, null, null);
    }

    public StockReport(Device device, InventoryStore store) {
        this(device, store, null);
    }

    public StockReport(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
        super(device);
    }

    public void setCaption(String caption) {
    }

    public void printZeros(boolean printZeros) {
    }

    public void setPartNumber(InventoryItemType partNumber) {
    }

    public void setPartNumbers(ObjectIterator<? extends InventoryItemType> partNumbers) {
    }

    public void separateCategories(boolean separateCategories) {
    }

    public void setServiceabilityStatus(int serviceabilityStatus) {
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return null;
    }

    @Override
    public void generateContent() throws Exception {
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
    }

    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }
}
