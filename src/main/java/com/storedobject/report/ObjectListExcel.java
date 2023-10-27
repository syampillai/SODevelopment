package com.storedobject.report;

import com.storedobject.core.*;
import com.storedobject.office.ExcelReport;
import com.storedobject.pdf.PDFElement;
import com.storedobject.pdf.TableHeader;

import java.util.ArrayList;
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
public class ObjectListExcel<T extends StoredObject> extends ExcelReport implements JSONParameter {

    protected ReportDefinition reportDefinition;
    private List<ReportColumnDefinition> columns;
    protected long row = 0;
    private String error;
    private TableHeader tableHeader;
    private ObjectIterator<T> objects;
    private String extraCondition;
    private Predicate<T> filter;
    private boolean init = false;

    public ObjectListExcel(Device device, String reportDefinitionName) {
        this(device, rd(reportDefinitionName));
        if(reportDefinition == null) {
            error = "Definition not found: " + reportDefinitionName;
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
            error = "Definition not found";
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

    private static ReportDefinition rd(String rd) {
        if(rd.startsWith("Id:")) {
            return StoredObject.get(ReportDefinition.class, "Id=" + rd.substring(3), true);
        }
        if(rd.contains(".")) {
            boolean any = rd.toLowerCase().endsWith("/any");
            if(any) {
                rd = rd.substring(0, rd.length() - 4);
            }
            if(JavaClassLoader.exists(rd)) {
                try {
                    Class<?> dClass = JavaClassLoader.getLogic(rd);
                    if(StoredObject.class.isAssignableFrom(dClass)) {
                        @SuppressWarnings("unchecked")
                        ReportDefinition rDef = ReportDefinition.create((Class<? extends StoredObject>) dClass);
                        if(any) {
                            rDef.setIncludeSubclasses(true);
                        }
                        return rDef;
                    }
                } catch(ClassNotFoundException ignored) {
                }
            }
        }
        return StoredObject.get(ReportDefinition.class, "lower(Name)='" + rd.toLowerCase().trim() + "'",
                true);
    }

    @Override
    public final void execute() {
        if(!init && error == null) {
            init = true;
            try {
                init();
            } catch(Throwable e) {
                return;
            }
        }
        super.execute();
    }

    protected String getColumnCaption(String columnName, int columnIndex) {
        return columns.get(columnIndex).getCaption();
    }

    private void init() {
        if(error != null || reportDefinition == null) {
            return;
        }
        columns = reportDefinition.getColumns();
        String[] captions = new String[columns.size()];
        int i;
        for(i = 0; i < captions.length; i++) {
            captions[i] = getColumnCaption(columns.get(i).getAttribute(), i);
        }
        String cond = getExtraCondition();
        if(cond == null || cond.isBlank()) {
            cond = reportDefinition.getCondition();
        } else {
            String c = reportDefinition.getCondition();
            if(c != null && !c.isBlank()) {
                cond = "(" + cond + ") AND " + c;
            }
        }
        tableHeader = new TableHeader(captions);
        //noinspection unchecked
        objects = StoredObject.list((Class<T>)reportDefinition.getClassForData(), cond, getOrderBy(),
                reportDefinition.getIncludeSubclasses());
        Predicate<T> filter = getLoadFilter();
        if(filter != null) {
            objects = objects.filter(filter);
        }
        List<T> head = new ArrayList<>();
        int count = 20;
        while(count-- > 0) {
            if(objects.hasNext()) {
                head.add(objects.next());
            } else {
                break;
            }
        }
        objects = ObjectIterator.create(head).add(objects);
        if(head.isEmpty()) {
            return;
        }
        int[] w = new int[columns.size()];
        for(i = 0; i < w.length; i++) {
            w[i] = columns.get(i).getRelativeWidth();
            if(w[i] == 0) {
                w[i] = 10;
            }
        }
        ReportColumnDefinition c;
        tableHeader.setWidths(w);
        T so = head.get(0);
        for(i = 0; i < columns.size(); i++) {
            c = columns.get(i);
            if(c.getHorizontalAlignment() == 0) {
                c.setHorizontalAlignment(Utility.isRightAligned(c.getValue().apply(so)) ? 3 : 1);
            }
            tableHeader.setHorizontalAlignment(i, switch(c.getHorizontalAlignment()) {
                case 1 -> PDFElement.ALIGN_LEFT;
                case 2 -> PDFElement.ALIGN_CENTER;
                case 3 -> PDFElement.ALIGN_RIGHT;
                default -> PDFElement.ALIGN_UNDEFINED;
            });
            tableHeader.setVerticalAlignment(i, switch(c.getVerticalAlignment()) {
                case 0 -> PDFElement.ALIGN_TOP;
                case 1 -> PDFElement.ALIGN_CENTER;
                case 2 -> PDFElement.ALIGN_BOTTOM;
                default -> PDFElement.ALIGN_UNDEFINED;
            });
        }
        customizeTableHeader(tableHeader);
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
        if(error != null) {
            setCellValue("Error: " + error);
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

    public String getOrderBy() {
        return reportDefinition.getOrderBy();
    }

    public void setExtraCondition(String extraCondition) {
        this.extraCondition = extraCondition;
    }

    public String getExtraCondition() {
        return extraCondition;
    }

    public void setLoadFilter(Predicate<T> loadFilter) {
        this.filter = loadFilter;
    }

    public Predicate<T> getLoadFilter() {
        return filter;
    }

    public long getRowCount(T object) {
        return row;
    }

    @Override
    public void setParameters(JSON json) {
        String definition = json.getString("definition");
        if(definition != null) {
            reportDefinition = rd(definition);
            if(reportDefinition == null) {
                return;
            }
            error = "Definition not found: " + definition;
            return;
        }
        try {
            @SuppressWarnings("unchecked") Class<T> dataClass = (Class<T>) json.getDataClass("className");
            reportDefinition = ReportDefinition.create(dataClass, json.getStringList("attributes"));
            Boolean any = json.getBoolean("any");
            if(any != null && any) {
                reportDefinition.setIncludeSubclasses(true);
            }
            String extra = json.getString("extraCondition");
            if(extra != null) {
                extraCondition = extra;
            }
        } catch (Exception e) {
            error = e.getMessage();
        }
    }
}
