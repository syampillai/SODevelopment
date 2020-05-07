package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;

@Tag("iframe")
public class IFrame extends Component implements HasSize {

    public IFrame() {
        this((String)null);
    }

    public IFrame(String sourceDocument) {
    }

    public IFrame(TextContent textContent) {
        this(textContent.getContent());
    }

    public void setSourceDocument(String sourceDocument) {
    }

    public void setSourceDocument(String sourceDocument, boolean parse) {
    }
}