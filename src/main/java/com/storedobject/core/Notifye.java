package com.storedobject.core;

/**
 * An interface that denotes that notifications can be received.
 *
 * @author Syam
 */
@FunctionalInterface
public interface Notifye {

    /**
     * Create and send a message to this received.
     * <p>Note: If the template doesn't exist, the default template is used.</p>
     * @param templateName Name of the template to create the message.
     * @param tm Transaction manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @return True the message is successfully created for delivery.
     */
    boolean notify(String templateName, TransactionManager tm, Object... messageParameters);
}
