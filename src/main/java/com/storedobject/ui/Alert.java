package com.storedobject.ui;

import java.util.function.Consumer;

public class Alert extends com.storedobject.vaadin.Alert implements StyledBuilder {

    private final Application application;

    public Alert(String htmlText) {
        this(htmlText, null);
    }

    public Alert(String htmlText, Consumer<Alert> action) {
        super(new ELabel(htmlText), action == null ? com.storedobject.vaadin.Alert::delete : a -> action.accept((Alert)a));
        application = Application.get();
    }

    @Override
    public ELabel getContent() {
        return (ELabel)super.getContent();
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return getContent();
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public boolean isNewLine() {
        return getContent().isNewLine();
    }

    @Override
    public StyledBuilder update() {
        show();
        return this;
    }

    @Override
    public void show() {
        getContent().update();
        super.show();
    }

    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean deleteOnClose() {
        return false;
    }
}