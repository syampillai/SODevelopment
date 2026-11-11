package com.storedobject.ui.report;

import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.DataForm;


public abstract class AbstractEntityReport extends DataForm {

    protected final ObjectField<SystemEntity> entityField = new ObjectField<>("Entity", SystemEntity.class);

    public AbstractEntityReport(String reportName) {
        this(reportName, null);
    }

    public AbstractEntityReport(String reportName, SystemEntity entity) {
        super(reportName);
        addField(entityField);
        if(entity != null) {
            entityField.setValue(entity);
            setFieldReadOnly(entityField);
        } else {
            setRequired(entityField);
            entityField.setValue(Application.get().getTransactionManager().getEntity());
        }
    }
}
