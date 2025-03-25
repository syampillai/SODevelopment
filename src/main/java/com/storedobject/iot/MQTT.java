package com.storedobject.iot;

import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionManager;

import java.util.Set;

public class MQTT extends StoredObject {

    public static Set<Class<?>> getDataClasses() {
        return Set.of();
    }

    public static long getMessageCount() {
        return 0;
    }

    public static long getErrorMessageCount() {
        return 0;
    }

    public static long getIgnoredMessageCount() {
        return 0;
    }

    public static long getLastProcessingTime() {
        return 0;
    }

    public static long getMinProcessingTime() {
        return 0;
    }

    public static long getMaxProcessingTime() {
        return 0;
    }

    public static long getTotalProcessingTime() {
        return 0;
    }

    public static long getPendingMessageCount() {
        return 0;
    }

    public static long getPurgedMessageCount() {
        return 0;
    }

    public void collect(TransactionManager transactionManager) {
    }

    public void removeAllListeners() {
    }

    public void disconnect() {
    }

    public void publish(Command command) {
    }

    public Data getData(ValueDefinition<?> valueDefinition, Id unitId) {
        return null;
    }

    public static Data getData(Block block, Class<?> dataClass) {
        return null;
    }

    public static MQTT get() {
        return null;
    }

    public static void reportDuplicateErrors() {
    }
}
