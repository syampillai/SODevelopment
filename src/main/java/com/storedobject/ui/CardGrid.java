package com.storedobject.ui;

import com.vaadin.flow.component.html.Div;

/**
 * A CardGrid is a UI component designed to display a grid of cards.
 * The grid is dynamic and adjusts to the available space while maintaining
 * a consistent card width and gap between the cards.
 * <p></p>
 * CardGrid extends the {@code Div} component and provides methods for customizing
 * the appearance and behavior of the grid layout. It supports setting a custom card width
 * and gap size, and it can also be linked to a {@code CardDashboard} for managing its content.
 *
 * @author Syam
 */
public class CardGrid extends Div {

    private CardDashboard dashboard;
    private int cardWidth, gap;

    /**
     * Constructs a new CardGrid instance and initializes its default styling and layout.
     * The CardGrid uses a CSS grid layout with specific configurations:
     * <pre>
     * - display is set to "grid".
     * - grid rows have a dynamic height with a minimum of 50px.
     * - content alignment is set to stretch horizontally and start vertically.
     * - the grid width occupies 100% of the container.
     * </pre>
     * Additionally, the default gap size and card width are set:
     * <pre>
     * - The initial gap size between grid items is 16 pixels.
     * - The initial card width for grid items is 350 pixels.
     * </pre>
     */
    public CardGrid() {
        getStyle()
                .set("display", "grid")
                .set("justify-content", "stretch")
                .set("align-items", "start")
                .set("grid-auto-rows", "minmax(50px, auto)")
                .set("width", "100%");
        setGap(16);
        setCardWidth(350);
    }

    /**
     * Sets the gap size between cards in the grid layout.
     * The gap size defines the spacing in pixels and will
     * be clamped to a minimum value of 0 to ensure non-negative spacing.
     *
     * @param gap the gap size in pixels to be set between cards
     */
    public void setGap(int gap) {
        this.gap = Math.max(0, gap);
        getStyle().set("gap", this.gap + "px");
    }

    /**
     * Retrieves the size of the gap between cards in the grid layout.
     * The gap determines the spacing between individual cards.
     *
     * @return the current gap size in pixels
     */
    public int getGap() {
        return gap;
    }

    /**
     * Sets the width of the cards displayed in the grid. If the specified width
     * is less than 1, the method defaults the card width to 350 pixels.
     * The card width is used to dynamically configure the grid layout so that
     * the cards fit within the available space while maintaining the specified
     * width.
     *
     * @param cardWidth the width to be set for the cards, in pixels. A value
     *                  less than 1 will reset the card width to 350 pixels.
     */
    public void setCardWidth(int cardWidth) {
        if(cardWidth < 1) cardWidth = 350;
        this.cardWidth = cardWidth;
        getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(0," + cardWidth + "px))");
    }

    /**
     * Returns the width of the cards in the grid.
     *
     * @return the card width in pixels
     */
    public int getCardWidth() {
        return cardWidth;
    }

    /**
     * Associates the current {@code CardGrid} instance with a specified {@code CardDashboard}.
     * This allows the {@code CardGrid} to synchronize its content and behavior with
     * the provided dashboard.
     * <p>Note: This will be set automatically by the respective desktop when displayed.</p>
     *
     * @param dashboard the {@code CardDashboard} instance to link with this {@code CardGrid}.
     *                  Passing {@code null} will disassociate the current dashboard.
     */
    void setDashboard(CardDashboard dashboard) {
        this.dashboard = dashboard;
    }

    /**
     * Returns the associated {@code CardDashboard} instance for this {@code CardGrid}.
     * The {@code CardDashboard} may contain additional logic, behavior, or state
     * associated with the grid's content and layout.
     *
     * @return the {@code CardDashboard} linked to this {@code CardGrid}, or {@code null}
     *         if no dashboard is currently set.
     */
    public CardDashboard getDashboard() {
        return dashboard;
    }
}
