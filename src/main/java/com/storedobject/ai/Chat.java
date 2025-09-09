package com.storedobject.ai;

import java.util.concurrent.Future;

/**
 * The Chat interface represents a basic contract for interaction between the user
 * and an AI system through text-based communication. It provides a method to ask
 * a question or send a message to the system, receiving a response asynchronously.
 *
 * @author Syam
 */
public interface Chat {
    /**
     * Sends a message to the chat system and receives a response asynchronously.
     *
     * @param message the message text being sent to the AI system.
     * @return a {@code Future<String>} representing the asynchronous result of the AI's response to the message.
     */
    Future<String> ask(String message);

    /**
     * Closes the chat interface, releasing any associated resources or connections.
     * This method should be called when the chat instance is no longer needed to ensure proper cleanup.
     */
    void close();

    /**
     * Checks whether the chat instance has been closed.
     *
     * @return {@code true} if the chat instance is closed, otherwise {@code false}.
     */
    boolean isClosed();

    /**
     * Sets a listener to be invoked when the chat is closed.
     * The listener is a {@code Runnable} that will execute when the chat instance is closed
     * by calling the {@code close} method.
     *
     * @param listener the {@code Runnable} to be executed when the chat is closed.
     */
    void setChatClosedListener(Runnable listener);
}
