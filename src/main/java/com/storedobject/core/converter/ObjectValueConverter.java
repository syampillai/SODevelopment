package com.storedobject.core.converter;

import com.storedobject.core.StoredObject;

public class ObjectValueConverter extends ValueConverter<StoredObject> {

    public ObjectValueConverter(Class<? extends StoredObject> objectClass) {
    }

    @Override
    public Class<StoredObject> getValueType() {
        return StoredObject.class;
    }

    @Override
    public StoredObject convert(Object value) {
        if(value instanceof StoredObject) {
            return (StoredObject) value;
        }
        return null;
    }
}
