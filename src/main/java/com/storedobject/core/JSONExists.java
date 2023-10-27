package com.storedobject.core;

public class JSONExists extends JSONRetrieve {

    @Override
    public void execute(Device device, JSON json, JSONMap result) {
        super.execute(device, json, result, 3);
    }
}
