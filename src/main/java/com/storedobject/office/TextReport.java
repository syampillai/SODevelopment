package com.storedobject.office;

import com.storedobject.common.TextContentGenerator;
import com.storedobject.core.*;

public class TextReport extends TextContentProducer {

    private final Device device;
    private Entity entity;

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

    @Override
    public final Entity getEntity() {
        if(entity != null) {
            return entity;
        }
        TransactionManager tm = getTransactionManager();
        return tm == null ? null : tm.getEntity().getEntity();
    }

    public final Device getDevice() {
        return device;
    }
}