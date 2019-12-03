package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectIterator;
import com.storedobject.office.ExcelReport;

public class StockReportExcel extends ExcelReport {

    public StockReportExcel(Device device) {
        this(device, null, null);
    }

    public StockReportExcel(Device device, InventoryStore store) {
        this(device, store, null);
    }

    public StockReportExcel(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
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

    public String getReportTitle() {
        return null;
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return null;
    }

    public void printHeading(String heading) {
    }

    @Override
    public void generateContent()throws Exception {
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers) {
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
    }

    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }
}
