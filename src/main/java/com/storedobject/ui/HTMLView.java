package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.dialog.Dialog;

import java.io.BufferedReader;
import java.io.Reader;

/**
 * Create a {@link View} from some HTML content. The HTML may contain
 * references to media content using ${media} variable where media is the name of the media to set.
 * ({@link com.storedobject.core.MediaFile}s can be stored in the DB). If the HTML is a full-fledged one (if it
 * starts with a html tag), an {@link IFrame} will be created as the component of the {@link View}. Otherwise,
 * a {@link TemplateLayout} is created.
 *
 * @author Syam
 */
public class HTMLView extends Viewer {

    private static final String MISSING = "HTML content missing!";
    private Component component;

    /**
     * Constructor. Currently, running logic title will be used as the "text content name".
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
     * Constructor. The running logic title will be used as the "text content name".
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
        super(textContent == null ? "View" : textContent.getName());
        if(textContent == null) {
            setContent(MISSING, null, windowMode);
        } else {
            setContent(null, textContent, windowMode);
        }
    }

    /**
     * Constructor.
     *
     * @param htmlContent HTML content.
     * @param windowMode Whether to show it in a window or not.
     */
    public HTMLView(Reader htmlContent, boolean windowMode) {
        super("View");
        setContent(html(htmlContent), null, windowMode);
    }

    /**
     * Constructor.
     *
     * @param htmlContent HTML content.
     */
    public HTMLView(Reader htmlContent) {
        this(htmlContent, false);
    }

    private void setContent(String content, TextContent textContent, boolean windowMode) {
        if(content == null) {
            content = textContent.getContent();
        }
        boolean asIFrame = isHTML(content);
        if(asIFrame || textContent == null) {
            IFrame iFrame = new IFrame();
            iFrame.getElement().getStyle().set("display", "block").set("overflow", "hidden");
            iFrame.setSourceDocument(content);
            component = iFrame;
        } else {
            component = new TemplateLayout(textContent);
        }
        ((HasSize)component).setSizeFull();
        Component c;
        if(windowMode) {
            c = createWindow(component);
        } else {
            if(showHeader()) {
                c = new ContentWithHeader(new WindowDecorator(this), component);
                c.getElement().getStyle().set("height", "92%");
            } else {
                c = component;
            }
        }
        setComponent(c);
    }

    /**
     * Whether to show a header with caption or not even in non-window mode. (Default implementation returns true)
     * unless it's a {@link HomeView}.
     *
     * @return True/false.
     */
    protected boolean showHeader() {
        return !isHomeView();
    }

    @Override
    public boolean isHomeView() {
        return this instanceof HomeView || getComponent() instanceof Dialog;
    }

    public static boolean isHTML(String content) {
        if(content.isBlank()) {
            return false;
        }
        int i = 0;
        while(Character.isSpaceChar(content.charAt(i)) || Character.isWhitespace(content.charAt(i))) {
            if(++i == content.length()) {
                return false;
            }
        }
        return content.startsWith("<html", i);
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

    private static String html(Reader reader) {
        BufferedReader r = IO.get(reader);
        StringBuilder s = new StringBuilder();
        String line;
        try {
            while((line = r.readLine()) != null) {
                if(!s.isEmpty()) {
                    s.append('\n');
                }
                if((s.length() + line.length()) > (100 * 1024)) {
                    return "Too big!";
                }
                s.append(line);
            }
        } catch(Throwable e) {
            return errorHTML(e);
        } finally {
            IO.close(r);
        }
        return s.toString();
    }

    /**
     * Get the component that is used internally to render the HTML.
     *
     * @return The viewer component.
     */
    public Component getViewerComponent() {
        return component;
    }

    private static String errorHTML(Object anything) {
        if(anything != null) {
            anything = ApplicationEnvironment.get().toDisplay(anything);
        }
        if(anything == null) {
            anything = "Nothing";
        }
        anything = HTMLText.encode(anything);
        return "<html><h1>" + anything + "</h1></html>";
    }
}