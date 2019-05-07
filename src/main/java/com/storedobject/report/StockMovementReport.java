package com.storedobject.report;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.Device;
import com.storedobject.core.InventoryIssue;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryItemName;
import com.storedobject.core.InventoryItemType;
import com.storedobject.core.InventoryReceipt;
import com.storedobject.core.InventoryStock;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.InventoryTransaction;
import com.storedobject.core.Money;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.Quantity;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class StockMovementReport extends PDFReport {
	
	private String caption = "Stock Movement";
	private InventoryStore store;
	private DatePeriod period;
	private ObjectIterator<? extends InventoryItemType> partNumbers;
	private boolean printZeros = false;
	private boolean summary = true;
	private boolean separateCategories;

	public StockMovementReport(Device device, InventoryStore store, DatePeriod period) {
		super(device);
		this.store = store;
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
		t.newLine().append(getFontSize() + 4).append("Store: ").append(store.getName()).append(", Period: ").append(period);
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
		int count = 0;
		Money.List cr, ci, runc, totalO = new Money.List(), totalR = new Money.List(), totalI = new Money.List();
		Quantity qr, qi, runq, q;
		Money c;
		InventoryStock is;
		InventoryReceipt ir;
		InventoryIssue ii;
		String condition = "Store=" + store.getId() + " AND Date " + period.getDBCondition() + " AND Item.PartNumber=";
		if(partNumbers == null) {
    		partNumbers = StoredObject.list(InventoryItemType.class, null, "T_Family", true);
    		separateCategories = true;
		}
        Class<? extends InventoryItemType> type, currentType = null;
        String s;
        InventoryItem item;
		for(InventoryItemType iit: partNumbers) {
        	if(separateCategories) {
        		type = iit.getClass();
        		if(currentType == null || type != currentType) {
        			currentType = type;
        			mainHeaderPrinted = false;
        		}
        	}
			is = null;
			qr = iit.getUnitOfMeasurement();
			qi = qr;
			runq = qr;
			cr = new Money.List();
			ci = new Money.List();
			runc = new Money.List();
			for(InventoryTransaction it: StoredObject.list(InventoryTransaction.class, condition + iit.getId(), "Date,Type")) {
				if(table == null) {
					if(!mainHeaderPrinted) {
						if(categoryHeading == null) {
							printHeading(getItemTypeTitle(iit));
						} else {
							printHeading(categoryHeading);
						}
						mainHeaderPrinted = true;
						categoryHeading = null;
					}
					table = printHeader();
				}
				if(is == null) {
					is = printOS(table, iit);
					totalO.add(is.getCost());
					++count;
					runq = runq.add(is.getQuantity());
					runc.add(is.getCost());
				}
				q = it.getQuantity();
				c = it.getCost();
				if(!summary) {
					table.addCell(createCell(it.getDate()));
					item = it.getItem();
					s = item.isSerialized() ? ("S/N: " + item.getSerialNumber() + ", ") : "";
					if(it.isReceipt()) {
						ir = it.getReceipt();
						table.addCell(createCell(s + (ir == null ? "" : ("Ref: " + ir.getGRN())) +
								", Received at: " + it.getStockLocation().getName()));
					} else {
						ii = it.getIssue();
						table.addCell(createCell(s + (ii == null ? "" : ("Ref: " + ii.getGIN())) +
								", Issued from: " + it.getStockLocation().getName()));
					}
				}
				if(it.isReceipt()) {
					cr.add(c);
					qr = qr.add(q);
					if(!summary) {
						table.addCell(createCell(q, true));
						table.addCell(createCell(c, true));
						table.addBlankCell();
						table.addBlankCell();
					}
					runq = runq.add(q);
					runc.add(c);
				} else {
					ci.add(c);
					qi = qi.add(q);
					if(!summary) {
						table.addBlankCell();
						table.addBlankCell();
						table.addCell(createCell(q, true));
						table.addCell(createCell(c, true));
					}
					runq = runq.subtract(q);
					runc.subtract(c);
				}
				if(!summary) {
					table.addCell(createCell(runq, true));
					table.addCell(createCell(runc.toString(true), true));
					printTable(table);
				}
			}
			totalR.add(cr);
			totalI.add(ci);
			if(is == null) {
				is = getOS(iit);
				if(!printZeros && is.getQuantity().isZero()) {
					continue;
				}
				if(table == null) {
					if(!mainHeaderPrinted) {
						if(categoryHeading == null) {
							printHeading(getItemTypeTitle(iit));
						} else {
							printHeading(categoryHeading);
						}
						mainHeaderPrinted = true;
						categoryHeading = null;
					}
					table = printHeader();
				}
				printOS(table, is, iit);
				totalO.add(is.getCost());
				++count;
				if(summary) {
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addCell(createCell(is.getQuantity(), true));
					table.addCell(createCell(is.getCost(), true));
					printTable(table);
				} else {
					table.addCell(createCell(period.getTo()));
					table.addCell(createCell("Closing Stock"));
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addBlankCell();
					table.addCell(createCell(is.getQuantity(), true));
					table.addCell(createCell(is.getCost(), true));
				}
				continue;
			}
			if(!summary) {
				continue;
			}
			table.addCell(createCell(qr.isZero() ? "" : qr, true));
			table.addCell(createCell(cr.isZero() ? "" : cr, true));
			table.addCell(createCell(qi.isZero() ? "" : qi, true));
			table.addCell(createCell(ci.isZero() ? "" : ci, true));
			table.addCell(createCell(runq, true));
			table.addCell(createCell(runc.toString(true), true));
			printTable(table);
		}
		if(table == null) {
			return;
		}
		if(summary) {
			table.addCell(gcell(count + " item" + (count == 1 ? "" : "s")));
			table.addCell(gcell(""));
			table.addCell(gcell(totalO.toString(), true));
			table.addCell(gcell(""));
			table.addCell(gcell(totalR.toString(), true));
			table.addCell(gcell(""));
			table.addCell(gcell(totalI.toString(), true));
			table.addCell(gcell(""));
		} else {
			table.addBlankRow();
			table.addBlankCell();
			table.addCell(title(count + " item" + (count == 1 ? "" : "s"), false));
			table.addCell(gcell(""));
			table.addCell(gcell(totalR.toString(), true));
			table.addCell(gcell(""));
			table.addCell(gcell(totalI.toString(), true));
			table.addCell(gcell(""));
		}
		table.addCell(gcell(totalO.add(totalR).subtract(totalI).toString(), true));
		add(table);
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
			table.addCell(title("Value"));
			table.addCell(title("Issued"));
			table.addCell(title("Value"));
			table.addCell(title("Closing"));
			table.addCell(title("Value"));
		} else {
			table = createTable(8, 28, 8, 10, 8, 10, 8, 10);
			table.addCell(title("Date", false));
			table.addCell(title("Particulars", false));
			table.addCell(title("Received"));
			table.addCell(title("Value"));
			table.addCell(title("Issued"));
			table.addCell(title("Value"));
			table.addCell(title("Stock"));
			table.addCell(title("Value"));
		}
		table.setSpacingBefore(0);
		table.setSpacingAfter(0);
		table.setHeaderRows(1);
		return table;
	}
	
	@Override
	public void generateContent()throws Exception {
		printStockMovement(partNumbers);
	}
	
	private InventoryStock getOS(InventoryItemType iit) {
		return InventoryStock.getOpeningStock(store.getId(), iit.getId(), period.getFrom());
	}
	
	private InventoryStock printOS(PDFTable t, InventoryItemType iit) {
		InventoryStock is = getOS(iit);
		printOS(t, is, iit);
		return is;
	}

	private void printOS(PDFTable t, InventoryStock is, InventoryItemType iit) {
		InventoryItemName name = iit.getName();
		if(summary) {
			t.addCell(createCell((name == null ? "" : (name.getName() + "\n")) + iit.getPartNumber()));
			t.addCell(createCell(is.getQuantity(), true));
			t.addCell(createCell(is.getCost(), true));
			return;
		}
		t.addBlankCell();
		PDFCell c = title((name == null ? "" : ("Item: " + name.getName() + ", ")) + "P/N: " + iit.getPartNumber(), false);
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

	private PDFCell gcell(String s) {
		return gcell(s, false);
	}

	private PDFCell gcell(String s, boolean rightAligned) {
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
