package com.storedobject.office;

import com.storedobject.core.*;

import java.io.InputStream;

public class ODSReport extends ODS {

    /**
     * Constructor.
     *
     * @param device Device
     * This will create an blank ODS file and the content may be manipulated in the generateContent() method.
     */
    public ODSReport(Device device) {
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param in Input stream containing an ODS file with some content.
     */
    public ODSReport(Device device, InputStream in) {
        super(in);
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param databaseFileName Database file name (FileData) containing ODS file with some content.
     */
    public ODSReport(Device device, String databaseFileName) {
        super(databaseFileName);
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param fileData Database file containing ODS file with some content.
     */
    public ODSReport(Device device, FileData fileData) {
        super(fileData);
    }

    /**
     * Constructor
     *
     * @param device Device
     * @param templateId Id of the stream data containing ODS file with some content.
     */
    public ODSReport(Device device, Id templateId) {
        super(templateId);
    }

    /**
     * Constructor
     *
     * @param device Device
     * @param streamData Stream data containing ODS file with some content.
     */
    public ODSReport(Device device, StreamData streamData) {
        super(streamData);
    }

    public void view() {
        execute();
    }

    @Override
    public final void setTransactionManager(TransactionManager tm) {
    }

    public final void setEntity(Entity entity) {
    }

    @Override
    public Entity getEntity() {
        return null;
    }

    public ReportFormat getReportFormat() {
        return null;
    }

    @Override
    public Device getDevice() {
        return null;
    }
}