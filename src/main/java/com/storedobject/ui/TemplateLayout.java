package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.HtmlTemplate;
import com.storedobject.ui.util.SOServlet;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * A layout {@link com.vaadin.flow.component.Component} based on a template stored in the DB as an HTML like file -
 * It should be stored as {@link com.storedobject.core.TextContent}. The template can contain HTML tags (and thus,
 * Vaadin component tags too) and if any image resources are referenced, respective
 * {@link com.storedobject.core.MediaFile}s must be used just like in {@link HTMLView}.
 *
 * @author Syam
 */
public class TemplateLayout extends HtmlTemplate {

    /**
     * Constructor.
     *
     * @param textContentName Name of the template content.
     */
    public TemplateLayout(String textContentName) {
        this(tc(textContentName));
    }

    private TemplateLayout(TextContent tc) {
        super(tc.getName() + "V" + tc.getVersion(), () -> new TextContentStream(tc));
    }

    private static TextContent tc(String textContentName) {
        TextContent tc = SOServlet.getTextContent(textContentName);
        if(tc == null) {
            throw new SORuntimeException(textContentName + " - Template not found!");
        }
        return tc;
    }

    private static class TextContentStream extends InputStream {

        private BufferedReader reader;
        private ByteArrayInputStream bytes = null;

        private TextContentStream(TextContent tc) {
            this.reader = IO.get(new StringReader(tc.getContent()));
        }

        @Override
        public int read() throws IOException {
            if(bytes != null) {
                int r = bytes.read();
                if(r != -1) {
                    return r;
                }
                bytes = null;
            }
            if(reader == null) {
                return -1;
            }
            String s = reader.readLine();
            if(s == null) {
                IO.close(reader);
                reader = null;
                return -1;
            }
            bytes = new ByteArrayInputStream(MediaCSS.parse(s).getBytes(StandardCharsets.UTF_8));
            return read();
        }
    }
}
