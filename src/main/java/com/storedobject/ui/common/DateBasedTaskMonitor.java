package com.storedobject.ui.common;

import com.storedobject.core.DateBasedTask;
import com.storedobject.core.TaskGroup;

public class DateBasedTaskMonitor extends AbstractTaskMonitor<DateBasedTask> {

    public DateBasedTaskMonitor() {
        this("Date Based Tasks", (TaskGroup)null);
    }

    public DateBasedTaskMonitor(String taskGroup) {
        this(null, taskGroup);
    }

    public DateBasedTaskMonitor(String caption, String taskGroup) {
        this(caption, TaskGroup.get(taskGroup));
    }

    public DateBasedTaskMonitor(String caption, TaskGroup taskGroup) {
        super(DateBasedTask.class, caption, taskGroup);
        load();
    }
}
