package com.storedobject.ui;

import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.StringUtility;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A template-based component for displaying and managing an object and its associated objects in a UI.
 *
 * @param <T> The type of data this template can manage.
 *
 * @author Syam
 */
public class ObjectTemplate<T> extends TemplateComponent {

    /**
     * The type of data this template is managing.
     */
    protected final Class<T> objectClass;
    /**
     * Current value of the object.
     */
    protected T object;
    private final List<Consumer<T>> painters = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param templateCode Template containing HTML and CSS (within style tag).
     */
    public ObjectTemplate(Class<T> objectClass, String templateCode) {
        super(templateCode);
        this.objectClass = objectClass;
        build();
    }

    /**
     * Sets the object and paint the component according to the object's attributes.
     *
     * @param object the object to be stored may be null.
     */
    public final void setObject(T object) {
        this.object = object;
        painters.forEach(p -> p.accept(object));
        paint(object);
    }

    /**
     * This method is called whenever the object is set. Typically, this
     * method is overridden by subclasses to provide custom painting logic.
     *
     * @param object Object to be painted.
     */
    public void paint(T object) {
    }

    /**
     * Retrieves the stored object of type T.
     *
     * @return the object of type T contained in this instance, or null if no object is set.
     */
    public final T getObject() {
        return object;
    }

    /**
     * Retrieves the class type of the object managed by this template.
     *
     * @return the {@code Class} instance representing the type parameter {@code T}.
     */
    public final Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    protected Component createComponentForId(String id, String tag) {
        try {
            StoredObjectUtility.MethodList m = StoredObjectUtility.createMethodList(objectClass, id);
            m.stringifyTail();
            HasText painter = getPainter(tag);
            if(painter != null) {
                painters.add(o -> painter.setText(StringUtility.toString(m.invoke(o))));
                return (Component) painter;
            }
        } catch (Throwable ignored) {
        }
        return super.createComponentForId(id, tag);
    }

    private HasText getPainter(String tag) {
        return switch(tag.toLowerCase()) {
            case "div" -> new Div();
            case "span" -> new Span();
            case "p" -> new Paragraph();
            case "h1" -> new H1();
            case "h2" -> new H2();
            case "h3" -> new H3();
            case "h4" -> new H4();
            case "h5" -> new H5();
            default -> null;
        };
    }
}
