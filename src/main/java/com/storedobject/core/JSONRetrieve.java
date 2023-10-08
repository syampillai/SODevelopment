package com.storedobject.core;

import com.storedobject.common.JSON;
import com.storedobject.common.StringList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JSONRetrieve implements JSONService {

    protected <T extends StoredObject> void execute(Device device, JSON json, Map<String, Object> result, int type) {
        T so = null;
        Parameters<T> p;
        try {
            p = new Parameters<>(json);
            if(type == 0) {
                so = p.so();
            }
        } catch (Exception e) {
            JSONService.error(e.getMessage(), result);
            return;
        }
        String dataLabel = json.getString("dataLabel");
        if(dataLabel == null) {
            dataLabel = "data";
        }
        if(type == 2) {
            result.put(dataLabel, p.count());
            return;
        }
        if(type == 3) {
            result.put(dataLabel, p.exists());
            return;
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
                try(ObjectIterator<T> objList = p.list()) {
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

    private static class Parameters<T extends StoredObject> {

        final String where, orderBy;
        final boolean any, master;
        final Id thisId;
        final Class<T> dataClass;
        final int linkType;

        Parameters(JSON json) throws Exception {
            String className = json.getString("className");
            try {
                if(className == null) {
                    throw new Exception("Class not specified");
                }
                Class<?> dClass = JavaClassLoader.getLogic(ApplicationServer.guessClass(className));
                if(StoredObject.class.isAssignableFrom(dClass)) {
                    //noinspection unchecked
                    dataClass = (Class<T>) dClass;
                } else {
                    throw new Exception("Not a data class - " + dClass.getName());
                }
                if(dataClass == Secret.class) {
                    throw new Exception("No access - " + dClass.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new Exception("Class not found - " + className);
            }
            where = json.getString("where");
            orderBy = json.getString("order");
            Boolean b = json.getBoolean("any");
            any = b != null && b;
            b = json.getBoolean("master");
            master = b != null && b;
            Id id = JSONService.getId(json, "this");
            Number n = json.getNumber("link");
            linkType = n == null ? (master ? 0 : -1) : n.intValue();
            if(id == null && (master || linkType >= 0)) {
                JSON curr = json.get("this");
                if(curr != null) {
                    Parameters<?> p = new Parameters<>(curr);
                    try {
                        StoredObject so = p.so();
                        id = so.getId();
                    } catch (Exception e) {
                        throw new Exception("Unable to retrieve current object");
                    }
                }
                if(id == null) {
                    throw new Exception("Current object not specified");
                }
            }
            thisId = id;
        }

        boolean exists() {
            if(master) {
                return thisId.existsMasters(linkType, dataClass, where, any);
            } else if(linkType >= 0) {
                return thisId.existsLinks(linkType, dataClass, where, any);
            } else {
                return StoredObject.exists(dataClass, where, any);
            }
        }

        T so() throws Exception {
            T so;
            if(master) {
                so = thisId.getMaster(linkType, dataClass, where, orderBy, any);
            } else if(linkType >= 0) {
                so = thisId.listLinks(linkType, dataClass, where, orderBy, any).findFirst();
            } else {
                so = StoredObject.get(dataClass, where, orderBy, any);
            }
            if (so == null) {
                throw new Exception("Data not found");
            }
            return so;
        }

        ObjectIterator<T> list() {
            if(master) {
                return thisId.listMasters(linkType, dataClass, where, orderBy, any);
            } else if(linkType >= 0) {
                return thisId.listLinks(linkType, dataClass, where, orderBy, any);
            } else {
                return StoredObject.list(dataClass, where, orderBy, any);
            }
        }

        long count() {
            if(master) {
                return thisId.countMasters(linkType, dataClass, where, any);
            } else if(linkType >= 0) {
                return thisId.countLinks(linkType, dataClass, where, any);
            } else {
                return StoredObject.count(dataClass, where, any);
            }
        }
    }
}
