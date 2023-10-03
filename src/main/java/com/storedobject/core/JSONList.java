package com.storedobject.core;

import com.storedobject.common.JSON;

import java.util.Map;

public class JSONList extends JSONRetrieve {

    @Override
    public void execute(Device device, JSON json, Map<String, Object> result) {
        super.execute(device, json, result, true);
    }
}