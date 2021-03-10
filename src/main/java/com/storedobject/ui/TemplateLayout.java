package com.storedobject.ui;

import com.storedobject.common.IO;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.HtmlTemplate;
import com.storedobject.ui.util.SOServlet;
import com.vaadin.flow.component.HasSize;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * A layout {@link com.vaadin.flow.component.Component} based on a template stored in the DB as an HTML like file -
 * It should be stored as {@link com.storedobject.core.TextContent}. The template can contain HTML tags (and thus,
 * Vaadin component tags too) and if any image resources are referenced, respective
 * {@link com.storedobject.core.MediaFile}s must be used just like in {@link HTMLView}. For CSS styling the content,
 * style tag should be used and if used, it should be the first part of the content. Style tags used in any other
 * part will be ignored.
 *
 * @author Syam
 */
public class TemplateLayout extends HtmlTemplate implements HasSize {

    /**
     * Constructor.
     *
     * @param textContentName Name of the template content.
     */
    public TemplateLayout(String textContentName) {
        this(tc(textContentName));
    }

    public TemplateLayout(TextContent tc) {
        this(tc.getName() + "V" + tc.getVersion(), new HTMLSupplier(tc));
    }

    private TemplateLayout(String cacheKey, HTMLSupplier htmlSupplier) {
        super(cacheKey, htmlSupplier, htmlSupplier);
    }

    private static TextContent tc(String textContentName) {
        TextContent tc = SOServlet.getTextContent(textContentName);
        if(tc == null) {
            throw new SORuntimeException(textContentName + " - Template not found!");
        }
        return tc;
    }

    private static class HTMLSupplier implements StreamSupplier, StyleSupplier {

        private final TextContent tc;
        private String[] htmlcss;

        private HTMLSupplier(TextContent tc) {
            this.tc = tc;
        }

        @Override
        public InputStream createStream() {
            if(htmlcss == null) {
                parse();
            }
            return new LinesStream(htmlcss[0]);
        }

        @Override
        public String getStyle() {
            if(htmlcss == null) {
                parse();
            }
            return htmlcss[1];
        }

        private void parse() {
            htmlcss = new String[2];
            String c = tc.getContent().strip();
            if(c.startsWith("<style")) {
                int p = c.indexOf("</style>");
                if(p > 0) {
                    htmlcss[1] = MediaCSS.parse(c.substring(c.indexOf('>') + 1, p));
                    htmlcss[0] = c.substring(p + 8);
                }
            }
            if(htmlcss[0] == null) {
                htmlcss[0] = c;
            }
        }
    }

    private static class LinesStream extends InputStream {

        private BufferedReader reader;
        private ByteArrayInputStream bytes = null;

        private LinesStream(String html) {
            this.reader = IO.get(new StringReader(html));
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
