package com.storedobject.report;

import java.sql.Timestamp;

import com.storedobject.core.ComputedDate;
import com.storedobject.core.ComputedMinute;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Device;
import com.storedobject.core.FileCirculation;
import com.storedobject.core.FileData;
import com.storedobject.core.FileFolder;
import com.storedobject.core.Logic;
import com.storedobject.core.StringUtility;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFFont;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

@SuppressWarnings("unused")
public class FileCirculationStatus extends PDFReport {
	
	private final static long H = 3600000L;
	private FileData file;
	private FileFolder folder;
	private boolean recursive;
	private Timestamp now = DateUtility.now();
	private long readBefore;
	
	public FileCirculationStatus(Device device) {
		this(device, Logic.getRunningLogicTitle(device, ""));
	}

	public FileCirculationStatus(Device device, FileData file) {
		super(device);
		this.file = file;
	}

	public FileCirculationStatus(Device device, String folderName) {
		this(device, FileFolder.get(folderName), FileData.get(folderName), true);
	}

	public FileCirculationStatus(Device device, FileFolder folder) {
		this(device, folder, true);
	}

	public FileCirculationStatus(Device device, FileFolder folder, boolean recursive) {
		this(device, folder, null, true);
	}
	
	private FileCirculationStatus(Device device, FileFolder folder, FileData file, boolean recursive) {
		super(device);
		this.folder = folder;
		this.file = file;
		this.recursive = recursive;
	}
	
	@Override
	public void generateContent() throws Exception {
		if(file != null) {
			print(file);
			return;
		}
		if(folder == null) {
			add("Nothing to print");
			return;
		}
		print(folder);
	}
	
	private PDFCell tcell(Object object) {
		return createCenteredCell(createTitleText(object.toString()));
	}
	
	private void print(FileData file) throws Exception {
		PDFTable table = createTable(40, 20, 20, 20);
		PDFCell cell;
		cell = tcell(file);
		cell.setColumnSpan(2);
		table.addCell(cell);
		ComputedDate readBeforeDate = file.getReadBefore();
		ComputedMinute readBeforeTime = file.getReadBeforeTime();
		if(readBeforeDate.consider()) {
			if(readBeforeTime.consider()) {
				readBefore = DateUtility.startTime(readBeforeDate).getTime() + readBeforeTime.getValue() * 60000L;
			} else {
				readBefore = DateUtility.endTime(readBeforeDate).getTime();
			}
			readBefore /= H;
		} else {
			readBefore = Long.MAX_VALUE / H;
		}
		String s;
		if(readBeforeDate.consider()) {
			s = DateUtility.formatDate(readBeforeDate);
			if(readBeforeTime.consider()) {
				s += readBeforeTime.toString();
			}
			s = "Read before: " + s;
		} else {
			s = "";
		}
		cell = tcell(s);
		cell.setColumnSpan(2);
		table.addCell(cell);
		s = file.getDetails();
		if(!StringUtility.isWhite(s)) {
			cell = createCell(s);
			cell.setColumnSpan(table.getNumberOfColumns());
			table.addCell(cell);
		}
		table.addCell(tcell("Person"));
		table.addCell(tcell("Circulated at"));
		table.addCell(tcell("Status"));
		table.addCell(tcell("Date & Time"));
		for(FileCirculation fc: file.listLinks(FileCirculation.class)) {
			table.addCell(createCell(fc.getPerson()));
			table.addCell(createCell(DateUtility.formatWithTimeHHMM(fc.getCirculatedAt())));
			table.addCell(createCell(new Text().append(color(fc)).append(fc.getStatusValue())));
			if(fc.getStatus() > 0) {
				table.addCell(createCell(DateUtility.formatWithTimeHHMM(fc.getReadAt())));				
			} else {
				table.addCell(createCell(""));
			}
			s = fc.getComments();
			if(!StringUtility.isWhite(s)) {
				cell = createCell("Comments: " + s);
				cell.setColumnSpan(table.getNumberOfColumns());
				table.addCell(cell);
			}
		}
		addTable(table);
	}
	
	private PDFColor color(FileCirculation fc) {
		long now = this.now.getTime() / H;
		long read;
		if(fc.getStatus() > 1) {
			read = fc.getReadAt().getTime() / H;
		} else {
			read = Long.MAX_VALUE / H;
		}
		if(fc.getStatus() > 1) {
			return read <= readBefore ? PDFColor.BLACK : PDFColor.PINK;
		}
		if(now > readBefore) {
			return PDFColor.RED;
		}
		long circ = fc.getCirculatedAt().getTime() / H;
		if(fc.getStatus() > 0 || ((now - circ) / 24) < 3) { // Less than 3 days
			return PDFColor.BLACK;
		}
		return PDFColor.ORANGE;
	}
	
	private void print(FileFolder folder) throws Exception {
		add(tcell("Folder: " + folder));
		for(FileData file: folder.listLinks(FileData.class, true)) {
			print(file);
			addGap(5);
		}
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
		t.append(16, PDFFont.BOLD).append("File/Document Circulation Status");
		t.newLine().append(14).append("Date: " + DateUtility.formatWithTimeHHMM(now) + " UTC");
    	return t;
    }
}
