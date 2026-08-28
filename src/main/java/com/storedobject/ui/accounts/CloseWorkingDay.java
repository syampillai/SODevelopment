package com.storedobject.ui.accounts;

import com.storedobject.core.DateUtility;
import com.storedobject.core.SystemEntity;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.BooleanField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

import java.sql.Date;

public class CloseWorkingDay extends DataForm implements Transactional {

    private final ObjectField<SystemEntity> systemEntity = new ObjectField<>("Entity", SystemEntity.class);
    private final DateField currentDay = new DateField("Current Working Day");
    private final DateField nextDay = new DateField("Next Working Day");
    private final BooleanField confirm = new BooleanField("Confirm");

    public CloseWorkingDay() {
        super("Close Working Day");
        addField(
                new ELabelField("Note", "Please make sure that financial entries for the current working day are complete before closing.",
                        Application.COLOR_ERROR));
        addField(systemEntity, currentDay, nextDay, confirm);
        confirm.setHelperText("Double-check and confirm closing of working day.");
        setFieldReadOnly(currentDay);
        systemEntity.addValueChangeListener(e -> {
            Date d = systemEntity.getObject().getWorkingDate();
            currentDay.setValue(d);
            nextDay.setValue(DateUtility.addDay(d, 1));
        });
        systemEntity.setValue(getTransactionManager().getEntity());
        setRequired(systemEntity);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        if(!confirm.getValue()) {
            warning("Please confirm closing of working day!");
            return false;
        }
        SystemEntity entity = systemEntity.getObject();
        Date nd = nextDay.getValue();
        if(nd.before(entity.getWorkingDate())) {
            warning("Next working day cannot be before current working day!");
            return false;
        }
        close();
        entity.setWorkingDate(nd);
        if(transact(entity::save)) {
            message("Working day closed successfully for " + entity.toDisplay());
        }
        return true;
    }
}
