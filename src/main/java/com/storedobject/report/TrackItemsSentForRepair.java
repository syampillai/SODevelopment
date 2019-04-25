package com.storedobject.report;

import java.sql.Date;

import com.storedobject.core.Database;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Device;
import com.storedobject.core.Entity;
import com.storedobject.core.InventoryIssueDocument;
import com.storedobject.core.InventoryRepairIssuedItem;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class TrackItemsSentForRepair extends PDFReport {
	
	private InventoryStore store;
	private Entity repairAgency;
	private Date deliveryDate;

	public TrackItemsSentForRepair(Device device, Entity repairAgency, Date deliveryDate) {
		this(device, null, repairAgency, deliveryDate);
	}
	
	public TrackItemsSentForRepair(Device device, InventoryStore store, Entity repairAgency, Date deliveryDate) {
		super(device);
		this.store = store;
		this.repairAgency = repairAgency;
		this.deliveryDate = deliveryDate;
	}
	
	@Override
    public PDFTable getTitleTable() {
    	PDFTable t = createTable(1);
    	String s = "Items Sent for Repair (" + (store == null ? "All Stores" : "Store: " + store.getName()) + ")\nRepair Agency: ";
    	s += repairAgency.getName();
    	if(deliveryDate != null) {
    		s += "\nDelivery expected on or before " + DateUtility.format(deliveryDate);
    	}
    	PDFCell c = createCenteredCell(createTitleText(s, 16));
    	c.setGrayFill(0.9f);
    	t.addCell(c);
    	return t;
    }

	@Override
	public void generateContent() throws Exception {
    	PDFTable t;
    	if(store == null) {
    		t = createTable(15, 20, 50, 15);
    	} else {
       		t = createTable(15, 70, 15);
    	}
    	t.addCell(createTitleText("Date"));
    	if(store == null) {
    		t.addCell(createTitleText("Store"));
    	}
    	t.addCell(createTitleText("Item"));
    	t.addCell(createTitleText("Delivery Expected"));
    	t.setHeaderRows(1);
		ObjectIterator<InventoryRepairIssuedItem> ritems;
		String c = "NOT Closed AND IssuedTo=" + repairAgency.getId();
		if(deliveryDate != null) {
			c += " AND DeliveryDate<='" + Database.format(deliveryDate) + "'";
		}
		InventoryIssueDocument document;
		ritems = StoredObject.list(InventoryRepairIssuedItem.class, c, "DeliveryDate");
		for(InventoryRepairIssuedItem ritem: ritems) {
			document = ritem.getDocument();
			if(store != null && !store.getId().equals(document.getStoreId())) {
				continue;
			}
			t.addCell(createCell(ritem.getDate()));
			if(store == null) {
				t.addCell(createCell(document.getStore().getName()));
			}
			t.addCell(createCell(ritem.getItem()));
			t.addCell(createCell(ritem.getDeliveryDate()));
		}
		addTable(t);
	}
}
