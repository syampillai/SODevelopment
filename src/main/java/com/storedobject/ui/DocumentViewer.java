package com.storedobject.ui;

import com.storedobject.core.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DocumentViewer extends Composite<VerticalLayout> implements HasSize {
    
    private final VerticalLayout layout = new VerticalLayout();
    
    private final com.storedobject.ui.util.DocumentViewer docViewer;

    public DocumentViewer() {
        docViewer = new com.storedobject.ui.util.DocumentViewer(this::contentChanged);
        layout.setMargin(false);
        layout.setPadding(false);
    }

    public DocumentViewer(Id streamDataId) {
        this();
        docViewer.setDocument(streamDataId);
    }

    public DocumentViewer(StreamData streamData) {
        this();
        docViewer.setDocument(streamData);
    }

    public DocumentViewer(ContentProducer contentProducer) {
        this();
        docViewer.setDocument(contentProducer);
    }

    public void setDocument(Id streamDataId) {
        docViewer.setDocument(streamDataId);
    }

    public void setDocument(StreamData streamData) {
        docViewer.setDocument(streamData);
    }

    public void setDocument(ContentProducer contentProducer) {
        docViewer.setDocument(contentProducer);
    }

    @Override
    protected final VerticalLayout initContent() {
        return layout;
    }

    @Override
    public void setWidth(String width) {
        HasSize.super.setWidth(width);
        if(docViewer.getViewerComponent() instanceof HasSize c) {
            c.setWidth(width);
        }
    }

    @Override
    public void setHeight(String height) {
        layout.setHeight(height);
        if(docViewer.getViewerComponent() instanceof HasSize c) {
            c.setHeight(height);
        }
    }

    @Override
    public void setMinWidth(String minWidth) {
        layout.setMinWidth(minWidth);
        if(docViewer.getViewerComponent() instanceof HasSize c) {
            c.setMinWidth(minWidth);
        }
    }

    @Override
    public void setMinHeight(String minHeight) {
        layout.setMinHeight(minHeight);
        if(docViewer.getViewerComponent() instanceof HasSize c) {
            c.setMinHeight(minHeight);
        }
    }

    @Override
    public void setMaxWidth(String maxWidth) {
        layout.setMaxWidth(maxWidth);
        if(docViewer.getViewerComponent() instanceof HasSize c) {
            c.setMaxWidth(maxWidth);
        }
    }

    @Override
    public void setMaxHeight(String maxHeight) {
        layout.setMaxHeight(maxHeight);
        if(docViewer.getViewerComponent() instanceof HasSize c) {
            c.setMaxHeight(maxHeight);
        }
    }

    private void contentChanged() {
        layout.removeAll();
        Component c = docViewer.getViewerComponent();
        c.getElement().getStyle().set("margin", "0px").set("padding", "0px");
        layout.add(c);
    }
}
