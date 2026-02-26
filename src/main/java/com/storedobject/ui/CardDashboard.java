package com.storedobject.ui;

import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;

public class CardDashboard extends ScrollingContent implements ExecutableView, CloseableView {

    private final CardGrid grid;
    private String caption;
    private V view;

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
