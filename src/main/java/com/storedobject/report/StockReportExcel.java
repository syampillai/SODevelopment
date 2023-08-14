package com.storedobject.report;

import com.storedobject.common.StyledString;
import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import org.apache.poi.ss.usermodel.Cell;

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
    private Predicate<InventoryItem> itemFilter;
    private final Stock stock;

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
        ObjectIterator<InventoryItem> stockList;
        String s, error = null;
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
            for(InventoryItem ii : stockList) {
                q = ii.getQuantity();
                if(costInLocalCurrency) {
                    c = ii.getCost().toLocal(se);
                }
                try {
                    totalQuantity = totalQuantity.add(q);
                    if(costInLocalCurrency) {
                        totalCost = totalCost.add(c);
                    }
                } catch(Throwable e) {
                    error = e.getMessage();
                }
                if(!ii.getInTransit()) {
                    stockLocation.append(ii.getLocation());
                }
                if(ii.getInTransit()) {
                    stockLocation.append(" (In Transit)");
                    inTransitQuantity = inTransitQuantity.add(q);
                    if(costInLocalCurrency) {
                        inTransitCost = inTransitCost.add(c);
                    }
                }
                stockLocation.newLine();
                s = ii.getSerialNumber();
                sno.append(s == null ? "" : s).newLine();
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
                tcell("Item");
                tcell("Part Number");
                tcell("Serial/Batch Number");
                tcell("Location");
                tcellR();
                tcell("Unit");
                if(costInLocalCurrency) {
                    tcell("Cost");
                }
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
            createCell(quantities, costs);
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

    private void createCell(List<Quantity> quantities, List<Money> costs) {
        int colIndex = getCellIndex();
        Quantity q;
        for(int i = 0; i < quantities.size(); i++) {
            q = quantities.get(i);
            getCell().setCellValue(q.getValue().doubleValue());
            getNextCell().setCellValue(q.getUnit().getUnit());
            if(costInLocalCurrency) {
                getNextCell().setCellValue(costs.get(i).toString());
            }
            if(getRowIndex() > maxRow) {
                maxRow = getRowIndex();
            }
            goToCell(colIndex, getRowIndex() + 1);
        }
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
