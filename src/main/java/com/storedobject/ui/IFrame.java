package com.storedobject.ui;

import com.storedobject.core.TextContent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.dom.Style;

/**
 * Component that represents an HTML iframe element. The source of the iframe should be set, and it may contain
 * references to media content using ${media} variable where media is the name of the media to set.
 *
 * @author Syam
 */
@Tag("iframe")
public class IFrame extends Component implements HasSize {

    /**
     * Constructor.
     */
    public IFrame() {
        this((String)null);
    }

    /**
     * Constructor.
     *
     * @param sourceDocument Source document to set.
     */
    public IFrame(String sourceDocument) {
        new ContextMenu(this);
        Style s = getElement().getStyle();
        s.set("border", "none");
        s.set("margin", "0px");
        s.set("padding", "0px");
        s.set("overflow", "auto");
        setSourceDocument(sourceDocument);
    }

    /**
     * Constructor.
     *
     * @param textContent Text content from which source document to be set.
     */
    public IFrame(TextContent textContent) {
        this(textContent == null ? null : textContent.getContent());
    }

    /**
     * Set the source.
     *
     * @param sourceDocument Source document to set.
     */
    public void setSourceDocument(String sourceDocument) {
        setSourceDocument(sourceDocument, true);
    }

    /**
     * Set the source.
     *
     * @param sourceDocument Source document to set.
     * @param parse Whether to parse or not.
     */
    public void setSourceDocument(String sourceDocument, boolean parse) {
        if(sourceDocument != null && !sourceDocument.isEmpty()) {
            if(parse) {
                sourceDocument = MediaCSS.parse(sourceDocument);
            }
            getElement().setAttribute("srcdoc", sourceDocument);
        }
    }
}