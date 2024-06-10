package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import com.storedobject.pdf.TableHeader;

import java.util.List;
import java.util.function.Predicate;

/**
 * Generate an Excel with list of objects. The generated output will be formatted as per the definition in the given
 * {@link ReportDefinition} instance.
 * <p>Note: This logic doesn't consider the filters {@link ReportDefinition#getFilter()} while generating the
 * output.</p>
 *
 * @param <T> Type of objects to list (as defined in the {@link ReportDefinition}).
 * @author Syam
 */
public class ObjectListExcel<T extends StoredObject> extends ExcelReport implements JSONParameter, ObjectLister<T> {

    protected ReportDefinition reportDefinition;
    protected long row = 0;
    private String errorMessage;
    private TableHeader tableHeader;
    private ObjectIterator<T> objects;
    private String extraCondition;
    private Predicate<T> filter;
    private boolean init = false;

    public ObjectListExcel(Device device, String reportDefinitionName) {
        this(device, ObjectLister.rd(reportDefinitionName));
        if(reportDefinition == null) {
            errorMessage = "Definition not found: " + reportDefinitionName;
        }
    }

    public ObjectListExcel(Device device, Class<T> dataClass, String... attributes) {
        this(device, dataClass, false, attributes);
    }

    public ObjectListExcel(Device device, Class<T> dataClass, boolean any, String... attributes) {
        this(device, ReportDefinition.create(dataClass, attributes), any);
    }

    public ObjectListExcel(Device device, Class<T> dataClass, Iterable<String> attributes) {
        this(device, dataClass, false, attributes);
    }

    public ObjectListExcel(Device device, Class<T> dataClass, boolean any, Iterable<String> attributes) {
        this(device, ReportDefinition.create(dataClass, attributes), any);
    }

    public ObjectListExcel(Device device, ReportDefinition reportDefinition) {
        super(device);
        this.reportDefinition = reportDefinition;
        if(reportDefinition == null) {
            errorMessage = "Definition not found";
        } else {
            reportDefinition.setExecutable(this);
        }
    }

    private ObjectListExcel(Device device, ReportDefinition reportDefinition, boolean any) {
        this(device, reportDefinition);
        if(reportDefinition != null) {
            reportDefinition.setIncludeSubclasses(any);
        }
    }

    @Override
    public final void execute() {
        if(!init && errorMessage == null) {
            init = true;
            try {
                init();
            } catch(Throwable e) {
                return;
            }
        }
        super.execute();
    }

    private void init() {
        if(errorMessage != null || reportDefinition == null) {
            return;
        }
        RDLister<T> rdLister = new RDLister<>(this);
        tableHeader = rdLister.getTableHeader();
        objects = rdLister.listObjects();
    }

    protected HasContacts getTitleEntity() {
        return getEntity();
    }

    protected String[] getExtraCaptions() {
        return null;
    }

    @Override
    public void generateContent() throws Exception {
        if(errorMessage != null) {
            setCellValue("Error: " + errorMessage);
            return;
        }
        String s;
        if(reportDefinition.getPrintDescription()) {
            s = reportDefinition.getDescription();
            if(!s.isBlank()) {
                setCellValue(s);
                getNextRow();
            }
        }
        tableHeader.fillHeaderCells(this);
        List<ReportColumnDefinition> columns = reportDefinition.getColumns();
        Object[] cells = new Object[columns.size()];
        int i;
        for(T so: objects) {
            ++row;
            for(i = 0; i < cells.length; i++) {
                cells[i] = columns.get(i).getValue().apply(so);
            }
            tableHeader.fillRow(this, cells);
        }
    }

    public void setExtraCondition(String extraCondition) {
        this.extraCondition = extraCondition;
    }

    @Override
    public String getExtraCondition() {
        return extraCondition;
    }

    public void setLoadFilter(Predicate<T> loadFilter) {
        this.filter = loadFilter;
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return filter;
    }

    public long getRowCount(T object) {
        return row;
    }

    @Override
    public void setParameters(JSON json) {
        reportDefinition = getReportDefinition(json);
    }

    @Override
    public ReportDefinition getReportDefinition() {
        return reportDefinition;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
