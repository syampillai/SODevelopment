package com.storedobject.ui.util;

import com.storedobject.chart.SOChart;
import com.storedobject.common.IO;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.TextContent;
import com.storedobject.ui.Image;
import com.storedobject.ui.MediaCSS;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.AnnotationReader;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.shared.util.SharedUtil;
import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Abstract base class for a component that is initialized based on the contents
 * of an HTML template. The HTML content is read to create a
 * server-side Element tree. For instance fields marked with @{@link Id}, an
 * element with the corresponding id attribute value is identified, upgraded to
 * a component of the type defined by the field and the component instance is
 * set as the field value. However, it is possible to custom-create the component instance by
 * overriding the {@link #createComponentForId(String)} or {@link #createComponentForId(String, String)} method.
 *
 * @author Leif Åstrand (Vaadin Ltd.). Enhanced by Syam.
 */
@Tag("div")
public abstract class HtmlTemplate extends Component {

    private record Doc(Document document, Map<String, Svg> svgMap) {}
    private static final ConcurrentHashMap<String, Doc> parserCache = new ConcurrentHashMap<>();
    private TemplateDetails templateDetails;
    private Object view;
    private ComponentCreator componentCreator;
    private boolean created = false;

    /**
     * Creates a new HTML template based on the content of the {@link TextContent} that has the same name of
     * this class.
     */
    protected HtmlTemplate() {
        this((TemplateDetails) null);
    }

    /**
     * Creates a new HTML template based on the content of the {@link TextContent} provided.
     *
     * @param tc Text content.
     */
    protected HtmlTemplate(TextContent tc) {
        this(td(tc));
    }

    /**
     * Creates a new HTML template based on the content of the {@link TextContent} provided.
     *
     * @param textContentName Text content name.
     */
    protected HtmlTemplate(String textContentName) {
        this(tc(textContentName));
    }

    /**
     * Creates a new HTML template based on the content provided by a supplier.
     *
     * @param contentSupplier Content supplier.
     */
    protected HtmlTemplate(Supplier<String> contentSupplier) {
        this(td(contentSupplier));
    }

    /**
     * Creates a new HTML template based on HTML read from an input stream.
     *
     * @param cacheKey
     *            the key to use for potentially caching the result of reading
     *            and parsing the template, or <code>null</code> never cache the
     *            result
     * @param streamSupplier
     *            an input stream supplier that will be used if caching isn't
     *            used or if there is a cache miss not <code>null</code>
     */

    protected HtmlTemplate(String cacheKey, StreamSupplier streamSupplier) {
        this(cacheKey, streamSupplier, null);
    }

    /**
     * Creates a new HTML template based on HTML read from an input stream.
     *
     * @param cacheKey
     *            the key to use for potentially caching the result of reading
     *            and parsing the template, or <code>null</code> never cache the
     *            result
     * @param streamSupplier
     *            an input stream supplier that will be used if caching isn't
     *            used or if there is a cache miss not <code>null</code>
     */
    protected HtmlTemplate(String cacheKey, StreamSupplier streamSupplier, StyleSupplier styleSupplier) {
        this(new TemplateDetails(cacheKey, streamSupplier, styleSupplier));
    }

    private HtmlTemplate(TemplateDetails templateDetails) {
        this.templateDetails = templateDetails == null ? td(tc(getClass().getName())) : templateDetails;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        build();
        super.onAttach(attachEvent);
    }

    /**
     * Build the template. This method is called automatically when the template
     * is attached to the UI.
     */
    public void build() {
        if(templateDetails != null) {
            populate(templateDetails.cacheKey, templateDetails.streamSupplier, templateDetails.styleSupplier);
            created = true;
            templateDetails = null;
        }
    }

    /**
     * Checks whether the HTML template has been successfully created.
     *
     * @return true if the template has been created, false otherwise
     */
    public boolean isCreated() {
        return created;
    }

    private static TemplateDetails td(TextContent tc) {
        HTMLSupplier hs = new HTMLSupplier(tc::getContent);
        return new TemplateDetails(tc.getName() + "V" + tc.getVersion(), hs, hs);
    }

    private static TemplateDetails td(Supplier<String> contentSupplier) {
        HTMLSupplier hs = new HTMLSupplier(contentSupplier);
        return new TemplateDetails(null, hs, hs);
    }

    /**
     * Sets the view object for the current template.
     *
     * @param view the view object to be associated with this template
     */
    public void setView(Object view) {
        this.view = view;
    }

    /**
     * Sets the {@code ComponentCreator} that will be used to create components
     * for specific IDs within the HTML template.
     *
     * @param componentCreator the {@code ComponentCreator} instance responsible
     *                         for creating components for specified IDs
     */
    public void setComponentCreator(ComponentCreator componentCreator) {
        this.componentCreator = componentCreator;
    }

    private void populate(String cacheKey, StreamSupplier streamSupplier, StyleSupplier styleSupplier) {
        Doc doc = getTemplate(cacheKey, streamSupplier);
        Map<String, Element> idElementMap = new HashMap<>();
        Map<String, Component> idComponentMap = new HashMap<>();
        convertAndAppend(doc.document.body(), getElement().attachShadow(), idElementMap::put, idComponentMap::put, styleSupplier, doc.svgMap);
        if(view == null) {
            view = this;
        }
        Class<?> myClass = view.getClass();
        while(myClass != HtmlTemplate.class && myClass != Object.class) {
            for(Field field: myClass.getDeclaredFields()) {
                AnnotationReader.getAnnotationFor(field, Id.class).map(Id::value).ifPresent(id -> {
                    if(id.isEmpty()) {
                        id = field.getName();
                    }
                    Component component = idComponentMap.get(id);
                    if(component == null) {
                        Element idElement = idElementMap.get(id);
                        if(idElement == null) {
                            component = createComponentForId(id);
                            if(component == null) {
                                component = new Span("[Id=" + id + "]");
                            }
                            component.setId(id);
                        } else {
                            component = Component.from(idElement, field.getType().asSubclass(Component.class));
                        }
                    }
                    if(component != null) {
                        ReflectTools.setJavaFieldValue(view, field, component);
                    }
                });
            }
            myClass = myClass.getSuperclass();
        }
    }

    private static Doc getTemplate(String cacheKey, StreamSupplier streamSupplier) {
        Map<String, Svg> svgMap;
        boolean useCache;
        if (cacheKey == null) {
            useCache = false;
        } else {
            VaadinService service = VaadinService.getCurrent();
            if (service != null) {
                useCache = service.getDeploymentConfiguration().isProductionMode();
            } else {
                useCache = true;
            }
        }
        Doc doc = null;
        if(useCache) {
            doc = parserCache.get(cacheKey);
        }
        if (doc == null) {
            svgMap = new HashMap<>();
            doc = new Doc(readTemplate(streamSupplier, svgMap), svgMap);
            if(useCache) {
                parserCache.put(cacheKey, doc);
            }
        }
        return doc;
    }

    private static Document readTemplate(StreamSupplier streamSupplier, Map<String, Svg> svgMap) {
        try (InputStream resourceAsStream = streamSupplier.createStream()) {
            Document d = Jsoup.parseBodyFragment(
                    StandardCharsets.UTF_8.decode(DataUtil.readToByteBuffer(resourceAsStream, 0)).toString());
            AtomicInteger svgCount = new AtomicInteger();
            // Replace all children of <svg> tags
            d.select("svg").forEach(svg -> {
                String id = svg.attr("id");
                if(id.isBlank()) {
                    id = "svg." + svgCount.incrementAndGet();
                    svg.attr("id", id);
                    String svgText = "<svg>" + svg.html() + "</svg>";
                    svgMap.put(id, new Svg(svgText));
                }
                svg.empty();
            });
            return d;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void convertAndAppend(org.jsoup.nodes.Element jsoupElement, com.vaadin.flow.dom.Node<?> flowNode,
                                  BiConsumer<String, Element> idElementConsumer,
                                  BiConsumer<String, Component> idComponentConsumer,
                                  StyleSupplier styleSupplier, Map<String, Svg> svgMap) {
        String style = styleSupplier == null ? null : styleSupplier.getStyle();
        if(style != null && !style.isBlank()) {
            Element styleElement = new Element("style");
            styleElement.setText(style);
            flowNode.appendChild(styleElement);
        }
        jsoupElement.childNodes().stream().map(child -> jsoupToFlow(child, idElementConsumer, idComponentConsumer, svgMap)).
                filter(Objects::nonNull).
                forEach(flowNode::appendChild);
    }

    private Element jsoupToFlow(Node node, BiConsumer<String, Element> idElementConsumer,
                                BiConsumer<String, Component> idComponentConsumer, Map<String, Svg> svgMap) {
        switch (node) {
            case org.jsoup.nodes.Element jsoupElement -> {
                String tag = jsoupElement.tagName();
                Component c = null;
                String id = jsoupElement.attributes().get("id");
                if (!id.isEmpty()) {
                    if("svg".equals(tag)) {
                        if(id.startsWith("svg.")) {
                            c = svgMap.get(id);
                        } else {
                            c = createSvgForId(id);
                            if(c == null) {
                                c = new Svg();
                            }
                        }
                    } else {
                        c = createComponentForId(id, tag);
                        if (!c.getElement().getTag().equals(tag)) {
                            throw new IllegalArgumentException("Incompatible component " + c.getClass().getName() +
                                    " for tag " + tag + ", Id = " + id);
                        }
                    }
                    if(c == null) {
                        throw new IllegalArgumentException("Component not created for id " + id);
                    }
                    c.setId(id);
                }
                if (c == null) {
                    c = createComponent(id, node.nodeName());
                }
                Component component = c;
                Element flowElement = component == null ? new Element(tag) : component.getElement();
                jsoupElement.attributes().forEach(attr -> {
                    String value = attr.getValue();
                    String key = attr.getKey();
                    if (key.startsWith("!")) {
                        String propertyName = SharedUtil.dashSeparatedToCamelCase(key.substring(1));
                        boolean valueBoolean = value.isEmpty() || value.equalsIgnoreCase("true")
                                || value.contentEquals("1");
                        flowElement.setProperty(propertyName, valueBoolean);
                    } else if (key.startsWith(".")) {
                        String propertyName = SharedUtil.dashSeparatedToCamelCase(key.substring(1));
                        flowElement.setProperty(propertyName, value);
                    } else if (key.startsWith("%")) {
                        String propertyName = SharedUtil.dashSeparatedToCamelCase(key.substring(1));
                        try {
                            flowElement.setProperty(propertyName, Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Cannot parse value for numeric property: " + propertyName);
                        }
                    } else if (value.isEmpty()) {
                        flowElement.setAttribute(key, true);
                    } else {
                        flowElement.setAttribute(key, value);
                        if ("id".equals(key)) {
                            idElementConsumer.accept(value, flowElement);
                            if (component != null) {
                                idComponentConsumer.accept(value, component);
                            }
                        }
                    }
                });
                convertAndAppend(jsoupElement, flowElement, idElementConsumer, idComponentConsumer, null, svgMap);
                return flowElement;
            }
            case TextNode textNode -> {
                return Element.createText(textNode.text());
            }
            case Comment ignored -> {
                return null;
            }
            case null -> {
                return null;
            }
            default -> throw new IllegalArgumentException("Unsupported tag: " + node.nodeName());
        }
    }

    private Component createComponent(String id, String tag) {
        Component c;
        if(tag.startsWith("so-")) {
            tag = tag.substring(2);
            c = createComponent(tag);
        } else {
            c = createHTMLComponent(tag);
        }
        if(!id.isEmpty() && c != null) {
            c.setId(id);
        }
        return c;
    }

    private String cName(String name) {
        int dash = name.indexOf("-");
        if(dash < 0) {
            return name;
        }
        String pre = name.substring(0, dash);
        name = name.substring(dash + 1);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return pre + cName(name);
    }

    private Component createHTMLComponent(String tag) {
        return switch (tag) {
            case "div" -> new Div();
            case "span" -> new Span();
            case "p" -> new Paragraph();
            case "img" -> new Image();
            case "button" -> new Button("", null);
            default -> null;
        };
    }

    private Component createComponent(String tag) { // Tag is starting with "so-"
        if("so-chart".equals(tag)) {
            return new SOChart();
        }
        tag = cName(tag);
        Component component;
        Class<?> c;
        try {
            c = Class.forName("com.storedobject.ui." + tag);
        } catch(Throwable ignored) {
            try {
                c = Class.forName("com.storedobject.vaadin." + tag);
            } catch(Throwable ignore) {
                c = null;
            }
        }
        if(c == null) {
            return null;
        }
        try {
            component = (Component) c.getDeclaredConstructor().newInstance();
        } catch(Throwable ignored) {
            component = null;
        }
        return component;
    }

    /**
     * Creates a {@code Component} for the given identifier. If a {@code ComponentCreator}
     * is set, the creation is delegated to it. Otherwise, a default {@code ISpan}
     * instance is created with the ID embedded in its text.
     *
     * @param id The identifier for which the {@code Component} is to be created.
     * @return The created {@code Component}, either provided by the {@code ComponentCreator}
     *         or an instance of {@code ISpan} with the given ID.
     */
    protected Component createComponentForId(String id) {
        if(componentCreator != null) {
            return componentCreator.createComponentForId(id);
        }
        return new ISpan("[Id = " + id + "]");
    }

    private static class ISpan extends Span {

        private ISpan(String text) {
            super(text);
        }
    }

    /**
     * Creates an SVG element for the given identifier. If a {@code ComponentCreator}
     * is set, the creation is delegated to it. Otherwise, {@code null} is returned.
     *
     * @param id The identifier for which to create the {@code Svg} element.
     * @return The created {@code Svg} element if a {@code ComponentCreator} is present,
     *         or {@code null} if no {@code ComponentCreator} is set or unable to handle the request.
     */
    protected Svg createSvgForId(String id) {
        return componentCreator == null ? null : componentCreator.createSvgForId(id);
    }

    /**
     * Creates a {@code Component} for the given identifier and tag. If a {@code ComponentCreator}
     * is available, it delegates the creation process. If no component is created by the
     * {@code ComponentCreator} or if the result is {@code null} or an {@code ISpan}, additional
     * logic is applied to create an appropriate component.
     *
     * @param id The identifier for which the {@code Component} is to be created.
     * @param tag The HTML tag used to infer the type of {@code Component} to create.
     * @return The created {@code Component}. If no specific component can be created,
     *         an {@code Html} object based on the given tag and identifier is returned.
     */
    protected Component createComponentForId(String id, String tag) {
        Component c;
        if(componentCreator != null) {
            c = componentCreator.createComponentForId(id, tag);
            if(c != null) {
                return c;
            }
        }
        c = createComponentForId(id);
        if(c == null || c instanceof ISpan) {
            c = createHTMLComponent(tag);
        }
        if(c != null) {
            return c;
        }
        return new Html("<" + tag + " id=\"" + id + "\"></" + tag + ">");
    }

    /**
     * Callback for creating an input stream on demand.
     *
     * @author Syam
     */
    @FunctionalInterface
    public interface StreamSupplier {
        /**
         * Creates an input stream. The caller of this method is responsible for
         * closing the returned stream.
         *
         * @return the created input stream, not <code>null</code>
         * @throws IOException
         *             if there was a problem when creating the stream
         */
        InputStream createStream() throws IOException;
    }

    /**
     * Represents a supplier functional interface for providing style information.
     * This interface is primarily designed to encapsulate the retrieval of style
     * details, allowing flexibility and reusability in components or templates
     * where style information needs to be dynamically supplied.
     * <p></p>
     * This interface is typically used in conjunction with HTML or other template-based
     * systems that incorporate style information during rendering or processing stages.
     * Implementations should provide a concrete mechanism to supply the style as a string.
     *
     * @author Syam
     */
    @FunctionalInterface
    public interface StyleSupplier {

        /**
         * Retrieves the style information as a string.
         * This method is intended to provide dynamic style content for use in rendering
         * or processing styles in templating systems or similar use cases.
         *
         * @return the style information as a string
         */
        String getStyle();
    }

    /**
     * Represents a functional interface that provides methods for creating components
     * and SVG elements based on unique identifiers or tags. It is primarily used
     * within the context of HTML templates to dynamically generate components.
     *
     * @author Syam
     */
    @FunctionalInterface
    public interface ComponentCreator {

        /**
         * Creates a component based on the given unique identifier.
         *
         * @param id the unique identifier used to create the component
         * @return the created component associated with the provided identifier
         */
        Component createComponentForId(String id);

        /**
         * Creates a component based on the given unique identifier and optionally a tag.
         *
         * @param id the unique identifier used to create the component
         * @param tag the tag used for additional customization or specification (currently unused in the implementation)
         * @return the created component associated with the provided identifier
         */
        default Component createComponentForId(String id, String tag) {
            return createComponentForId(id);
        }

        /**
         * Creates an SVG element based on the given unique identifier.
         *
         * @param id the unique identifier used to create the SVG element
         * @return the created SVG element associated with the provided identifier,
         *         or null if no SVG element could be created
         */
        default Svg createSvgForId(String id) {
            return null;
        }
    }

    private static TextContent tc(String textContentName) {
        int p = textContentName.indexOf(JavaClassLoader.VERSION_SEPARATOR);
        if(p > 0) {
            textContentName = textContentName.substring(0, p);
        }
        TextContent tc = SOServlet.getTextContent(textContentName);
        if(tc == null) {
            throw new SORuntimeException(textContentName + " - Template not found!");
        }
        return tc;
    }

    private static class HTMLSupplier implements StreamSupplier, StyleSupplier {

        private final Supplier<String> contentSupplier;
        private String[] html_css;

        private HTMLSupplier(Supplier<String> contentSupplier) {
            this.contentSupplier = contentSupplier;
        }

        @Override
        public InputStream createStream() {
            if(html_css == null) {
                parse();
            }
            return new LinesStream(html_css[0]);
        }

        @Override
        public String getStyle() {
            if(html_css == null) {
                parse();
            }
            return html_css[1];
        }

        private void parse() {
            html_css = new String[2];
            String c = contentSupplier.get();
            if(c == null) {
                c = "<span></span>";
            }
            c = c.strip();
            int p1 = c.indexOf("<style"), p2 = c.indexOf("</style>");
            if(p1 >= 0 && p2 >= 0) {
                p2 += 8;
                html_css[1] = MediaCSS.parse(c.substring(p1, p2));
                html_css[0] = c.substring(0, p1) + c.substring(p2);
            } else {
                html_css[0] = c;
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

    private record TemplateDetails(String cacheKey, StreamSupplier streamSupplier, StyleSupplier styleSupplier) {
    }

    /**
     * Clears the cache used by the HTML template parser.
     * This method removes all cached entries within the `parserCache`,
     * ensuring that later parsing operations start with an empty cache.
     * It is typically invoked to force the system to reparse and refresh
     * template data without relying on previously cached results.
     */
    public static void clearCache() {
        parserCache.clear();
    }
}
