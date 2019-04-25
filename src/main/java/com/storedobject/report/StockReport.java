package com.storedobject.report;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Device;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryStockLocation;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.Quantity;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class StockReport extends PDFReport {

    private String caption = "Stock Report";
    private InventoryStore store;
    private ObjectIterator<? extends InventoryItemType> partNumbers;
    private boolean printZeros = false;
    private boolean separateCategories;
    private int serviceabilityStatus = 0;

    public StockReport(Device device) {
        this(device, null, null);
    }

    public StockReport(Device device, InventoryStore store) {
        this(device, store, null);
    }

    public StockReport(Device device, InventoryStore store, ObjectIterator<? extends InventoryItemType> partNumbers) {
        super(device);
        this.store = store;
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

    public void setServiceabilityStatus(int serviceabilityStatus) {
        this.serviceabilityStatus = serviceabilityStatus;
    }

    @Override
    public PDFTable getTitleTable() {
        PDFTable t = createTable(1);
        PDFCell c = createCenteredCell(createTitleText(caption + " (" +
                (store == null ? "All Stores" : "Store: " + store.getName()) + ", Serviceability: " +
                InventoryItem.getServiceabilityStatusValue(serviceabilityStatus) + ") Date: " +
                DateUtility.formatDate(DateUtility.today()), getFontSize() + 4));
        c.setGrayFill(0.9f);
        t.addCell(c);
        return t;
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return StringUtility.makeLabel(itemType.getClass(), true);
    }

    public void printHeading(String heading) {
        PDFTable t = createTable(1);
        PDFCell c = createCenteredCell(createTitleText(heading, getFontSize() + 2));
        c.setGrayFill(0.9f);
        t.addCell(c);
        add(t);
    }

    @Override
    public void generateContent() {
        printStock(partNumbers);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers) {
        printStock(partNumbers, null);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
        if(partNumbers == null) {
            partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
            separateCategories = true;
        }
        InventoryStockLocation location;
        Quantity total, inTransit, q;
        Text stockLocation, quantity, sno;
        PDFTable table = createTable(25, 20, 15, 30, 15);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);
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
                stockLocation = new Text();
                quantity = new Text();
                sno = new Text();
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
                            stockLocation.append(PDFColor.RED).append(" (In Transit)").append(PDFColor.BLACK);
                            inTransit = inTransit.add(q);
                        }
                        stockLocation.newLine();
                        s = ii.getSerialNumber();
                        sno.append(s == null ? "" : s).newLine();
                        quantity.append(q).newLine();
                    }
                }
                if(error == null && (total.isZero() || total.isGreaterThan(q))) {
                    if(!total.isZero()) {
                        stockLocation.newLine(true);
                        quantity.newLine(true);
                    }
                    stockLocation.append("Total");
                    quantity.append(total);
                }
                if(!inTransit.isZero()) {
                    stockLocation.newLine(true);
                    quantity.newLine(true);
                    stockLocation.append("In Transit");
                    quantity.append(inTransit);
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
                    categoryHeaderPrinted = true;
                    categoryHeading = null;
                }
                if(!headerPrinted) {
                    table.addCell(tcell("Item"));
                    table.addCell(tcell("Part Number"));
                    table.addCell(tcell("Serial/Batch Number"));
                    table.addCell(tcell("Location"));
                    table.addCell(tcellR());
                    table.setHeaderRows(1);
                    headerPrinted = true;
                }
                table.addCell(createCell(item.getName().getName()));
                table.addCell(createCell(item.getPartNumber()));
                table.addCell(createCell(sno));
                table.addCell(createCell(stockLocation));
                if(error != null) {
                    quantity.newLine().append(error, PDFColor.RED);
                    error = null;
                }
                table.addCell(createCell(quantity, true));
            }
            if(headerPrinted) {
                add(table);
                table.deleteBodyRows();
            }
        }
    }

    private PDFCell tcell(String s) {
        return createCell(createTitleText(s, getFontSize()));
    }

    private PDFCell tcellR() {
        return createCell(createTitleText("Quantity", getFontSize()), true);
    }
}
