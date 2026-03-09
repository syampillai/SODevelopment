package com.storedobject.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialized context menu that appears on right-click actions within a grid. Typically, instances of
 * {@link RightClickButton}s are added to this menu.
 * This menu dynamically updates its content based on specified predicates and actions of the {@link RightClickButton}s.
 *
 * @param <T> The type of data associated with the grid rows and menu items.
 */
public class RightClickMenu<T> extends GridContextMenu<T> {

    private final List<Button<T>> buttons = new ArrayList<>();

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
            return any;
        });
    }

    /**
     * Any call to this method will be ignored because the dynamic content is automatically managed by the menu.
     *
     * @param dynamicContentHandler a {@link SerializablePredicate} instance.
     */
    @Override
    public void setDynamicContentHandler(SerializablePredicate<T> dynamicContentHandler) {
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

    private record Button<T>(RightClickButton<T> button, GridMenuItem<T> menuItem) {
    }
}
