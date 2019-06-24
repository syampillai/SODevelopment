package com.storedobject.ui;

import java.util.function.Consumer;

public class Alert extends com.storedobject.vaadin.Alert implements StyledBuilder {

    public Alert(String htmlText) {
        this(htmlText, null);
    }

    public Alert(String htmlText, Consumer<Alert> action) {
        super(new ELabel(htmlText), a -> action.accept((Alert)a));
    }

    @Override
    public ELabel getContent() {
        return null;
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return null;
    }

    @Override
    public Application getApplication() {
        return null;
    }
}