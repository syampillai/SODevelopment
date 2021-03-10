package com.storedobject.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import com.vaadin.flow.component.template.Id;
import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.AnnotationReader;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.shared.util.SharedUtil;

/**
 * Abstract base class for a component that is initialized based on the contents
 * of a HTML file. The HTML file is read from the classpath to create a
 * server-side Element tree. For instance fields marked with @{@link Id}, an
 * element with the corresponding id attribute value is identified, upgraded to
 * a component of the type defined by the field and the component instance is
 * set as the field value.
 *
 * @author Leif Åstrand (Vaadin Ltd.). Modified by Syam.
 */
@Tag("div")
public abstract class HtmlTemplate extends Component {

    private static final ConcurrentHashMap<String, Document> parserCache = new ConcurrentHashMap<>();

    /**
     * Creates a new HTML template based on an HTML file on the classpath with
     * the same name as this class, but using .html as the file extension.
     */
    protected HtmlTemplate() {
        populateFromClasspath(getClass().getSimpleName() + ".html");
    }

    /**
     * Creates a new HTML template based on the given HTML template URL. The
     * template is loaded from the classpath relative to the location of the
     * instantiated class.
     *
     * @param templateUrl
     *            the HTML template URL, not <code>null</code>
     */
    protected HtmlTemplate(String templateUrl) {
        Objects.requireNonNull(templateUrl, "The template URL cannot be null");
        populateFromClasspath(templateUrl);
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
     *            used or if there is a cache miss, not <code>null</code>
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
     *            used or if there is a cache miss, not <code>null</code>
     */
    protected HtmlTemplate(String cacheKey, StreamSupplier streamSupplier, StyleSupplier styleSupplier) {
        populate(cacheKey, streamSupplier, styleSupplier);
    }

    private void populateFromClasspath(String templateUrl) {
        Class<?> origin = getClass();
        populate("classpath:" + origin + "/" + templateUrl, () -> {
            InputStream resourceAsStream = origin.getResourceAsStream(templateUrl);
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Could not find " + templateUrl
                        + " on the classpath relative to the class " + origin.getName());
            }
            return resourceAsStream;
        }, null);
    }

    private void populate(String cacheKey, StreamSupplier streamSupplier, StyleSupplier styleSupplier) {
        Document document = getTemplate(cacheKey, streamSupplier);
        Map<String, Element> idMap = new HashMap<>();
        convertAndAppend(document.body(), getElement().attachShadow(), idMap::put, styleSupplier);
        for (Field field : getClass().getDeclaredFields()) {
            AnnotationReader.getAnnotationFor(field, Id.class).map(Id::value).ifPresent(id -> {
                if (id.isEmpty()) {
                    id = field.getName();
                }
                Component component;
                Element idElement = idMap.get(id);
                if (idElement == null) {
                    try {
                        component = getComponentForId(id);
                    } catch(NO_COMPONENT no_component) {
                        throw new IllegalArgumentException("There is no element with id " + id + " to match " + field);
                    }
                } else {
                    component = Component.from(idElement, field.getType().asSubclass(Component.class));
                }
                if(component != null) {
                    ReflectTools.setJavaFieldValue(this, field, component);
                }
            });
        }
    }

    private static Document getTemplate(String cacheKey, StreamSupplier streamSupplier) {
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
        if (useCache) {
            return parserCache.computeIfAbsent(cacheKey, ignore -> readTemplate(streamSupplier));
        } else {
            /*
             * Read without caching in dev mode so that changes are available
             * without redeploy (as long as the application is run in a way that
             * reads resources straight from their original file system
             * location).
             */
            return readTemplate(streamSupplier);
        }
    }

    private static Document readTemplate(StreamSupplier streamSupplier) {
        try (InputStream resourceAsStream = streamSupplier.createStream()) {
            return Jsoup.parseBodyFragment(
                    StandardCharsets.UTF_8.decode(DataUtil.readToByteBuffer(resourceAsStream, 0)).toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void convertAndAppend(org.jsoup.nodes.Element jsoupElement, com.vaadin.flow.dom.Node<?> flowNode,
                                  BiConsumer<String, Element> idElementConsumer, StyleSupplier styleSupplier) {
        String style = styleSupplier == null ? null : styleSupplier.getStyle();
        if(style != null && !style.isBlank()) {
            Element styleElement = new Element("style");
            styleElement.setText(style);
            flowNode.appendChild(styleElement);
        }
        jsoupElement.childNodes().stream().map(child -> jsoupToFlow(child, idElementConsumer)).filter(Objects::nonNull)
                .forEach(flowNode::appendChild);
    }

    private Element jsoupToFlow(Node node, BiConsumer<String, Element> idElementConsumer) {
        if (node instanceof org.jsoup.nodes.Element) {
            org.jsoup.nodes.Element jsoupElement = (org.jsoup.nodes.Element) node;
            Element flowElement = new Element(jsoupElement.tagName());
            jsoupElement.attributes().forEach(attr -> {
                String value = attr.getValue();
                String key = attr.getKey();
                if (key.startsWith("!")) {
                    String propertyName = SharedUtil.dashSeparatedToCamelCase(key.substring(1));
                    boolean valueBoolean = value.equals("") || value.equalsIgnoreCase("true")
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
                } else if (value == null || value.equals("")) {
                    flowElement.setAttribute(key, true);
                } else {
                    flowElement.setAttribute(key, value);
                    if ("id".equals(key)) {
                        idElementConsumer.accept(value, flowElement);
                    }
                }
            });
            convertAndAppend(jsoupElement, flowElement, idElementConsumer, null);
            return flowElement;
        } else if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            return Element.createText(textNode.text());
        } else if (node instanceof Comment) {
            return null;
        } else {
            throw new IllegalArgumentException("Unsupported tag: " + node.getClass().getName());
        }
    }

    protected Component getComponentForId(String id) {
        throw new NO_COMPONENT();
    }

    private static class NO_COMPONENT extends RuntimeException {
    }

    /**
     * Callback for creating an input stream on demand.
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

    public interface StyleSupplier {
        String getStyle();
    }
}
