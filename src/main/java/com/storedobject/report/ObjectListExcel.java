package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.HasContacts;
import com.storedobject.core.ReportDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.TableHeader;

import java.util.function.Predicate;

public class ObjectListExcel<T extends StoredObject> extends PDFReport {

    protected final ReportDefinition reportDefinition;

    public ObjectListExcel(Device device, String reportDefinitionName) {
        this(device, (ReportDefinition) null);
    }

    public ObjectListExcel(Device device, Class<T> dataClass, String... attributes) {
        this(device, dataClass, false, attributes);
    }

    public ObjectListExcel(Device device, Class<T> dataClass, boolean any, String... attributes) {
        this(device, ReportDefinition.create(dataClass, attributes));
    }

    public ObjectListExcel(Device device, Class<T> dataClass, Iterable<String> attributes) {
        this(device, dataClass, false, attributes);
    }

    public ObjectListExcel(Device device, Class<T> dataClass, boolean any, Iterable<String> attributes) {
        this(device, ReportDefinition.create(dataClass, attributes));
    }

    public ObjectListExcel(Device device, ReportDefinition reportDefinition) {
        super(device);
        this.reportDefinition = reportDefinition;
    }

    protected String getColumnCaption(String columnName, int columnIndex) {
        return "";
    }

    protected void customizeTableHeader(TableHeader tableHeader) {
    }

    protected HasContacts getTitleEntity() {
        return getEntity();
    }

    protected String[] getExtraCaptions() {
        return null;
    }

    @Override
    public void generateContent() throws Exception {
    }

    public String getOrderBy() {
        return reportDefinition.getOrderBy();
    }

    public void setExtraCondition(String extraCondition) {
    }

    public String getExtraCondition() {
        return null;
    }

    public void setLoadFilter(Predicate<T> loadFilter) {
    }

    public Predicate<T> getLoadFilter() {
        return null;
    }

    public long getRowCount(T object) {
        return Math.random() > 0.5 ? 1 : 2;
    }
}
