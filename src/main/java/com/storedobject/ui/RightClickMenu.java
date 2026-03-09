package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A specialized context menu that appears on right-click actions within a grid. Typically, instances of
 * {@link RightClickButton}s are added to this menu.
 * This menu dynamically updates its content based on specified predicates and actions of the {@link RightClickButton}s.
 *
 * @param <T> The type of data associated with the grid rows and menu items.
 */
public class RightClickMenu<T> extends GridContextMenu<T> {

    private final List<Button<T>> buttons = new ArrayList<>();
    private SerializablePredicate<T> customContentHandler;

    /**
     * Constructs a new instance of the RightClickMenu.
     * This constructor initializes the context menu with default settings
     * and prepares it to dynamically update its content based on custom
     * predicates associated with menu items.
     */
    public RightClickMenu() {
        super();
        init();
    }

    /**
     * Creates a new instance of the RightClickMenu and associates it with the specified grid.
     * The menu updates dynamically based on the provided grid's context,
     * showing or hiding menu items based on the logic in their associated predicates.
     *
     * @param grid the grid to which this right-clicks menu will be attached
     *             and whose context will drive the menu's dynamic behavior
     */
    public RightClickMenu(Grid<T> grid) {
        super(grid);
        init();
        if(grid instanceof DataGrid<T> g) {
            g.setRightClickMenu(this);
        } else if(grid instanceof DataTreeGrid<T> tg) {
            tg.setRightClickMenu(this);
        }
    }

    private void init() {
        super.setDynamicContentHandler(i -> {
            boolean any = false;
            for(Button<T> button: buttons) {
                String label = button.menuItem.getText();
                if(i != null && button.button.test(i)) {
                    button.menuItem.setVisible(true);
                    any = true;
                } else {
                    button.menuItem.setVisible(false);
                }
                String changedLabel = button.button.getLabel();
                if(!label.equals(changedLabel)) {
                    button.menuItem.setText(changedLabel);
                }
            }
            // Custom handler, if any, will always be tested, no short-circuiting.
            if(customContentHandler != null && customContentHandler.test(i)) {
                any = true;
            }
            return any;
        });
    }

    @Override
    public final void setTarget(Component target) {
        if(getTarget() == target) {
            return;
        }
        super.setTarget(target);
        if(target instanceof DataTreeGrid<?> dtg) {
            @SuppressWarnings("unchecked") DataTreeGrid<T> g = (DataTreeGrid<T>) dtg;
            g.setRightClickMenu(this);
        }
        if(target instanceof DataGrid<?> dg) {
            @SuppressWarnings("unchecked") DataGrid<T> g = (DataGrid<T>) dg;
            g.setRightClickMenu(this);
        }
    }

    /**
     * Any call to this method will raise a runtime exception because the menu automatically manages visibility of the
     * {@link RightClickButton}s contained in it.
     * However, you can add one or more custom content handlers via {@link #addCustomContentHandler(SerializablePredicate)}
     * for handling visibility of the items other than {@link RightClickButton}s.
     *
     * @param dynamicContentHandler a {@link SerializablePredicate} instance.
     */
    @Override
    public void setDynamicContentHandler(SerializablePredicate<T> dynamicContentHandler) {
        throw new SORuntimeException("Not supported");
    }

    /**
     * Adds a custom content handler for the right-click menu.
     * The custom content handler is a predicate that allows for dynamically
     * determining whether certain menu items should be displayed based on given conditions.
     *
     * @param customContentHandler a {@link SerializablePredicate} used to evaluate conditions
     *                              for the menu's dynamic content. This predicate is invoked
     *                              to determine which menu items should be displayed
     *                              based on the current context.
     */
    public void addCustomContentHandler(SerializablePredicate<T> customContentHandler) {
        if(this.customContentHandler == null) {
            this.customContentHandler = customContentHandler;
            return;
        }
        SerializablePredicate<T> old = this.customContentHandler;
        this.customContentHandler = t -> {
            boolean any = old.test(t);
            if(customContentHandler.test(t)) {
                any = true;
            }
            return any;
        };
    }

    /**
     * Resets the custom content handler for the right-click menu.
     * This method removes any previously set custom content handler by setting
     * the internal reference to null. Once invoked, the menu will no longer use
     * any custom logic to dynamically determine the visibility or behavior
     * of its items based on a custom predicate.
     */
    public void resetCustomContentHandler() {
        this.customContentHandler = null;
    }

    /**
     * Adds a new button to the right-click menu and configures its behavior.
     *
     * @param button the right-click button to be added to the menu
     * @return the instance of the RightClickMenu with the newly added button
     */
    public RightClickMenu<T> add(RightClickButton<T> button) {
        buttons.add(new Button<>(button, addItem(button.getLabel(), e ->
                e.getItem().ifPresent(t -> {
                    String label = e.getSource().getText();
                    button.accept(t);
                    String changedLabel = button.getLabel();
                    if(!label.equals(changedLabel)) {
                        e.getSource().setText(changedLabel);
                    }
                }))));
        return this;
    }

    /**
     * Removes the specified button and its associated menu item from the right-click menu.
     *
     * @param button the button to be removed from the right-click menu
     * @return the current instance of the RightClickMenu after the button has been removed
     */
    public RightClickMenu<T> remove(RightClickButton<T> button) {
        buttons.removeIf(b -> {
            if(b.button == button) {
                remove(b.menuItem);
                return true;
            }
            return false;
        });
        return this;
    }

    public Stream<RightClickButton<T>> buttons() {
        return buttons.stream().map(b -> b.button);
    }

    private record Button<T>(RightClickButton<T> button, GridMenuItem<T> menuItem) {
    }
}
