package com.storedobject.report;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.pdf.*;

public class StockAvailability extends PDFReport {

    private final InventoryTransaction requirement;
    private InventoryStore store;

    public StockAvailability(Device device, InventoryTransaction requirement) {
        this(device, requirement, false);
    }

    public StockAvailability(Device device, InventoryTransaction requirement, boolean checkAllStores) {
        super(device);
        this.requirement = requirement;
        InventoryLocation location = requirement.getLocationFrom();
        if(!(location instanceof InventoryBin)) {
            throw new SORuntimeException("Stock availability can not be checked at location '" + location + "'");
        }
        if(!checkAllStores) {
            this.store = ((InventoryBin)location).getStore();
        }
    }

    @Override
    public String getTitleText() {
        return "Stock Availability (" + (store == null ? "All Stores" : "Store: " + store.toDisplay()) + ")";
    }

    @Override
    public void generateContent() {
        PDFTable table = createTable(90, 10);
        requirement.entries().forEach(entry -> {
            Text b1 = new Text(), b2 = new Text();
            InventoryItemType itemType = entry.getItemType();
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
            q = entry.getQuantity();
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
        });
        add(table);
    }
}