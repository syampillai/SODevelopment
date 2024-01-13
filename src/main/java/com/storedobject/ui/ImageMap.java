package com.storedobject.ui;

import com.storedobject.vaadin.ClickHandler;
import com.storedobject.vaadin.util.ElementClick;
import com.vaadin.flow.component.*;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ShadowRoot;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import java.util.Objects;
import java.util.Optional;

/**
 * An image component with clickable rectangular areas.
 *
 * @author Syam (Original version was written by Leif Åstrand of Vaadin)
 */
@Tag("div")
public class ImageMap extends Component {

    /**
     * Event fired when an area is clicked.
     */
    public static class AreaClickEvent extends ComponentEvent<ImageMap> {

        private final Area area;

        /**
         * Creates a new area click event.
         *
         * @param source The image map that was clicked, not <code>null</code>
         * @param area The clicked area inside the image map, not
         *            <code>null</code>
         * @param fromClient <code>true</code> if the event originated from the client
         *            side, <code>false</code> otherwise
         */
        public AreaClickEvent(ImageMap source, Area area, boolean fromClient) {
            super(source, fromClient);
            this.area = Objects.requireNonNull(area, "Area cannot be null");
        }

        public Area getArea() {
            return area;
        }
    }

    /**
     * A clickable rectangular area in an image map.
     * Instances are created using {@link ImageMap#addArea(int, int, int, int)}.
     */
    public static class Area {

        private final Element element = new Element("div");
        private final ImageMap owner;

        private Area(ImageMap owner, int x, int y, int width, int height) {
            this.owner = owner;
            element.getClassList().add("area");
            setLeft(x);
            setTop(y);
            setWidth(width);
            setHeight(height);
        }

        /**
         * Sets the position of the left edge of this area, relative to the left
         * edge of the image map.
         *
         * @param left The left edge position, in pixels
         * @return this area, for chaining
         */
        public Area setLeft(int left) {
            return setStylePx("left", left);
        }

        /**
         * Sets the position of the top edge of this area, relative to the top
         * edge of the image map.
         *
         * @param top The top edge position, in pixels
         * @return this area, for chaining
         */
        public Area setTop(int top) {
            return setStylePx("top", top);
        }

        /**
         * Sets the width of this area.
         *
         * @param width The area width, in pixels
         * @return this area, for chaining
         */
        public Area setWidth(int width) {
            return setStylePx("width", width);
        }

        /**
         * Sets the height of this area.
         *
         * @param height The area height, in pixels
         * @return this area, for chaining
         */
        public Area setHeight(int height) {
            return setStylePx("height", height);
        }

        /**
         * Adds a click listener that is invoked whenever this area is clicked.
         *
         * @param listener The listener to add, not <code>null</code>
         * @return a registration for removing the listener
         */
        public Registration addClickListener(ComponentEventListener<AreaClickEvent> listener) {
            Objects.requireNonNull(listener, "Listener cannot be null");
            return element.addEventListener("click",
                    event -> listener.onComponentEvent(new AreaClickEvent(owner, this, true)));
        }

        /**
         * Sets the title of this area. The title is typically shown as a
         * tooltip.
         *
         * @param title The title to set, or <code>""</code> to remove any
         *            previously set title. Not null.
         * @return this area, for chaining
         */
        public Area setTitle(String title) {
            titleDescriptor.set(element, title);
            return this;
        }

        private Area setStylePx(String name, int value) {
            return setStyle(name, value + "px");
        }

        private Area setStyle(String name, String value) {
            element.getStyle().set(name, value);
            return this;
        }

        /**
         * Sets the CSS background definition to use for this area when it is
         * not hovered. If no value is set, then the value set using
         * {@link ImageMap#setAreaBackground(String)} is used as the default.
         *
         * @param background The CSS background definition, or <code>null</code> to
         *            clear any set value and use the default
         * @return this area, for chaining
         */
        public Area setBackground(String background) {
            return setStyle("--imagemap-background", background);
        }

        /**
         * Sets the CSS background definition to use for this area when it is
         * hovered. If no value is set, then the value set using
         * {@link ImageMap.Area#setBackground(String)} is used as the default.
         *
         * @param hoverBackground The CSS background definition, or <code>null</code> to
         *            clear any set value and use the default
         * @return this area, for chaining
         */
        public Area setHoverBackground(String hoverBackground) {
            return setStyle("--imagemap-hover-background", hoverBackground);
        }

        /**
         * Sets the CSS border definition to use for this area when it is not
         * hovered. If no value is set, then the value set using
         * {@link ImageMap#setAreaBorder(String)} is used as the default.
         *
         * @param border The CSS border definition, or <code>null</code> to clear
         *            any set value and use the default
         * @return this area, for chaining
         */
        public Area setBorder(String border) {
            return setStyle("--imagemap-border", border);
        }

        /**
         * Sets the CSS border definition to use for this area when it is
         * hovered. If no value is set, then the value set using
         * {@link ImageMap#setHoverBorder(String)} is used as the default.
         *
         * @param hoverBorder The CSS border definition, or <code>null</code> to clear
         *            any set value and use the default
         * @return this area, for chaining
         */
        public Area setHoverBorder(String hoverBorder) {
            return setStyle("--imagemap-hover-border", hoverBorder);
        }
    }

    private static final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors
            .attributeWithDefault("src", "");
    private static final PropertyDescriptor<String, String> titleDescriptor = PropertyDescriptors
            .attributeWithDefault("title", "");
    private static final PropertyDescriptor<String, Optional<String>> altDescriptor = PropertyDescriptors
            .optionalAttributeWithDefault("alt", "");
    private static final String styles = loadStyles();
    private final Element image = new Element("img");
    private final ShadowRoot shadowRoot;
    private final ElementClick click;

    /**
     * Creates an empty image map.
     */
    public ImageMap() {
        shadowRoot = getElement().attachShadow();
        Element style = new Element("style");
        shadowRoot.appendChild(style);
        shadowRoot.appendChild(image);
        image.getStyle().set("z-index", "-1");
        style.setText(styles);
        click = new ElementClick(this);
    }

    /**
     * Creates an empty image map with the given image URL and alt text.
     *
     * @param source The image URL to use, not <code>null</code>
     */
    public ImageMap(String source) {
        this();
        setSource(Objects.requireNonNull(source, "Source should not be null"));
    }

    /**
     * Creates an empty image map with the given image resource and alt text.
     *
     * @param source The image resource to use, not <code>null</code>
     */
    public ImageMap(AbstractStreamResource source) {
        this();
        setSource(Objects.requireNonNull(source, "Source should not be null"));
    }

    /**
     * Adds a clickable rectangle area to this image map. The area coordinates
     * are relative to the upper left corner of the image map.
     *
     * @param x The x coordinate of the area, in pixels
     * @param y The y coordinate of the area, in pixels
     * @param width The area width, in pixels
     * @param height The area height, in pixels
     * @return the created area, not <code>null</code>
     */
    public Area addArea(int x, int y, int width, int height) {
        Area area = new Area(this, x, y, width, height);
        shadowRoot.insertChild(1, area.element);
        return area;
    }

    /**
     * Removes an Area that was added earlier.
     *
     * @param area Area to be removed
     */
    public void removeArea(Area area) {
        if(area != null) {
            shadowRoot.removeChild(area.element);
        }
    }

    /**
     * Add click handler.
     *
     * @param clickHandler Click handler
     * @return Registration.
     */
    @SuppressWarnings("UnusedReturnValue")
    public Registration addClickHandler(ClickHandler clickHandler) {
        return click.addClickListener(ClickHandler.convert(clickHandler));
    }

    /**
     * Sets the image URL.
     *
     * @param source The image URL.
     */
    public void setSource(String source) {
        srcDescriptor.set(image, source);
    }

    /**
     * Sets the image URL with the URL of the given {@link StreamResource}.
     *
     * @param source The resource value, not null
     */
    public void setSource(AbstractStreamResource source) {
        image.setAttribute("src", source);
    }

    /**
     * Sets the alternate text for the image.
     *
     * @param alt The alternate text
     */
    public void setAlt(String alt) {
        altDescriptor.set(image, alt);
    }

    /**
     * Gets the alternate text for the image.
     *
     * @return an optional alternate text, or an empty optional if no alternate
     *         text has been set
     */
    public Optional<String> getAlt() {
        return altDescriptor.get(image);
    }

    /**
     * Sets the default CSS background definition to use for hovered areas. The
     * setting can be overridden on a per-area basis using
     * {@link Area#setHoverBackground(String)}. By default, black with 90%
     * transparency is used (defined as <code>rgba(0,0,0,0.1)</code>).
     *
     * @param hoverBackground The CSS background definition, or <code>null</code> to clear
     *            any set value and use the default
     */
    public void setHoverBackground(String hoverBackground) {
        getElement().getStyle().set("--imagemap-hover-background", hoverBackground);
    }

    /**
     * Sets the default CSS border definition to use for hovered areas. The
     * setting can be overridden on a per-area basis using
     * {@link Area#setHoverBorder(String)}. By default, no border is used
     *
     * @param hoverBorder The CSS border definition, or <code>null</code> to clear any
     *            set value and use the default
     */
    public void setHoverBorder(String hoverBorder) {
        getElement().getStyle().set("--imagemap-hover-border", hoverBorder);
    }

    /**
     * Sets the default CSS background definition to use for areas that are not
     * hovered. The setting can be overridden on a per-area basis using
     * {@link Area#setBackground(String)}. By default, no background is used.
     *
     * @param areaBackground The CSS background definition, or <code>null</code> to clear
     *            any set value and use the default
     */
    public void setAreaBackground(String areaBackground) {
        getElement().getStyle().set("--imagemap-background", areaBackground);
    }

    /**
     * Sets the default CSS border definition to use for areas that are not
     * hovered. The setting can be overridden on a per-area basis using
     * {@link Area#setBorder(String)}. By default, no border is used.
     *
     * @param areaBorder The CSS border definition, or <code>null</code> to clear any
     *            set value and use the default
     */
    public void setAreaBorder(String areaBorder) {
        getElement().getStyle().set("--imagemap-border", areaBorder);
    }

    private static String loadStyles() {
        return ":host {" +
                "position: relative;" +
                "}" +
                ".area {" +
                "position: absolute;" +
                "box-sizing: border-box;" +
                "cursor: var(--imagemap-cursor, pointer);" +
                "border: var(--imagemap-border);" +
                "background: var(--imagemap-background);" +
                "}" +
                ".area:hover {" +
                "border: var(--imagemap-hover-border, var(--imagemap-border));" +
                "background: var(--imagemap-hover-background, rgba(0,0,0,0.1));" +
                "}";
    }
}