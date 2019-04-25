package com.storedobject.report;

import java.sql.Date;

import com.storedobject.core.ComputedDate;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Device;
import com.storedobject.core.FileData;
import com.storedobject.core.FileFolder;
import com.storedobject.core.Logic;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

public class FileExpiryReport extends PDFReport {
	
	private FileFolder folder;
	private boolean recursive;
	private Date today = DateUtility.today();
	private int days;

	public FileExpiryReport(Device device) {
		this(device, Logic.getRunningLogicTitle(device, ""));
	}
	
	public FileExpiryReport(Device device, String folderName) {
		this(device, FileFolder.get(folderName), true);
	}

	public FileExpiryReport(Device device, FileFolder folder) {
		this(device, folder, true);
	}

	public FileExpiryReport(Device device, FileFolder folder, boolean recursive) {
		this(device, folder, 30, recursive);
	}

	public FileExpiryReport(Device device, String folderName, int days) {
		this(device, FileFolder.get(folderName), days, true);
	}

	public FileExpiryReport(Device device, FileFolder folder, int days) {
		this(device, folder, days, true);
	}

	public FileExpiryReport(Device device, FileFolder folder, int days, boolean recursive) {
		super(device);
		this.folder = folder;
		this.recursive = recursive;
		this.days = days < 2 ? 2 : days;
	}

	@Override
	public void generateContent() throws Exception {
		if(folder == null) {
			add("Nothing to print");
			return;
		}
		print(folder);
	}
	
	private PDFCell tcell(Object object) {
		return createCenteredCell(createTitleText(object.toString()));
	}
	
	private void printFiles(FileFolder folder) throws Exception {
		PDFTable table = createTable(15, 40, 25, 20);
		int n = 0, rem;
		ComputedDate d;
		for(FileData file: folder.listLinks(FileData.class, true)) {
			d = file.getExpiryDate();
			if(d.ignore() || (rem = DateUtility.getPeriodInDays(today, d)) > days) {
				continue;
			}
			if(n == 0) {
				table.addCell(tcell("Sl. No."));
				table.addCell(tcell("File/Document"));
				table.addCell(tcell("Expiry Date"));
				table.addCell(tcell("Days Left"));				
			}
			++n;
			table.addCell(createCenteredCell(n));
			table.addCell(createCell(file.getName()));
			table.addCell(createCenteredCell(d));
			if(rem > 0) {
				table.addCell(createCenteredCell(rem));
			} else {
				table.addCell(createCenteredCell(new Text().append(PDFColor.RED).append("" + rem)));
			}
		}
		if(n == 0) {
			PDFCell cell = createCenteredCell("None");
			cell.setColumnSpan(table.getNumberOfColumns());
			table.addCell(cell);
		}
		addTable(table);
	}
	
	private void print(FileFolder folder) throws Exception {
		add(tcell("Folder: " + folder));
		printFiles(folder);
		if(!recursive) {
			return;
		}
		for(FileFolder f: folder.listLinks(FileFolder.class)) {
			print(f);
		}
	}
	
    @Override
	public Object getTitleText() {
		Text t = new Text();
		t.append(16, PDFFont.BOLD).append("File/Document Expiry Status Report");
		t.newLine().append(14).append("Files/Documents Expiring in " + days + " days. Date: " + DateUtility.formatDate(today));
    	return t;
    }
}
