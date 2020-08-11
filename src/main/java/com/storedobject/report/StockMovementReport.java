package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.sql.Date;

public class StockMovementReport extends PDFReport {

	private String caption = "Stock Movement";
	private final InventoryLocation location;
	private final DatePeriod period;
	private ObjectIterator<? extends InventoryItemType> partNumbers;
	private boolean printZeros = false;
	private boolean summary = true;
	private boolean separateCategories;

	public StockMovementReport(Device device, InventoryStore store, DatePeriod period) {
		this(device, store.getStoreBin(), period);
	}

	public StockMovementReport(Device device, InventoryLocation location, DatePeriod period) {
		super(device);
		this.location = location;
		this.period = period;
		setPageSizeIndex(2);
		setFontSize(8);
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void printSummary(boolean summary) {
		this.summary = summary;
	}

	public void printZeros(boolean printZeros) {
		this.printZeros = printZeros;
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
		Text t = new Text();
		t.append(getFontSize() + 6, PDFFont.BOLD).append(caption);
		t.newLine().append(getFontSize() + 4).
				append(location instanceof InventoryStoreBin ? "Store" : (location instanceof InventoryFitmentPosition? "Assembly" : "Location")).
				append(": ").append(location.toDisplay()).append(", Period: ").append(period);
		return t;
	}

	public void printHeading(String heading) {
		PDFTable t = createTable(1);
		PDFCell c = createCenteredCell(createTitleText(heading, getFontSize() + 2));
		c.setGrayFill(0.9f);
		t.addCell(c);
		add(t);
	}

	public String getItemTypeTitle(InventoryItemType itemType) {
		return StringUtility.makeLabel(itemType.getClass(), true);
	}

	public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers) {
		printStockMovement(partNumbers, null);
	}

	public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
		PDFTable table = null;
		boolean mainHeaderPrinted = categoryHeading == null;
		InventoryItem item;
		InventoryLocation locFrom, locTo;
		Id storeId = location instanceof InventoryStoreBin ? ((InventoryStoreBin) location).getStoreId() : null;
		int count = 0;
		Money.List costOfReceipts, costOfIssues, runningCost, totalCost = new Money.List(),
				totalCostOfReceipts = new Money.List(), totalCostOfIssues = new Money.List();
		Quantity quantityReceived, quantityIssued, runningQuantity, q;
		Money cost;
		QuantityWithCost opening;
		if(partNumbers == null) {
			partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
			separateCategories = true;
		}
		boolean isReceipt;
		Class<? extends InventoryItemType> type, currentType = null;
		String s;
		for(InventoryItemType partNumber: partNumbers) {
			if(separateCategories) {
				type = partNumber.getClass();
				if(currentType == null || type != currentType) {
					currentType = type;
					mainHeaderPrinted = false;
				}
			}
			opening = null;
			quantityReceived = partNumber.getUnitOfMeasurement();
			quantityIssued = quantityReceived;
			runningQuantity = quantityReceived;
			costOfReceipts = new Money.List();
			costOfIssues = new Money.List();
			runningCost = new Money.List();
			for(InventoryLedger movement:
					StoredObject.list(InventoryLedger.class, "ItemType=" + partNumber.getId() + " AND Date " + period.getDBCondition(), "ItemType,Date,TranId")) {
				locFrom = movement.getLocationFrom();
				locTo = movement.getLocationTo();
				if(storeId != null) {
					if(isRebin(locFrom, locTo)) {
						continue;
					}
					if(locFrom instanceof InventoryBin && ((InventoryBin) locFrom).getStoreId().equals(storeId)) {
						isReceipt = false;
					} else if(locTo instanceof InventoryBin && ((InventoryBin) locTo).getStoreId().equals(storeId)) {
						isReceipt = true;
					} else {
						continue;
					}
				} else {
					if(location.getId().equals(locFrom.getId())) {
						isReceipt = false;
					} else if(location.getId().equals(locTo.getId())) {
						isReceipt = true;
					} else {
						continue;
					}
				}
				if(table == null) {
					if(!mainHeaderPrinted) {
						if(categoryHeading == null) {
							printHeading(getItemTypeTitle(partNumber));
						} else {
							printHeading(categoryHeading);
						}
						mainHeaderPrinted = true;
						categoryHeading = null;
					}
					table = printHeader();
				}
				if(opening == null) {
					opening = printOS(table, partNumber);
					totalCost.add(opening.getCost());
					++count;
					runningQuantity = runningQuantity.add(opening.getQuantity());
					runningCost.add(opening.getCost());
				}
				q = movement.getQuantity();
				cost = movement.getCost();
				if(!summary) {
					table.addCell(dCell(movement.getDate()));
					item = movement.getItem();
					s = item.getSerialNumber();
					if(partNumber.isSerialized()) {
						s = "S/N: " + s;
					} else {
						if(s.isBlank()) {
							s = "";
						} else {
							s = "No.: " + s;
						}
					}
					if(!s.isEmpty()) {
						s += ", ";
					}
					if(isReceipt) {
						table.addCell(createCell(s + "Ref: " + movement.getReference() +
								", " + locFrom.getIssueText()));
					} else {
						table.addCell(createCell(s + "Ref: " + movement.getReference() +
								", " + locTo.getReceiptText()));
					}
				}
				if(isReceipt) {
					costOfReceipts.add(cost);
					quantityReceived = quantityReceived.add(q);
					if(!summary) {
						table.addCell(createCell(q, true));
						table.addCell(createCell(cost, true));
						table.addBlankCell();
						table.addBlankCell();
					}
					runningQuantity = runningQuantity.add(q);
					runningCost.add(cost);
				} else {
					costOfIssues.add(cost);
					quantityIssued = quantityIssued.add(q);
					if(!summary) {
						table.addBlankCell();
						table.addBlankCell();
						table.addCell(createCell(q, true));
						table.addCell(createCell(cost, true));
					}
					runningQuantity = runningQuantity.subtract(q);
					runningCost.subtract(cost);
				}
				if(!summary) {
					table.addCell(createCell(runningQuantity, true));
					table.addCell(createCell(runningCost.toString(true), true));
					printTable(table);
				}
			}
			totalCostOfReceipts.add(costOfReceipts);
			totalCostOfIssues.add(costOfIssues);
			if(opening == null) {
				opening = getOS(partNumber);
				if(!printZeros && opening.getQuantity().isZero()) {
					continue;
				}
				if(table == null) {
					if(!mainHeaderPrinted) {
						if(categoryHeading == null) {
							printHeading(getItemTypeTitle(partNumber));
						} else {
							printHeading(categoryHeading);
						}
						mainHeaderPrinted = true;
						categoryHeading = null;
					}
					table = printHeader();
				}
				printOS(table, opening, partNumber);
				totalCost.add(opening.getCost());
				++count;
				if(summary) {
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addCell(createCell(opening.getQuantity(), true));
					table.addCell(createCell(opening.getCost(), true));
					printTable(table);
				} else {
					table.addCell(createCell(period.getTo()));
					table.addCell(createCell("Closing Stock"));
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addCell(createCell(opening.getQuantity(), true));
					table.addCell(createCell(opening.getCost(), true));
				}
				continue;
			}
			if(!summary) {
				continue;
			}
			table.addCell(createCell(quantityReceived.isZero() ? "" : quantityReceived, true));
			table.addCell(createCell(costOfReceipts.isZero() ? "" : costOfReceipts, true));
			table.addCell(createCell(quantityIssued.isZero() ? "" : quantityIssued, true));
			table.addCell(createCell(costOfIssues.isZero() ? "" : costOfIssues, true));
			table.addCell(createCell(runningQuantity, true));
			table.addCell(createCell(runningCost.toString(true), true));
			printTable(table);
		}
		if(table == null) {
			return;
		}
		if(summary) {
			table.addCell(gCell(count + " item" + (count == 1 ? "" : "s")));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCost.toString(), true));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfReceipts.toString(), true));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfIssues.toString(), true));
			table.addCell(gCell(""));
		} else {
			table.addBlankRow();
			table.addCell(gCell(""));
			table.addCell(title(count + " item" + (count == 1 ? "" : "s"), false));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfReceipts.toString(), true));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfIssues.toString(), true));
			table.addCell(gCell(""));
		}
		table.addCell(gCell(totalCost.add(totalCostOfReceipts).subtract(totalCostOfIssues).toString(), true));
		add(table);
	}

	private boolean isRebin(InventoryLocation locationFrom, InventoryLocation locationTo) {
		return locationFrom instanceof InventoryBin && locationTo instanceof InventoryBin &&
				((InventoryBin) locationFrom).getStoreId().equals(((InventoryBin) locationTo).getStoreId());
	}

	private void printTable(PDFTable table) {
		if(table.getNumberOfRows() > 80) {
			add(table);
		}
	}

	private PDFTable printHeader() {
		PDFTable table;
		if(summary) {
			table = createTable(20, 8, 10, 8, 10, 8, 10, 8, 10);
			table.addCell(title("Item", false));
			table.addCell(title("Opening"));
			table.addCell(title("Value"));
			table.addCell(title("Received"));
			table.addCell(title("Value (Receipts)"));
			table.addCell(title("Issued"));
			table.addCell(title("Value (Issues)"));
			table.addCell(title("Closing"));
		} else {
			table = createTable(8, 28, 8, 10, 8, 10, 8, 10);
			table.addCell(title("Date", false));
			table.addCell(title("Particulars", false));
			table.addCell(title("Quantity Received"));
			table.addCell(title("Value (Receipts)"));
			table.addCell(title("Quantity Issued"));
			table.addCell(title("Value (Issues)"));
			table.addCell(title("Stock"));
		}
		table.addCell(title("Value"));
		table.setSpacingBefore(0);
		table.setSpacingAfter(0);
		table.setHeaderRows(1);
		return table;
	}

	@SuppressWarnings("RedundantThrows")
	@Override
	public void generateContent() throws Exception {
		printStockMovement(partNumbers);
	}

	private QuantityWithCost getOS(InventoryItemType iit) {
		return InventoryLedger.getOpeningStock(iit, period.getFrom(), location);
	}

	private QuantityWithCost printOS(PDFTable t, InventoryItemType iit) {
		QuantityWithCost is = getOS(iit);
		printOS(t, is, iit);
		return is;
	}

	private void printOS(PDFTable t, QuantityWithCost is, InventoryItemType iit) {
		if(summary) {
			t.addCell(createCell(iit.getName() + "\n" + iit.getPartNumber()));
			t.addCell(createCell(is.getQuantity(), true));
			t.addCell(createCell(is.getCost(), true));
			return;
		}
		t.addBlankCell();
		PDFCell c = title("Item: " + iit.getName() + ", P/N: " + iit.getPartNumber(), false);
		c.setColumnSpan(t.getNumberOfColumns() - 1);
		t.addCell(c);
		t.addCell(createCell(period.getFrom()));
		t.addCell(createCell("Opening Stock"));
		t.addBlankCell();
		t.addBlankCell();
		t.addBlankCell();
		t.addBlankCell();
		t.addCell(createCell(is.getQuantity().toString(), true));
		t.addCell(createCell(is.getCost().toString(), true));
	}

	private PDFCell dCell(Date date) {
		if(DateUtility.equals(date, InventoryTransaction.dataPickupDate)) {
			return createCell("Initial data");
		}
		return createCell(date);
	}

	private PDFCell gCell(String s) {
		return gCell(s, false);
	}

	private PDFCell gCell(String s, boolean rightAligned) {
		PDFCell c = createCell(s, rightAligned);
		c.setGrayFill(0.9f);
		return c;
	}

	private PDFCell title(String title) {
		return title(title, true);
	}

	private PDFCell title(String title, boolean rightAligned) {
		PDFCell c = createCell(createTitleText(title, getFontSize()), rightAligned);
		c.setGrayFill(0.9f);
		return c;
	}
}
