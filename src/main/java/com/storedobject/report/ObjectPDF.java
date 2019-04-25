package com.storedobject.report;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.storedobject.common.MethodInvoker;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.pdf.PDFCell;
import com.storedobject.pdf.PDFColor;
import com.storedobject.pdf.PDFElement;
import com.storedobject.pdf.PDFRectangle;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

@SuppressWarnings("unused")
public class ObjectPDF<M extends StoredObject> extends PDFReport {

	private final ArrayList<PDFBlock<StoredObject>> blocks = new ArrayList<>();
	private boolean generated = false;
	private Iterable<M> iterator;
	private ObjectConverter<M, M> filter;
	private boolean rowBands = true;

	public ObjectPDF(Device device) {
		this(device, null, false, null);
	}

	public ObjectPDF(Device device, Class<M> masterClass) {
		this(device, masterClass, false, null);
	}

	public ObjectPDF(Device device, Class<M> masterClass, boolean any) {
		this(device, masterClass, any, null);
	}

	public ObjectPDF(Device device, Class<M> masterClass, StringList columns) {
		this(device, masterClass, false, columns);
	}
	
	public ObjectPDF(Device device, Class<M> masterClass, boolean any, StringList columns) {
		super(device);
		if(masterClass != null) {
			createBlock(-1, masterClass, any, columns);
		}
	}

	public <T extends StoredObject> PDFBlock<T> createBlock(int blockNumber, Class<T> objectClass, boolean any) {
		return createBlock(blockNumber, objectClass, any, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends StoredObject> PDFBlock<T> createBlock(int blockNumber, Class<T> objectClass, boolean any, StringList columns) {
		if(blockNumber < 0) {
			blockNumber = blocks.size() + 1;
		}
		PDFBlock<T> block = new PDFBlock<>(blockNumber, objectClass, any, columns);
		int i = 0;
		for(PDFBlock<StoredObject> b: blocks) {
			if(b.blockNumber == blockNumber) {
				return null;
			}
			if(b.blockNumber > blockNumber) {
				blocks.add(i, (ObjectPDF<M>.PDFBlock<StoredObject>) block);
				return block;
			}
			++i;
		}
		blocks.add((ObjectPDF<M>.PDFBlock<StoredObject>) block);
		return block;
	}

	public PDFBlock<?> createBlock(int blockNumber, Class<? extends StoredObject> objectClass, boolean any, int link, int[] position) {
		return createBlock(blockNumber, objectClass, any, link, null, position);
	}

	public PDFBlock<?> createBlock(int blockNumber, Class<? extends StoredObject> objectClass, boolean any, int link,
								   StringList columns, int[] position) {
		if(position == null) {
			position = new int[] { 1 };
		}
		PDFBlock<?> b = getBlock(position);
		return b == null ? null : b.createBlock(blockNumber, objectClass, any, link, columns);
	}

	public PDFBlock<?> getBlock(int[] position) {
		if(position == null || position.length == 0) {
			return null;
		}
		return getBlock(blocks, position, 0);
	}

	private PDFBlock<?> getBlock(ArrayList<PDFBlock<StoredObject>> blocks, int[] position, int startIndex) {
		for(PDFBlock<?> b: blocks) {
			if(b.blockNumber == position[startIndex]) {
				if(++startIndex == position.length) {
					return b;
				}
				return getBlock(b.blocks, position, startIndex);
			}
		}
		return null;
	}

	public int getBlockCount() {
		return blocks.size();
	}

	public List<PDFBlock<StoredObject>> getBlocks() {
		return blocks;
	}

	public boolean moveUp(PDFBlock<StoredObject> block) {
		try {
			return moveUp(blocks, block);
		} catch (Exception ignored) {
		}
		return false;
	}

	private boolean moveUp(ArrayList<PDFBlock<StoredObject>> blocks, PDFBlock<?> block) throws Exception {
		PDFBlock<StoredObject> b;
		int i;
		for(i = 0; i < blocks.size(); i++) {
			b = blocks.get(i);
			if(b == block) {
				if(i == 0) {
					throw new Exception();
				}
				b.blockNumber--;
				blocks.get(i - 1).blockNumber++;
				blocks.remove(i);
				blocks.add(i - 1, b);
				return true;
			}
			if(moveUp(b.blocks, block)) {
				return true;
			}
		}
		return false;
	}

	public boolean moveDown(PDFBlock<StoredObject> block) {
		try {
			return moveDown(blocks, block);
		} catch (Exception ignored) {
		}
		return false;
	}

	public void setRowBands(boolean rowBands) {
		this.rowBands = rowBands;
	}

	public boolean getRowBands() {
		return rowBands;
	}

	private boolean moveDown(ArrayList<PDFBlock<StoredObject>> blocks, PDFBlock<?> block) throws Exception {
		PDFBlock<StoredObject> b;
		int i;
		for(i = 0; i < blocks.size(); i++) {
			b = blocks.get(i);
			if(b == block) {
				if(i == (blocks.size() - 1)) {
					throw new Exception();
				}
				b.blockNumber++;
				blocks.get(i + 1).blockNumber--;
				blocks.remove(i);
				blocks.add(i + 1, b);
				return true;
			}
			if(moveDown(b.blocks, block)) {
				return true;
			}
		}
		return false;
	}

	public void remove(PDFBlock<StoredObject> block) {
		remove(blocks, block);
	}

	private boolean remove(ArrayList<PDFBlock<StoredObject>> blocks, PDFBlock<?> block) {
		PDFBlock<?> b;
		int i;
		for(i = 0; i < blocks.size(); i++) {
			b = blocks.get(i);
			if(b == block) {
				blocks.remove(i);
				for(; i < blocks.size(); i++) {
					blocks.get(i).blockNumber--;
				}
				return true;
			}
			if(remove(b.blocks, block)) {
				return true;
			}
		}
		return false;
	}

	public void renumberBlocks() {
		renumberBlocks(blocks);
	}

	private void renumberBlocks(ArrayList<PDFBlock<StoredObject>> blocks) {
		PDFBlock<?> b;
		for(int i = 0; i < blocks.size(); i++) {
			b = blocks.get(i);
			b.blockNumber = i + 1;
			renumberBlocks(b.blocks);
		}
	}

	public StringBuilder save(StringBuilder s) {
		if(blocks.size() == 0) {
			return s;
		}
		s.append("R:");
		PDFBlock<?> b;
		for(int i = 0; i < blocks.size(); i++) {
			if(i > 0) {
				s.append('|');
			}
			b = blocks.get(i);
			s.append('(').append(b.blockNumber).append(") ");
			s.append(b.objectClass.getName());
			if(b.any) {
				s.append("/Any");
			}
			s.append('|').append(b.titleText == null ? "" : b.titleText).append('|');
			if(b.columns != null) {
				for(ReportColumn<?> rc: b.columns) {
					rc.save(s);
					s.append(',');
				}
			}
			s.deleteCharAt(s.length() - 1);
			saveLink("" + b.blockNumber, b.blocks, s);
		}
		return s;
	}

	private void saveLink(String prefix, ArrayList<PDFBlock<StoredObject>> blocks, StringBuilder s) {
		prefix += ".";
		PDFBlock<?> b;
		for (PDFBlock<StoredObject> block : blocks) {
			b = block;
			s.append("|L:(").append(prefix).append(b.blockNumber).append(") ");
			s.append(b.objectClass.getName());
			if (b.any) {
				s.append("/Any");
			}
			if (b.link > 0) {
				s.append('/').append(b.link);
			}
			s.append('|').append(b.titleText == null ? "" : b.titleText).append('|');
			if (b.columns != null) {
				for (ReportColumn<?> rc : b.columns) {
					rc.save(s);
					s.append(',');
				}
			}
			s.deleteCharAt(s.length() - 1);
			saveLink(prefix + b.blockNumber, b.blocks, s);
		}
	}

	@Override
	public String getTitleText() {
		if(blocks.size() == 0) {
			return null;
		}
		PDFBlock<?> b = blocks.get(0);
		if(blocks.size() == 1) {
			return b.getTitleText();
		}
		return null;
	}

	@Override
	public PDFTable getTitleTable() {
		if(blocks.size() == 0) {
			return null;
		}
		PDFBlock<?> b = blocks.get(0);
		if(blocks.size() == 1) {
			return b.getTitleTable(super.getTitleTable());
		}
		return null;
	}

	@Override
	public PDFRectangle getPageSize() {
		PDFRectangle p = super.getPageSize();
		if(getPageSizeIndex() != 0) {
			return p;
		}
		if(canFit(103)) {
			return p;
		}
		canFit(153);
		return p.rotate();
	}

	private boolean canFit(int max) {
		boolean canFit = true;
		for(PDFBlock<?> b: blocks) {
			canFit &= b.canFit(max);
		}
		return canFit;
	}

	@Override
	public void produce() {
		if(!generated) {
			for(PDFBlock<?> b: blocks) {
				b.execute();
			}
			generated = true;
		}
		super.produce();
	}
	
	public void close() {
		super.close();
		generated = false;
	}

	@Override
	public void generateContent() throws Exception {
		for(PDFBlock<?> b: blocks) {
			b.generateContent(blocks.size() != 1, false);
		}
	}
	
	protected void setIterator(Iterable<M> iterator) {
		this.iterator = iterator;
	}
	
	protected MethodInvoker getMethodInvoker(String columnName) {
		return null;
	}
	
	protected String getColumnCaption(String columnName) {
		return null;
	}
	
	protected int getHorizontalAlignment(String columnName) {
		return PDFElement.ALIGN_LEFT;
	}
	
	protected int getVerticalAlignment(String columnName) {
		return PDFElement.ALIGN_MIDDLE;
	}
	
	protected int getMinimumColumnWidth(String columnName) {
		return 0;
	}
	
	protected void setFilter(ObjectConverter<M, M> filter) {
		this.filter = filter;
	}
	
	protected void prependHeaderRows(PDFTable table) {
	}

	protected void appendHeaderRows(PDFTable table) {
	}
	
    protected boolean canAdd(M object) {
    	return true;
    }

	private static void createColumns(String prefix, ClassAttribute<?> ca, ArrayList<String> columns, int level) {
		if(level <= 0) {
			return;
		}
		--level;
		Method m;
		String cc, newPrefix;
		for(String c: ca.getAttributes()) {
			if(prefix.length() > 0) {
				newPrefix = prefix + "." + c;
			} else {
				newPrefix = c;
			}
			columns.add(newPrefix);
			m = ca.getMethod(c);
			if(Id.class.isAssignableFrom(m.getReturnType())) {
				cc = m.getName();
				try {
					m = ca.getObjectClass().getMethod(cc.substring(0, cc.length() - 2));
				} catch (Exception e) {
					m = null;
				}
				if(m != null && !Modifier.isStatic(m.getModifiers()) && StoredObject.class.isAssignableFrom(m.getReturnType())) {
					@SuppressWarnings("unchecked")
					Class<? extends StoredObject> soClass = (Class<? extends StoredObject>) m.getReturnType();
					createColumns(newPrefix, StoredObjectUtility.classAttribute(soClass), columns, level);
				}
			}
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	public class PDFBlock<T extends StoredObject> implements ObjectConverter<T, T> {

		private final ArrayList<PDFBlock<StoredObject>> blocks = new ArrayList<>();
		private final ArrayList<T> objects = new ArrayList<>();
		private final Class<T> objectClass;
		private final ClassAttribute<T> ca;
		private ReportColumn<T>[] columns = null;
		private Iterable<T> iterable;
		private Iterator<T> iterator;
		private int columnCount;
		private boolean canFit = true;
		private String titleText;
		private int link;
		private boolean any;
		private StoredObject parentObject;
		private int blockNumber;

		private PDFBlock(int blockNumber, Class<T> objectClass, boolean any, StringList columns) {
			this.objectClass = objectClass;
			this.blockNumber = blockNumber;
			this.any = any;
			ca = StoredObjectUtility.classAttribute(objectClass);
			if(columns == null) {
				return;
			}
			setColumns(columns);
		}

		public int getBlockNumber() {
			return blockNumber;
		}

		public int getBlockCount() {
			return blocks.size();
		}

		public List<PDFBlock<StoredObject>> getBlocks() {
			return blocks;
		}

		public <O extends StoredObject> PDFBlock<O> createBlock(int blockNumber, Class<O> objectClass, boolean any, int link) {
			return createBlock(blockNumber, objectClass, any, link, null);
		}

		@SuppressWarnings("unchecked")
		public <O extends StoredObject> PDFBlock<O> createBlock(int blockNumber, Class<O> objectClass, boolean any, int link, StringList columns) {
			if(blockNumber < 0) {
				blockNumber = blocks.size() + 1;
			}
			PDFBlock<O> block = new PDFBlock<>(blockNumber, objectClass, any, columns);
			block.link = link;
			int i = 0;
			for(PDFBlock<?> b: blocks) {
				if(b.blockNumber == blockNumber) {
					return null;
				}
				if(b.blockNumber > blockNumber) {
					blocks.add(i, (ObjectPDF<M>.PDFBlock<StoredObject>) block);
					return block;
				}
				++i;
			}
			blocks.add((ObjectPDF<M>.PDFBlock<StoredObject>) block);
			return block;
		}

		public ReportColumn<T>[] getColumns() {
			if(columns == null) {
				setColumns((StringList)null);
			}
			return columns;
		}

		public int getColumnCount() {
			return columns == null ? 0 : columns.length;
		}

		public int getVisibleColumnCount() {
			if(columns == null) {
				columnCount = 0;
				return 0;
			}
			int c = 0;
			for(ReportColumn<T> rc: columns) {
				if(rc.isVisible()) {
					++c;
				}
			}
			columnCount = c;
			return c;
		}
		
		private MethodInvoker getMethodInvoker(String columnName) {
			if(blockNumber != 1) {
				return null;
			}
			return ObjectPDF.this.getMethodInvoker(columnName);
		}
		
		private String getColumnCaption(String columnName) {
			if(blockNumber != 1) {
				return null;
			}
			return ObjectPDF.this.getColumnCaption(columnName);
		}

		private int getHorizontalAlignment(String columnName) {
			if(blockNumber != 1) {
				return PDFElement.ALIGN_LEFT;
			}
			return ObjectPDF.this.getHorizontalAlignment(columnName);
		}
		
		private int getVerticalAlignment(String columnName) {
			if(blockNumber != 1) {
				return PDFElement.ALIGN_MIDDLE;
			}
			return ObjectPDF.this.getVerticalAlignment(columnName);
		}
		
		private int getMinimumColumnWidth(String columnName) {
			if(blockNumber != 1) {
				return 0;
			}
			return ObjectPDF.this.getMinimumColumnWidth(columnName);
		}

		@SuppressWarnings("unchecked")
		public void setColumns(StringList columns) {
			boolean deep;
			if(columns == null) {
				deep = true;
				ArrayList<String> vc = new ArrayList<>();
				createColumns("", ca, vc, 3);
				columns = new StringList(vc);
			} else {
				deep = false;
			}
			this.columns = new ReportColumn[columns.size()];
			ReportColumn<T> rc;
			int i = 0, w;
			String c;
			for(String cname: columns) {
				try {
					rc = new ReportColumn<>(ca, cname, getMethodInvoker(cname));
				} catch(Throwable error) {
					rc = new ReportColumn<>(ca, "Class.Name as [ERROR] " + cname);
				}
				c = getColumnCaption(cname);
				if(c != null) {
					rc.setTitle(c);
				}
				w = getMinimumColumnWidth(cname);
				if(w > 1) {
					rc.setWidht(w);
				}
				this.columns[i++] = rc;
				if(deep && cname.indexOf('.') > 0) {
					rc.setVisible(false);
				}
			}
			getVisibleColumnCount();
		}

		public void setColumns(ReportColumn<T>[] columns) {
			if(columns == null) {
				setColumns((StringList)null);
				return;
			}
			this.columns = columns;
			getVisibleColumnCount();
		}

		public void setColumns(Collection<ReportColumn<T>> columns) {
			if(columns == null) {
				setColumns((StringList)null);
				return;
			}
			@SuppressWarnings("unchecked")
			ReportColumn<T>[] cs = new ReportColumn[columns.size()];
			int i = 0;
			for(ReportColumn<T> rc: columns) {
				cs[i++] = rc;
			}
			setColumns(cs);
		}

		public void setBrowseColumns() {
			setColumns(StoredObjectUtility.browseColumns(ca.getObjectClass()));
		}

		public void setTitleText(String titleText) {
			this.titleText = titleText;
		}

		public String getTitleText() {
			return titleText == null ? ca.getTitle() : titleText;
		}

		private PDFTable getTitleTable(PDFTable t) {
			StringBuilder s = describeCondition();
			if(s.length() > 0) {
				t.addCell(createCenteredCell(createTitleText(s.toString())));
			}
			return t;
		}

		private StringBuilder describeCondition() {
			StringBuilder s = new StringBuilder();
			for(ReportColumn<T> rc: columns) {
				rc.describeCondition(s);
			}
			return s;
		}

		private void printTitle(boolean links) {
			String t = ObjectPDF.this.blocks.size() > 1 ? getTitleText() : titleText;
			StringBuilder s = describeCondition();
			if((t != null && t.length() > 0) || s.length() > 0) {
				PDFTable table = createTable(1);
				if(t != null && t.length() > 0) {
					table.addCell(createCenteredCell(createTitleText(t, getFontSize() + (links ? -1 : 0))));
				}
				if(s.length() > 0) {
					table.addCell(createCenteredCell(createTitleText(s.toString(), getFontSize())));
				}
				if(links) {
					table.setSpacingBefore(0);
					table.setSpacingAfter(0);
				}
				add(table);
			}
		}

		private boolean canFit(int max) {
			if(columns == null) {
				setColumns(ca.getAttributes());
			}
			if(columnCount == 0) {
				getVisibleColumnCount();
			}
			boolean canFit = canFitInt(max);
			for(PDFBlock<?> b: blocks) {
				canFit &= b.canFit(max);
			}
			return canFit;
		}

		private boolean canFitInt(int max) {
			canFit = true;
			int totalWidth = 0;
			for(ReportColumn<T> rc: columns) {
				if(!rc.isVisible()) {
					continue;
				}
				totalWidth += rc.getWidth() + 2;
			}
			if(totalWidth <= max) {
				return true;
			}
			int w;
			for(ReportColumn<T> rc: columns) {
				if(!rc.isVisible()) {
					continue;
				}
				w = max * (rc.getWidth() + 2) / totalWidth;
				if(w < (max == 103 ? (rc.getMinumumWidth() + 2) : 3)) {
					canFit = false;
					return false;
				}
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		private void load(int count) {
			if(iterable == null) {
				return;
			}
			if(iterator == null) {
				iterator = iterable.iterator();
			}
			T object;
			while(iterator.hasNext()) {
				object = iterator.next();
				if(blockNumber == 1 && ObjectPDF.this.filter != null) {
					object = (T) ObjectPDF.this.filter.convert((M) object);
					if(object == null) {
						continue;
					}
				}
				if(blockNumber == 1 && !canAdd((M)object)) {
					continue;
				}
				if(blocks.size() > 0) {
					objects.add(object);
				}
				for(ReportColumn<T> rc: columns) {
					rc.setValue(object);
				}
				--count;
				if(count == 0) {
					return;
				}
			}
			if(iterable instanceof Closeable) {
				try {
					((Closeable)iterable).close();
				} catch (IOException ignored) {
				}
			}
			if(iterator instanceof Closeable) {
				try {
					((Closeable)iterator).close();
				} catch (IOException ignored) {
				}
			}
		}

		private boolean setParentObject(StoredObject parentObject) throws Exception {
			if(this.parentObject == null || this.parentObject != parentObject) {
				this.parentObject = parentObject;
				execute();
			}
			return generateContent(true, true);
		}

		@SuppressWarnings("unchecked")
		private void execute() {
			if(columns == null) {
				setColumns(ca.getAttributes());
			}
			if(columnCount > 0) {
				StringBuilder sb = new StringBuilder();
				int o, nOrder, order = 1;
				while(true) {
					nOrder = order;
					for(ReportColumn<T> rc: columns) {
						o = rc.getOrder();
						if(o == order) {
							if(sb.length() > 0) {
								sb.append(',');
							}
							sb.append(rc.getName());
							++order;
							nOrder = Integer.MAX_VALUE;
							break;
						}
						if(o > order && (nOrder == order || nOrder > o)) {
							nOrder = o;
						}
					}
					if(order == nOrder) {
						break;
					}
					if(nOrder != Integer.MAX_VALUE) {
						order = nOrder;
					}
				}
				String orderBy = sb.toString();
				sb.delete(0, sb.length());
				for(ReportColumn<T> rc: columns) {
					rc.appendCondition(sb);
				}
				if(parentObject == null) {
					if(ObjectPDF.this.iterator == null || blockNumber != 1) {
						iterable = ObjectIterator.create(StoredObject.list(objectClass, sb.toString(), orderBy, any), this);
					} else {
						iterable = (Iterable<T>) ObjectPDF.this.iterator;
					}
				} else {
					iterable = ObjectIterator.create(parentObject.listLinks(link, objectClass, sb.toString(), orderBy, any), this);
				}
				load(100);
				if(columns[0].valueExists() && blocks.size() > 0) {
					T object = objects.get(0);
					for(PDFBlock<?> b: blocks) {
						b.parentObject = object;
						b.execute();
					}
				}
			} else {
				iterable = null;
			}
		}

		private boolean generateContent(boolean printTitle, boolean links) throws Exception {
			if(iterable == null || columnCount == 0) {
				return false;
			}
			if(printTitle) {
				printTitle(links);
			}
			PDFCell cell;
			PDFTable table;
			int i;
			if(!canFit) {
				Text t = new Text();
				t.append("Report contains " + columnCount + " columns and the content does not fit!\nReporting pruned!!", PDFColor.RED);
				ObjectPDF.this.add(t);
				table = createTable(5, 70, 25);
				table.setWidthPercentage(70);
				table.addCell(createCenteredCell(createTitleText("No.", getFontSize())));
				table.addCell(createCenteredCell(createTitleText("Column Heading", getFontSize())));
				table.addCell(createCell(createTitleText("Minimum Character Width", getFontSize())));
				table.setHeaderRows(1);
				i = 0;
				for(ReportColumn<T> rc: columns) {
					if(!rc.isVisible()) {
						continue;
					}
					++i;
					table.addCell(createCenteredCell("" + i));
					table.addCell(createCell(rc.getTitle()));
					table.addCell(createCenteredCell("" + rc.getWidth()));
				}
				add(table);
				return true;
			}
			if(columnCount == 1) {
				table = createTable(1);
			} else {
				int[] w = new int[columnCount];
				i = 0;
				for(ReportColumn<T> rc: columns) {
					if(!rc.isVisible()) {
						continue;
					}
					w[i++] = rc.getWidth();
				}
				table = createTable(w);
			}
			if(blockNumber == 1) {
				prependHeaderRows(table);
			}
			for(ReportColumn<T> rc: columns) {
				if(!rc.isVisible()) {
					continue;
				}
				cell = createCenteredCell(createTitleText(rc.getTitle(), getFontSize()));
				table.addCell(cell);
			}
			if(blockNumber == 1) {
				appendHeaderRows(table);
			}
			table.setHeaderRows(table.getNumberOfRows());
			if(links) {
				table.setSpacingBefore(0);
			}
			int rows = 0;
			boolean printed = false;
			while(true) {
				if(!columns[0].valueExists()) {
					load(10);
					if(!columns[0].valueExists()) {
						break;
					}
				}
				++rows;
				for(ReportColumn<T> rc: columns) {
					if(!rc.isVisible()) {
						continue;
					}
					cell = createCell(rc.removeValue(), getHorizontalAlignment(rc.getName()), getVerticalAlignment(rc.getName()));
					if(rowBands) {
						cell.setGrayFill(rows % 2 == 0 ? 0.9f : 1f);
					}
					table.addCell(cell);
				}
				if(blocks.size() > 0) {
					table.setSpacingAfter(0);
					printed = true;
					add(table);
					table.deleteBodyRows();
					rows = 0;
					T object = objects.remove(0);
					boolean linksPrinted = false;
					for(PDFBlock<?> b: blocks) {
						linksPrinted = linksPrinted || b.setParentObject(object);
					}
					table.setSkipFirstHeader(!linksPrinted);
					continue;
				}
				if((rows % 80) == 0) {
					printed = true;
					addTable(table);
					rows = 0;
				}
			}
			if(rows > 0) {
				printed = true;
				add(table);
			}
			return printed;
		}

		public Class<T> getObjectClass() {
			return objectClass;
		}

		@Override
		public T convert(T object) {
			for(ReportColumn<T> rc: columns) {
				if(rc.isDatabase()) {
					continue;
				}
				object = rc.convert(object);
				if(object == null) {
					return null;
				}
			}
			return object;
		}
	}
}
