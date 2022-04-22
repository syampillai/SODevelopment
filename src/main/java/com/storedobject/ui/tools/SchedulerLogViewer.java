package com.storedobject.ui.tools;

import com.storedobject.core.DatePeriod;
import com.storedobject.core.DateUtility;
import com.storedobject.core.EditorAction;
import com.storedobject.common.StringList;
import com.storedobject.job.Log;
import com.storedobject.job.Schedule;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.DataForm;

public class SchedulerLogViewer extends DataForm {

    private ObjectField<Schedule> jobField;
    private DatePeriodField periodField;

    public SchedulerLogViewer() {
        super("Scheduler Log");
    }

    @Override
    protected void buildFields() {
        jobField = new ObjectField<>("Scheduler", Schedule.class);
        setRequired(jobField);
        addField(jobField);
        periodField = new DatePeriodField("Period", new DatePeriod(DateUtility.yesterday(), DateUtility.today()));
        addField(periodField);
    }

    @Override
    protected boolean process() {
        close();
        Schedule s = jobField.getObject();
        DatePeriod p = periodField.getValue();
        StringList cols = StringList.create("Status", "Message", "Date", "StartedAt", "CompletedAt");
        ObjectBrowser<Log> logs = new ObjectBrowser<>(Log.class, cols, EditorAction.VIEW, s.getName() + " (" + s.getDescription() + ")");
        logs.load("Schedule=" + s.getId() + " AND Date " + p.getDBCondition());
        logs.execute();
        return true;
    }
}