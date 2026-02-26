package com.storedobject.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A CardGrid is a UI component designed to display a grid of cards.
 * The grid is dynamic and adjusts to the available space while maintaining
 * a consistent card width and gap between the cards.
 * <p></p>
 * CardGrid extends the {@code Div} component and provides methods for customizing
 * the appearance and behavior of the grid layout. It supports setting a custom card width
 * and gap size, and it can also be linked to a {@code CardDashboard} for managing its content.
 *
 * @param <T> The type of object associated with the cards in the grid.
 * @author Syam
 */
public class CardGrid<T> extends Div {

    private Grid.SelectionMode selectionMode = Grid.SelectionMode.NONE;
    private CardDashboard<T> dashboard;
    private int cardWidth, gap;
    private Card<T> selectedCard;
    private List<Consumer<Card<T>>> cardSelectedListeners;

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
    void setDashboard(CardDashboard<T> dashboard) {
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
    public CardDashboard<T> getDashboard() {
        return dashboard;
    }

    /**
     * Sets the selection mode for the grid. The selection mode determines
     * how cards in the grid can be selected, such as single, multi, or none.
     *
     * @param selectionMode the {@code Grid.SelectionMode} to define the
     *                       card selection behavior in the grid. This value
     *                       cannot be null.
     */
    public void setSelectionMode(Grid.SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    /**
     * Retrieves the current selection mode of the grid.
     * The selection mode defines how cards in the grid can be selected
     * (e.g., single, multiple, or none).
     *
     * @return the current {@code Grid.SelectionMode} for the grid
     */
    public final Grid.SelectionMode getSelectionMode() {
        return selectionMode;
    }

    /**
     * Registers a listener triggered whenever a card is selected in the grid.
     * The listener will receive the selected {@code Card} as its input parameter.
     *
     * @param listener a {@code Consumer} that processes the selected {@code Card}. This listener
     *                 will be notified whenever a card is selected.
     * @return a {@code Registration} instance that can be used to remove the listener when it
     *         is no longer necessary.
     */
    public Registration addCardSelectedListener(Consumer<Card<T>> listener) {
        if(cardSelectedListeners == null) {
            cardSelectedListeners = new ArrayList<>();
        }
        cardSelectedListeners.add(listener);
        return () -> cardSelectedListeners.remove(listener);
    }

    void selected(Card<T> card) {
        if(cardSelectedListeners != null) {
            cardSelectedListeners.forEach(l -> l.accept(card));
        }
    }

    void clicked(Card<T> card) {
        switch (selectionMode) {
            case null -> {
            }
            case NONE -> {
            }
            case SINGLE -> {
                card.toggleSelection();
                if(selectedCard != null && selectedCard != card) {
                    selectedCard.setSelected(!card.isSelected());
                }
                selectedCard = card.isSelected() ? card : null;
            }
            case MULTI -> {
                card.toggleSelection();
                selectedCard = null;
            }
        }
    }

    /**
     * Toggles the selection state of all cards in the grid based on the specified parameter.
     * This method only operates when the grid's selection mode is set to {@code Grid.SelectionMode.MULTI}.
     *
     * @param select a boolean value indicating the desired selection state for all cards.
     *               If true, all cards will be marked as selected; otherwise, all cards
     *               will be deselected.
     */
    public void selectAllCards(boolean select) {
        if(selectionMode == Grid.SelectionMode.MULTI) {
            getCards().forEach(c -> c.setSelected(select));
        }
    }

    /**
     * Retrieves the currently selected card based on the grid's current selection mode.
     * If the selection mode is set to {@code NONE}, no card can be selected and this method will return {@code null}.
     * If the selection mode is {@code SINGLE}, this method will return the selected card if one is selected,
     * or {@code null} if no card is selected. If multiple cards are selected, the first card in the stream
     * of selected cards will be returned.
     *
     * @return the selected {@code Card} if a selection exists; {@code null} otherwise
     */
    public Card<T> getSelectedCard() {
        if(selectionMode == Grid.SelectionMode.NONE) {
            return null;
        }
        if(selectionMode == Grid.SelectionMode.SINGLE) {
            return selectedCard != null && selectedCard.isSelected() ? selectedCard : null;
        }
        return getSelectedCards().findFirst().orElse(null);
    }

    /**
     * Retrieves a stream of all cards that are currently selected.
     * This method filters the list of available cards to include only
     * those that are marked as selected.
     *
     * @return a stream of selected cards
     */
    public Stream<Card<T>> getSelectedCards() {
        return getCards().filter(Card::isSelected);
    }

    /**
     * Retrieves a stream of all {@code Card} elements currently present as children of this {@code CardGrid}.
     * This method filters the child components of the grid, retaining only those that are instances of {@code Card}.
     *
     * @return a {@code Stream} of {@code Card} elements representing the cards in the grid
     */
    public Stream<Card<T>> getCards() {
        //noinspection unchecked
        return getChildren().filter(Card.class::isInstance).map(Card.class::cast);
    }
}
