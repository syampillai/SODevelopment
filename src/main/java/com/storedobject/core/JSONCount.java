package com.storedobject.core;

public class JSONCount extends JSONRetrieve {

    @Override
    public void execute(Device device, JSON json, JSONMap result) {
        super.execute(device, json, result, 2);
    }
}
