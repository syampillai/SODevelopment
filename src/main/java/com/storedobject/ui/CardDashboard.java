package com.storedobject.ui;

import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;

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
 * @author Syam
 */
public class CardDashboard extends ScrollingContent implements ExecutableView, CloseableView {

    private final CardGrid grid;
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
    public CardDashboard(String caption, CardGrid grid) {
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
    public CardGrid getGrid() {
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
}
