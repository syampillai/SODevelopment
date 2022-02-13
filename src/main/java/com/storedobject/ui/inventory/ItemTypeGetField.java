package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItemType;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.SystemEntity;
import com.storedobject.ui.ObjectGetField;

public class ItemTypeGetField<T extends InventoryItemType> extends ObjectGetField<T> {

    public ItemTypeGetField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ItemTypeGetField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ItemTypeGetField(Class<T> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    public ItemTypeGetField(String label, Class<T> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny, false);
    }

    public ItemTypeGetField(Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        this(null, objectClass, allowAny, allowAdd);
    }

    public ItemTypeGetField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        super(label, objectClass, allowAny, allowAdd);
    }

    @Override
    protected GetProvider<T> createGetProvider() {
        return new GetSupplier<>();
    }

    @SuppressWarnings("unchecked")
    private class GetSupplier<O extends T> implements GetProvider<O> {

        @Override
        public O getTextObject(SystemEntity systemEntity, String searchText) throws Exception {
            return (O) InventoryItemType.getByPartNumber(getObjectClass(), searchText, isAllowAny());
        }

        @Override
        public ObjectIterator<O> listTextObjects(SystemEntity systemEntity, String searchText) throws Exception {
            return InventoryItemType.listByPartNumber(getObjectClass(), searchText, isAllowAny())
                    .map(t -> (O)t);
        }
    }
}
