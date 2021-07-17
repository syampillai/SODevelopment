package com.storedobject.office;

import com.storedobject.core.Device;
import com.storedobject.core.FileData;
import com.storedobject.core.Id;
import com.storedobject.core.StreamData;

import java.io.InputStream;

public class ExcelReport extends Excel {

    private final Device device;

    /**
     * Constructor.
     *
     * @param device Device
     * This will create an blank Excel file and the content may be manipulated in the generateContent() method.
     */
    public ExcelReport(Device device) {
        this.device = device;
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param in Input stream containing an Excel file with some content.
     */
    public ExcelReport(Device device, InputStream in) {
        super(in);
        this.device = device;
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param databaseFileName Database file name (FileData) containing Excel file with some content.
     */
    public ExcelReport(Device device, String databaseFileName) {
        super(databaseFileName);
        this.device = device;
    }

    /**
     * Constructor.
     *
     * @param device Device
     * @param fileData Database file containing Excel file with some content.
     */
    public ExcelReport(Device device, FileData fileData) {
        super(fileData);
        this.device = device;
    }

    /**
     * Constructor
     *
     * @param device Device
     * @param templateId Id of the stream data containing Excel file with some content.
     */
    public ExcelReport(Device device, Id templateId) {
        super(templateId);
        this.device = device;
    }

    /**
     * Constructor
     *
     * @param device Device
     * @param streamData Stream data containing Excel file with some content.
     */
    public ExcelReport(Device device, StreamData streamData) {
        this.device = device;
    }

    public void view() {
        execute();
    }

    public Device getDevice() {
        return device;
    }

    /**
     * Log something via the logger associated with this report.
     *
     * @param anything Anything to log.
     */
    public void log(Object anything) {
        device.log(anything);
    }
}