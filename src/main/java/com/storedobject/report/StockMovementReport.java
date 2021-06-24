package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFReport;

public class StockMovementReport extends PDFReport {

	private boolean separateCategories;

	public StockMovementReport(Device device, InventoryStore store, DatePeriod period) {
		this(device, store.getStoreBin(), period);
	}

	public StockMovementReport(Device device, InventoryLocation location, DatePeriod period) {
		super(device);
		setPageSizeIndex(2);
		setFontSize(8);
	}

	public void setCaption(String caption) {
	}

	public void printSummary(boolean summary) {
	}

	public void printZeros(boolean printZeros) {
	}

	public void setPartNumber(InventoryItemType partNumber) {
		if(partNumber != null) {
			setPartNumbers(ObjectIterator.create(partNumber));
			separateCategories = false;
		}
	}

	public void setPartNumbers(ObjectIterator<? extends InventoryItemType> partNumbers) {
	}

	public void separateCategories(boolean separateCategories) {
		this.separateCategories = separateCategories;
	}

	@Override
	public Object getTitleText() {
		return new Text();
	}

	public String getItemTypeTitle(InventoryItemType itemType) {
		return StringUtility.makeLabel(itemType.getClass(), true);
	}

	public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers) {
		printStockMovement(partNumbers, null);
	}

	public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
	}

	@SuppressWarnings("RedundantThrows")
	@Override
	public void generateContent() throws Exception {
	}
}
