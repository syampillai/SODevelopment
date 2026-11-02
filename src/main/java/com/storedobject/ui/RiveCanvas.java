package com.storedobject.ui;

import com.storedobject.core.FileData;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StreamData;
import com.storedobject.vaadin.AbstractResourcedComponent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.JsonObject;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The RiveCanvas class represents a custom composite component for
 * embedding and interacting with Rive animations within a Vaadin web application.
 * By utilizing various configuration and playback options, this class provides
 * a seamless way to manage Rive's state machines, properties, and animations in the browser.
 * This component is built to integrate with Rive's JavaScript Canvas implementation.
 * <pre>
 * Features:
 * - Interactive animations with support for state machines, properties,
 *   and customizable alignment and fitting modes.
 * - ViewModel property manipulation, observation, and viewing capabilities.
 * - Offers event listeners for handling ready and loading states.
 * </pre>
 * <p>
 * The component relies on external JavaScript-based Rive integrations
 * to manage animations and properties dynamically.
 * </p>
 * Enums:
 * - {@link Fit}: Specifies how the canvas content is scaled and cropped.
 * - {@link Alignment}: Specifies the alignment of the content on the canvas.
 *
 * @author Syam
 */
@Tag("so-rive")
@NpmPackage(value = "@rive-app/canvas", version = "2.47.1")
@JsModule("./so/rive/so-rive.js")
public class RiveCanvas extends AbstractResourcedComponent implements HasSize {

    /**
     * The Fit enum defines various fitting strategies for rendering and aligning visual content
     * within a container. These strategies determine how the visual content scales and adjusts
     * to fit the container dimensions.
     *
     * <pre>
     * Enum constants:
     * - CONTAIN: Scales the content to fit entirely within the container while maintaining its aspect ratio.
     * - COVER: Scales the content to cover the entire container while maintaining its aspect ratio.
     * - FILL: Scales the content to fill the container without preserving the aspect ratio.
     * - WIDTH: Scales the content to fit the full width of the container while maintaining the aspect ratio.
     * - HEIGHT: Scales the content to fit the full height of the container while maintaining the aspect ratio.
     * - NONE: Disables scaling, leaving the content at its original size.
     * - SCALE_DOWN: Scales down the content only if it is larger than the container, preserving its aspect ratio.
     * - LAYOUT: A placeholder for specific layout-related fitting, exact behavior depends on the implementation.
     * </pre>
     *
     * @author Syam
     */
    public enum Fit {
        CONTAIN("contain"),
        COVER("cover"),
        FILL("fill"),
        WIDTH("fitWidth"),
        HEIGHT("fitHeight"),
        NONE("none"),
        SCALE_DOWN("scaleDown"),
        LAYOUT("layout");

        private final String value;

        /**
         * Constructs a new {@code Fit} instance with the specified fitting strategy value.
         *
         * @param value the string representation of the fitting strategy
         *              (e.g., "contain", "cover", "fill", etc.)
         */
        Fit(String value) { this.value = value; }

        /**
         * Retrieves the string representation of the fitting strategy associated with this enum constant.
         *
         * @return the string value of the fitting strategy (e.g., "contain", "cover", "fill").
         */
        public String getValue() { return value; }
    }

    /**
     * The Alignment enum defines various alignment options for positioning content
     * within a container. These alignments specify the relative placement of
     * the content such as centered, aligned to specific corners, or sides.
     *
     * @author Syam
     */
    public enum Alignment {
        CENTER("center"),
        TOP_LEFT("topLeft"),
        TOP_CENTER("topCenter"),
        TOP_RIGHT("topRight"),
        CENTER_LEFT("centerLeft"),
        CENTER_RIGHT("centerRight"),
        BOTTOM_LEFT("bottomLeft"),
        BOTTOM_CENTER("bottomCenter"),
        BOTTOM_RIGHT("bottomRight");

        private final String value;

        /**
         * Constructs an instance of the Alignment enum with the specified string value.
         * The string value describes the alignment option for positioning content.
         *
         * @param value the string representation of the alignment option
         */
        Alignment(String value) { this.value = value; }

        /**
         * Retrieves the current value stored in this object.
         *
         * @return the value as a String
         */
        public String getValue() { return value; }
    }

    private final Set<String> watchedProperties = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a new instance of the RiveCanvas class using the specified file name.
     * This constructor initializes the RiveCanvas object by loading the animation data
     * from the provided file.
     *
     * @param fileName the name of the file containing the Rive animation data
     */
    public RiveCanvas(String fileName) {
        this(fileName, null);
    }

    /**
     * Constructs a new instance of RiveCanvas by loading a Rive animation file from the given file name
     * and initializes it with the specified state machine name.
     *
     * @param fileName the file name of the Rive animation to load; must not be null or empty.
     * @param stateMachineName the name of the state machine to initialize; can be null or empty
     *                         if no specific state machine is required.
     */
    public RiveCanvas(String fileName, String stateMachineName) {
        this(FileData.get(fileName).getFile(), stateMachineName);
    }

    /**
     * Constructs a new instance of RiveCanvas using the provided stream data as the source.
     * By default, the canvas is set to autoplay and uses a "contain" fit with center alignment.
     * If a state machine is associated with the canvas, you can instantiate it by using the relevant constructors.
     *
     * @param source the StreamData object representing the source of the canvas.
     */
    public RiveCanvas(StreamData source) {
        this(source, null);
    }

    /**
     * Constructs a new RiveCanvas object with the specified data source and state machine name.
     *
     * @param source          the StreamData object representing the data source for the canvas
     * @param stateMachineName the name of the state machine to be used, or null if no state machine is specified
     */
    public RiveCanvas(StreamData source, String stateMachineName) {
        setAutoplay(true);
        setFit(Fit.CONTAIN);
        setAlignment(Alignment.CENTER);
        setSource(new DBResource(source));
        if (stateMachineName != null && !stateMachineName.isBlank()) {
            setStateMachine(stateMachineName);
        }
    }

    /**
     * Constructs a new instance of the RiveCanvas class using the given source ID.
     * This constructor initializes the RiveCanvas with the specified identifier and no state machine.
     *
     * @param sourceId the identifier of the source used to initialize the RiveCanvas
     */
    public RiveCanvas(Id sourceId) {
        this(sourceId, null);
    }

    /**
     * Constructs a new RiveCanvas instance with the specified source ID and state machine name.
     *
     * @param sourceId          the ID of the source object used to create the RiveCanvas.
     * @param stateMachineName  the name of the state machine to be associated with this canvas.
     */
    public RiveCanvas(Id sourceId, String stateMachineName) {
        this(StoredObject.get(StreamData.class, sourceId), stateMachineName);
    }

    // ---------------------------------------------------------
    // Configuration
    // ---------------------------------------------------------

    /**
     * Sets the artboard attribute to the specified name.
     *
     * @param name the name of the artboard to be set
     */
    public void setArtboard(String name) {
        setOptionalAttribute("artboard", name);
    }

    /**
     * Sets the state machine name to be used by the RiveCanvas.
     * This method associates a state machine with the canvas by configuring
     * the appropriate attribute.
     *
     * @param name the name of the state machine to set; if null or blank, the state machine
     *             attribute will be removed from the canvas configuration.
     */
    public void setStateMachine(String name) {
        setOptionalAttribute("statemachine", name);
    }

    /**
     * Sets the fitting strategy for rendering the visual content within the container.
     * The selected {@link Fit} value determines how the content scales and adjusts
     * to fit the container dimensions.
     *
     * @param fit the {@link Fit} strategy to apply; must not be null. Utilizes the
     *            string value of the provided {@code Fit} instance to specify the fitting behavior.
     */
    public void setFit(Fit fit) {
        getElement().setAttribute(
                "fit",
                Objects.requireNonNull(fit).getValue());
    }

    /**
     * Sets the alignment configuration for the canvas. The alignment determines
     * how the content is positioned within the canvas.
     *
     * @param alignment the desired alignment value; must not be null.
     *                  Available options are defined in the {@code Alignment} enum.
     */
    public void setAlignment(Alignment alignment) {
        getElement().setAttribute(
                "alignment",
                Objects.requireNonNull(alignment).getValue());
    }

    /**
     * Enables or disables the autoplay functionality of the RiveCanvas instance.
     * When autoplay is enabled, the animation will start automatically.
     *
     * @param autoplay a boolean indicating whether autoplay should be enabled.
     *                 If true, the "autoplay" attribute is added to the element;
     *                 if false, the attribute is removed.
     */
    public void setAutoplay(boolean autoplay) {
        if (autoplay) {
            getElement().setAttribute("autoplay", true);
        } else {
            getElement().removeAttribute("autoplay");
        }
    }

    /**
     * Sets the name of the view model to be associated with the canvas.
     *
     * @param name the name of the view model to set; if null or blank,
     *             the associated "viewmodel" attribute will be removed.
     */
    public void setViewModel(String name) {
        setOptionalAttribute("viewmodel", name);
    }

    /**
     * Sets the view model instance for this canvas by associating it with the specified name.
     * This method is used to define or update the "viewmodelinstance" attribute of the canvas
     * to reference a specific ViewModel instance.
     *
     * @param name the name of the ViewModel instance to associate with this canvas;
     *             if null or empty, the "viewmodelinstance" attribute will be removed.
     */
    public void setViewModelInstance(String name) {
        setOptionalAttribute("viewmodelinstance", name);
    }

    private void setOptionalAttribute(String name, String value) {
        name = name.toLowerCase();
        if (value == null || value.isBlank()) {
            getElement().removeAttribute(name);
        } else {
            getElement().setAttribute(name, value);
        }
    }

    // ---------------------------------------------------------
    // Playback
    // ---------------------------------------------------------

    /**
     * Initiates playback of the associated Rive animation on the canvas.
     * This method triggers the "play" JavaScript function on the client-side
     * to start or resume animation behavior.
     */
    public void play() { getElement().callJsFunction("play"); }

    /**
     * Pauses the currently playing animation on the RiveCanvas.
     * This method invokes the "pause" function in the underlying JavaScript implementation
     * to stop the animation playback at its current position.
     */
    public void pause() { getElement().callJsFunction("pause"); }

    /**
     * Stops the execution or operation of a specific process or functionality.
     * This method invokes a JavaScript function named "stop" on the associated element.
     * Use this method when you need to halt an ongoing action or behavior.
     */
    public void stop() { getElement().callJsFunction("stop"); }

    // ---------------------------------------------------------
    // View-model writes (replaces state machine inputs)
    // ---------------------------------------------------------

    /**
     * Sets a string property in the view model associated with the canvas.
     * This method updates or defines a property of type "string" using the provided value.
     *
     * @param path the property path within the view model; must not be null.
     * @param value the string value to set for the property; must not be null.
     */
    public void setStringProperty(String path, String value) {
        setViewModelProperty(path, "string",
                Objects.requireNonNull(value));
    }

    /**
     * Sets a numeric property for the specified path in the view model.
     * This method updates the view model by associating the given numeric value
     * with the specified path as a "number" type.
     *
     * @param path  the path identifying the property to set; must not be null.
     * @param value the numeric value to assign to the property; must not be null.
     */
    public void setNumberProperty(String path, Number value) {
        setViewModelProperty(path, "number",
                Objects.requireNonNull(value).doubleValue());
    }

    /**
     * Sets a boolean property in the underlying view model configuration.
     * This method maps a property identified by the specified path to the given boolean value.
     *
     * @param path the path identifying the property to be set; must not be null.
     * @param value the boolean value to assign to the specified property.
     */
    public void setBooleanProperty(String path, boolean value) {
        setViewModelProperty(path, "boolean", value);
    }

    /**
     * Sets an enum property on the specified path with the provided value.
     * This method utilizes the internal {@code setViewModelProperty} function
     * to update the enum property.
     *
     * @param path the property path to set; must not be null.
     * @param value the string value to set for the enum property; must not be null.
     */
    public void setEnumProperty(String path, String value) {
        setViewModelProperty(path, "enum",
                Objects.requireNonNull(value));
    }

    /**
     * Sets a color property for the view model associated with the specified path.
     * The color is represented as an integer and converted to an unsigned long
     * before applying it to the view model property.
     *
     * @param path the path identifying the view model property; must not be null.
     * @param color the color value to set, expressed as a 32-bit integer.
     */
    public void setColorProperty(String path, int color) {
        setViewModelProperty(path, "color",
                Integer.toUnsignedLong(color));
    }

    /**
     * Sets a color property in the view model associated with the specified path.
     * The color value is masked to ensure it fits within a 32-bit unsigned integer range.
     *
     * @param path the property path in the view model; must be a non-null string representing
     *             the hierarchical location of the property.
     * @param color the color value to set, represented as a long. This value will be masked
     *              to a 32-bit unsigned integer.
     */
    public void setColorProperty(String path, long color) {
        setViewModelProperty(path, "color", color & 0xFFFFFFFFL);
    }

    /**
     * Fires a property trigger for the specified path in the associated view model.
     * This method calls the client-side JavaScript function "fireViewModelTrigger"
     * to initiate a corresponding behavior or action based on the path provided.
     *
     * @param path the property path to trigger; must not be null.
     *             This path identifies the specific property in the view model
     *             whose trigger needs to be activated.
     */
    public void firePropertyTrigger(String path) {
        getElement().callJsFunction(
                "fireViewModelTrigger",
                Objects.requireNonNull(path));
    }

    private void setViewModelProperty(
            String path, String type, Serializable value) {
        Objects.requireNonNull(path);
        getElement().callJsFunction(
                "setViewModelProperty",
                path, type, value);
    }

    /**
     * Updates the ViewModel properties by mapping the provided key-value pairs to supported types
     * and passing them to a JavaScript function for further processing.
     *
     * @param values a map containing the key-value pairs representing the ViewModel properties.
     *               The keys must be non-null strings, and values can be instances of String, Boolean, or Number.
     *               Unsupported or null values will result in an {@link IllegalArgumentException}.
     * @throws NullPointerException if the provided map or any of its keys is null.
     * @throws IllegalArgumentException if a value in the map is of an unsupported type or is null.
     */
    public void setViewModelProperties(Map<String, ?> values) {
        Objects.requireNonNull(values);

        JsonObject object = Json.createObject();

        values.forEach((key, value) -> {
            Objects.requireNonNull(key);

            if (value instanceof String s) {
                object.put(key, s);
            } else if (value instanceof Boolean b) {
                object.put(key, b);
            } else if (value instanceof Number n) {
                object.put(key, n.doubleValue());
            } else {
                throw new IllegalArgumentException(
                        "Unsupported Rive property value for "
                                + key + ": "
                                + (value == null
                                ? "null"
                                : value.getClass().getName()));
            }
        });

        getElement().callJsFunction(
                "setViewModelProperties", object);
    }

    // ---------------------------------------------------------
    // View-model observation (replaces state-change listener)
    // ---------------------------------------------------------

    /**
     * Starts monitoring a specific ViewModel property for changes.
     *
     * @param path the path to the property within the ViewModel; must not be null
     * @param type the data type of the property to monitor; must not be null
     */
    public void watchViewModelProperty(String path, String type) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(type);

        if (!watchedProperties.add(type + ":" + path)) {
            return;
        }

        getElement().callJsFunction(
                "watchViewModelProperty", path, type);
    }

    /**
     * Unwatches a ViewModel property by removing it from the watched properties
     * list and invoking the corresponding JavaScript function to stop tracking it.
     *
     * @param path the path of the property to unwatch, must not be null
     * @param type the type of the property to unwatch, must not be null
     */
    public void unwatchViewModelProperty(String path, String type) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(type);

        watchedProperties.remove(type + ":" + path);

        getElement().callJsFunction(
                "unwatchViewModelProperty", path, type);
    }

    /**
     * Watches a string property at the specified path and binds it for observation.
     *
     * @param path the path to the string property to be watched
     */
    public void watchStringProperty(String path) {
        watchViewModelProperty(path, "string");
    }

    /**
     * Observes changes to a property defined at the specified path and interprets it as a number property.
     *
     * @param path the path to the property to be observed in the view model
     */
    public void watchNumberProperty(String path) {
        watchViewModelProperty(path, "number");
    }

    /**
     * Observes a boolean property in the ViewModel based on the specified path.
     *
     * @param path the dot-separated path string representing the boolean property in the ViewModel
     */
    public void watchBooleanProperty(String path) {
        watchViewModelProperty(path, "boolean");
    }

    /**
     * Watches a specified property in the view model identified as an enumerated type.
     *
     * @param path the property path to watch within the view model
     */
    public void watchEnumProperty(String path) {
        watchViewModelProperty(path, "enum");
    }

    // ---------------------------------------------------------
    // Browser-to-Java events
    // ---------------------------------------------------------

    /**
     * Adds a listener for the {@link ReadyEvent} that will be triggered when the component is ready.
     *
     * @param listener the listener to be added, which will handle the {@link ReadyEvent}
     * @return a {@link Registration} object that can be used to remove the listener
     */
    public Registration addReadyListener(
            ComponentEventListener<ReadyEvent> listener) {
        return addListener(ReadyEvent.class, listener);
    }

    /**
     * Adds a listener for load error events. The listener will be notified whenever
     * a load error occurs in the related component.
     *
     * @param listener the listener to be added, which will handle load error events
     * @return a {@code Registration} object that can be used to remove the listener
     */
    public Registration addLoadErrorListener(
            ComponentEventListener<LoadErrorEvent> listener) {
        return addListener(LoadErrorEvent.class, listener);
    }

    /**
     * Adds a listener that will be notified when a {@link CommandErrorEvent} occurs.
     * The listener will handle events related to errors during command execution.
     *
     * @param listener the listener to be added, which handles {@link CommandErrorEvent}
     * @return a {@link Registration} object that can be used to remove the registered listener
     */
    public Registration addCommandErrorListener(
            ComponentEventListener<CommandErrorEvent> listener) {
        return addListener(CommandErrorEvent.class, listener);
    }

    /**
     * Registers a listener to be notified when a {@link ViewModelChangeEvent} occurs.
     * The listener will be triggered whenever the view model changes.
     *
     * @param listener the listener to be registered, cannot be null
     * @return a {@link Registration} object that can be used to remove the listener
     */
    public Registration addViewModelChangeListener(
            ComponentEventListener<ViewModelChangeEvent> listener) {
        return addListener(ViewModelChangeEvent.class, listener);
    }

    /**
     * Represents a custom component event fired when the "rive-ready" event occurs.
     * This event is associated with the RiveCanvas component and provides
     * information about the state of the canvas when it is ready.
     * <p>
     * The event contains details about the loaded Rive animation source,
     * the active artboard, and the active state machine.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("rive-ready")
    public static class ReadyEvent extends ComponentEvent<RiveCanvas> {

        private final String src;
        private final String artboard;
        private final String stateMachine;

        /**
         * Constructs a ReadyEvent instance when the "rive-ready" event occurs.
         *
         * @param source        The source component that fired the event, typically an instance of RiveCanvas.
         * @param fromClient    A boolean indicating whether the event originated from the client-side.
         * @param src           The Rive animation source associated with the event.
         * @param artboard      The name of the artboard active when the event occurred.
         * @param stateMachine  The name of the active state machine associated with the event.
         */
        public ReadyEvent(
                RiveCanvas source,
                boolean fromClient,
                @EventData("event.detail.src") String src,
                @EventData("event.detail.artboard") String artboard,
                @EventData("event.detail.stateMachine") String stateMachine) {
            super(source, fromClient);
            this.src = src;
            this.artboard = artboard;
            this.stateMachine = stateMachine;
        }

        /**
         * Retrieves the source of the Rive animation that is associated with the "rive-ready" event.
         * This method provides the source URL or identifier of the loaded Rive animation.
         *
         * @return the source of the Rive animation as a String
         */
        public String getSrc() { return src; }

        /**
         * Retrieves the name of the active artboard associated with this event.
         *
         * @return the name of the active artboard as a string
         */
        public String getArtboard() { return artboard; }

        /**
         * Retrieves the name of the active state machine associated with the event.
         *
         * @return the name of the active state machine as a String, or null if no state machine is set
         */
        public String getStateMachine() { return stateMachine; }
    }

    /**
     * Represents an event triggered when a load error occurs in a RiveCanvas component.
     * This event corresponds to the "rive-load-error" DOM event.
     * <p>
     * The LoadErrorEvent provides information about the error through the message property.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("rive-load-error")
    public static class LoadErrorEvent
            extends ComponentEvent<RiveCanvas> {

        private final String message;

        /**
         * Constructs a new LoadErrorEvent, which is triggered when a load error occurs
         * in a RiveCanvas component.
         *
         * @param source the RiveCanvas component where the event originated
         * @param fromClient a boolean indicating whether the event was triggered by the client
         * @param message the error message provided with the event details
         */
        public LoadErrorEvent(
                RiveCanvas source,
                boolean fromClient,
                @EventData("event.detail.message") String message) {
            super(source, fromClient);
            this.message = message;
        }

        /**
         * Retrieves the error message associated with the load error event.
         *
         * @return the error message describing the details of the load error
         */
        public String getMessage() { return message; }
    }

    /**
     * Represents an event triggered when a command error occurs in the RiveCanvas component.
     * This event encapsulates details about the error, including an error message.
     * <p>
     * The event is identified by the "rive-command-error" DOM event.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("rive-command-error")
    public static class CommandErrorEvent extends ComponentEvent<RiveCanvas> {

        private final String message;

        /**
         * Constructs a new CommandErrorEvent.
         *
         * @param source the source component where the event originated
         * @param fromClient a boolean indicating whether the event was triggered
         *                   by the client-side
         * @param message the error message associated with the command error,
         *                retrieved from the event's detail
         */
        public CommandErrorEvent(
                RiveCanvas source,
                boolean fromClient,
                @EventData("event.detail.message") String message) {
            super(source, fromClient);
            this.message = message;
        }

        /**
         * Retrieves the error message encapsulated in this event.
         *
         * @return the error message as a string
         */
        public String getMessage() { return message; }
    }

    /**
     * Represents an event triggered when the ViewModel of a {@link RiveCanvas}
     * component changes. This event is associated with the client-side
     * "rive-viewmodel-change" event.
     * <p>
     * The event provides details about the changed ViewModel properties, including
     * the path to the property, the type of the change, and the new value in JSON
     * format.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("rive-viewmodel-change")
    public static class ViewModelChangeEvent extends ComponentEvent<RiveCanvas> {

        private final String path;
        private final String type;
        private final String valueJson;

        /**
         * Constructs a new ViewModelChangeEvent.
         *
         * @param source the source {@link RiveCanvas} component where the event originated
         * @param fromClient indicates whether the event comes from the client side
         * @param path the path to the ViewModel property that changed
         * @param type the type of change that occurred (e.g., add, update, remove)
         * @param valueJson the new value associated with the changed property, in JSON format
         */
        public ViewModelChangeEvent(
                RiveCanvas source,
                boolean fromClient,
                @EventData("event.detail.path") String path,
                @EventData("event.detail.type") String type,
                @EventData("JSON.stringify(event.detail.value ?? null)")
                String valueJson) {
            super(source, fromClient);
            this.path = path;
            this.type = type;
            this.valueJson = valueJson;
        }

        /**
         * Retrieves the path associated with this event.
         *
         * @return the string representation of the path that describes the changed ViewModel property
         */
        public String getPath() { return path; }

        /**
         * Retrieves the type of the current object.
         *
         * @return the type as a String
         */
        public String getType() { return type; }

        /**
         * Retrieves the JSON representation of the value associated with this event.
         * The JSON string contains details about the new value of the ViewModel property
         * that was changed.
         *
         * @return a JSON string representing the updated value of the ViewModel property
         */
        public String getValueJson() { return valueJson; }
    }

    @Override
    protected String getURIAttributeName() {
        return "src";
    }
}