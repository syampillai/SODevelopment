package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;

/**
 * Create a {@link View} from some HTML content. The HTML may contain
 * references to media content using ${media} variable where media is the name of the media to set.
 * ({@link com.storedobject.core.MediaFile}s can be stored in the DB). If the HTML is a full-fledged one (if it
 * starts with an html tag), an {@link IFrame} will be created as the component of the {@link View}. Otherwise,
 * a {@link TemplateLayout} is created.
 *
 * @author Syam
 */
public class HTMLView extends Viewer {

    private static final String MISSING = "HTML content missing!";

    /**
     * Constructor. Currently running logic title will be used as the "text content name".
     *
     * @param application Application instance.
     */
    public HTMLView(Application application) {
        this(application.getLogicTitle(null));
    }

    /**
     * Constructor.
     *
     * @param textContentName Name of the text content to set.
     */
    public HTMLView(String textContentName) {
        this(tc(textContentName));
    }

    /**
     * Constructor. Currently running logic title will be used as the "text content name".
     *
     * @param application Application instance.
     * @param windowMode Whether to show it in a window or not.
     */
    public HTMLView(Application application, boolean windowMode) {
        this(application.getLogicTitle(null), windowMode);
    }

    /**
     * Constructor.
     *
     * @param textContentName Name of the text content to set.
     * @param windowMode Whether to show it in a window or not.
     */
    public HTMLView(String textContentName, boolean windowMode) {
        this(tc(textContentName), windowMode);
    }

    /**
     * Constructor.
     *
     * @param textContent Text content to set.
     */
    public HTMLView(TextContent textContent) {
        this(textContent, false);
    }

    /**
     * Constructor.
     *
     * @param textContent Text content to set.
     * @param windowMode Whether to show it in a window or not.
     */
    public HTMLView(TextContent textContent, boolean windowMode) {
        super(textContent == null ? "HTML" : textContent.getName());
        if(textContent == null) {
            throw new SORuntimeException(MISSING);
        }
        boolean asIFrame = textContent.getContent().startsWith("<html");
        Component component;
        if(asIFrame) {
            IFrame iFrame = new IFrame();
            iFrame.getElement().getStyle().set("display", "block").set("overflow", "hidden");
            iFrame.setSourceDocument(textContent.getContent());
            component = iFrame;
        } else {
            component = new TemplateLayout(textContent);
        }
        ((HasSize)component).setSizeFull();
        setComponent(windowMode ? createWindow(component) : component);
    }

    private static TextContent tc(String name) {
        if(name == null || name.isEmpty()) {
            throw new SORuntimeException(MISSING);
        }
        TextContent tc = SOServlet.getTextContent(name);
        if(tc == null) {
            throw new SORuntimeException(MISSING + " - " + name);
        }
        return tc;
    }
}