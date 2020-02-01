package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.server.AbstractStreamResource;

public class PDFViewer extends Composite<Component> {

    private Component component;

    public PDFViewer() {
    }

    PDFViewer(String source) {
        setSource(source);
    }

    public PDFViewer(AbstractStreamResource resource) {
        setSource(resource);
    }

    @Override
    protected Component initContent() {
        return component();
    }

    private Component component() {
        return component;
    }

    public void setSource(String fileURL) {
    }

    public void setSource(AbstractStreamResource streamResource) {
    }

    public void clear() {
    }
}
