package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.sql.Date;
import java.util.function.Predicate;

@SuppressWarnings("resource")
public class StockReport extends PDFReport {

    private static final int LOC_WIDTH = 47;
    private String caption = "Stock Report";
    private ObjectIterator<? extends InventoryItemType> partNumbers;
    private boolean printZeros = false;
    private boolean separateCategories;
    private boolean costInLocalCurrency = true;
    private int count;
    private Predicate<StockHistory> itemFilter;
    private final Stock stock;

    public StockReport(Device device) {
        this(device, (InventoryLocation)null, null, null);
    }

    public StockReport(Device device, Date date) {
        this(device, (InventoryLocation)null, null, date);
    }

    public StockReport(Device device, InventoryStore store) {
        this(device, store, null, null);
    }

    public StockReport(Device device, InventoryStore store, Date date) {
        this(device, store, null, date);
    }

    public StockReport(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
        this(device, store == null ? null : store.getStoreBin(), partNumbers);
    }

    public StockReport(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers,
                       Date date) {
        this(device, store == null ? null : store.getStoreBin(), partNumbers, date);
    }

    public StockReport(Device device, InventoryLocation location) {
        this(device, location, null, null);
    }

    public StockReport(Device device, InventoryLocation location, Date date) {
        this(device, location, null, date);
    }

    public StockReport(Device device, InventoryLocation location,
                       ObjectIterator<? extends InventoryItemType> partNumbers) {
        this(device, location, partNumbers, null);
    }

    public StockReport(Device device, InventoryLocation location,
                       ObjectIterator<? extends InventoryItemType> partNumbers, Date date) {
        super(device);
        stock = new Stock(location, date);
        stock.setTransactionManager(getTransactionManager());
        this.partNumbers = partNumbers;
        separateCategories = partNumbers == null;
        setPageSizeIndex(2);
        setFontSize(8);
    }

    public void addStore(InventoryStore store) {
        stock.addStore(store);
    }

    public void addLocation(InventoryLocation location) {
        stock.addLocation(location);
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void printZeros(boolean printZeros) {
        this.printZeros = printZeros;
    }

    public void printCostInLocalCurrency(boolean costInLocalCurrency) {
        this.costInLocalCurrency = costInLocalCurrency;
    }

    public void setPartNumber(InventoryItemType partNumber) {
        if(partNumber != null) {
            setPartNumbers(ObjectIterator.create(partNumber));
            separateCategories = false;
        }
    }

    public void setPartNumbers(ObjectIterator<? extends InventoryItemType> partNumbers) {
        this.partNumbers = partNumbers;
    }

    public void separateCategories(boolean separateCategories) {
        this.separateCategories = separateCategories;
    }

    @Override
    public Object getTitleText() {
        PDFCell c = createCenteredCell(createTitleText(caption + " (" + stock.getLabel() + ") Date: " +
                DateUtility.formatDate(stock.getDate()), getFontSize() + 4));
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
        stock.close();
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
        SystemEntity se = getTransactionManager().getEntity();
        boolean needGap = count > 0;
        int catCount = 0, countCat = 0;
        if(partNumbers == null) {
            partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
            separateCategories = true;
        }
        String catName = categoryHeading;
        Money grandTotalCost = new Money(se.getCurrency()), totalCostCat = new Money(se.getCurrency()),
                totalCost, costInTransit, cost;
        Quantity totalQty, qtyInTransit, qty;
        Text stockLocation, qtyStr, sno, costStr;
        PDFTable table = table();
        if(!newPage && needGap) {
            table.setSkipFirstHeader(true);
        }
        ObjectIterator<StockHistory> stockList;
        String s, s1, error = null;
        Class<? extends InventoryItemType> type, currentType = null;
        boolean categoryHeaderPrinted = categoryHeading == null, headerPrinted = false, wide;
        for(InventoryItemType itemType: partNumbers) {
            setError("Printing P/N " + itemType.getPartNumber());
            stock.setPartNumber(itemType);
            if(separateCategories) {
                type = itemType.getClass();
                if(currentType == null || type != currentType) {
                    if(costInLocalCurrency) {
                        if(countCat > 0) {
                            table.addRowCell(createCell(catName + " (" + countCat + ") Stock Value: " + totalCostCat, true));
                        }
                        countCat = 0;
                        totalCostCat = totalCostCat.zero();
                    }
                    currentType = type;
                    categoryHeaderPrinted = false;
                }
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
            stockList = ObjectIterator.create(stock.getStocks());
            if(itemFilter != null) {
                stockList = stockList.filter(itemFilter);
            }
            for(StockHistory ii : stockList) {
                qty = ii.getQuantity();
                cost = ii.getCost();
                if(costInLocalCurrency) {
                    cost = cost.toLocal(stock.getDate(), se);
                    grandTotalCost = grandTotalCost.add(cost);
                    totalCostCat = totalCostCat.add(cost);
                }
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
                if(ii.inTransit()) {
                    stockLocation.append(PDFColor.RED);
                    s1 = " - In transit";
                    if((s.length() + s1.length()) <= LOC_WIDTH) {
                        stockLocation.append(s1);
                        s1 = "";
                    } else {
                        s1 += " ";
                    }
                    tLoc = ii.getPreviousLocation();
                    if(tLoc != null) {
                        s1 += "from " + tLoc.toDisplay();
                        stockLocation.newLine(true).append(trim(s1));
                        wide = true;
                    }
                    stockLocation.append(PDFColor.BLACK);
                    qtyInTransit = qtyInTransit.add(qty);
                    costInTransit = costInTransit.add(cost);
                }
                stockLocation.newLine(true);
                sno.append(itemType.getSerialNumberShortName() + " " + ii.getSerialNumber()).newLine(true);
                qtyStr.append(qty).newLine(true);
                costStr.append(cost).newLine(true);
                if(wide) {
                    sno.newLine(true);
                    qtyStr.newLine(true);
                    costStr.newLine(true);
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
                printHeading(catName = categoryHeading == null ? getItemTypeTitle(itemType) : categoryHeading, table);
                categoryHeaderPrinted = true;
                categoryHeading = null;
                ++catCount;
            }
            if(!headerPrinted) {
                headerPrinted = true;
            }
            ++count;
            ++countCat;
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
        if(separateCategories && costInLocalCurrency && countCat >  0 && count > countCat) {
            table.addRowCell(createCell(catName + " (" + countCat + ") Stock Value: " + totalCostCat, true));
        }
        add(table);
        if(costInLocalCurrency) {
            table.addBlankRow();
            table.addRowCell(createCell("Total (" + count + ") Stock Value: " + grandTotalCost, true));
            add(table);
        }
    }

    private static String trim(StoredObject so) {
        return trim(so.toDisplay());
    }

    private static String trim(String s) {
        if(StockReport.LOC_WIDTH <= 0) {
            return "";
        }
        int i = 0, len = s.length(), w = 0;
        char c;
        while(i < len) {
            c = s.charAt(i);
            w += c >= 'a' && c <= 'z' ? 4 : 5;
            if((w >> 2) > StockReport.LOC_WIDTH) {
                return s.substring(0, i - 3) + "...";
            }
            i++;
        }
        return s;
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
        table.setSplitRows(true);
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

    public void setItemFilter(Predicate<StockHistory> itemFilter) {
        this.itemFilter = itemFilter;
    }
}
