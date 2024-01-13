package com.storedobject.ui;

import com.storedobject.core.MediaFile;
import com.storedobject.vaadin.Image;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.AbstractStreamResource;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link View} that shows an image with other overlay-components including other images.
 *
 * @author Syam
 */
public class ImageViewer extends View {

    private final Div parent = new Div();
    private final Image parentImage;
    private final List<Element> children = new ArrayList<>();

    /**
     * Create an empty image view. Image source can be set later.
     */
    public ImageViewer(String caption) {
        this(caption, (Image)null);
    }

    /**
     * Create the image view from a URL.
     *
     * @param url Image URL.
     */
    public ImageViewer(String caption, String url) {
        this(caption, (Image)null);
        setSource(url);
    }

    /**
     * Create the image view from a resource.
     *
     * @param resource Image resource
     */
    public ImageViewer(String caption, AbstractStreamResource resource) {
        this(caption, (Image)null);
        setSource(resource);
    }

    /**
     * Create the image view from the given image.
     * @param parentImage Image.
     */
    public ImageViewer(String caption, Image parentImage) {
        super(caption);
        parent.setSizeFull();
        parent.getStyle().set("overflow", "hidden");
        this.parentImage = parentImage == null ? new Image((String)null) : parentImage;
        this.parentImage.setSizeFull();
        this.parentImage.getStyle().set("object-fit", "fill");
        parent.add(this.parentImage);
    }

    public ImageViewer(MediaFile mediaFile) {
        this(mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : "");
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
     * Set the URL resource of the image content.
     *
     * @param source URL resource.
     */
    public void setSource(String source) {
        parentImage.setSource(source);
    }

    /**
     * Set the stream resource of the image content.
     *
     * @param source Stream resource of the image content.
     */
    public void setSource(AbstractStreamResource source) {
        parentImage.setSource(source);
    }

    public void setSource(MediaFile mediaFile) {
        setSource(mediaFile != null && mediaFile.isImage() ? ("media/" + mediaFile.getFileName()) : null);
    }

    public void add(Component component, int x, int y) {
        add(component.getElement(), x, y);
    }

    public void remove(Component component) {
        remove(component.getElement());
    }

    public void add(Element element, int x, int y) {
        if(!children.contains(element)) {
            children.add(element);
        }
        element.getStyle().set("margin-left", x + "px").set("margin-top", y + "px").set("position", "absolute");
        parent.getElement().appendChild(element);
    }

    public void remove(Element element) {
        children.remove(element);
        parent.getElement().removeChild(element);
    }

    public void removeAll() {
        while(!children.isEmpty()) {
            remove(children.get(0));
        }
    }

    public Image getParentImage() {
        return parentImage;
    }
}