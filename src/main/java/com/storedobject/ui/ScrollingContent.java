package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a container component that manages a scrolling content area
 * along with an optional header. It is designed to handle both the header
 * and content sections flexibly, where the content area supports scrolling
 * and the header has a fixed height.
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
        super.add(List.of(headerContainer, container));
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

    /**
     * Adds the specified components to the content-container.
     *
     * @param components the array of components to be added to the content-container.
     */
    @Override
    public void add(Component... components) {
        container.add(components);
    }

    /**
     * Adds the specified text to the content-container.
     *
     * @param text the text to be added to the content-container
     */
    @Override
    public void add(String text) {
        container.add(text);
    }

    /**
     * Adds a collection of components to the content-container.
     *
     * @param components the collection of components to be added to the content-container
     */
    @Override
    public void add(Collection<Component> components) {
        container.add(components);
    }

    /**
     * Removes the specified components from the content-container.
     *
     * @param components the components to be removed from the content-container
     */
    @Override
    public void remove(Component... components) {
        container.remove(components);
    }

    /**
     * Removes the specified collection of components from the content-container.
     *
     * @param components the collection of components to be removed from the content-container;
     *                   if the collection is null or empty, no action is performed
     */
    @Override
    public void remove(Collection<Component> components) {
        container.remove(components);
    }

    /**
     * Removes all components from the content-container maintained within this instance.
     * This operation clears all content currently present in the content-container,
     * leaving it empty.
     * This method overrides the {@code removeAll()} implementation from the
     * parent class to specifically target the components stored in the internal
     * {@code container}.
     */
    @Override
    public void removeAll() {
        container.removeAll();
    }

    /**
     * Adds a given component to the content-container as the first component.
     *
     * @param component the component to add as the first element in the content-container;
     *                  must not be null
     */
    @Override
    public void addComponentAsFirst(Component component) {
        container.addComponentAsFirst(component);
    }

    /**
     * Adds a component to the specified index within the content-container.
     * This method allows for precise placement of the component at the given index.
     *
     * @param index the position at which the component should be inserted.
     *              Must be a valid index within the content-container.
     * @param component the component to be added to the content-container.
     *                  Cannot be null.
     */
    @Override
    public void addComponentAtIndex(int index, Component component) {
        container.addComponentAtIndex(index, component);
    }

    /**
     * Replaces an existing component with a new component in the content-container.
     *
     * @param oldComponent the component to be replaced
     * @param newComponent the component to replace with
     */
    @Override
    public void replace(Component oldComponent, Component newComponent) {
        container.replace(oldComponent, newComponent);
    }

    /**
     * Returns the index of the specified component within the content-container.
     *
     * @param component the component whose index is to be determined
     * @return the index of the component within the content-container, or -1 if the component is not found
     */
    @Override
    public int indexOf(Component component) {
        return container.indexOf(component);
    }

    /**
     * Retrieves the component located at the specified index within the content-container.
     * If the index is out of bounds or the specified position does not contain a component,
     * this method may throw an exception.
     *
     * @param index the position of the desired component within the content-container, zero-based.
     *              Must be a valid index, ranging from 0 to the total component count minus one.
     * @return the component at the specified index; null may be returned based on the container's implementation if no component exists.
     */
    @Override
    public Component getComponentAt(int index) {
        return container.getComponentAt(index);
    }

    /**
     * Returns the total number of components present in the content-container.
     *
     * @return the count of components currently contained in the content-container
     */
    @Override
    public int getComponentCount() {
        return container.getComponentCount();
    }

    /**
     * Retrieves a stream of child components contained within the content-container.
     *
     * @return a Stream of Component objects representing the child components.
     */
    @Override
    public Stream<Component> getChildren() {
        return container.getChildren();
    }
}
