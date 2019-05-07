package com.storedobject.report;

import com.storedobject.common.StyledString;
import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;

public class StockReportExcel extends ExcelReport {

    private String caption = "Stock Report";
    private InventoryStore store;
    private ObjectIterator<? extends InventoryItemType> partNumbers;
    private boolean printZeros = false;
    private boolean separateCategories;
    private int serviceabilityStatus = 0;
    private int maxRow = -1;

    public StockReportExcel(Device device) {
        this(device, null, null);
    }

    public StockReportExcel(Device device, InventoryStore store) {
        this(device, store, null);
    }

    public StockReportExcel(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
        super(device);
        this.store = store;
        this.partNumbers = partNumbers;
        separateCategories = partNumbers == null;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void printZeros(boolean printZeros) {
        this.printZeros = printZeros;
    }

    public void setPartNumber(InventoryItemType partNumber) {
        setPartNumbers(ObjectIterator.create(partNumber));
        separateCategories = false;
    }

    public void setPartNumbers(ObjectIterator<? extends InventoryItemType> partNumbers) {
        this.partNumbers = partNumbers;
    }

    public void separateCategories(boolean separateCategories) {
        this.separateCategories = separateCategories;
    }

    public void setServiceabilityStatus(int serviceabilityStatus) {
        this.serviceabilityStatus = serviceabilityStatus;
    }

    public String getReportTitle() {
        return caption + " (" + (store == null ? "All Stores" : "Store: " + store.getName()) + ", Serviceability: " +
                InventoryItem.getServiceabilityStatusValue(serviceabilityStatus) +
                ") Date: " + DateUtility.formatDate(DateUtility.today());
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return StringUtility.makeLabel(itemType.getClass(), true);
    }

    public void printHeading(String heading) {
        getCell().setCellValue(heading);
        getNextRow();
    }

    @Override
    public void generateContent()throws Exception {
        printStock(partNumbers);
    }

    private void printTitle() {
        if(getRowIndex() == 0 && getCellIndex() == 0) {
            getCell().setCellValue(getReportTitle());
            getNextRow();
        }
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers) {
        printTitle();
        printStock(partNumbers, null);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
        printTitle();
        if(partNumbers == null) {
            partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
            separateCategories = true;
        }
        InventoryStockLocation location;
        Quantity total, inTransit, q;
        StyledString stockLocation, sno;
        ArrayList<Quantity> quantity = new ArrayList<>();
        ObjectIterator<InventoryStore> stores;
        String storeName = null, s, error = null;
        Class<? extends InventoryItemType> type, currentType = null;
        boolean categoryHeaderPrinted = categoryHeading == null, headerPrinted = false;
        for(InventoryItemType item: partNumbers) {
            if(separateCategories) {
                type = item.getClass();
                if(currentType == null || type != currentType) {
                    currentType = type;
                    categoryHeaderPrinted = false;
                }
            }
            if(this.store == null) {
                stores = StoredObject.list(InventoryStore.class, true);
            } else {
                stores = ObjectIterator.create(this.store);
            }
            for(InventoryStore store: stores) {
                if(this.store == null) {
                    storeName = store.getName();
                }
                total = item.getUnitOfMeasurement().zero();
                inTransit = total.zero();
                q = item.getUnitOfMeasurement().zero();
                stockLocation = new StyledString();
                quantity.clear();
                sno = new StyledString();
                for(InventoryItem ii: InventoryItem.listStock(item, store.getId(), serviceabilityStatus)) {
                    q = ii.getQuantity();
                    if(!q.isZero()) {
                        try {
                            total = total.add(q);
                        } catch(Throwable e) {
                            error = e.getMessage();
                        }
                        if(!ii.getInTransit()) {
                            location = ii.getStockLocation();
                            if(location != null) {
                                s = ii.getStockLocation().getName();
                                if(storeName != null) {
                                    if(!s.contains(storeName)) {
                                        s += " (" + storeName + ")";
                                    }
                                    storeName = null;
                                }
                                stockLocation.append(s);
                            }
                        }
                        if(ii.getInTransit()) {
                            stockLocation.append(" (In Transit)");
                            inTransit = inTransit.add(q);
                        }
                        stockLocation.newLine();
                        s = ii.getSerialNumber();
                        sno.append(s == null ? "" : s).newLine();
                        quantity.add(q);
                    }
                }
                if(error == null && (total.isZero() || total.isGreaterThan(q))) {
                    if(!total.isZero()) {
                        stockLocation.newLine();
                    }
                    stockLocation.append("Total");
                    quantity.add(total);
                }
                if(!inTransit.isZero()) {
                    stockLocation.newLine();
                    stockLocation.append("In Transit");
                    quantity.add(inTransit);
                }
                if(total.isZero() && !printZeros && error == null) {
                    continue;
                }
                if(!categoryHeaderPrinted) {
                    if(categoryHeading == null) {
                        printHeading(getItemTypeTitle(item));
                    } else {
                        printHeading(categoryHeading);
                    }
                    headerPrinted = false;
                    categoryHeaderPrinted = true;
                    categoryHeading = null;
                }
                if(!headerPrinted) {
                    tcell("Item");
                    tcell("Part Number");
                    tcell("Serial/Batch Number");
                    tcell("Location");
                    tcellR();
                    tcell("Unit");
                    getNextRow();
                    headerPrinted = true;
                }
                tcell(item.getName().getName());
                tcell(item.getPartNumber());
                maxRow = getRowIndex();
                createCell(sno);
                if(error != null) {
                    stockLocation.append(" (").append(error).append(')');
                    error = null;
                }
                createCell(stockLocation);
                createCell(quantity);
                goToCell(0, maxRow + 1);
            }
        }
    }

    private void createCell(StyledString s) {
        String string = s.toString();
        while(string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        if(string.indexOf('\n') < 0) {
            tcell(string);
            return;
        }
        int c = getCellIndex() + 1, r = getRowIndex();
        String[] strings = string.split("\\n");
        for (String item : strings) {
            getCell().setCellValue(item);
            if(getRowIndex() > maxRow) {
                maxRow = getRowIndex();
            }
            goToCell(getCellIndex(), getRowIndex() + 1);
        }
        goToCell(c, r);
    }

    private void createCell(ArrayList<Quantity> quantities) {
        int c = getCellIndex();
        quantities.forEach(q -> {
            getCell().setCellValue(q.getValue().doubleValue());
            getNextCell().setCellValue(q.getUnit().getUnit());
            if(getRowIndex() > maxRow) {
                maxRow = getRowIndex();
            }
            goToCell(c, getRowIndex() + 1);
        });
    }

    private void tcell(String s) {
        getCell().setCellValue(s);
        getNextCell();
    }

    private void tcellR() {
        Cell c = getCell();
        c.setCellValue("Quantity");
        getNextCell();
    }
}
