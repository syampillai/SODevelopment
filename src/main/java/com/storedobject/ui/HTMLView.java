package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;

/**
 * Create a {@link View} from some HTML content. The HTML may contain
 * references to media content using ${media} variable where media is the name of the media to set.
 *
 * @author Syam
 */
public class HTMLView extends View implements CloseableView {

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
        IFrame iFrame = new IFrame();
        iFrame.setSizeFull();
        iFrame.getElement().getStyle().set("display", "block").set("overflow", "hidden");
        iFrame.setSourceDocument(textContent.getContent());
        setComponent(windowMode ? createWindow(iFrame) : iFrame);
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

    /**
     * Create the "window" to show the HTML content. This will be invoked only if the "window mode" is on.
     *
     * @param iFrame Iframe containing the HTML to display.
     * @return A window containing the iframe passed.
     */
    protected Window createWindow(IFrame iFrame) {
        Window w = new Window(new WindowDecorator(this), iFrame);
        w.setWidth("80vw");
        w.setHeight("80vh");
        iFrame.setHeight("90%");
        new Scrollable(iFrame);
        return w;
    }

    @Override
    public void decorateComponent() {
        super.decorateComponent();
        getComponent().getElement().getStyle().set("padding", "0px");
    }
}