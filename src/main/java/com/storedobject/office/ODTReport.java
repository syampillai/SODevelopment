package com.storedobject.office;

import com.storedobject.core.Device;
import com.storedobject.core.Id;
import com.storedobject.core.ReportFormat;
import com.storedobject.core.StreamData;

public class ODTReport extends ODT<Object> {

    private Device device;

    public ODTReport(Device device) {
        this(device, (StreamData)null, null);
    }

    public ODTReport(Device device, Id templateId) {
        this(device, templateId, null);
    }

    public ODTReport(Device device, Id templateId, Object filler) {
        this(device, (StreamData)null, filler);
        setTemplate(templateId);
        this.device = device;
    }

    public ODTReport(Device device, StreamData streamData) {
        this(device, streamData, null);
    }

    public ODTReport(Device device, StreamData streamData, Object filler) {
        super(streamData, filler);
    }

    public void view() {
        execute();
    }

    public ReportFormat getReportFormat() {
        return null;
    }

    @Override
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