package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.util.ArrayList;
import java.util.List;

public class StockAvailability extends PDFReport {

    private final List<InventoryItemType> itemTypes = new ArrayList<>();
    private final List<Quantity> quantities = new ArrayList<>();
    private final InventoryStore store;

    public StockAvailability(Device device) {
        this(device, null, null, null);
    }

    public StockAvailability(Device device, InventoryItemType partNumber, Quantity quantity) {
        this(device, partNumber, quantity, null);
    }

    public StockAvailability(Device device, InventoryStore store) {
        this(device, null, null, store);
    }

    public StockAvailability(Device device, InventoryItemType partNumber, Quantity quantity, InventoryStore store) {
        super(device);
        this.store = store;
        add(partNumber, quantity);
    }

    public void add(InventoryItemType partNumber, Quantity quantity) {
        if(partNumber != null && quantity != null && quantity.isPositive()) {
            int i = itemTypes.indexOf(partNumber);
            if(i >= 0) {
                quantities.set(i, quantities.get(i).add(quantity));
            } else {
                itemTypes.add(partNumber);
                quantities.add(quantity);
            }
        }
    }

    @Override
    public String getTitleText() {
        return "Stock Availability (" + (store == null ? "All Stores" : "Store: " + store.toDisplay()) + ")";
    }

    @Override
    public void generateContent() {
        PDFTable table = createTable(90, 10);
        for(int index = 0; index < itemTypes.size(); index++) {
            Text b1 = new Text(), b2 = new Text();
            InventoryItemType itemType = itemTypes.get(index);
            b1.append(12, PDFFont.BOLD).append(itemType).newLine().append(10, PDFFont.NORMAL);
            b2.newLine(true);
            Quantity total = itemType.getUnitOfMeasurement(), inTransit = itemType.getUnitOfMeasurement(), q = itemType.getUnitOfMeasurement();
            String sn;
            for(InventoryItem inventory: InventoryItem.listStock(itemType, store)) {
                q = inventory.getQuantity();
                total = total.add(q);
                b1.append(inventory.getLocation());
                sn = inventory.getSerialNumber();
                if(!StringUtility.isWhite(sn)) {
                    b1.append(" (").append(itemType.isSerialized() ? "S/N" : "Batch/Lot").append(": ").append(sn).append(")");
                }
                if(inventory.getInTransit()) {
                    b1.append(PDFColor.RED).append(" (In Transit)").append(PDFColor.BLACK);
                    inTransit = inTransit.add(q);
                }
                b1.newLine();
                b2.append(q).newLine();
            }
            if(total.isZero() || total.isGreaterThan(q)) {
                if(!total.isZero()) {
                    b1.newLine();
                    b2.newLine();
                }
                b1.append("Total");
                b2.append(total);
            }
            if(!inTransit.isZero()) {
                b1.newLine();
                b2.newLine();
                b1.append("In Transit");
                b2.append(inTransit);
            }
            if(total.isZero() || total.isGreaterThan(q)) {
                b1.newLine();
                b2.newLine();
            }
            q = quantities.get(index);
            b1.newLine().append("Required");
            b2.newLine().append(q);
            if(total.isConvertible(q)) {
                if(total.subtract(inTransit).isLessThan(q)) {
                    q = q.subtract(total).subtract(inTransit);
                    b1.newLine().newLine().append("Shortage");
                    b2.newLine().newLine().append(PDFColor.RED).append(q);
                }
            } else {
                b1.newLine().newLine().append(PDFColor.RED).append("Error in Unit");
                b2.newLine().newLine().append("");
            }
            table.addCell(createCell(b1));
            table.addCell(createCell(b2, true));
        }
        add(table);
    }
}