package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Represents a container component that manages a scrolling content area
 * along with an optional header. It is designed to handle both the header
 * and content sections flexibly, where the content area supports scrolling
 * and the header has a fixed height.
 * <p></p>
 * This class extends {@code Div} and provides a customizable structure for
 * laying out a header and a scrollable content area. It is styled and
 * configured by default to support these features but also allows further
 * customization.
 *
 * @author Syam
 */
public class ScrollingContent extends Div {

    private final Div headerContainer = new Div();
    private final Div container = new Div();
    private int margin;

    /**
     * Default constructor for the ScrollingContent class. This constructor initializes
     * an instance of ScrollingContent without any pre-defined header or content.
     * By default, it configures the internal layout and styles for the scrollable
     * content area and header section, ensuring a consistent appearance and behavior.
     */
    public ScrollingContent() {
        this(null, null);
    }

    /**
     * Constructs a {@code ScrollingContent} instance with a scrollable content area
     * and an optional header component.
     *
     * @param content the scrollable content. If {@code null}, no content is set initially.
     */
    public ScrollingContent(Component content) {
        this(null, content);
    }

    /**
     * A UI component that organizes content with a scrollable area and a header.
     *
     * @param header  the header component to be displayed at the top of the scrolling area
     * @param content the main content component to be displayed inside the scrollable container
     */
    public ScrollingContent(Component header, Component content) {
        setMargin(12);
        container.getStyle()
                .set("flex", "1")
                .set("overflow", "auto");
        headerContainer.getStyle().set("display", "flex");
        headerContainer.setWidthFull();
        setHeaderHeight(16);
        if(content != null) {
            container.add(content);
        }
        getStyle()
                .set("height", "100%")
                .set("min-height", "0")
                .set("min-width", "0")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("overflow", "hidden");
        setHeader(header);
        add(headerContainer, container);
    }

    /**
     * Retrieves the content component of the container. If the container does not
     * contain any components, this method returns {@code null}.
     *
     * @return the first component in the container, or {@code null} if the container is empty
     */
    public Component getContent() {
        return container.getComponentCount() > 0 ? container.getComponentAt(0) : null;
    }

    /**
     * Updates the content of the container by removing all existing components
     * and adding the specified component as the new content.
     * If the provided content is null, the container will be cleared and left empty.
     *
     * @param content The new content to be set in the container. If null, the container is cleared and no content is added.
     */
    public void setContent(Component content) {
        container.removeAll();
        if(content != null) {
            container.add(content);
        }
    }

    /**
     * Retrieves the first component from the header container if it exists.
     *
     * @return the first component in the header container if available, otherwise null
     */
    public Component getHeader() {
        return headerContainer.getComponentCount() > 0 ? headerContainer.getComponentAt(0) : null;
    }

    /**
     * Sets the header component of the container.
     * This method removes all existing components from the header container
     * and adds the specified header component if it is not null.
     *
     * @param header the component to set as the header; if null, the header container will remain empty
     */
    public void setHeader(Component header) {
        headerContainer.removeAll();
        if(header != null) {
            headerContainer.add(header);
        }
    }

    /**
     * Sets the height of the header section in the container.
     * This method updates the CSS flex property to ensure the header occupies
     * the specified height and maintains a fixed size within the layout.
     *
     * @param headerHeight the height of the header, in pixels; must be a non-negative integer
     */
    public void setHeaderHeight(int headerHeight) {
        headerContainer.getStyle().set("flex", "0 0 " + headerHeight + "px");
    }

    /**
     * Retrieves the margin value.
     *
     * @return the margin value as an integer
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Sets the margin size for the container and header container.
     *
     * @param margin the desired margin size in pixels. If the provided value is negative, it will be adjusted to 0.
     */
    public void setMargin(int margin) {
        margin = Math.max(0, margin);
        this.margin = margin;
        container.getStyle().set("margin", margin + "px");
        headerContainer.getStyle().set("margin", margin + "px " + margin + "px 0px " + margin + "px");
    }
}
