package com.storedobject.core;

import com.storedobject.common.JSON;
import com.storedobject.common.StringList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JSONRetrieve implements JSONService{

    protected <T extends StoredObject> void execute(Device device, JSON json, Map<String, Object> result, boolean list) {
        String className = json.getString("className");
        Class<T> dataClass;
        try {
            if(className == null) {
                JSONService.error("Class not specified", result);
                return;
            }
            Class<?> dClass = JavaClassLoader.getLogic(ApplicationServer.guessClass(className));
            if(StoredObject.class.isAssignableFrom(dClass)) {
                //noinspection unchecked
                dataClass = (Class<T>) dClass;
            } else {
                JSONService.error("Not a data class - " + dClass.getName(), result);
                return;
            }
            if(dataClass == Secret.class) {
                JSONService.error("No access - " + dClass.getName(), result);
                return;
            }
        } catch (ClassNotFoundException e) {
            JSONService.error("Class not found - " + className, result);
            return;
        }
        String where = json.getString("where");
        StoredObject so = null;
        if(!list) {
            so = StoredObject.get(dataClass, where);
            if (so == null) {
                JSONService.error("Data not found", result);
                return;
            }
        }
        String dataLabel = json.getString("dataLabel");
        if(dataLabel == null) {
            dataLabel = "data";
        }
        StringList attributes = null;
        json = json.get("attributes");
        if(json != null) {
            if(json.getType() == JSON.Type.ARRAY) {
                String[] as = new String[json.getArraySize()];
                for(int i = 0; i < as.length; i++) {
                    as[i] = json.get(i).getString();
                }
                attributes = StringList.create(as);
            } else if(json.getType() == JSON.Type.STRING) {
                attributes = StringList.create(json.getString());
            }
        }
        try {
            if(so == null) {
                List<Object> oList = new ArrayList<>();
                Map<String, Object> value;
                try(ObjectIterator<T> objList = StoredObject.list(dataClass, where)) {
                    for(StoredObject object: objList) {
                        value = new HashMap<>();
                        object.save(value, attributes);
                        oList.add(value);
                    }
                    value = new HashMap<>();
                    value.put(dataLabel, oList);
                }
                result.put(dataLabel, oList);
            } else {
                so.save(result, attributes, dataLabel);
            }
        } catch (Throwable e) {
            device.log(e);
            JSONService.error("Error while retrieving data", result);
        }
    }
}
