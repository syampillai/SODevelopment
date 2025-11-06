package com.storedobject.report;

import com.storedobject.common.StyledString;
import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("resource")
public class StockReportExcel extends ExcelReport {

    private String caption = "Stock Report";
    private ObjectIterator<? extends InventoryItemType> partNumbers;
    private boolean printZeros = false;
    private boolean separateCategories;
    private boolean costInLocalCurrency = true;
    SystemEntity se;
    private int maxRow = -1;
    private Predicate<StockHistory> itemFilter;
    private final Stock stock;
    private int itemCount = 0;

    public StockReportExcel(Device device) {
        this(device, (InventoryLocation)null, null, null);
    }

    public StockReportExcel(Device device, Date date) {
        this(device, (InventoryLocation)null, null, date);
    }

    public StockReportExcel(Device device, InventoryStore store) {
        this(device, store, null, null);
    }

    public StockReportExcel(Device device, InventoryStore store, Date date) {
        this(device, store, null, date);
    }

    public StockReportExcel(Device device, InventoryStore store,
                            ObjectIterator<? extends InventoryItemType> partNumbers) {
        this(device, store == null ? null : store.getStoreBin(), partNumbers);
    }

    public StockReportExcel(Device device, InventoryStore store,
                            ObjectIterator<? extends InventoryItemType> partNumbers, Date date) {
        this(device, store == null ? null : store.getStoreBin(), partNumbers, date);
    }

    public StockReportExcel(Device device, InventoryLocation location) {
        this(device, location, null, null);
    }

    public StockReportExcel(Device device, InventoryLocation location, Date date) {
        this(device, location, null, date);
    }

    public StockReportExcel(Device device, InventoryLocation location,
                            ObjectIterator<? extends InventoryItemType> partNumbers) {
        this(device, location, partNumbers, null);
    }

    public StockReportExcel(Device device, InventoryLocation location,
                            ObjectIterator<? extends InventoryItemType> partNumbers, Date date) {
        super(device);
        this.stock = new Stock(location, date);
        this.stock.setTransactionManager(getTransactionManager());
        this.partNumbers = partNumbers;
        separateCategories = partNumbers == null;
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

    public String getReportTitle() {
        return caption + " (" + stock.getLabel() + ") Date: " + DateUtility.formatDate(stock.getDate());
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
        stock.close();
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
        se = getTransactionManager().getEntity();
        printTitle();
        if(partNumbers == null) {
            partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
            separateCategories = true;
        }
        Quantity totalQuantity, inTransitQuantity, q;
        Money totalCost, inTransitCost, c = new Money(se.getCurrency());
        totalCost = inTransitCost = c;
        StyledString stockLocation, sno;
        List<Quantity> quantities = new ArrayList<>();
        List<Money> costs = new ArrayList<>();
        ObjectIterator<StockHistory> stockList;
        String error = null;
        Class<? extends InventoryItemType> type, currentType = null;
        boolean categoryHeaderPrinted = categoryHeading == null, headerPrinted = false;
        for(InventoryItemType itemType: partNumbers) {
            stock.setPartNumber(itemType);
            if(separateCategories) {
                type = itemType.getClass();
                if(currentType == null || type != currentType) {
                    currentType = type;
                    categoryHeaderPrinted = false;
                }
            }
            totalQuantity = itemType.getUnitOfMeasurement().zero();
            inTransitQuantity = totalQuantity.zero();
            q = itemType.getUnitOfMeasurement().zero();
            totalCost = totalCost.zero();
            inTransitCost = inTransitCost.zero();
            c = c.zero();
            stockLocation = new StyledString();
            quantities.clear();
            costs.clear();
            sno = new StyledString();
            stockList = ObjectIterator.create(stock.getStocks());
            if(itemFilter != null) {
                stockList = stockList.filter(itemFilter);
            }
            for(StockHistory ii : stockList) {
                q = ii.getQuantity();
                if(costInLocalCurrency) {
                    c = ii.getCost().toLocal(stock.getDate(), se);
                }
                try {
                    totalQuantity = totalQuantity.add(q);
                    if(costInLocalCurrency) {
                        totalCost = totalCost.add(c);
                    }
                } catch(Throwable e) {
                    error = e.getMessage();
                }
                if(!ii.inTransit()) {
                    stockLocation.append(ii.getLocation());
                }
                if(ii.inTransit()) {
                    stockLocation.append(" (In Transit)");
                    inTransitQuantity = inTransitQuantity.add(q);
                    if(costInLocalCurrency) {
                        inTransitCost = inTransitCost.add(c);
                    }
                }
                stockLocation.newLine();
                sno.append(itemType.getSerialNumberShortName() + " "  + ii.getSerialNumber()).newLine();
                quantities.add(q);
                if(costInLocalCurrency) {
                    costs.add(c);
                }
            }
            if(error == null && (totalQuantity.isZero() || totalQuantity.isGreaterThan(q))) {
                if(!totalQuantity.isZero()) {
                    stockLocation.newLine();
                }
                stockLocation.append("Total");
                quantities.add(totalQuantity);
                costs.add(totalCost);
            }
            if(!inTransitQuantity.isZero()) {
                stockLocation.newLine();
                stockLocation.append("In Transit");
                quantities.add(inTransitQuantity);
                costs.add(inTransitCost);
            }
            if(error == null) {
                if(totalQuantity.isZero()) {
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
                setCellValues("Item", "Part Number", "Serial/Batch Number", "Location", "Quantity", "Unit", costInLocalCurrency ? "Cost" : null);
                getNextRow();
                headerPrinted = true;
            }
            setCellValues(itemType.getName(), itemType.getPartNumber());
            maxRow = getRowIndex();
            if(error != null) {
                stockLocation.append(" (").append(error).append(')');
                error = null;
            }
            createCell(cellValues(sno), cellValues(stockLocation), quantities, costs);
            goToCell(0, maxRow + 1);
        }
    }

    private String[] cellValues(StyledString s) {
        String string = s.toString();
        while(string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        if(string.indexOf('\n') < 0) {
            return new String[] { string };
        }
        return string.split("\\n");
    }

    private void createCell(String[] sno, String[] locations, List<Quantity> quantities, List<Money> costs) {
        ++itemCount;
        int colIndex = getCellIndex();
        Quantity q;
        int n = Math.max(sno.length, Math.max(quantities.size(), locations.length));
        for(int i = 0; i < n; i++) {
            q = i < quantities.size() ? quantities.get(i) : null;
            setCellValues(i < sno.length ? sno[i] : null, i < locations.length ? locations[i] : null,
                    q == null ? null : q.getValue().doubleValue(), q == null ? null : q.getUnit().getUnit(),
                    costInLocalCurrency && q != null ? costs.get(i) : null);
            if(getRowIndex() > maxRow) {
                maxRow = getRowIndex();
            }
            if((getRowIndex() + 1) % 100 == 0) {
                feedback("Processed " + (getRowIndex() + 1)  + " rows, " + itemCount + " items");
            }
            if(i != (n - 1)) goToCell(colIndex, getRowIndex() + 1);
        }
    }

    @SuppressWarnings("unused")
    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }

    public void setItemFilter(Predicate<StockHistory> itemFilter) {
        this.itemFilter = itemFilter;
    }
}
