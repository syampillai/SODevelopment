package com.storedobject.report;

import com.storedobject.common.StyledString;
import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.function.Predicate;

public class StockReportExcel extends ExcelReport {

    private String caption = "Stock Report";
    private final InventoryLocation location;
    private ObjectIterator<? extends InventoryItemType> partNumbers;
    private boolean printZeros = false;
    private boolean separateCategories;
    private int maxRow = -1;
    private Predicate<InventoryItem> itemFilter;

    public StockReportExcel(Device device) {
        this(device, (InventoryLocation)null, null);
    }

    public StockReportExcel(Device device, InventoryStore store) {
        this(device, store, null);
    }

    public StockReportExcel(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
        this(device, store == null ? null : store.getStoreBin(), partNumbers);
    }

    public StockReportExcel(Device device, InventoryLocation location) {
        this(device, location, null);
    }

    public StockReportExcel(Device device, InventoryLocation location, ObjectIterator<? extends InventoryItemType> partNumbers) {
        super(device);
        this.location = location;
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

    public String getReportTitle() {
        return caption + " (" +
                (location == null ? "All Stores" : (location instanceof InventoryStoreBin ? "Store" :
                        (location instanceof InventoryFitmentPosition ? "Assembly" : "Location")) + ": " + location) +
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
    public void generateContent() throws Exception {
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
        Quantity total, inTransit, q;
        StyledString stockLocation, sno;
        ArrayList<Quantity> quantity = new ArrayList<>();
        ObjectIterator<? extends InventoryLocation> locations;
        ObjectIterator<InventoryItem> stockList;
        String s, error = null;
        Class<? extends InventoryItemType> type, currentType = null;
        boolean categoryHeaderPrinted = categoryHeading == null, headerPrinted = false;
        for(InventoryItemType itemType: partNumbers) {
            if(separateCategories) {
                type = itemType.getClass();
                if(currentType == null || type != currentType) {
                    currentType = type;
                    categoryHeaderPrinted = false;
                }
            }
            if(this.location == null) {
                locations = StoredObject.list(InventoryStoreBin.class, true);
            } else {
                locations = ObjectIterator.create(this.location);
            }
            total = itemType.getUnitOfMeasurement().zero();
            inTransit = total.zero();
            q = itemType.getUnitOfMeasurement().zero();
            stockLocation = new StyledString();
            quantity.clear();
            sno = new StyledString();
            for(InventoryLocation loc: locations) {
                if(loc instanceof InventoryStoreBin) {
                    stockList = InventoryItem.listStock(itemType, ((InventoryStoreBin) loc).getStore());
                } else {
                    stockList = InventoryItem.listStock(itemType, loc);
                }
                if(itemFilter != null) {
                    stockList = stockList.filter(itemFilter);
                }
                for(InventoryItem ii : stockList) {
                    q = ii.getQuantity();
                    try {
                        total = total.add(q);
                    } catch(Throwable e) {
                        error = e.getMessage();
                    }
                    if(!ii.getInTransit()) {
                        stockLocation.append(ii.getLocation());
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
            if(error == null) {
                if(total.isZero()) {
                    if(!printZeros) {
                        continue;
                    }
                }
            }
            if(!categoryHeaderPrinted) {
                if(categoryHeading == null) {
                    printHeading(getItemTypeTitle(itemType));
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
            tcell(itemType.getName());
            tcell(itemType.getPartNumber());
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

    @SuppressWarnings("unused")
    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }

    public void setItemFilter(Predicate<InventoryItem> itemFilter) {
        this.itemFilter = itemFilter;
    }
}
