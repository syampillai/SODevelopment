package com.storedobject.core;

import com.storedobject.common.StringList;

public abstract class JSONRetrieve implements JSONService {

    protected <T extends StoredObject> void execute(Device device, JSON json, JSONMap result, int type) {
        T so = null;
        Parameters<T> p;
        try {
            p = new Parameters<>(json);
            if(type == 0) {
                so = p.so();
            }
        } catch (Exception e) {
            result.error(e.getMessage());
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
        StringList attributes = json.getStringList("attributes");
        Boolean b = json.getBoolean("includeReferences");
        boolean includeReferences = b != null && b;
        b = json.getBoolean("includeClassInfo");
        boolean includeClassInfo = b != null && b;
        b = json.getBoolean("stringify");
        boolean stringify = b != null && b;
        try {
            if(so == null) {
                JSONMap.Array oList = result.array(dataLabel);
                JSONMap value;
                try(ObjectIterator<T> objList = p.list()) {
                    for(StoredObject object: objList) {
                        value = oList.map();
                        object.save(value, attributes, null, includeReferences, includeClassInfo, stringify);
                    }
                }
            } else {
                so.save(result, attributes, dataLabel, includeReferences, includeClassInfo, stringify);
            }
        } catch (Throwable e) {
            device.log(e);
            result.error("Error while retrieving data");
        }
    }
    
    private static class Parameters<T extends StoredObject> {

        final QueryBuilder<T> queryBuilder;
        final boolean master;
        final Id thisId;
        final int linkType;
        
        Parameters(JSON json) throws Exception {
            //noinspection unchecked
            queryBuilder = QueryBuilder.from((Class<T>) json.getDataClass("className"));
            queryBuilder.where(json.getString("where")).orderBy(json.getString("order"))
                    .any(json.getBoolean("any"));
            Boolean b = json.getBoolean("master");
            master = b != null && b;
            Id id = json.getId("this");
            Number n = json.getNumber("skip");
            if(n != null) {
                queryBuilder.skip(n.intValue());
            }
            n = json.getNumber("limit");
            if(n != null) {
                queryBuilder.limit(n.intValue());
            }
            n = json.getNumber("link");
            linkType = n == null ? (master ? 0 : -1) : n.intValue();
            if(id == null && (master || linkType >= 0)) {
                com.storedobject.common.JSON curr = json.get("this");
                if(curr != null) {
                    Parameters<?> p = new Parameters<>(new JSON(curr));
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
                return queryBuilder.existsMasters(thisId, linkType);
            } else if(linkType >= 0) {
                return queryBuilder.existsLinks(thisId, linkType);
            } else {
                return queryBuilder.exists();
            }
        }

        T so() throws Exception {
            T so;
            if(master) {
                so = queryBuilder.getMaster(thisId, linkType);
            } else if(linkType >= 0) {
                so = queryBuilder.getLink(thisId, linkType);
            } else {
                so = queryBuilder.get();
            }
            if (so == null) {
                throw new Exception("Data not found");
            }
            return so;
        }

        ObjectIterator<T> list() {
            if(master) {
                return queryBuilder.listMasters(thisId, linkType);
            } else if(linkType >= 0) {
                return queryBuilder.listLinks(thisId, linkType);
            } else {
                return queryBuilder.list();
            }
        }

        long count() {
            if(master) {
                return queryBuilder.countMasters(thisId, linkType);
            } else if(linkType >= 0) {
                return queryBuilder.countLinks(thisId, linkType);
            } else {
                return queryBuilder.count();
            }
        }
    }
}
