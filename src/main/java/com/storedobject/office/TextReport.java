package com.storedobject.office;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.TextContentGenerator;
import com.storedobject.core.*;

public class TextReport extends TextContentProducer {

    private final Device device;
    private boolean executed = false;

    /**
     * Constructor.
     *
     * @param device Device.
     * @param contentGenerator Content generator.
     */
    public TextReport(Device device, TextContentGenerator contentGenerator) {
        super(contentGenerator);
        this.device = device;
    }

    @Override
    public final void execute() {
        if(!executed) {
            executed = true;
            if(isBlocked("Excel")) {
                throw new SORuntimeException(AccessControl.MESSAGE);
            }
            getDevice().view(this);
            return;
        }
        super.execute();
    }

    public final void view() {
        execute();
    }

    @Override
    public final void setTransactionManager(TransactionManager tm) {
        super.setTransactionManager(tm);
    }

    public final void setEntity(Entity entity) {
        this.entity = entity;
    }

    public final Device getDevice() {
        return device;
    }
}