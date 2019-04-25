package com.storedobject.report;

import java.util.List;

import com.storedobject.core.Device;
import com.storedobject.core.Id;
import com.storedobject.core.InventoryIssueDocument;
import com.storedobject.core.InventoryIssueDocumentItem;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.Quantity;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class StockAvailability extends PDFReport {

	private InventoryIssueDocument document;
	private InventoryStore store;
	
    public StockAvailability(Device device, InventoryIssueDocument document) {
    	this(device, document, false);
    }
    
    public StockAvailability(Device device, InventoryIssueDocument document, boolean checkAllStores) {
    	super(device);
    	this.document = document;
    	if(!checkAllStores) {
    		this.store = document.getStore();
    	}
    }

    @Override
    public String getTitleText() {
        return "Stock Availability (" + (store == null ? "All Stores" : "Store: " + store.getName()) + ")";
    }

    @Override
    public void generateContent() {
    	Id storeId;
    	if(store == null) {
    		storeId = document.getStoreId();
    	} else {
    		storeId = store.getId();
    	}
    	List < InventoryIssueDocumentItem > items = document.getItems();
        Quantity total, inTransit, q;
        Text b1, b2;
        PDFTable table = createTable(7, 15, 5);
        PDFCell cell;
        InventoryItemType item;
        ObjectIterator<InventoryStore> stores;
        String sn;
        for(InventoryIssueDocumentItem iidi: items) {
            item = iidi.getItem();
            cell = createCenteredCell(new Text().append(12, PDFFont.BOLD).append(item));
            cell.setColumnSpan(3);
            table.addCell(cell);
            table.addCell(createCell(createTitleText("Store")));
            table.addCell(createCell(createTitleText("Location")));
            table.addCell(createCell(createTitleText("Quantity"), true));
            if(this.store == null) {
            	stores = StoredObject.list(InventoryStore.class, true);
            } else {
            	stores = ObjectIterator.create(this.store);
            }
            for(InventoryStore store: stores) {
                total = item.getUnitOfMeasurement().zero();
                inTransit = total.zero();
                q = item.getUnitOfMeasurement().zero();
                b1 = new Text();
                b2 = new Text();
                for(InventoryItem ii: InventoryItem.listStock(item, store.getId())) {
                    q = ii.getQuantity();
                    if(!q.isZero()) {
                        total = total.add(q);
                        b1.append(ii.getStockLocation());
                        sn = ii.getSerialNumber();
                        if(!StringUtility.isWhite(sn)) {
                        	b1.append(" (").append(item.isSerialized() ? "S/N" : "Batch/Lot").append(": ").append(sn).append(")");
                        }
                        if(ii.getInTransit()) {
                            b1.append(PDFColor.RED).append(" (In Transit)").append(PDFColor.BLACK);
                            inTransit = inTransit.add(q);
                        }
                        b1.newLine();
                        b2.append(q).newLine();
                    }
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
                if(store.getId().equals(storeId)) {
                    if(total.isZero() || total.isGreaterThan(q)) {
                        b1.newLine();
                        b2.newLine();
                    }
                    q = iidi.getQuantity().subtract(iidi.getQuantityIssued());
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
                } else if(total.isZero()) {
                    continue;
                }
                table.addCell(createCell(store));
                table.addCell(createCell(b1));
                table.addCell(createCell(b2, true));
            }
            add(table);
        }
    }
}
