package com.storedobject.core;

public class JSONContentType implements JSONService {

    @Override
    public void execute(Device device, JSON json, JSONMap result) {
        String filePathName = json.getString("name");
        if(filePathName == null) {
            filePathName = json.getString("file");
        }
        if(filePathName == null) {
            filePathName = json.getString("stream");
        }
        if(filePathName == null) {
            result.error("Name not specified");
            return;
        }
        StreamData sd = StreamData.get(filePathName);
        if(sd == null) {
            result.error("Not found");
            return;
        }
        result.put("type", sd.getMimeType());
        result.put("id", sd.getId());
    }
}
