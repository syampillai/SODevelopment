package com.storedobject.report;

import com.storedobject.core.Device;
import com.storedobject.core.HasContacts;
import com.storedobject.core.ReportDefinition;
import com.storedobject.core.StoredObject;
import com.storedobject.pdf.PDFReport;
import com.storedobject.pdf.TableHeader;

public class ObjectList<T extends StoredObject> extends PDFReport {

    protected final ReportDefinition reportDefinition;

    public ObjectList(Device device, String reportDefinitionName) {
        this(device, (ReportDefinition) null);
    }

    public ObjectList(Device device, Class<T> dataClass) {
        this(device, dataClass, false);
    }

    public ObjectList(Device device, Class<T> dataClass, boolean any) {
        this(device, ReportDefinition.create(dataClass));
    }

    public ObjectList(Device device, ReportDefinition reportDefinition) {
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
}
