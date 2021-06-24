package com.storedobject.office;

import java.io.InputStream;

import com.storedobject.core.Device;
import com.storedobject.core.Id;
import com.storedobject.core.StreamData;

public class IteratorODTReport<T> extends ODT<T> {

    private Device device;

    public IteratorODTReport(Device device) {
        super();
        this.device = device;
    }

    public IteratorODTReport(Device device, Id streamDataId) {
        super(streamDataId);
        this.device = device;
    }

    public IteratorODTReport(Device device, Id streamDataId, Object filler) {
        super(streamDataId, filler);
        this.device = device;
    }

    public IteratorODTReport(Device device, StreamData streamData) {
        super(streamData);
        this.device = device;
    }

    public IteratorODTReport(Device device, StreamData streamData, Object filler) {
        super(streamData, filler);
        this.device = device;
    }

    public InputStream extractContent() throws Exception {
        produce();
        return getContent();
    }

    @Override
    public void execute() {
        device.view(this);
    }

    public void view() {
        execute();
    }

    @Override
    public void produce() {
        super.execute();
    }

    public Device getDevice() {
        return device;
    }
}