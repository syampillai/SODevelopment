package com.storedobject.ui;

import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.shared.Registration;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * The CardDashboard class represents a dashboard composed of interactive cards laid out on a grid.
 * It extends the functionality of the ScrollingContent class and implements the ExecutableView
 * and CloseableView interfaces. This allows the dashboard to be part of an application's interactive
 * view system with capabilities to execute and manage its lifecycle.
 * <p></p>
 * The CardDashboard is designed to display a collection of cards organized using a grid layout,
 * and its caption can be dynamically set. It also includes an inner view component that manages
 * display-specific details and interactions related to its content. The caption updates are propagated
 * to the view if it exists.
 *
 * @param <T> The type of object associated with the cards in the dashboard.
 *
 * @author Syam
 */
public class CardDashboard<T> extends ScrollingContent implements ExecutableView, CloseableView {

    private final CardGrid<T> grid;
    private String caption;
    private V view;

    /**
     * Constructs a new {@code CardDashboard} instance with the specified caption and grid layout.
     * This constructor initializes the dashboard by associating it with the given {@code CardGrid}.
     * The provided grid is set as the scrollable content of the dashboard, and it is linked back
     * to the dashboard using the {@code setDashboard} method. Additionally, the caption for the
     * dashboard is set, and it can be dynamically modified later.
     *
     * @param caption the display caption for the dashboard. If {@code null} or blank, a default value ("Dashboard") is used.
     * @param grid    the {@code CardGrid} that organizes and manages the cards displayed in the dashboard.
     *                This grid is essential for layout and content management within the dashboard.
     */
    public CardDashboard(String caption, CardGrid<T> grid) {
        super(grid);
        this.grid = grid;
        grid.setDashboard(this);
        setCaption(caption);
    }

    @Override
    public void setCaption(String caption) {
        this.caption = caption == null || caption.isBlank() ? "Dashboard" : caption;
        if(view != null) {
            view.setCaption(caption);
        }
    }

    @Override
    public String getCaption() {
        return caption;
    }

    /**
     * Retrieves the {@code CardGrid} associated with this {@code CardDashboard}.
     * The {@code CardGrid} organizes and manages the layout and content of the cards
     * displayed within the dashboard.
     *
     * @return the {@code CardGrid} instance linked to this {@code CardDashboard}.
     */
    public CardGrid<T> getGrid() {
        return grid;
    }

    @Override
    public void execute(View lock) {
        ExecutableView.super.execute(lock);
        Application.get().closeMenu();
    }

    @Override
    public View getView(boolean create) {
        if(view == null && create) {
            view = new V();
        }
        return view;
    }

    private class V extends View {

        public V() {
            super(caption);
            setComponent(CardDashboard.this);
        }

        @Override
        public boolean isCloseable() {
            return CardDashboard.this.isCloseable();
        }
    }

    /**
     * Configures the selection mode for the grid within the {@code CardDashboard}.
     * The selection mode determines how cards in the grid can be selected
     * (e.g., single, multi, or none).
     *
     * @param selectionMode the {@code Grid.SelectionMode} that specifies the
     *                      card selection behavior in the grid. Must not be null.
     */
    public final void setSelectionMode(Grid.SelectionMode selectionMode) {
        getGrid().setSelectionMode(selectionMode);
    }

    /**
     * Retrieves the current selection mode of the {@code Grid} associated with this dashboard.
     * The selection mode determines how items within the grid can be selected
     * (e.g., single, multiple, or none).
     *
     * @return the current {@code Grid.SelectionMode} of the associated {@code Grid}.
     */
    public final Grid.SelectionMode getSelectionMode() {
        return getGrid().getSelectionMode();
    }

    /**
     * Adds a card selection listener to the dashboard's associated {@code CardGrid}.
     * The listener is triggered whenever a card is selected in the grid.
     *
     * @param listener a {@code Consumer} that processes the {@code Card} selected by the user.
     *                 This listener is invoked with the selected card as its input.
     * @return a {@code Registration} object that can be used to remove the listener if it is no longer needed.
     */
    public final Registration addCardSelectedListener(Consumer<Card<T>> listener) {
        return getGrid().addCardSelectedListener(listener);
    }

    /**
     * Toggles the selection state of all cards within the dashboard's grid.
     * This method delegates the operation to the {@code CardGrid} associated with
     * the {@code CardDashboard}. It allows for selecting or deselecting all cards
     * simultaneously based on the specified parameter. This operation is only
     * effective if the grid's selection mode is set to {@code Grid.SelectionMode.MULTI}.
     *
     * @param select a boolean value indicating the desired selection state for all cards.
     *               If {@code true}, all cards in the grid will be selected; if {@code false},
     *               all cards will be deselected.
     */
    public void selectAllCards(boolean select) {
        getGrid().selectAllCards(select);
    }

    /**
     * Retrieves the currently selected card from the associated grid.
     * The selected card is managed by the {@code CardGrid} linked to this dashboard.
     *
     * @return the selected {@code Card}, or {@code null} if no card is selected.
     */
    public Card<T> getSelectedCard() {
        return getGrid().getSelectedCard();
    }

    /**
     * Retrieves a stream of the currently selected cards from the associated {@code CardGrid}.
     *
     * @return a {@code Stream} of {@code Card} objects that are currently selected in the grid.
     */
    public Stream<Card<T>> getSelectedCards() {
        return getGrid().getSelectedCards();
    }

    /**
     * Retrieves a stream of {@code Card} objects managed by the associated {@code CardGrid}.
     * This method is used to access and iterate through all the cards currently organized
     * within the grid layout of the dashboard.
     *
     * @return a {@code Stream} of {@code Card<T>} instances representing the cards in the grid.
     */
    public Stream<Card<T>> getCards() {
        return getGrid().getCards();
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
        return grid == null || grid.ignoreSelection;
    }
}
