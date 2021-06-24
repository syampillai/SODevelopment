package com.storedobject.report;

import com.storedobject.common.MethodInvoker;
import com.storedobject.common.StringList;
import com.storedobject.core.Device;
import com.storedobject.core.ObjectConverter;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFElement;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.PDFTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectPDF<M extends StoredObject> extends PDFReport {

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

	public <T extends StoredObject> PDFBlock<T> createBlock(int blockNumber, Class<T> objectClass, boolean any, StringList columns) {
		return new PDFBlock<>(blockNumber, objectClass, any, columns);
	}

	public PDFBlock<?> createBlock(int blockNumber, Class<? extends StoredObject> objectClass, boolean any, int link, int[] position) {
		return createBlock(blockNumber, objectClass, any, link, null, position);
	}

	public PDFBlock<?> createBlock(int blockNumber, Class<? extends StoredObject> objectClass, boolean any, int link, StringList columns, int[] position) {
		if(position == null) {
			position = new int[] { 1 };
		}
		PDFBlock<?> b = getBlock(position);
		return b == null ? null : b.createBlock(blockNumber, objectClass, any, link, columns);
	}

	public PDFBlock<?> getBlock(int[] position) {
		return null;
	}

	public int getBlockCount() {
		return 0;
	}

	public List<PDFBlock<StoredObject>> getBlocks() {
		return new ArrayList<>();
	}

	public boolean moveUp(PDFBlock<StoredObject> block) {
		return false;
	}

	public boolean moveDown(PDFBlock<StoredObject> block) {
		return false;
	}

	public void setRowBands(boolean rowBands) {
	}

	public boolean getRowBands() {
		return true;
	}

	public void remove(PDFBlock<StoredObject> block) {
	}

	public void renumberBlocks() {
	}

	public StringBuilder save(StringBuilder s) {
		return new StringBuilder();
	}

	@Override
	public String getTitleText() {
		return null;
	}

	@Override
	public PDFTable getTitleTable() {
		return null;
	}

	@Override
	public void produce() {
	}

	public void close() {
	}

	@Override
	public void generateContent() throws Exception {
	}

	protected void setIterator(Iterable<M> iterator) {
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
	}

	protected void prependHeaderRows(PDFTable table) {
	}

	protected void appendHeaderRows(PDFTable table) {
	}

	protected boolean canAdd(M object) {
		return true;
	}

	@SuppressWarnings("UnusedReturnValue")
	public class PDFBlock<T extends StoredObject> implements ObjectConverter<T, T> {

		private PDFBlock(int blockNumber, Class<T> objectClass, boolean any, StringList columns) {
		}

		public int getBlockNumber() {
			return 0;
		}

		public int getBlockCount() {
			return 0;
		}

		public List<PDFBlock<StoredObject>> getBlocks() {
			return new ArrayList<>();
		}

		public <O extends StoredObject> PDFBlock<O> createBlock(int blockNumber, Class<O> objectClass, boolean any, int link) {
			return createBlock(blockNumber, objectClass, any, link, null);
		}

		public <O extends StoredObject> PDFBlock<O> createBlock(int blockNumber, Class<O> objectClass, boolean any, int link, StringList columns) {
			return new PDFBlock<>(blockNumber, objectClass, any, columns);
		}

		public ReportColumn<T>[] getColumns() {
			//noinspection unchecked
			return new ReportColumn[0];
		}

		public int getColumnCount() {
			return 0;
		}

		public int getVisibleColumnCount() {
			return 0;
		}

		public void setColumns(StringList columns) {
		}

		public void setColumns(ReportColumn<T>[] columns) {
		}

		public void setColumns(Collection<ReportColumn<T>> columns) {
		}

		public void setBrowseColumns() {
		}

		public void setTitleText(String titleText) {
		}

		public String getTitleText() {
			return "";
		}

		public Class<T> getObjectClass() {
			ObjectPDF.this.getRowBands();
			return null;
		}

		@Override
		public T convert(T object) {
			return object;
		}
	}
}