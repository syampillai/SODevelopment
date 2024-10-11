package com.storedobject.iot;

import com.storedobject.common.JSON;

public interface CustomMessageProcessor {
    JSON process(String module, JSON json);
}
