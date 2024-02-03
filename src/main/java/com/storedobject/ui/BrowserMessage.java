package com.storedobject.ui;

import com.storedobject.common.JSON;
import com.storedobject.helper.LitComponent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

import java.util.function.BiConsumer;

/**
 * An invisible component to grab messages sent to the browser window. The messages could be from an embedded iframe
 * content. You should add an instance of this to your component layout to grab the messages.
 *
 * @author Syam
 */
@Tag("so-browser-message")
@JsModule("./so/browser-message/browser-message.js")
public class BrowserMessage extends LitComponent {

    private final BiConsumer<String, JSON> messageGrabber;

    /**
     * Constructor. A message grabber is passed as the parameter. The grabber is an instance of a {@link BiConsumer}
     * that accepts a String value and a JSON value. The String value will contain the origin of the message and the
     * JSON value will contain the actual message.
     *
     * @param messageGrabber Message consumer. Message is passed as a JSON.
     */
    public BrowserMessage(BiConsumer<String, JSON> messageGrabber) {
        this.messageGrabber = messageGrabber;
    }

    @ClientCallable
    private void message(String origin, String json) {
        messageGrabber.accept(origin, JSON.create(json));
    }
}
