package com.storedobject.ui;

import com.storedobject.core.P5;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The P5 class is a UI component that integrates the p5.js JavaScript library with the application.
 * It allows users to create and manage interactive graphics and visualizations by defining a sketch
 * written in p5.js. The class supports setting up sketches, updating parameters dynamically, and
 * handling feedback from the client-side.
 * <p>
 * This class uses Vaadin's Component framework and provides methods for configuring the sketch code,
 * dynamically updating parameters, and receiving feedback from the browser.
 * </p><p>
 * An instance of this component calls p5.js methods to handle artistic or graphical rendering within
 * a web interface.
 * </p><p>
 * The feedback from the client-side is handled via a callback consumer functionality.
 * </p>
 * <pre>
 * Annotations:
 * - {@code @Tag("so-p5")}: Defines the custom HTML tag associated with the component.
 * - {@code @NpmPackage(value = "p5", version = "^2.3.4")}: Indicates the dependency on the p5.js library.
 * - {@code @JsModule("./so/p5/so-p5.js")}: Specifies the client-side JavaScript module used for the integration.
 *
 * Features:
 * - Supports defining sketch code for rendering within the browser.
 * - Validates and extracts prefixes from the sketch for setup and draw hooks.
 * - Allows updating of parameters in the sketch at runtime.
 * - Supports feedback from the client-side via callback consumer functionality.
 * </pre>
 *
 * @author Syam
 */
@Tag("so-p5")
@NpmPackage(value = "p5", version = "^2.3.4")
@JsModule("./so/p5/so-p5.js")
public class P5Canvas extends Component implements HasSize {

    private static final Pattern INSTANCE_HOOKS = Pattern.compile(
            "\\b([a-zA-Z_$][\\w$]*)\\s*\\.\\s*(setup|draw)\\s*=",
            Pattern.MULTILINE
    );
    private Consumer<String> feedbackConsumer;

    /**
     * Default constructor for the P5 class.
     * Initializes the P5 instance with no predefined sketch code.
     * Delegates the initialization to the overloaded constructor with a null parameter.
     */
    public P5Canvas() {
        this((String)null);
    }

    /**
     * Constructs a new instance of the P5 class and initializes it with the specified sketch code.
     * If the provided sketch code is not null or blank, it sets the sketch code for this instance.
     *
     * @param sketchCode the sketch code to be initialized, which must not be null or blank to set the sketch.
     */
    public P5Canvas(String sketchCode) {
        if (sketchCode != null && !sketchCode.isBlank()) {
            setSketch(sketchCode);
        }
    }

    /**
     * Constructs a new instance of the P5 class using the specified ProcessingLanguage instance.
     * Extracts the content from the ProcessingLanguage object and initializes the P5 instance with it.
     *
     * @param language the ProcessingLanguage instance providing the sketch code content
     */
    public P5Canvas(P5 language) {
        this(language.getContent());
    }

    /**
     * Sets the sketch code and its associated prefix for the component.
     * The prefix is derived from the given sketch code and used to configure the component properties.
     * If the prefix cannot be determined, an error message will be logged.
     *
     * @param sketchCode the sketch code to set, which includes the content for the sketch and determines the prefix
     */
    public void setSketch(String sketchCode) {
        String prefix = prefix(sketchCode);
        if (prefix == null) {
            Application.error("Unable to detect sketch prefix");
            return;
        }
        getElement().callJsFunction("setSketch", sketchCode.trim(), prefix);
    }

    /**
     * Updates a parameter in the underlying JavaScript function with the specified name and value.
     *
     * @param paramName the name of the parameter to update
     * @param value the new value to assign to the parameter
     */
    public void updateParameter(String paramName, double value) {
        getElement().callJsFunction("updateParam", paramName, value);
    }

    private static String prefix(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        Matcher m = INSTANCE_HOOKS.matcher(code);
        return m.find() ? m.group(1) : null;
    }

    @ClientCallable
    private void feedback(String resultData) {
        if (feedbackConsumer != null) {
            feedbackConsumer.accept(resultData);
        }
    }

    /**
     * Sets the feedback consumer for this instance. The feedback consumer is a function
     * that processes feedback messages, typically provided as strings.
     *
     * @param feedbackConsumer the feedback consumer to be used, which consumes strings
     *                         representing feedback messages
     */
    public void setFeedbackConsumer(Consumer<String> feedbackConsumer) {
        this.feedbackConsumer = feedbackConsumer;
    }
}