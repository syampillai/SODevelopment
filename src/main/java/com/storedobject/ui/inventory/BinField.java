package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;

public class BinField extends ObjectGetField<InventoryBin> {

    private final Getter getter;

    public BinField() {
        this((String)null);
    }

    public BinField(String caption) {
        this(caption, (ObjectProvider<? extends InventoryStore>) null);
    }

    public BinField(InventoryStore store) {
        this(null, store);
    }

    public BinField(ObjectProvider<? extends InventoryStore> storeField) {
        this(null, storeField);
    }

    public BinField(String caption, InventoryStore store) {
        this(caption, () -> store);
    }

    public BinField(String caption, ObjectProvider<? extends InventoryStore> storeField) {
        this(caption, storeField, new Getter());
    }

    private BinField(String caption, ObjectProvider<? extends InventoryStore> storeField, Getter getter) {
        super(caption, InventoryBin.class, true, getter);
        this.getter = getter;
        setStoreField(storeField);
        getSearcher().getSearchBuilder().removeSearchField("Store.Name");
        setFilter(new InventoryFilterProvider());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(getter.storeProvider == null) {
            getter.findStore(this.getParent().orElse(null));
        }
        setFilter("");
    }

    @Override
    public void setFilter(ObjectLoadFilter<InventoryBin> filter) {
        throw new UnsupportedOperationException();
    }

    public void setStore(InventoryStore store) {
        getter.storeProvider = store == null ? null : () -> store;
        applyFilter();
    }

    public void setStoreField(ObjectProvider<? extends InventoryStore> storeField) {
        getter.storeProvider = storeField;
        applyFilter();
    }

    private class InventoryFilterProvider implements FilterProvider {

        @Override
        public String getFilterCondition() {
            return getter.storeProvider == null ? null : ("Store=" + getter.storeProvider.getObjectId());
        }
    }

    private static class Getter implements GetProvider<InventoryBin> {

        ObjectProvider<? extends InventoryStore> storeProvider;

        void findStore(Component me) {
            if(me == null || storeProvider != null) {
                return;
            }
            Component sf = me.getChildren().
                    filter(f -> f instanceof ObjectField && InventoryStore.class.isAssignableFrom(((ObjectField<?>)f).getObjectClass())).
                    findAny().orElse(null);
            if(sf != null) {
                //noinspection unchecked
                storeProvider = (ObjectProvider<? extends InventoryStore>) sf;
            } else {
                findStore(me.getParent().orElse(null));
            }
        }

        @Override
        public InventoryBin getTextObject(SystemEntity systemEntity, String searchText) {
            InventoryStore store = storeProvider == null ? null : storeProvider.getObject();
            return store == null ? null : InventoryBin.get(searchText, store);
        }

        @Override
        public ObjectIterator<InventoryBin> listTextObjects(SystemEntity systemEntity, String searchText) {
            InventoryStore store = storeProvider == null ? null : storeProvider.getObject();
            return store == null ? ObjectIterator.create() : InventoryBin.list(searchText, store);
        }
    }
}