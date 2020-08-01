package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.InventoryBin;
import com.storedobject.core.InventoryStore;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectProvider;

public class BinField extends ObjectGetField<InventoryBin> {

    public BinField() {
        this((String)null);
    }

    public BinField(String caption) {
        this(caption, (InventoryStore)null);
    }

    public BinField(InventoryStore store) {
        this(null, store);
    }

    public BinField(ObjectProvider<? extends InventoryStore> storeField) {
        this(null, storeField);
    }

    public BinField(String caption, InventoryStore store) {
        this(caption, ObjectProvider.create(store));
    }

    public BinField(String caption, ObjectProvider<? extends InventoryStore> storeField) {
        super(caption, InventoryBin.class, true);
    }

    @Override
    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
        throw new UnsupportedOperationException();
    }

    public void setStore(InventoryStore store) {
    }

    public void setStoreField(ObjectProvider<? extends InventoryStore> storeField) {
    }
}