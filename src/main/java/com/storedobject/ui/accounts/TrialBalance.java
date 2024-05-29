package com.storedobject.ui.accounts;

import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.View;

import java.sql.Date;

public class TrialBalance extends DataForm implements Transactional {

    private ObjectField<SystemEntity> entityField;
    private DateField dateField;
    private Date date;
    private SystemEntity systemEntity;

    public TrialBalance() {
        super("Trial Balance", "View", "Quit");
    }

    public TrialBalance(SystemEntity systemEntity) {
        this(systemEntity, null);
    }

    public TrialBalance(SystemEntity systemEntity, Date date) {
        this();
        this.systemEntity = systemEntity;
        this.date = date;
    }

    @Override
    protected void buildFields() {
        addField(entityField = new ObjectField<>("Entity", SystemEntity.class));
        setRequired(entityField);
        if(systemEntity != null) {
            entityField.setValue(systemEntity);
            entityField.setReadOnly(true);
        }
        addField(dateField = new DateField("As on"));
        setRequired(dateField);
        if(date != null) {
            dateField.setValue(date);
            dateField.setReadOnly(true);
        }
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(systemEntity != null && date != null) {
            process();
        } else {
            super.execute(parent, doNotLock);
        }
    }

    @Override
    protected boolean process() {
        close();
        if(systemEntity == null) {
            systemEntity = entityField.getObject();
        }
        if(date == null) {
            date = dateField.getValue();
        }
        Application a = Application.get();
        a.view("Trial Balance", new com.storedobject.report.TrialBalance(a, systemEntity, date));
        return true;
    }
}