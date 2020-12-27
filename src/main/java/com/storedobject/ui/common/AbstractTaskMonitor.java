package com.storedobject.ui.common;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.stream.Stream;

public abstract class AbstractTaskMonitor<T extends AbstractTask> extends ObjectBrowser<T> {

    protected TaskGroup taskGroup;
    protected Button markAsDone;
    protected DateBasedTaskHistory history;
    private DateBasedTask taskDone;
    private ObjectEditor<DateBasedTaskHistory> historyEditor;

    public AbstractTaskMonitor(Class<T> taskClass, String caption, String taskGroup) {
        this(taskClass, caption, TaskGroup.get(taskGroup));
    }

    public AbstractTaskMonitor(Class<T> taskClass, TaskGroup taskGroup) {
        this(taskClass, null, taskGroup);
    }

    public AbstractTaskMonitor(Class<T> taskClass, String caption, TaskGroup taskGroup) {
        super(taskClass, caption == null ? (taskGroup == null ? "Tasks" : taskGroup.getName()) : caption);
        this.taskGroup = taskGroup;
        addConstructedListener(o -> con());
    }

    private void con() {
        getColumn("NextDue").setSortable(false);
        setSelectionMode(SelectionMode.SINGLE);
        setColumnReorderingAllowed(true);
        addObjectChangedListener(new ObjectListener());
        addObjectEditorListener(new EditorListener());
        setFilter(() -> "NOT Inactive" + (taskGroup == null ? "" : (" AND TaskGroup=" + taskGroup.getId())));
    }

    @Override
    public void addExtraButtons() {
        buttonPanel.add(markAsDone = new ConfirmButton("Mark As Done", VaadinIcon.THUMBS_UP_O, this));
    }

    @Override
    public void clicked(Component c) {
        if(c == markAsDone) {
            T task = getSelected();
            if(task == null) {
                return;
            }
            computingNextDue(task);
            task.computeNextDue();
            getObjectEditor().editObject(task, getView(), true);
            return;
        }
        super.clicked(c);
    }

    @Override
    public String getOrderBy() {
        return "NextDue";
    }

    @Override
    public final Stream<String> getColumnNames() {
        return getColumnList().stream();
    }

    public StringList getColumnList() {
        return StringList.create(StringList.create(taskGroup == null ? "TaskGroup" : null),
                StringList.create("Name", "Periodicity", "NextDue", "Remarks"));
    }

    public GridCellText getNextDue(AbstractTask task) {
        GridCellText text = getGridCellText();
        text.append(task.formatNextDue(), task.isDue() ? "red" : "orange");
        return text;
    }

    @Override
    public String getColumnCaption(String columnName) {
        switch (columnName) {
            case "NextDueValue":
                return "Next Due";
            case "Name":
                return "Task";
        }
        return super.getColumnCaption(columnName);
    }

    @Override
    public ObjectEditor<T> createObjectEditor() {
        ObjectEditor<T> ed = super.createObjectEditor();
        ed.setFieldReadOnly("Name", "Periodicity");
        ed.setFieldHidden("Inactive");
        if(taskGroup != null) {
            ed.setFieldHidden("TaskGroup");
        }
        return ed;
    }

    @SuppressWarnings("unchecked")
    public ObjectEditor<DateBasedTaskHistory> createHistoryEditor() {
        return (ObjectEditor<DateBasedTaskHistory>) ObjectEditor.create(history.getClass(),
                EditorAction.EDIT | EditorAction.VIEW, "Details of Task Done");
    }

    private ObjectEditor<DateBasedTaskHistory> historyEditor() {
        if(historyEditor == null) {
            historyEditor = createHistoryEditor();
            historyEditor.addObjectChangedListener(new HistoryListener());
            historyEditor.setFieldLabel("Remarks", "Remarks/Observations");
        }
        return historyEditor;
    }

    public void computingNextDue(T task) {
        if(task instanceof DateBasedTask) {
            taskDone = (DateBasedTask) task;
            history = createHistory(taskDone);
        }
    }

    public DateBasedTaskHistory createHistory(DateBasedTask task) {
        history = new DateBasedTaskHistory();
        history.setLastDoneDate(task.getNextDue());
        history.setRemarks(task.getRemarks());
        return history;
    }

    private class EditorListener implements ObjectEditorListener {

        @Override
        public void editingCancelled() {
            history = null;
            taskDone = null;
        }
    }

    private class ObjectListener implements ObjectChangedListener<T> {

        @Override
        public void saved(T object) {
            if(history != null) {
                historyEditor().editObject(history, getView());
                history = null;
            }
        }
    }

    private class HistoryListener implements ObjectChangedListener<DateBasedTaskHistory> {

        @Override
        public void saved(DateBasedTaskHistory object) {
            transact(t -> taskDone.addLink(t, object));
        }
    }
}
