package com.storedobject.ui;

import com.storedobject.core.ObjectCacheList;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectLink;

public class ObjectLinkListProvider<L extends StoredObject> extends EditableObjectListProvider<L> implements StoredObjectLink<L> {

    private final String fieldName;

    public ObjectLinkListProvider(String fieldName, Class<L> objectClass, int linkType, String orderBy) {
        this(fieldName, objectClass, linkType, orderBy, false);
    }

    public ObjectLinkListProvider(String fieldName, Class<L> objectClass, int linkType, String orderBy, boolean allowAny) {
        super(new ObjectCacheList<>(objectClass, allowAny));
        if(fieldName != null && fieldName.endsWith(".l")) {
            fieldName = fieldName.substring(0, fieldName.length() - 2);
        }
        this.fieldName = fieldName;
        setLinkType(linkType);
        setOrderBy(orderBy);
    }

    @Override
    public final String getName() {
        return fieldName;
    }

    @Override
    public final int getType() {
        return getLinkType();
    }
}
