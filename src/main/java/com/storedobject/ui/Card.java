package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Represents a styled container with configurable layout and styling options.
 * The Card class extends Div and provides a pre-defined style for creating card-like components.
 * It supports alignment, justification, and grid span configuration.
 *
 * @param <T> The type of object associated with the card.
 *
 * @author Syam
 */
public class Card<T> extends Div {

    private CardGrid<T> grid;
    private T object;
    private final Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
    private boolean selected;

    /**
     * Default constructor for the Card component. It initializes the card
     * with a predefined set of styles for appearance and layout.
     * <pre>
     * The following styles are applied by default:
     * - Border radius set to 12px for rounded corners.
     * - Padding set to 16px for internal spacing.
     * - Background color set to white.
     * - Box shadow set to create a subtle shadow effect.
     * - Flex display for layout management.
     * - Column-based flex-direction for stacking elements vertically.
     * - No margin.
     * - Alignment set to stretch to occupy available space.
     * - Gap set to 8px between child elements.
     * </pre>
     */
    public Card() {
        getStyle()
                .set("border-radius", "12px")
                .set("padding", "16px")
                .set("background", "white")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("margin", "0")
                .set("align-self", "stretch")
                .set("gap", "8px")
                .set("user-select", "none")
                .set("position", "relative");
        checkIcon.getStyle()
                .set("position", "absolute")
                .set("top", "8px")
                .set("right", "8px")
                .set("color", "var(--lumo-primary-color)")
                .set("display", "none");
        add(checkIcon);
        addClickListener(e -> dispatchClick());
    }

    /**
     * Aligns the content of the card to the top of the container.
     * Sets the "align-items" style property to "start".
     */
    public void alignTop() {
        getStyle().set("align-items", "start");
    }

    /**
     * Aligns the component content to the bottom of the container.
     * This method sets the CSS style property "align-items" to "end",
     * effectively positioning child elements at the bottom within
     * a flex container.
     */
    public void alignBottom() {
        getStyle().set("align-items", "end");
    }

    /**
     * Aligns the content of the component to the center by setting
     * the `align-items` style property to "center".
     * This method applies a CSS style to center the alignment of child elements
     * vertically within the component's layout. It is typically used in flexbox
     * or grid layouts where centering content is needed.
     */
    public void alignCenter() {
        getStyle().set("align-items", "center");
    }

    /**
     * Adjusts the horizontal alignment of items within the card to the left.
     * This method sets the CSS `justify-items` property to `start`, ensuring
     * that the content aligns along the left side of the container.
     */
    public void justifyLeft() {
        getStyle().set("justify-items", "start");
    }

    /**
     * Adjusts the alignment of the item's content within its grid cell to the right.
     * This method sets the "justify-items" CSS property to "end", ensuring the content
     * is aligned to the end (right) of the grid cell it resides in.
     */
    public void justifyRight() {
        getStyle().set("justify-items", "end");
    }

    /**
     * Aligns the content of the component to the center horizontally within its container.
     * This method sets the `justify-items` CSS property to `center`.
     */
    public void justifyCenter() {
        getStyle().set("justify-items", "center");
    }

    /**
     * Sets the column span for the grid layout of the component.
     * The span determines how many columns the component will occupy.
     *
     * @param span The number of columns to span must be a positive integer.
     */
    public void setColumnSpan(int span) {
        getStyle().set("grid-column", "span " + span);
    }

    /**
     * Sets the number of rows that the component should span in a CSS Grid layout.
     *
     * @param span the number of rows the component should span
     */
    public void setRowSpan(int span) {
        getStyle().set("grid-row", "span " + span);
    }

    /**
     * Represents a flex container that manages its child components with a customizable layout.
     * The Cell class extends the Div component and provides specialized alignment and layout
     * capabilities, enabling components to be organized either in a column or row direction,
     * with adjustable gaps and alignment options.
     *
     * @author Syam
     */
    public static class Cell extends Div {

        /**
         * Creates a new {@code Cell} instance with the specified child components arranged
         * in a flexible container layout. The layout defaults to a column direction with
         * a standard gap between elements.
         *
         * @param components The child components to be added to this {@code Cell}. These components
         *                   will be arranged within the flexible container.
         */
        public Cell(Component... components) {
            this(-1, components);
        }

        /**
         * Constructs a {@code Cell} instance with the specified gap between components
         * and an array of child components. The gap is used to define the spacing
         * between components in the layout.
         *
         * @param gap The gap (in pixels) to be applied between components. If the value is negative, a default gap is used.
         * @param components The components to be added to this {@code Cell}.
         */
        public Cell(int gap, Component... components) {
            this(gap, true, components);
        }

        Cell(int gap, boolean column, Component... components) {
            super(components);
            getStyle().set("display", "flex").set("justify-content", "space-between");
            getStyle().set("flex-direction", column ? "column" : "row");
            setGap(gap);
        }

        /**
         * Sets the gap between elements in the layout. If the specified gap value is negative,
         * a default gap of 4 pixels will be applied.
         *
         * @param gap The amount of the gap (in pixels) to set between elements. If provided as a negative value,
         *            it defaults to 4 pixels.
         */
        public void setGap(int gap) {
            getStyle().set("gap", (gap < 0 ? 4 : gap) + "px");
        }


        /**
         * Aligns the content or component placement to the top of the layout.
         * If the invoking instance is of type {@code Cell}, this method sets the CSS
         * property {@code align-self} to {@code start}, positioning the content of the
         * cell at the top. For other component types, it sets the CSS property
         * {@code align-items} to {@code start}, aligning child components of the layout
         * container towards the top edge.
         */
        public void alignTop() {
            getStyle().set(getClass() == Cell.class ? "align-self" : "align-items", "start");
        }

        /**
         * Aligns the component to the bottom in its container.
         * For instances of the {@code Cell} class, this method sets the CSS property
         * {@code align-self} to {@code end}, which aligns the component to the bottom edge
         * of its parent container.
         * For other classes, this method sets the CSS property {@code align-items} to
         * {@code end}, aligning child elements of the container to the bottom edge.
         */
        public void alignBottom() {
            getStyle().set(getClass() == Cell.class ? "align-self" : "align-items", "end");
        }

        /**
         * Aligns the content of the current component or cell to the center.
         * In the case where this method is called on an instance of the {@code Cell} class,
         * it sets the "align-self" CSS property to "center". For other components,
         * it sets the "align-items" CSS property to "center".
         */
        public void alignCenter() {
            getStyle().set(getClass() == Cell.class ? "align-self" : "align-items", "center");
        }

        /**
         * Aligns the content of the cell or container to the left.
         * This method applies the "justify-self" or "justify-items" style property
         * with the value "start" depending on whether the current instance is of type {@code Cell}.
         * For instances of {@code Cell}, the "justify-self" property is set, and for other
         * container types, the "justify-items" property is set.
         */
        public void justifyLeft() {
            getStyle().set(getClass() == Cell.class ? "justify-self" : "justify-items", "start");
        }

        /**
         * Aligns the justification of this component or cell to the right.
         * If the current instance belongs to the {@code Cell} class, this method
         * sets the CSS style property "justify-self" to "end". For other types of
         * components, it sets the CSS style property "justify-items" to "end".
         */
        public void justifyRight() {
            getStyle().set(getClass() == Cell.class ? "justify-self" : "justify-items", "end");
        }

        /**
         * Aligns the content of a cell or container to the horizontal center.
         * When invoked on an instance of {@code Cell}, it sets the {@code justify-self}
         * style property to {@code center}. For other container types, it sets the
         * {@code justify-items} style property to {@code center}. This method ensures
         * that the alignment behavior is specific to the type of container it is applied on.
         */
        public void justifyCenter() {
            getStyle().set(getClass() == Cell.class ? "justify-self" : "justify-items", "center");
        }
    }

    /**
     * Represents a column-based layout container that arranges its child components
     * vertically with an optional gap between them.
     * The {@code Column} class extends the {@code Cell} class, inheriting its
     * flex container properties while enforcing a column layout direction.
     * This class provides constructors to quickly configure the gap size
     * and the components to include in the layout.
     *
     * @author Syam
     */
    public static class Column extends Cell {

        /**
         * Creates a new {@code Column} instance with the provided components and a default gap size.
         *
         * @param components The components to be arranged vertically within the column layout.
         */
        public Column(Component... components) {
            this(-1, components);
        }

        /**
         * Constructs a {@code Column} layout container with the specified gap between components
         * and an array of child components to arrange.
         *
         * @param gap The gap (in pixels) to be applied between components. If negative, no additional gap will be applied.
         * @param components The components to include in the column layout.
         */
        public Column(int gap, Component... components) {
            super(gap, true, components);
        }
    }

    /**
     * Represents a container component designed to arrange child components
     * in a horizontal row layout with customizable gap spacing.
     * The Row class extends the functionality of the {@code Cell} class,
     * inheriting its flex layout capabilities and tailoring them specifically for row arrangements.
     *
     * @author Syam
     */
    public static class Row extends Cell {

        /**
         * Constructs a {@code Row} instance with the specified components arranged in a horizontal row layout.
         * The gap between the components will use the default value.
         *
         * @param components The components to be arranged in a horizontal row.
         */
        public Row(Component... components) {
            this(-1, components);
        }

        /**
         * Constructs a {@code Row} object that arranges components in a horizontal row layout.
         * The layout includes an optional gap between the components.
         *
         * @param gap A gap value (in pixels) to be applied between components in the row.
         *            A value of -1 indicates that the default gap should be used.
         * @param components The child components to be arranged in the row layout.
         */
        public Row(int gap, Component... components) {
            super(gap, false, components);
        }
    }

    /**
     * The Line class represents a styled horizontal line component with customizable properties.
     * It extends the {@code Hr} class and is designed to be a simple visual separator in a UI layout.
     * <pre>
     * Features:
     * - By default, it renders as a horizontal line with no border and a top border styled
     *   as "1px solid #e0e0e0".
     * - The height of the line is set to 3px.
     * - Margins are applied to create spacing above and below the line, with values of 8px for top and bottom.
     * </pre>
     * It also provides a method to set the background color of the line dynamically.
     *
     * @author Syam
     */
    public static class Line extends Hr {

        /**
         * Constructs a Line component with predefined styles.
         * This constructor initializes a horizontal line with the following styles:
         * - Removes all borders except for the top border.
         * - Sets the top border style to "1px solid #e0e0e0" to create a visual separator.
         * - Assigns a height of 3px to the line.
         * - Adds margins of 8px above and below the line for spacing.
         */
        public Line() {
            getStyle()
                    .set("border", "none")
                    .set("border-top", "1px solid #e0e0e0")
                    .set("height", "3px")
                    .set("margin", "8px 0");
        }

        /**
         * Sets the background color for this component.
         *
         * @param color The background color to be applied, specified as a CSS color value
         *              (e.g., "red", "#FF0000", "rgb(255, 0, 0)").
         */
        public void setColor(String color) {
            getStyle().set("background-color", color);
        }
    }

    /**
     * Toggles the selection state of the card.
     * If the card is currently selected, it will be deselected.
     * If it is not selected, it will be marked as selected.
     * Internally, this method calls {@code setSelected(boolean selected)}
     * with the negated value of the current selection state.
     */
    public void toggleSelection() {
        setSelected(!selected);
    }

    /**
     * Updates the selection state of the card and applies corresponding visual styles.
     * If the new selection state differs from the current state, this method updates
     * the card's appearance, including the visibility of the check icon, and notifies
     * the associated data grid (if any) of the selection change.
     *
     * @param selected The new selection state. If true, the card is marked as selected;
     *                 otherwise, it is unselected.
     */
    public void setSelected(boolean selected) {
        if(selected == this.selected) {
            return;
        }
        this.selected = selected;
        checkIcon.getStyle().set("display", selected ? "block" : "none");
        updateSelectionStyles();
        dispatchSelection();
    }

    /**
     * Checks whether the card is currently selected.
     *
     * @return {@code true} if the card is selected, {@code false} otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    private void updateSelectionStyles() {
        if (this.selected) {
            getStyle()
                    .set("background-color", "var(--lumo-primary-color-10pct)");
            checkIcon.getStyle().set("opacity", "1").set("transform", "scale(1)");
        } else {
            getStyle()
                    .set("background-color", "white");
            checkIcon.getStyle().set("opacity", "0").set("transform", "scale(0)");
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        grid = null;
    }

    private void dispatchClick() {
        if(grid == null) {
            grid = findGrid(this);
        }
        if(grid != null) {
            if(grid.ignoreSelection) {
                grid.ignoreSelection = false;
                return;
            }
            grid.clicked(this);
        }
    }

    private void dispatchSelection() {
        if(grid == null) {
            grid = findGrid(this);
        }
        if(grid != null) {
            grid.selected(this);
        }
    }

    private static <O> CardGrid<O> findGrid(Component c) {
        Component p = c.getParent().orElse(null);
        if(p == null) {
            return null;
        }
        if(p instanceof CardGrid<?> g) {
            //noinspection unchecked
            return (CardGrid<O>) g;
        }
        return findGrid(p);
    }

    /**
     * Retrieves the object of type T stored in this card.
     *
     * @return the object of type T stored in this card
     */
    public final T getObject() {
        return object;
    }

    /**
     * Sets the object of type T for this card. Display attributes of the card should be set based on the object's property values.
     * If this method is overridden, the object must be set by invoking the superclass implementation.
     *
     * @param object the object to be associated with this card must be of type T
     *               where T is a subclass of StoredObject
     */
    public void setObject(T object) {
        this.object = object;
    }

    /**
     * Disables card selection in the associated grid.
     * When this method is invoked, it prevents the grid from processing
     * any card selection events, effectively ignoring selection input or state changes.
     * <p></p>
     * This can be useful in scenarios where card selection functionality
     * needs to be temporarily suspended without altering the grid's selection state.
     * <p></p>
     * Use case: When you have clickable components inside the cards, you may not want to fire the selection events
     * when the user clicks on them. In such cases, you can temporarily suspend card selection to avoid unintended behavior.
     * This is typically achieved by invoking this method from within your click-handlers. It will be automatically enabled
     * again when your click-handlers complete their execution.
     */
    public void ignoreSelection() {
        CardGrid<T> grid = findGrid(this);
        if(grid != null) {
            grid.ignoreSelection = true;
        }
    }

    /**
     * Checks whether card selection is currently being ignored in the associated grid.
     * When this method returns {@code true}, card selection events are not processed,
     * effectively disabling selection functionality for the grid.
     *
     * @return {@code true} if card selection is being ignored; {@code false} otherwise
     */
    public boolean isIgnoreSelection() {
        CardGrid<T> grid = findGrid(this);
        return grid == null || grid.ignoreSelection;
    }
}
