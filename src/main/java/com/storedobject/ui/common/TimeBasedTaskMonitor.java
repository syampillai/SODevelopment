package com.storedobject.ui.common;

import com.storedobject.core.TaskGroup;
import com.storedobject.core.TimeBasedTask;

public class TimeBasedTaskMonitor extends AbstractTaskMonitor<TimeBasedTask> {

    public TimeBasedTaskMonitor() {
        this("Time Based Tasks", (TaskGroup)null);
    }

    public TimeBasedTaskMonitor(String taskGroup) {
        this(null, taskGroup);
    }

    public TimeBasedTaskMonitor(String caption, String taskGroup) {
        this(caption, TaskGroup.get(taskGroup));
    }

    public TimeBasedTaskMonitor(String caption, TaskGroup taskGroup) {
        super(TimeBasedTask.class, caption, taskGroup);
        load();
    }
}