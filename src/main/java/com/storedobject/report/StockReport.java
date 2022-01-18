package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.util.function.Predicate;

public class StockReport extends PDFReport {

    private static final int LOC_WIDTH = 47;
    private String caption = "Stock Report";
    private final InventoryLocation location;
    private ObjectIterator<? extends InventoryItemType> partNumbers;
    private boolean printZeros = false;
    private boolean separateCategories;
    private int count;
    private Predicate<InventoryItem> itemFilter;

    public StockReport(Device device) {
        this(device, (InventoryLocation)null, null);
    }

    public StockReport(Device device, InventoryStore store) {
        this(device, store, null);
    }

    public StockReport(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
        this(device, store == null ? null : store.getStoreBin(), partNumbers);
    }

    public StockReport(Device device, InventoryLocation location) {
        this(device, location, null);
    }

    public StockReport(Device device, InventoryLocation location, ObjectIterator<? extends InventoryItemType> partNumbers) {
        super(device);
        this.location = location;
        this.partNumbers = partNumbers;
        separateCategories = partNumbers == null;
        setPageSizeIndex(2);
        setFontSize(8);
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

    @Override
    public Object getTitleText() {
        PDFCell c = createCenteredCell(createTitleText(caption + " (" +
                (location == null ? "All Stores" : (location instanceof InventoryStoreBin ? "Store" :
                        (location instanceof InventoryFitmentPosition ? "Assembly" : "Location")) + ": " + location) +
                ") Date: " +
                DateUtility.formatDate(DateUtility.today()), getFontSize() + 4));
        c.setGrayFill(0.9f);
        return c;
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return StringUtility.makeLabel(itemType.getClass(), true);
    }

    private void printHeading(String heading, PDFTable table) {
        table.addBlankRow();
        PDFCell c = createCenteredCell(createTitleText(heading, getFontSize() + 2));
        c.setGrayFill(0.9f);
        table.addRowCell(c);
    }

    @Override
    public void generateContent() throws Exception {
        count = 0;
        printStock(partNumbers);
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
        boolean needGap = count > 0;
        int catCount = 0;
        if(partNumbers == null) {
            partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
            separateCategories = true;
        }
        Money totalCost, costInTransit, cost;
        Quantity totalQty, qtyInTransit, qty;
        Text stockLocation, qtyStr, sno, costStr;
        PDFTable table = table();
        if(!newPage && needGap) {
            table.setSkipFirstHeader(true);
        }
        ObjectIterator<? extends InventoryLocation> locations;
        ObjectIterator<InventoryItem> stockList;
        String s, s1, error = null;
        Class<? extends InventoryItemType> type, currentType = null;
        boolean categoryHeaderPrinted = categoryHeading == null, headerPrinted = false, wide;
        for(InventoryItemType itemType: partNumbers) {
            if(separateCategories) {
                type = itemType.getClass();
                if(currentType == null || type != currentType) {
                    currentType = type;
                    categoryHeaderPrinted = false;
                }
            }
            if(this.location == null) {
                locations = StoredObject.list(InventoryStoreBin.class);
            } else {
                locations = ObjectIterator.create(this.location);
            }
            totalQty = itemType.getUnitOfMeasurement();
            qtyInTransit = totalQty;
            qty = totalQty;
            totalCost = new Money();
            costInTransit = new Money();
            stockLocation = new Text();
            qtyStr = new Text();
            sno = new Text();
            costStr = new Text();
            InventoryLocation tLoc;
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
                    qty = ii.getQuantity();
                    cost = ii.getCost();
                    try {
                        totalQty = totalQty.add(qty);
                    } catch(Throwable e) {
                        error = e.getMessage();
                    }
                    try {
                        totalCost = totalCost.add(cost);
                    } catch(Throwable e) {
                        error = e.getMessage();
                    }
                    stockLocation.append(s = trim(ii.getLocation()));
                    wide = false;
                    if(ii.getInTransit()) {
                        s1 = " - In transit";
                        tLoc = ii.getPreviousLocation();
                        if(tLoc != null) {
                            s1 += " from " + tLoc.toDisplay();
                        }
                        if(s.length() <= (LOC_WIDTH >> 1)) {
                            s = trim(s1, LOC_WIDTH - s.length());
                        } else {
                            wide = true;
                            s = trim(s1, LOC_WIDTH);
                            stockLocation.newLine(true);
                        }
                        stockLocation.append(PDFColor.RED).append(s).append(PDFColor.BLACK);
                        qtyInTransit = qtyInTransit.add(qty);
                        costInTransit = costInTransit.add(cost);
                    }
                    stockLocation.newLine(true);
                    s = ii.getSerialNumber();
                    sno.append(s == null ? "" : s).newLine(true);
                    qtyStr.append(qty).newLine(true);
                    costStr.append(cost).newLine(true);
                    if(wide) {
                        sno.newLine(true);
                        qtyStr.newLine(true);
                        costStr.newLine(true);
                    }
                }
            }
            if(error == null && (totalQty.isZero() || totalQty.isGreaterThan(qty))) {
                if(!totalQty.isZero()) {
                    stockLocation.newLine(true);
                    qtyStr.newLine(true);
                    costStr.newLine(true);
                }
                stockLocation.append("Total");
                qtyStr.append(totalQty);
                costStr.append(totalCost);
            }
            if(!qtyInTransit.isZero()) {
                stockLocation.newLine(true);
                qtyStr.newLine(true);
                costStr.newLine(true);
                stockLocation.append(PDFColor.RED).append("In Transit").append(PDFColor.BLACK);
                qtyStr.append(qtyInTransit);
                costStr.append(costInTransit);
            }
            if(error == null) {
                if(totalQty.isZero()) {
                    if(!printZeros) {
                        continue;
                    }
                }
            }
            if(!categoryHeaderPrinted) {
                printHeading(categoryHeading == null ? getItemTypeTitle(itemType) : categoryHeading, table);
                categoryHeaderPrinted = true;
                categoryHeading = null;
                ++catCount;
            }
            if(!headerPrinted) {
                headerPrinted = true;
            }
            ++count;
            table.addCell(createCell((separateCategories ? (catCount + "/") : "") + count));
            table.addCell(createCell(itemType.getName()));
            table.addCell(createCell(itemType.getPartNumber()));
            table.addCell(createCell(sno));
            table.addCell(createCell(stockLocation));
            if(error != null) {
                qtyStr.newLine(true).append(error, PDFColor.RED);
                costStr.newLine(true).append("-", PDFColor.RED);
                error = null;
            }
            table.addCell(createCell(qtyStr, true));
            table.addCell(createCell(costStr, true));
            if(table.getNumberOfRows() > 80) {
                if(newPage) {
                    newPage();
                    newPage = false;
                    needGap = false;
                } else {
                    if(needGap) {
                        addGap(5);
                        needGap = false;
                    }
                }
                add(table);
            }
        }
        if(newPage) {
            newPage();
        } else {
            if(needGap) {
                addGap(5);
            }
        }
        add(table);
    }

    private static String trim(StoredObject so) {
        return trim(so.toDisplay(), LOC_WIDTH);
    }

    private static String trim(String s, int width) {
        if(width <= 0) {
            return "";
        }
        if(s.length() <= width) {
            return s;
        }
        return s.substring(0, width - 3) + "...";
    }

    private PDFTable table() {
        PDFTable table = createTable(7, 25, 20, 15, 30, 15, 15);
        table.addCell(tCell("No."));
        table.addCell(tCell("Item"));
        table.addCell(tCell("Part Number"));
        table.addCell(tCell("Serial/Batch Number"));
        table.addCell(tCell("Location"));
        table.addCell(tCellR("Quantity"));
        table.addCell(tCellR("Value"));
        table.setHeaderRows(1);
        return table;
    }

    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return true;
    }

    private PDFCell tCell(String s) {
        PDFCell c = createCell(createTitleText(s, getFontSize()));
        c.setGrayFill(0.9f);
        return c;
    }

    private PDFCell tCellR(String s) {
        PDFCell c = createCell(createTitleText(s, getFontSize()), true);
        c.setGrayFill(0.9f);
        return c;
    }

    public void setItemFilter(Predicate<InventoryItem> itemFilter) {
        this.itemFilter = itemFilter;
    }
}
