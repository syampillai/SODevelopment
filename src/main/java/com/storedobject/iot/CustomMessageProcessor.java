package com.storedobject.iot;

import com.storedobject.common.JSON;

public interface CustomMessageProcessor {

    default JSON process(String module, byte[] payload) {
        return null;
    }

    default JSON process(String module, String payload) {
        return null;
    }

    default JSON process(String module, JSON payload) {
        return null;
    }
}
