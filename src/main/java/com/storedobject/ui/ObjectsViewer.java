package com.storedobject.ui;

import com.storedobject.core.EditorAction;
import com.storedobject.core.HasReference;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;

/**
 * ObjectsViewer is a specialized UI component that serves as a container for displaying
 * various stored objects within a tabbed interface. It extends the {@link View} class and
 * implements the {@link CloseableView} interface.
 * ObjectsViewer allows dynamically adding objects to be displayed in separate tabs,
 * with options to customize the tab captions.
 *
 * @author Syam
 */
public class ObjectsViewer extends View implements CloseableView {

    private final Tabs tabs = new Tabs();

    /**
     * Constructs an {@code ObjectsViewer} instance with a specified caption and an optional
     * array of additional UI components to be displayed in the header.
     *
     * @param caption The caption text to be displayed as the title of this view.
     * @param components Optional additional {@code Component}s to be included in the header;
     *                   if no components are provided, a default label with the caption
     *                   will be displayed in the header.
     */
    public ObjectsViewer(String caption, Component... components) {
        setCaption(caption);
        ButtonLayout header = new ButtonLayout();
        if(components == null || components.length == 0) {
            header.add(new ELabel(caption, "font-weight:bold;color:blue"));
        } else {
            for(Component c: components) {
                if(c != null) header.add(c);
            }
        }
        header.add(new Button("Exit", e -> close()));
        setComponent(new ContentWithHeader(header, tabs));
    }

    /**
     * Adds a stored object to the system with the option to specify additional parameters.
     *
     * @param object The stored object to be added. Must be a subclass of StoredObject.
     * @param <T>    The type of the object to be added, restricted to subclasses of StoredObject.
     */
    public <T extends StoredObject> void add(T object) {
        add(object, null);
    }

    /**
     * Adds an object to the editor with the specified caption or generates one if not provided.
     *
     * @param <T>     The type of the object to be added, restricted to subclasses of StoredObject.
     * @param object  The object to be added. If this is null, the method will exit without performing any action.
     * @param caption The caption to be used for the object tab. If this is null, empty, or "_", a caption will be
     *                generated based on the object's reference (if it implements HasReference) or the class name.
     */
    public <T extends StoredObject> void add(T object, String caption) {
        if(object == null) {
            return;
        }
        if(caption == null || caption.isEmpty() || "_".equals(caption)) {
            caption = object instanceof HasReference hr ? hr.getReference() : StringUtility.makeLabel(object.getClass());
        }
        @SuppressWarnings("unchecked") ObjectEditor<T> oe = (ObjectEditor<T>) ObjectEditor.create(object.getClass(), EditorAction.VIEW, caption);
        oe.setObject(object);
        tabs.createTab(caption, oe.getContent());
    }
}
