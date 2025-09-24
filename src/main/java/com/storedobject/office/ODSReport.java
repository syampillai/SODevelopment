package com.storedobject.office;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

import java.io.InputStream;

public class ODSReport extends ODS {

    private final Device device;
    private boolean executed = false;

    /**
     * Constructor.
     *
     * @param device Device
     * This will create an blank ODS file and the content may be manipulated in the generateContent() method.
     */
    public ODSReport(Device device) {
        this.device = device;
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param in Input stream containing an ODS file with some content.
     */
    public ODSReport(Device device, InputStream in) {
        super(in);
        this.device = device;
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param databaseFileName Database file name (FileData) containing ODS file with some content.
     */
    public ODSReport(Device device, String databaseFileName) {
        super(databaseFileName);
        this.device = device;
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param fileData Database file containing ODS file with some content.
     */
    public ODSReport(Device device, FileData fileData) {
        super(fileData);
        this.device = device;
    }

    /**
     * Constructor
     *
     * @param device Device
     * @param templateId Id of the stream data containing ODS file with some content.
     */
    public ODSReport(Device device, Id templateId) {
        super(templateId);
        this.device = device;
    }

    /**
     * Constructor
     *
     * @param device Device
     * @param streamData Stream data containing ODS file with some content.
     */
    public ODSReport(Device device, StreamData streamData) {
        super(streamData);
        this.device = device;
    }

    @Override
    public void execute() {
        if(!executed) {
            executed = true;
            if(blocked()) {
                throw new SORuntimeException(AccessControl.MESSAGE);
            }
            getDevice().view(this);
            return;
        }
        super.execute();
    }

    public void view() {
        execute();
    }

    @Override
    public final void setTransactionManager(TransactionManager tm) {
        super.setTransactionManager(tm);
    }

    @Override
    public final TransactionManager getTransactionManager() {
        return super.getTransactionManager();
    }

    public final void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Device getDevice() {
        return device;
    }

    private boolean blocked() {
        return isBlocked("ODS");
    }
}