package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.*;

import java.sql.Date;

public class StockMovementReport extends PDFReport {

	private String caption = "Stock Movement";
	private final InventoryLocation location;
	private final DatePeriod period;
	private ObjectIterator<? extends InventoryItemType> partNumbers;
	private boolean printZeros = false;
	private boolean summary = true;
	private boolean separateCategories;
	private boolean costInLocalCurrency = true;
	private SystemEntity se;

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
		Text t = new Text();
		t.append(getFontSize() + 6, PDFFont.BOLD).append(caption);
		t.newLine().append(getFontSize() + 4).
				append(location instanceof InventoryStoreBin ? "Store"
						: (location instanceof InventoryFitmentPosition? "Assembly" : "Location")).
				append(": ").append(location.toDisplay()).append(", Period: ").append(period);
		PDFCell c = createCell(t);
		c.setGrayFill(0.9f);
		return c;
    }

	private void printHeading(String heading, PDFTable table) {
		table.addBlankRow();
		PDFCell c = createCenteredCell(createTitleText(heading, getFontSize() + 2));
		c.setGrayFill(0.9f);
		table.addRowCell(c);
	}

	public String getItemTypeTitle(InventoryItemType itemType) {
		return StringUtility.makeLabel(itemType.getClass(), true);
	}

	public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers) {
		printStockMovement(partNumbers, null);
	}

	public void printStockMovement(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
		se = costInLocalCurrency ? getTransactionManager().getEntity() : null;
		PDFTable table = table();
		boolean tableUsed = false;
		boolean categoryHeaderPrinted = categoryHeading == null;
		InventoryLocation locFrom, locTo;
		Id storeId = location instanceof InventoryStoreBin ? ((InventoryStoreBin) location).getStoreId() : null;
		int count = 0, countCat = 0;
		Money.List costOfReceipts, costOfIssues, runningCost,
				totalCost = new Money.List(),
				totalCostOfReceipts = new Money.List(), totalCostOfIssues = new Money.List(),
				totalCostCat = new Money.List(),
				totalCostOfReceiptsCat = new Money.List(), totalCostOfIssuesCat = new Money.List();
		String catName = categoryHeading;
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
		InventoryItem item;
		Text text;
		for(InventoryItemType partNumber: partNumbers) {
			setError("Printing " + partNumber.toDisplay());
			if(separateCategories) {
				type = partNumber.getClass();
				if(currentType == null || type != currentType) {
					if(countCat > 0) {
						printTotals(table, countCat, totalCostCat, totalCostOfReceiptsCat, totalCostOfIssuesCat, "Total (" + catName + ")");
					}
					countCat = 0;
					totalCostCat.clear();
					totalCostOfReceiptsCat.clear();
					totalCostOfIssuesCat.clear();
					currentType = type;
					categoryHeaderPrinted = false;
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
					StoredObject.list(InventoryLedger.class, "ItemType=" + partNumber.getId() + " AND Date "
							+ period.getDBCondition(), "ItemType,Date,TranId")) {
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
				if(!tableUsed) {
					tableUsed = true;
				}
				if(!categoryHeaderPrinted) {
					if(categoryHeading == null) {
						catName = getItemTypeTitle(partNumber);
					} else {
						catName = categoryHeading;
					}
					printHeading(catName, table);
					categoryHeaderPrinted = true;
					categoryHeading = null;
				}
				if(opening == null) {
					opening = printOS(table, partNumber);
					totalCost.add(opening.cost());
					totalCostCat.add(opening.cost());
					++count;
					++countCat;
					runningQuantity = runningQuantity.add(opening.quantity());
					runningCost.add(opening.cost());
				}
				q = movement.getQuantity();
				cost = movement.getCost();
				if(!summary) {
					item = movement.getItem();
					table.addCell(dCell(movement.getDate()));
					if(item != null) {
						s = item.getSerialNumber();
						if(!s.isBlank()) {
							s = partNumber.getSerialNumberShortName() + ": " + s;
						}
					} else {
						s = "";
					}
					if(!s.isEmpty()) {
						s += ", ";
					}
					if(isReceipt) {
						s += "Ref: " + movement.getReference() + ", " + locFrom.getIssueText();
						if(locFrom.getType() == 12) { // Data-pickup
							AuditTrail at = AuditTrail.create(movement);
							if(at != null) {
								s += " (Created at " + DateUtility.formatWithTimeHHMM(at.getTimestamp()) + ")";
							}
						}
					} else {
						s += "Ref: " + movement.getReference() + ", " + locTo.getReceiptText();
					}
					if(item != null && item.getInTransit()) {
						text = new Text(s);
						text.append(" - ").append(PDFColor.RED).append("In transit");
						table.addCell(createCell(text));
					} else {
						table.addCell(createCell(s));
					}
				}
				if(isReceipt) {
					costOfReceipts.add(cost);
					quantityReceived = quantityReceived.add(q);
					if(!summary) {
						table.addCell(qCell(q));
						table.addCell(cCell(cost));
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
						table.addCell(qCell(q));
						table.addCell(cCell(cost));
					}
					runningQuantity = runningQuantity.subtract(q);
					runningCost.subtract(cost);
				}
				if(!summary) {
					table.addCell(qCell(runningQuantity));
					table.addCell(cCell(runningCost));
					printTable(table);
				}
			}
			totalCostOfReceipts.add(costOfReceipts);
			totalCostOfIssues.add(costOfIssues);
			totalCostOfReceiptsCat.add(costOfReceipts);
			totalCostOfIssuesCat.add(costOfIssues);
			if(opening == null) {
				opening = getOS(partNumber);
				if(!printZeros && opening.quantity().isZero()) {
					continue;
				}
				if(!tableUsed) {
					tableUsed = true;
				}
				if(!categoryHeaderPrinted) {
					if(categoryHeading == null) {
						catName = getItemTypeTitle(partNumber);
					} else {
						catName = categoryHeading;
					}
					printHeading(catName, table);
					categoryHeaderPrinted = true;
					categoryHeading = null;
				}
				printOS(table, opening, partNumber);
				totalCost.add(opening.cost());
				totalCostCat.add(opening.cost());
				++count;
				++countCat;
				if(summary) {
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addCell(qCell(opening.quantity()));
					table.addCell(cCell(opening.cost()));
					printTable(table);
				} else {
					table.addCell(createCell(period.getTo()));
					table.addCell(createCell("Closing Stock"));
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addCell(qCell(opening.quantity()));
					table.addCell(cCell(opening.cost()));
				}
				continue;
			}
			if(!summary) {
				continue;
			}
			table.addCell(qCell(quantityReceived, false));
			table.addCell(cCell(costOfReceipts));
			table.addCell(qCell(quantityIssued, false));
			table.addCell(cCell(costOfIssues));
			table.addCell(qCell(runningQuantity));
			table.addCell(cCell(runningCost));
			printTable(table);
		}
		if(!tableUsed) {
			return;
		}
		if(count > countCat) {
			printTotals(table, count, totalCost, totalCostOfReceipts, totalCostOfIssues, "Grand Total");
		}
		add(table);
	}

	private void printTotals(PDFTable table, int count, Money.List totalCost, Money.List totalCostOfReceipts,
							 Money.List totalCostOfIssues, String label) {
		printHeading(label, table);
		if(summary) {
			table.addCell(gCell("Count: " + count));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCost));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfReceipts));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfIssues));
			table.addCell(gCell(""));
		} else {
			table.addCell(gCell(""));
			table.addCell(title("Opening Stock Value as of " + DateUtility.format(period.getFrom()), false));
			table.addCell(gCell(""));
			table.addCell(gCell(""));
			table.addCell(gCell(""));
			table.addCell(gCell(""));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCost));
			table.addCell(gCell(""));
			table.addCell(title("Closing Stock Vale as of " + DateUtility.format(period.getTo()) + " (Count: "
					+ count + ")", false));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfReceipts));
			table.addCell(gCell(""));
			table.addCell(gCell(totalCostOfIssues));
			table.addCell(gCell(""));
		}
		table.addCell(gCell(totalCost.add(totalCostOfReceipts).subtract(totalCostOfIssues)));
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

	private PDFTable table() {
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
			table = createTable(7, 29, 8, 10, 8, 10, 8, 10);
			table.addCell(title("Date", false));
			table.addCell(title("Particulars", false));
			table.addCell(title("Quantity Received"));
			table.addCell(title("Value (Receipts)"));
			table.addCell(title("Quantity Issued"));
			table.addCell(title("Value (Issues)"));
			table.addCell(title("Stock"));
		}
		table.addCell(title("Value"));
		table.setHeaderRows(1);
		return table;
	}

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
			t.addCell(qCell(is.quantity()));
			t.addCell(cCell(is.cost()));
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
		t.addCell(qCell(is.quantity()));
		t.addCell(cCell(is.cost()));
	}

	private PDFCell qCell(Quantity quantity) {
		return qCell(quantity, true);
	}

	private PDFCell qCell(Quantity quantity, boolean printZeros) {
		Object p;
		if(quantity.isNegative()) {
			p = "";
		} else {
			if(!printZeros && quantity.isZero()) {
				p = "";
			} else {
				p = quantity;
			}
		}
		return createCell(p);
	}

	private PDFCell cCell(Money cost) {
		return createCell(cost.isNegative() ? "" : costInLocalCurrency ? cost.toLocal(se) : cost);
	}

	private PDFCell cCell(Money.List monlyList) {
		return createCell(toString(monlyList), true);
	}

	private PDFCell dCell(Date date) {
		if(DateUtility.equals(date, InventoryTransaction.dataPickupDate)) {
			return createCell("Initial data");
		}
		return createCell(date);
	}

	private String toString(Money.List monlyList) {
		boolean negative = monlyList.values().stream().anyMatch(Money::isNegative);
		if(negative) {
			return "";
		}
		if(costInLocalCurrency) {
			return monlyList.toLocal(se).toString();
		}
		return monlyList.toString(true);
	}

	private PDFCell gCell(Money.List monlyList) {
		return gCell(toString(monlyList));
	}

	private PDFCell gCell(String s) {
		PDFCell c = createCell(s, true);
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
