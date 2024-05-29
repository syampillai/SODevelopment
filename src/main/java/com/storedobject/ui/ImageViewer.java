package com.storedobject.ui;

import com.storedobject.core.MediaFile;
import com.storedobject.vaadin.Image;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dnd.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.AbstractStreamResource;
import elemental.json.JsonObject;

import java.util.ArrayList;

/**
 * A {@link View} that shows a background image, a parent image and other overlay-components relative to the parent
 * image. The background image and/or parent image may be absent.
 *
 * @author Syam
 */
public class ImageViewer extends View {

    private final Div parent = new Div();
    private final Image backgroundImage;
    private Image parentImage = null;
    private int offsetX, offsetY;
    private final Children children = new Children();
    private Component movingComponent;
    private Object movingItem;
    private boolean allowMovement;

    /**
     * Create an empty view. Images and components can be set later.
     *
     * @param caption Caption.
     */
    public ImageViewer(String caption) {
        this(caption, (Image)null);
    }

    /**
     * Create the image view with a background image from a URL.
     *
     * @param caption Caption.
     * @param backgroundImageURL Image URL for the background.
     */
    public ImageViewer(String caption, String backgroundImageURL) {
        this(caption, (Image)null);
        setBackgroundSource(backgroundImageURL);
    }

    /**
     * Create the image view with a background image from a resource.
     *
     * @param caption Caption.
     * @param backgroundImageResource Resource for the background image.
     */
    public ImageViewer(String caption, AbstractStreamResource backgroundImageResource) {
        this(caption, (Image)null);
        setBackgroundSource(backgroundImageResource);
    }

    /**
     * Create the image view from the given image.
     *
     * @param caption Caption.
     * @param backgroundImage Image.
     */
    public ImageViewer(String caption, Image backgroundImage) {
        super(caption);
        parent.setSizeFull();
        parent.getStyle().set("overflow", "hidden");
        this.backgroundImage = backgroundImage == null ? new Image((String)null) : backgroundImage;
        this.backgroundImage.setSizeFull();
        this.backgroundImage.getStyle().set("object-fit", "fill");
        parent.add(this.backgroundImage);
    }

    /**
     * Create the image view from the given media image.
     *
     * @param caption Caption.
     * @param mediaFileForBackground Background image.
     */
    public ImageViewer(String caption, MediaFile mediaFileForBackground) {
        this(caption, mediaFileForBackground != null
                && mediaFileForBackground.isImage() ? ("media/" + mediaFileForBackground.getFileName()) : "");
    }

    @Override
    protected void initUI() {
        setComponent(parent);
    }

    @Override
    public void decorateComponent() {
        super.decorateComponent();
        getComponent().getElement().getStyle().set("padding", "0px");
    }

    /**
     * Set the source of the background image content.
     *
     * @param source URL resource.
     */
    public void setBackgroundSource(String source) {
        backgroundImage.setSource(source);
    }

    /**
     * Set the source of the background image content.
     *
     * @param source Stream resource of the image content.
     */
    public void setBackgroundSource(AbstractStreamResource source) {
        backgroundImage.setSource(source);
    }

    /**
     * Set the source of the background image content.
     *
     * @param mediaFile Image source
     */
    public void setBackgroundSource(MediaFile mediaFile) {
        setBackgroundSource(mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : null);
    }

    /**
     * Invoke this method (typically from your constructor) if you want to allow dynamic movement (drag and drop) of
     * overlaid components.
     */
    public final void allowMovement() {
        if(parentImage != null || !children.isEmpty()) {
            error("Movement not allowed");
        } else {
            allowMovement = true;
        }
    }

    /**
     * Check whether movement is allowed or not.
     *
     * @return True/false.
     */
    public final boolean isMovementAllowed() {
        return allowMovement;
    }

    /**
     * Add a component at the given location (relative to the parent component if exists, otherwise relative
     * the origin - origin (0, 0) in the upper left corner).
     *
     * @param component Component to add.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void add(Component component, int x, int y) {
        add(component.getElement(), x, y);
    }

    /**
     * Add a component at the given location.
     *
     * @param component Component to add.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param absolute Whether the (X, Y) values given are absolute values or not.
     */
    public void add(Component component, int x, int y, boolean absolute) {
        add(component.getElement(), x, y, absolute);
    }

    /**
     * Add a component at the given location (relative to the parent component if exists, otherwise relative
     * the origin - origin (0, 0) in the upper left corner).
     *
     * @param component Component to add.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param itemData Associated item-data.
     */
    public void add(Component component, int x, int y, Object itemData) {
        add(component.getElement(), x, y);
        if(allowMovement && itemData != null) {
            DragSource<Component> ds = DragSource.configure(component, true);
            ds.setEffectAllowed(EffectAllowed.MOVE);
            ds.setDragData(itemData);
        }
    }

    /**
     * Remove a component.
     *
     * @param component Component to remove.
     */
    public void remove(Component component) {
        remove(component.getElement());
    }

    /**
     * Add an element at the given location (relative to the parent component if exists, otherwise relative
     * the origin - origin (0, 0) in the upper left corner).
     *
     * @param element Element to add.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void add(Element element, int x, int y) {
        add(element, x, y, false);
    }

    /**
     * Add an element at the given location.
     *
     * @param element Element to add.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @param absolute Whether the (X, Y) values given are absolute values or not.
     */
    public void add(Element element, int x, int y, boolean absolute) {
        children.add(element, x, y, absolute);
        if(!absolute) {
            x += offsetX;
            y += offsetY;
        }
        element.getStyle().set("margin-left", x + "px").set("margin-top", y + "px")
                .set("margin-right", "0px").set("margin-bottom", "0px").set("padding", "0px")
                .set("box-sizing", "border-box")
                .set("position", "absolute");
        parent.getElement().appendChild(element);
    }

    /**
     * Remove an element.
     *
     * @param element Element to remove.
     */
    public void remove(Element element) {
        children.remove(element);
        parent.getElement().removeChild(element);
        if(parentImage != null && element == parentImage.getElement()) {
            offsetX = offsetY = 0;
            children.reposition();
        }
    }

    /**
     * Re-position a component.
     *
     * @param component Component to re-position.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void reposition(Component component, int x, int y) {
        reposition(component.getElement(), x, y);
    }

    /**
     * Re-position an element.
     *
     * @param element Element to re-position.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     */
    public void reposition(Element element, int x, int y) {
        boolean a = children.isAbsolute(element);
        element.getStyle().set("margin-left", (x + (a ? 0 : offsetX)) + "px")
                .set("margin-top", (y + (a ? 0 : offsetY)) + "px")
                .set("position", "absolute");
        if(parentImage != null && parentImage.getElement() == element) {
            offsetX = x;
            offsetY = y;
            children.reposition();
        }
    }

    /**
     * Remove all components.
     */
    public void removeAll() {
        while(!children.isEmpty()) {
            remove(children.get(0).e);
        }
        removeParentImage();
    }

    /**
     * Remove the parent image.
     */
    public void removeParentImage() {
        if(parentImage != null) {
            parent.getElement().removeChild(this.parentImage.getElement());
            parentImage = null;
            offsetX = offsetY = 0;
            children.reposition();
        }
    }

    /**
     * Set a parent image.
     *
     * @param parentImage Parent image.
     * @param offsetX X-offset of the image.
     * @param offsetY Y-offset of the image.
     */
    public void setParentImage(Image parentImage, int offsetX, int offsetY) {
        if(this.parentImage != null) {
            parent.getElement().removeChild(this.parentImage.getElement());
        }
        this.parentImage = parentImage;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        Element pie = parentImage.getElement();
        parent.getElement().appendChild(pie);
        reposition(pie, offsetX, offsetY);
        if(allowMovement) {
            DropTarget<Image> dt = DropTarget.create(parentImage);
            dt.setActive(true);
            dt.addDropListener(this::dropped);
            dt.setDropEffect(DropEffect.MOVE);
            dt.getElement().addEventListener("drop", this::onDrop)
                    .addEventData("event.offsetX")
                    .addEventData("event.offsetY");
        }
    }

    /**
     * Get the X-offset of the parent image.
     *
     * @return X-offset.
     */
    public int getParentOffsetX() {
        return offsetX;
    }

    /**
     * Get the Y-offset of the parent image.
     *
     * @return Y-offset.
     */
    public int getParentOffsetY() {
        return offsetY;
    }

    private void onDrop(DomEvent event) {
        JsonObject eventData = event.getEventData();
        int x = (int)eventData.getNumber("event.offsetX");
        int y = (int)eventData.getNumber("event.offsetY");
        if(movingItem != null) {
            if(children.get(movingComponent.getElement()) != null) {
                new SavePos(movingItem, movingComponent, x, y).execute();
            }
            movingItem = null;
        }
    }

    private void dropped(DropEvent<Image> de) {
        Component component = de.getDragSourceComponent().orElse(null);
        if(component != null && children.get(component.getElement()) != null) {
            movingItem = de.getDragData().orElse(null);
            if(movingItem != null) {
                movingComponent = component;
            }
        }
    }

    /**
     * Save the position. This will be called when a component is dropped by the user (via drag and drop).
     *
     * @param item Associate item (This could be saved).
     * @param x X-coordinate at which it was dropped.
     * @param y Y-coordinate at which it was dropped.
     *
     * @return Should return <code>true</code> if the save was successful. If the return value is <code>false</code>,
     * the component will be placed back at its original location.
     */
    protected boolean savePosition(Object item, int x, int y) {
        return false;
    }

    private record Child(Element e, int x, int y, boolean absolute) {
    }

    private class Children extends ArrayList<Child> {

        private Child get(Element e) {
            return stream().filter(c -> c.e == e).findAny().orElse(null);
        }

        private void add(Element e, int x, int y, boolean absolute) {
            Child c = get(e);
            if(c != null) {
                if (c.x == x && c.y == y) {
                    return;
                }
                remove(c);
            }
            add(new Child(e, x, y, absolute));
        }

        private void remove(Element e) {
            Child c = get(e);
            if(c != null) {
                remove(c);
            }
        }

        private void reposition() {
            stream().filter(c -> !c.absolute).forEach(c -> c.e.getStyle()
                    .set("margin-left", (c.x + offsetX) + "px")
                    .set("margin-top", (c.y + offsetY) + "px"));
        }

        private boolean isAbsolute(Element e) {
            Child c = get(e);
            return c == null || c.absolute;
        }
    }

    private class SavePos extends DataForm {

        private final Object item;
        private final Component component;
        private int x, y;
        private final int originalX, originalY;

        public SavePos(Object item, Component component, int x, int y) {
            super("Position");
            this.item = item;
            this.component = component;
            this.x = x;
            this.y = y;
            Child c = children.get(component.getElement());
            originalX = c == null ? 0 : c.x;
            originalY = c == null ? 0 : c.y;
            add(new ButtonLayout(
                    new ImageButton("Up", VaadinIcon.ARROW_CIRCLE_UP, e -> up()),
                    new ImageButton("Down", VaadinIcon.ARROW_CIRCLE_DOWN, e -> down()),
                    new ImageButton("Left", VaadinIcon.ARROW_CIRCLE_LEFT, e -> left()),
                    new ImageButton("Right", VaadinIcon.ARROW_CIRCLE_RIGHT, e -> right())
            ));
            pos();
            addConstructedListener(e -> {
                Window w = (Window)getComponent();
                w.setDraggable(true);
            });
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setText("Save");
        }

        @Override
        protected void cancel() {
            super.cancel();
            reposition(component, originalX, originalY);
        }

        private void up() {
            --y;
            pos();
        }

        private void down() {
            ++y;
            pos();
        }

        private void left() {
            --x;
            pos();
        }

        private void right() {
            ++x;
            pos();
        }

        private void pos() {
            reposition(component, x, y);
        }

        @Override
        protected boolean process() {
            close();
            if(savePosition(item, x, y)) {
                pos();
            } else {
                cancel();
            }
            return true;
        }
    }
}