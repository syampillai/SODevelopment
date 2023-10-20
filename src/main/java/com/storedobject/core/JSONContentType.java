package com.storedobject.core;

import com.storedobject.common.JSON;

import java.util.Map;

public class JSONContentType implements JSONService {

    @Override
    public void execute(Device device, JSON json, Map<String, Object> result) {
        String filePathName = json.getString("file");
        if(filePathName == null) {
            JSONService.error("Name not specified", result);
            return;
        }
        StreamData sd = JSONService.getStreamData(filePathName);
        if(sd == null) {
            JSONService.error("Not found", result);
            return;
        }
        result.put("type", sd.getMimeType());
    }
}
