package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.core.TextContent;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ExecutableView;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.WrappedView;
import com.vaadin.flow.component.Component;

public class ObjectDashboard<T extends StoredObject> extends ObjectTemplate<T>
        implements ExecutableView, CloseableView {

    private String caption;
    View view;

    public ObjectDashboard(T object) {
        //noinspection unchecked
        this((Class<T>) object.getClass());
        setObject(object);
    }

    public ObjectDashboard(Class<T> objectClass, String templateCode) {
        super(objectClass, templateCode);
        setCaption(Application.getLogicCaption(() -> StringUtility.makeLabel(getClass())));
    }

    /**
     * Constructor.
     *
     * @param objectClass The class type of the objects that will be represented and managed.
     */
    public ObjectDashboard(Class<T> objectClass) {
        this(objectClass, tc(objectClass, "Dashboard"));
    }

    static String tc(Class<?> objectClass, String type) {
        String name = objectClass.getName();
        int p = name.lastIndexOf('.');
        if(p > 0) {
            name = name.substring(0, p) + ".logic" + name.substring(p) + type;
        }
        TextContent tc = TextContent.get(name);
        if(tc == null || !tc.getName().equals(name)) {
            throw new SORuntimeException("No dashboard template found for: " + name);
        }
        return tc.getContent();
    }

    public static <O extends StoredObject> ObjectDashboard<O> create(Class<O> objectClass) {
        Class<?> tc = LogicParser.createLogicClass(objectClass, "Dashboard");
        if (tc != null && !ObjectDashboard.class.isAssignableFrom(tc)) {
            return new ObjectDashboard<>(objectClass, "<span>Invalid dashboard class: " + tc.getName() + "</span>");
        }
        if(tc != null && tc != ObjectDashboard.class) {
            try {
                //noinspection unchecked
                return (ObjectDashboard<O>) tc.getConstructor().newInstance();
            } catch (Exception e) {
                Application.get().log(e);
                return new ObjectDashboard<>(objectClass, "<span>Error creating dashboard: " + tc.getName() + "</span>");
            }
        }
        try {
            return new ObjectDashboard<>(objectClass, tc(objectClass, "Dashboard"));
        } catch (Exception e) {
            return new ObjectDashboard<>(objectClass, "<span>" + e.getMessage() + "</span>");
        }
    }

    @Override
    public View getView(boolean create) {
        if(view == null && create) {
            view = new WrappedView(this, caption) {
                @Override
                public void decorateComponent() {
                    super.decorateComponent();
                    ObjectDashboard.this.decorateComponent();
                }
            };
            build();
            viewConstructed(view);
        }
        return view;
    }

    @Override
    public void setCaption(String caption) {
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
