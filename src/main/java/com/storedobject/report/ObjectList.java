package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.pdf.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generate a PDF with a list of objects. The generated output will be formatted as per the definition in the given
 * {@link ReportDefinition} instance.
 * <p>Note: This logic doesn't consider the filters {@link ReportDefinition#getFilter()} while generating the
 * output.</p>
 *
 * @param <T> Type of objects to list (as defined in the {@link ReportDefinition}).
 * @author Syam
 */
public class ObjectList<T extends StoredObject> extends PDFReport implements JSONParameter, ObjectLister<T> {

    protected ReportDefinition reportDefinition;
    protected long row = 0;
    private String errorMessage;
    private TableHeader tableHeader;
    private ObjectIterator<T> objects;
    private String extraCondition;
    private Predicate<T> filter;

    public ObjectList(Device device) {
        super(device);
    }

    public ObjectList(Device device, String reportDefinitionName) {
        this(device, ObjectLister.rd(reportDefinitionName));
        if(reportDefinition == null) {
            errorMessage = "Definition not found: " + reportDefinitionName;
        }
    }

    public ObjectList(Device device, Class<T> dataClass, String... attributes) {
        this(device, dataClass, false, attributes);
    }

    public ObjectList(Device device, Class<T> dataClass, boolean any, String... attributes) {
        this(device, ReportDefinition.create(dataClass, attributes), any);
    }

    public ObjectList(Device device, Class<T> dataClass, Iterable<String> attributes) {
        this(device, dataClass, false, attributes);
    }

    public ObjectList(Device device, Class<T> dataClass, boolean any, Iterable<String> attributes) {
        this(device, ReportDefinition.create(dataClass, attributes), any);
    }

    public ObjectList(Device device, ReportDefinition reportDefinition) {
        super(device);
        this.reportDefinition = reportDefinition;
        if(reportDefinition == null) {
            errorMessage = "Definition not found";
        } else {
            reportDefinition.setExecutable(this);
        }
    }

    private ObjectList(Device device, ReportDefinition reportDefinition, boolean any) {
        this(device, reportDefinition);
        if(reportDefinition != null) {
            reportDefinition.setIncludeSubclasses(any);
        }
    }

    @Override
    public void setParameters(JSON json) {
        reportDefinition = getReportDefinition(json);
    }

    @Override
    public void open() {
        if(errorMessage == null) {
            try {
                init();
            } catch(Throwable e) {
                super.open();
                dumpError(e);
                return;
            }
        }
        super.open();
    }

    private void init() {
        if(errorMessage != null || reportDefinition == null) {
            return;
        }
        setFontSize(switch(reportDefinition.getBaseFontSize()) {
            case 0 -> 8;
            case 1 -> 6;
            default -> 10;
        });
        RDLister<T> rdLister = new RDLister<>(this);
        tableHeader = rdLister.getTableHeader();
        objects = rdLister.listObjects();
    }

    @Override
    public PDFTable getTitleTable() {
        if(errorMessage == null && reportDefinition == null) {
            errorMessage = "Parameters missing";
        }
        if(errorMessage != null) {
            PDFTable table = new PDFTable(1);
            table.addCell(createCenteredCell("ERROR"));
            return table;
        }
        String[] t = getExtraCaptions();
        String[] c = new String[1 + (t == null ? 0 : t.length)];
        c[0] = reportDefinition.getTitle();
        if(t != null) {
            System.arraycopy(t, 0, c, 1, c.length - 1);
        }
        return createTitleTable(getTitleEntity(), c);
    }

    protected HasContacts getTitleEntity() {
        return getEntity();
    }

    protected String[] getExtraCaptions() {
        return null;
    }

    @Override
    public int getPageOrientation() {
        if(reportDefinition != null) {
            return reportDefinition.getOrientation() == 0 ? ORIENTATION_PORTRAIT : ORIENTATION_LANDSCAPE;
        }
        return super.getPageOrientation();
    }

    @Override
    public void generateContent() throws Exception {
        if(errorMessage != null) {
            add("Error: " + errorMessage);
            return;
        }
        if(reportDefinition == null) {
            add("Parameters missing");
            return;
        }
        String s;
        if(reportDefinition.getPrintDescription()) {
            s = reportDefinition.getDescription();
            if(!s.isBlank()) {
                PDFCell cell = createCenteredCell(createTitleText(s));
                cell.setPadding(3);
                add(cell);
            }
        }
        Function<PDFCell, PDFCell> cellCustomizer = getHeaderCellCustomizer();
        if(cellCustomizer != null) {
            tableHeader.setCellCustomizer(cellCustomizer);
        }
        PDFTable table = tableHeader.createTable(this);
        cellCustomizer = getBodyCellCustomizer();
        if(cellCustomizer != null) {
            tableHeader.setCellCustomizer(cellCustomizer);
        }
        List<ReportColumnDefinition> columns = reportDefinition.getColumns();
        Object[] cells = new Object[columns.size()];
        int i;
        for(T so: objects) {
            ++row;
            for(i = 0; i < cells.length; i++) {
                cells[i] = columns.get(i).getValue().apply(so);
            }
            tableHeader.addRow(cells);
            if(row > 50) {
                add(table);
            }
        }
        table.setComplete(true);
        add(table);
    }

    protected Function<PDFCell, PDFCell> getHeaderCellCustomizer() {
        return null;
    }

    protected Function<PDFCell, PDFCell> getBodyCellCustomizer() {
        return null;
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
    public ReportDefinition getReportDefinition() {
        return reportDefinition;
    }

    @Override
    public int getCharCount(Object object) {
        return toCharCount(object);
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
