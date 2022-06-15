package com.storedobject.ui;

import com.storedobject.vaadin.*;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.util.List;

/**
 * A grid to be shown in a window with "Yes/No" buttons and an action is carried out if the "Yes" button is pressed.
 *
 * @param <T> Type of object in the grid.
 * @author Syam
 */
public class ActionGrid<T> extends ListGrid<T> {

    private final Runnable action;
    private final Component message;
    private final ELabel confirmMessage = new ELabel("Do you really want to proceed?", Application.COLOR_ERROR);

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     */
    public ActionGrid(Class<T> objectClass, List<T> items) {
        this(objectClass, items, (Iterable<String>) null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Iterable<String> columns) {
        this(objectClass, items, columns, (Component) null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, String message) {
        this(objectClass, items, null, message);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, String message) {
        this(objectClass, items, columns, m(message));
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Component message) {
        this(objectClass, items, null, message);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, Component message) {
        this(objectClass, items, columns, message, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     * @param action Action to be carried out when the "Yes" button is pressed. If null is passed,
     *               {@link #process()} method will be invoked and if it returns true,
     *               the {@link View} will be closed.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, String message, Runnable action) {
        this(objectClass, items, null, message, action);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     * @param action Action to be carried out when the "Yes" button is pressed. If null is passed,
     *               {@link #process()} method will be invoked and if it returns true,
     *               the {@link View} will be closed.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, String message, Runnable action) {
        this(objectClass, items, columns, m(message), action);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     * @param action Action to be carried out when the "Yes" button is pressed. If null is passed,
     *               {@link #process()} method will be invoked and if it returns true,
     *               the {@link View} will be closed.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Component message, Runnable action) {
        this(objectClass, items, null, message, action);
    }

    /**
     * Constructor.
     *
     * @param objectClass Type object in the grid.
     * @param items Items of the grid.
     * @param columns Column names of the grid.
     * @param message Message to be shown at the top. If null is passed, no message will be shown.
     * @param action Action to be carried out when the "Yes" button is pressed. If null is passed,
     *               {@link #process()} method will be invoked and if it returns true,
     *               the {@link View} will be closed.
     */
    public ActionGrid(Class<T> objectClass, List<T> items, Iterable<String> columns, Component message, Runnable action) {
        super(objectClass, items, DataGrid.columns(objectClass, columns));
        this.action = action;
        this.message = message;
        setWidth("60vw");
        setHeight("60vh");
    }

    private static Component m(String message) {
        if(message == null || message.isBlank()) {
            return null;
        }
        return new ELabel(message, "font-size:large", "font-weight:bold", "color:blue");
    }

    @Override
    public final Component createHeader() {
        ButtonLayout b;
        if(this instanceof MessageGrid) {
            b = new ButtonLayout(new Button("Ok", e -> close()).asSmall());
        } else {
            b = new ButtonLayout(
                    confirmMessage,
                    new Button("Yes", e -> act()).asSmall(),
                    new Button("No", e -> cancel()).asSmall()
            );
        }
        return message == null ? b : new Div(message, b);
    }

    private void act() {
        if(action == null) {
            if(process()) {
                close();
            }
            return;
        }
        try {
            close();
            action.run();
        } catch(Throwable error) {
            error(error);
        }
    }

    /**
     * This will be invoked when the "No" button is pressed. The default action is to abort the {@link View}.
     */
    protected void cancel() {
        abort();
    }

    /**
     * This will be invoked when the "Yes" button is pressed and no "action" is set. If returned true from this
     * method, {@link View} will be closed. The default implementation returns true.
     */
    protected boolean process() {
        return true;
    }

    /**
     * Get the confirm message component for customization.
     *
     * @return The confirm message component.
     */
    public ELabel getConfirmMessage() {
        return confirmMessage;
    }

    @Override
    public final View createView() {
        return new AGView();
    }

    private class AGView extends View {

        private AGView() {
            super(ActionGrid.this instanceof MessageGrid ? "Message" : "Confirm");
            setComponent(ActionGrid.this);
            setWindowMode(true);
        }
    }
}
