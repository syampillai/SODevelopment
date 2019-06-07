package com.storedobject.ui.common;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.stream.Stream;

@SuppressWarnings("serial")
public abstract class AbstractTaskMonitor<T extends AbstractTask> extends ObjectBrowser<T> {

    protected TaskGroup taskGroup;
    protected Button markAsDone;
    protected DateBasedTaskHistory history;

    public AbstractTaskMonitor(Class<T> taskClass, String caption, String taskGroup) {
        this(taskClass, caption, TaskGroup.get(taskGroup));
    }

    public AbstractTaskMonitor(Class<T> taskClass, TaskGroup taskGroup) {
        this(taskClass, null, taskGroup);
    }

    public AbstractTaskMonitor(Class<T> taskClass, String caption, TaskGroup taskGroup) {
        super(taskClass, caption == null ? (taskGroup == null ? "Tasks" : taskGroup.getName()) : caption);
        this.taskGroup = taskGroup;
    }

    public GridCellText getNextDue(AbstractTask task) {
        return null;
    }

    public ObjectEditor<DateBasedTaskHistory> createHistoryEditor() {
        return null;
    }

    public void computingNextDue(T task) {
    }

    public DateBasedTaskHistory createHistory(DateBasedTask task) {
        return null;
    }
}
