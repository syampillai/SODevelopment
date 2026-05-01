package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.WrappedView;
import com.vaadin.flow.component.Component;

/**
 * UI of a dashboard that uses a structured template-based UI. The data object can have "get" methods that return
 * values that can be used to populate the dashboard layout defined in the template as "id" attributes of the HTML tags.
 *
 * @param <T> The type of object data that this dashboard manages.
 *           
 * @author Syam
 */
public class Dashboard<T> extends ObjectTemplate<T> implements ExecutableView, CloseableView {

    private String caption;
    View view;

    /**
     * Constructor to create a Dashboard instance based on the given object.
     *
     * @param object The data that will be represented and managed.
     */
    public Dashboard(T object) {
        this(null, object);
    }

    /**
     * Constructor to create a Dashboard instance based on the given object and template name.
     *
     * @param caption Caption to set.
     * @param object The data that will be represented and managed.
     */
    public Dashboard(String caption, T object) {
        this(caption, object, (String) null);
    }

    /**
     * Constructor to create a Dashboard instance based on the given object and template name.
     *
     * @param object The data that will be represented and managed.
     * @param templateName The template name to use for the dashboard layout.
     */
    public Dashboard(T object, String templateName) {
        this(null, object, templateName);
    }

    /**
     * Constructor to create a Dashboard instance based on the given object and template name.
     *
     * @param caption Caption to set.
     * @param object The data that will be represented and managed.
     * @param templateName The template name to use for the dashboard layout.
     */
    public Dashboard(String caption, T object, String templateName) {
        this(caption, object, tc(object.getClass(), templateName));
    }

    /**
     * Constructor to create a Dashboard instance based on the given object and template.
     *
     * @param object The data that will be represented and managed.
     * @param template The template to use for the dashboard layout.
     */
    public Dashboard(T object, TextContent template) {
        this(null, object, template);
    }

    /**
     * Constructor to create a Dashboard instance based on the given object and template.
     *
     * @param caption Caption to set.
     * @param object The data that will be represented and managed.
     * @param template The template to use for the dashboard layout.
     */
    public Dashboard(String caption, T object, TextContent template) {
        //noinspection unchecked
        this(caption, (Class<T>) object.getClass(), template);
        setObject(object);
    }

    /**
     * Constructor to create a Dashboard instance based on the given object type and template.
     *
     * @param caption Caption to set.
     * @param objectClass The class type of the objects that will be represented and managed.
     * @param template The template to use for the dashboard layout.
     */
    protected Dashboard(String caption, Class<T> objectClass, TextContent template) {
        super(objectClass, template.getContent());
        setCaption(caption);
    }

    /**
     * Constructor to create an ObjectDashboard instance based on the fully qualified name of a class.
     *
     * @param className The fully qualified name of the class whose objects will be represented and managed.
     * @throws Exception If the specified class name cannot be loaded or cast to the required type.
     */
    public Dashboard(String className) throws Exception {
        //noinspection unchecked
        this(caption(className), (T) JavaClassLoader.getLogic(className(className)).getConstructor().newInstance(), templateName(className));
    }

    private static String className(String className) {
        int p = className.indexOf('|');
        if(p < 0) return className;
        String name = className.substring(0, p);
        if(name.contains(".") && !name.contains(". ")) {
            return name;
        }
        return className(className.substring(p + 1));
    }

    private static String caption(String className) {
        int p = className.indexOf('|');
        if(p < 0) return className;
        String name = className.substring(0, p);
        if(name.contains(".") && !name.contains(". ")) {
            return null;
        }
        return className.substring(0, p);
    }

    private static String templateName(String className) {
        int p = className.lastIndexOf('|');
        if(p < 0) return null;
        return className.substring(p + 1);
    }

    private static TextContent tc(Class<?> objectClass, String templateName) {
        if((templateName == null || templateName.isBlank()) && StoredObject.class.isAssignableFrom(objectClass)) {
            return ObjectDashboard.textContent(objectClass, "Dashboard");
        }
        String name = templateName == null || templateName.isBlank() ? objectClass.getName() : templateName;
        TextContent tc = TextContent.get(name);
        if(tc == null || !tc.getName().equals(name)) {
            throw new SORuntimeException("No dashboard template found for: " + name);
        }
        return tc;
    }

    @Override
    public View getView(boolean create) {
        if(view == null && create) {
            view = new WrappedView(this, caption) {
                @Override
                public void decorateComponent() {
                    super.decorateComponent();
                    Dashboard.this.decorateComponent();
                }
            };
            build();
            viewConstructed(view);
        }
        return view;
    }

    @Override
    public void setCaption(String caption) {
        if(caption == null) {
            caption = "Dashboard";
        }
        if(view == null) {
            this.caption = caption;
        } else {
            view.setCaption(caption);
        }
    }

    /**
     * This will be invoked when the {@link View} is constructed for the first time.
     *
     * @param view View that is constructed now.
     */
    public void viewConstructed(View view) {
    }

    /**
     * Decorate the outermost component if required. This will be invoked after applying the
     * {@link View#decorateComponent()}.
     */
    public void decorateComponent() {
    }

    /**
     * Get the component that represents this template view. (This will be the outermost component).
     * This is an equivalent to {@link View#getComponent()}.
     *
     * @return The outermost component.
     */
    public Component getComponent() {
        return getView(true).getComponent();
    }
}
