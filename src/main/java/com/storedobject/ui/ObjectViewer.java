package com.storedobject.ui;

import com.storedobject.core.*;

import java.util.function.Consumer;

/**
 * The ObjectViewer class is a utility for viewing {@link StoredObject} instances within an application.
 * This class extends {@link Executor} to provide execution functionalities and implements
 * {@link ObjectSetter} to set objects for viewing and {@link AlertHandler} to handle alerts associated
 * with {@link StoredObject}.
 *
 * @author Syam
 */
public class ObjectViewer extends Executor implements ObjectSetter<StoredObject>, AlertHandler {

    private StoredObject object;
    private Id id;
    @SuppressWarnings("rawtypes")
    private ObjectEditor viewer;

    /**
     * Constructs an ObjectViewer instance with the specified application.
     *
     * @param a The application instance used to initialize the ObjectViewer.
     */
    public ObjectViewer(Application a) {
        super(a);
    }

    /**
     * Executes the primary action associated with the ObjectViewer by invoking the view method.
     * This method triggers the default view behavior for the object or content managed by the viewer.
     */
    @Override
    public void execute() {
        view();
    }

    /**
     * Sets the specified {@link StoredObject} instance for this ObjectViewer.
     *
     * @param object The {@link StoredObject} instance to be set.
     */
    @Override
    public void setObject(StoredObject object) {
        this.object = object;
    }

    /**
     * Sets the object for the viewer using the given object ID.
     *
     * @param objectId The ID of the object to set.
     */
    @Override
    public void setObject(Id objectId) {
        this.id = objectId;
        this.object = null;
    }

    private ObjectEditor<?> viewer(String caption) {
        if(object == null) {
            if(Id.isNull(id)) {
                return null;
            }
            object = StoredObject.get(id);
        }
        if(object == null) {
            return null;
        }
        if(object instanceof FileFolder ff) {
            new FileViewer(ff, caption).execute();
            return null;
        }
        if(viewer != null) {
            if(viewer.getObjectClass() != object.getClass()) {
                viewer = null;
            }
        }
        if(caption == null || caption.isEmpty() || "_".equals(caption)) {
            caption = StringUtility.makeLabel(object.getClass());
        }
        if(viewer == null) {
            viewer = ObjectEditor.create(object.getClass(), EditorAction.VIEW, caption);
        } else {
            viewer.setCaption(caption);
        }
        return viewer;
    }

    /**
     * Initiates the process of viewing an object with a specified action.
     *
     * @param caption the title or heading for the view
     * @param actionName the name of the action to perform in the view
     * @param action the operation to be executed, provided as a Consumer of StoredObject
     */
    @SuppressWarnings("unchecked")
    public void view(String caption, String actionName, Consumer<StoredObject> action) {
        if(viewer(caption) != null) {
            viewer.viewObject(object, actionName, action);
        }
    }

    /**
     * Opens a viewer for the specified object with the provided caption and optional buttons.
     * If a viewer instance corresponding to the caption is found, it invokes the view mechanism
     * on the associated object, adding the specified buttons to the interface.
     *
     * @param caption The caption to display for the object viewer. It is used to lookup or initialize the viewer instance.
     * @param buttons Optional buttons of type {@link ObjectViewerButton} to enhance the viewer's functionality.
     */
    @SuppressWarnings("unchecked")
    public void view(String caption, ObjectViewerButton<?>... buttons) {
        if(viewer(caption) != null) {
            viewer.viewObject(object, buttons);
        }
    }

    /**
     * Triggers the default view behavior for the object or content managed by the viewer
     * without any specific parameters. Delegates to the overloaded {@code view(String, String, Consumer<StoredObject>)}
     * method with all parameters set to {@code null}.
     */
    public void view() {
        view((String) null, (String) null, null);
    }

    /**
     * Triggers the view functionality using the specified buttons for the ObjectViewer.
     * This method invokes a secondary view method with a {@code null} caption and the provided buttons.
     *
     * @param buttons An array of {@link ObjectViewerButton} instances that define actions or behaviors
     *                during the view operation.
     */
    public void view(ObjectViewerButton<?>... buttons) {
        view((String) null, buttons);
    }

    /**
     * Initiates a view operation using a specified action name and action logic.
     *
     * @param actionName The name of the action to be performed.
     * @param action A {@link Consumer} representing the logic to be executed on the {@link StoredObject}.
     */
    public void view(String actionName, Consumer<StoredObject> action) {
        view((String) null, actionName, action);
    }

    /**
     * Initiates the view process with the specified caption. This method is a convenience
     * method that internally calls the overloaded {@code view} method with the given
     * caption, a {@code null} action name, and no action.
     *
     * @param caption The caption to be displayed for the view.
     */
    public void view(String caption) {
        view(caption, (String) null, null);
    }

    /**
     * Displays the specified stored object.
     *
     * @param object the StoredObject instance to be displayed. If null is passed, the method may handle it as per its implementation.
     */
    public void view(StoredObject object) {
        view(null, object);
    }

    /**
     * Displays the specified {@link StoredObject} with an associated action.
     *
     * @param object The {@link StoredObject} instance to be displayed.
     * @param actionName The name of the action associated with the displayed object.
     * @param action A {@link Consumer} that defines the action to be performed on the {@link StoredObject}.
     */
    public void view(StoredObject object, String actionName, Consumer<StoredObject> action) {
        view(null, object, actionName, action);
    }

    /**
     * Triggers the viewing process for the given {@link StoredObject} instance and optional viewer buttons.
     * The additional buttons can allow for extended actions or interactions during the viewing process.
     *
     * @param object The {@link StoredObject} instance to be viewed.
     * @param buttons Optional {@link ObjectViewerButton} instances to include additional actions or controls.
     */
    public void view(StoredObject object, ObjectViewerButton<?>... buttons) {
        view(null, object, buttons);
    }

    /**
     * Displays a view using the specified caption and stored object.
     *
     * @param caption the title or label of the view
     * @param object the stored object to be displayed in the view
     */
    public void view(String caption, StoredObject object) {
        view(caption, object, null, null);
    }

    /**
     * Displays a view with the specified caption and an action on the given stored object.
     *
     * @param caption the title or label for the view
     * @param object the stored object to be displayed in the view
     * @param actionName the name of the action to be performed
     * @param action a consumer defining the action to be executed on the stored object
     */
    public void view(String caption, StoredObject object, String actionName, Consumer<StoredObject> action) {
        setObject(object);
        view(caption, actionName, action);
    }

    /**
     * Displays the specified {@link StoredObject} in the ObjectViewer with a given caption and optional action buttons.
     *
     * @param caption The caption to display in the viewer.
     * @param object The {@link StoredObject} instance to be viewed.
     * @param buttons Optional buttons of type {@link ObjectViewerButton} to add custom actions in the viewer.
     */
    public void view(String caption, StoredObject object, ObjectViewerButton<?>... buttons) {
        setObject(object);
        view(caption, buttons);
    }

    /**
     * Displays a view based on the given caption and object ID.
     *
     * @param caption The caption or title to display in the view.
     * @param objectId The ID of the object to be viewed.
     */
    public void view(String caption, Id objectId) {
        view(caption, objectId, null, null);
    }

    /**
     * Displays a view with the specified parameters.
     *
     * @param caption The caption to be displayed in the view.
     * @param objectId The ID of the object to set for the view.
     * @param actionName The name of the action to be associated with the view.
     * @param action A consumer that defines the action to be performed on the {@link StoredObject}.
     */
    public void view(String caption, Id objectId, String actionName, Consumer<StoredObject> action) {
        setObject(objectId);
        view(caption, actionName, action);
    }

    /**
     * Displays a view of the specified object, identified by its ID, with the provided caption
     * and optional buttons for additional actions.
     *
     * @param caption The caption to display in the view.
     * @param objectId The ID of the object to be viewed.
     * @param buttons Optional buttons to provide additional actions to perform in the view.
     */
    public void view(String caption, Id objectId, ObjectViewerButton<?>... buttons) {
        setObject(objectId);
        view(caption, buttons);
    }

    /**
     * Displays the view associated with the specified object ID.
     * This method utilizes the default caption for the view.
     *
     * @param objectId The ID of the object to be viewed.
     */
    public void view(Id objectId) {
        view(null, objectId);
    }

    /**
     * Executes an action on a stored object identified by the provided objectId.
     * Delegates the actual processing by invoking an overloaded version of the view method.
     *
     * @param objectId the identifier of the stored object to be acted upon
     * @param actionName the name of the action to be performed on the stored object
     * @param action the consumer that defines the specific action to apply to the stored object
     */
    public void view(Id objectId, String actionName, Consumer<StoredObject> action) {
        view(null, objectId, actionName, action);
    }

    /**
     * Opens a view for a specific object identified by its ID and allows optional buttons to be added for interaction.
     *
     * @param objectId the ID of the object to be viewed
     * @param buttons a varargs parameter allowing optional interactive buttons to be included in the view
     */
    public void view(Id objectId, ObjectViewerButton<?>... buttons) {
        view(null, objectId, buttons);
    }

    /**
     * Checks if the viewer is currently executing an action.
     *
     * @return {@code true} if a viewer exists and it is executing an action, {@code false} otherwise.
     */
    public boolean executing() {
        return viewer != null && viewer.executing();
    }

    /**
     * Closes the ObjectViewer by releasing associated resources and setting relevant fields to null.
     * If the underlying viewer is not null, it will invoke the `close()` method of the viewer before
     * resetting the viewer and related object fields to null.
     */
    public void close() {
        if(viewer != null) {
            viewer.close();
            viewer = null;
            object = null;
        }
    }

    /**
     * Handles the alert action for the given {@link StoredObject}.
     *
     * @param object The {@link StoredObject} instance for which the alert is handled.
     */
    @Override
    public void handleAlert(StoredObject object) {
        view(object);
    }

    /**
     * Retrieves the icon associated with alert notifications for the ObjectViewer.
     *
     * @return A string representing the identifier of the alert icon.
     */
    @Override
    public String getAlertIcon() {
        return "vaadin:eye";
    }

    @Override
    public String getAlertCaption() {
        return "View";
    }
}