package com.storedobject.ui;

/**
 * Represents an abstract tree grid data structure capable of displaying hierarchical data.
 * This class extends the DataTreeGrid functionality with additional support for handling
 * right-click menus and transactional behavior. It is designed to display objects of type {@code T}.
 *
 * @param <T> the type of the objects displayed in this tree grid
 *
 * @author Syam
 */
public abstract class DataTreeGrid<T> extends com.storedobject.vaadin.DataTreeGrid<T> implements Transactional {

    private RightClickMenu<T> rightClickMenu;

    /**
     * Constructs a new DataTreeGrid with the specified object class and default column configuration.
     *
     * @param objectClass the {@link Class} of the objects displayed in this grid; must not be null
     */
    public DataTreeGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    /**
     * Constructs a DataTreeGrid instance using the specified object class and column definitions.
     * The object class represents the type of data displayed in the grid, and the columns parameter is
     * used to define the grid's column structure.
     *
     * @param objectClass the class type of the objects displayed in the grid
     * @param columns an iterable of column definitions specifying the properties to be displayed in the grid.
     *                If null, grid columns will be inferred based on the object class.
     */
    public DataTreeGrid(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, DataGrid.columns(objectClass, columns));
    }

    /**
     * Returns the instance of the right-click menu associated with this grid.
     * If the right-click menu is not already initialized, a new instance is created.
     *
     * @return the right-click menu instance
     */
    public RightClickMenu<T> getRightClickMenu() {
        if(rightClickMenu == null) {
            rightClickMenu = new RightClickMenu<>(this);
        }
        return rightClickMenu;
    }

    /**
     * Sets the right-click menu for this grid.
     *
     * @param rightClickMenu the right-click menu to be associated with this grid. A null value will remove
     *                       the current menu.
     */
    public void setRightClickMenu(RightClickMenu<T> rightClickMenu) {
        if(this.rightClickMenu == rightClickMenu) {
            return;
        }
        if(this.rightClickMenu != null) {
            this.rightClickMenu.setTarget(null);
            this.rightClickMenu.removeFromParent();
        }
        if(rightClickMenu != null && rightClickMenu.getTarget() != this) {
            rightClickMenu.setTarget(this);
        }
        this.rightClickMenu = rightClickMenu;
    }
}
