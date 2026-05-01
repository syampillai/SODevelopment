package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.LogicParser;

/**
 * UI of a dashboard that uses a structured template-based UI. The data object's "get" methods that return
 * values that are used to populate the dashboard layout are defined in the template as "id" attributes of the HTML tags.
 *
 * @param <T> The type of {@code StoredObject} that this dashboard manages.
 *
 * @author Syam
 */
public class ObjectDashboard<T extends StoredObject> extends Dashboard<T> implements ObjectSetter<T> {

    /**
     * Constructor to create an ObjectDashboard instance based on the given object class type and template code.
     *
     * @param objectClass The class type of the objects that will be represented and managed.
     * @param template The template to use for the dashboard layout.
     */
    public ObjectDashboard(Class<T> objectClass, TextContent template) {
        super(null, objectClass, template);
        setCaption(Application.getLogicCaption(() -> StringUtility.makeLabel(objectClass)));
    }

    /**
     * Constructor.
     *
     * @param objectClass The class type of the objects that will be represented and managed.
     */
    public ObjectDashboard(Class<T> objectClass) {
        this(objectClass, textContent(objectClass, "Dashboard"));
    }

    /**
     * Constructor to create an ObjectDashboard instance based on the fully qualified name of a class.
     *
     * @param className The fully qualified name of the class whose objects will be represented and managed.
     * @throws Exception If the specified class name cannot be loaded or cast to the required type.
     */
    public ObjectDashboard(String className) throws Exception {
        super(className);
    }

    static TextContent textContent(Class<?> objectClass, String type) {
        String name = objectClass.getName();
        int p = name.lastIndexOf('.');
        if(p > 0) {
            name = name.substring(0, p) + ".logic" + name.substring(p) + type;
        }
        TextContent tc = TextContent.get(name);
        if(tc == null || !tc.getName().equals(name)) {
            throw new SORuntimeException("No dashboard template found for: " + name);
        }
        return tc;
    }

    static String cardTemplate(Class<?> objectClass) {
        return textContent(objectClass, "CardTemplate").getContent();
    }

    /**
     * Creates an instance of {@code ObjectDashboard} for the specified object class. This method
     * attempts to dynamically load or create a dashboard class based on the naming convention
     * and associated logic. If the process fails at any step, an error message will be embedded
     * in the resulting dashboard instance.
     *
     * @param objectClass The class of objects for which the dashboard is being created.
     * @param <O>         The type of the objects that extend {@code StoredObject}.
     * @return            An instance of {@code ObjectDashboard} for the specified object class.
     */
    public static <O extends StoredObject> ObjectDashboard<O> create(Class<O> objectClass) {
        Class<?> tc = LogicParser.createLogicClass(objectClass, "Dashboard");
        if (tc != null && !ObjectDashboard.class.isAssignableFrom(tc)) {
            return new ObjectDashboard<>(objectClass, textContent("Invalid dashboard class: " + tc.getName()));
        }
        if(tc != null && tc != ObjectDashboard.class) {
            try {
                //noinspection unchecked
                return (ObjectDashboard<O>) tc.getConstructor().newInstance();
            } catch (Exception e) {
                Application.get().log(e);
                return new ObjectDashboard<>(objectClass, textContent("Error creating dashboard: " + tc.getName()));
            }
        }
        try {
            return new ObjectDashboard<>(objectClass, textContent(objectClass, "Dashboard"));
        } catch (Exception e) {
            return new ObjectDashboard<>(objectClass, textContent(e.getMessage()));
        }
    }

    private static TextContent textContent(String content) {
        TextContent tc = new TextContent();
        tc.setContent("<span>" + content + "</span>");
        return tc;
    }
}
