package com.storedobject.ui;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ListGrid;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * A grid to be shown in a window with "Proceed/Cancel" buttons to select an entry. When the "Proceed" button is
 * pressed, the selected entry is passed to a {@link java.util.function.Consumer} that can be set.
 *
 * @param <T> Type of object in the grid.
 * @author Syam
 */
public class SelectGrid<T> extends ListGrid<T> {

    private String emtpyRowsMessage = "No entries found!";
    protected final Button proceed = new Button("Proceed", e -> act());
    protected final Button cancel = new Button("Cancel", e -> cancel());
    private final Consumer<T> consumer;

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     */
    public SelectGrid(Class<T> objectClass, List<T> items) {
        this(objectClass, items, (Consumer<T>) null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     */
    public SelectGrid(Class<T> objectClass, List<T> items, Iterable<String> columns) {
        this(objectClass, items, columns, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param consumer Consumer to consume the selected item.
     */
    public SelectGrid(Class<T> objectClass, List<T> items, Consumer<T> consumer) {
        this(objectClass, items, null, consumer);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     * @param consumer Consumer to consume the selected item.
     */
    public SelectGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, Consumer<T> consumer) {
        super(objectClass, items, columns);
        this.consumer = consumer;
        setWidth("60vw");
        setHeight("60vh");
        addItemDoubleClickListener(e -> act(e.getItem()));
    }

    @Override
    public final Component createHeader() {
        return new ButtonLayout(proceed, cancel);
    }

    @Override
    public void execute(View lock) {
        if(isEmpty()) {
            if(emtpyRowsMessage != null) {
                warning(emtpyRowsMessage);
            }
            return;
        }
        super.execute(lock);
    }

    private void act() {
        act(getSelected());
    }

    private void act(T selected) {
        if(selected == null) {
            warning("Please select an entry");
            return;
        }
        close();
        if(consumer == null) {
            process(selected);
        } else {
            consumer.accept(selected);
        }
    }

    /**
     * This will be invoked when the "Cancel" button is pressed. The default action is to abort the {@link View}.
     */
    protected void cancel() {
        abort();
    }

    /**
     * This will be invoked when the "Proceed" button is pressed and no "consumer" is set. (View will have already
     * closed before calling this).
     *
     * @param selected Selected item.
     */
    protected void process(T selected) {
    }

    @Override
    public final View createView() {
        return new SelectView();
    }

    /**
     * Set the message to be displayed when no rows exists while executing.
     * If set to null, no message will be displayed.
     *
     * @param emptyRowsMessage Message to be displayed.
     */
    public void setEmptyRowsMessage(String emptyRowsMessage) {
        this.emtpyRowsMessage = emptyRowsMessage;
    }

    private class SelectView extends View {

        private SelectView() {
            super("Select an Entry");
            setComponent(SelectGrid.this);
            setWindowMode(true);
        }
    }
}
