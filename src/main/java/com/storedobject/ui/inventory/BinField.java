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

    public BinField(ObjectProvider<? extends InventoryStore> store) {
        this(null, store);
    }

    public BinField(String caption, InventoryStore store) {
        this(caption, () -> store);
    }

    public BinField(String caption, ObjectProvider<? extends InventoryStore> store) {
        this(caption, store, new Getter());
    }

    private BinField(String caption, ObjectProvider<? extends InventoryStore> store, Getter getter) {
        super(caption, InventoryBin.class, true, getter);
        this.getter = getter;
        setStore(store, false);
        getSearcher().getSearchBuilder().removeSearchField("Store.Name");
        setFilter(new InventoryFilterProvider(), false);
        setLoadFilter(getter::filter, false);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(getter.storeProvider == null) {
            getter.findStore(this.getParent().orElse(null));
        }
    }

    @Override
    public void setFilter(ObjectLoadFilter<InventoryBin> filter) {
        throw new UnsupportedOperationException();
    }

    public void setStore(InventoryStore store) {
        setStore(store, true);
    }

    public void setStore(InventoryStore store, boolean apply) {
        getter.storeProvider = store == null ? null : () -> store;
        if(apply) {
            applyFilter();
        }
    }

    public void setStore(ObjectProvider<? extends InventoryStore> storeField) {
        setStore(storeField, true);
    }

    public void setStore(ObjectProvider<? extends InventoryStore> storeField, boolean apply) {
        getter.storeProvider = storeField;
        if(apply) {
            applyFilter();
        }
    }

    public void setItem(InventoryItem item) {
        setItem(item, true);
    }

    public void setItem(InventoryItem item, boolean apply) {
        getter.itemProvider = item == null ? null : () -> item;
        if(apply) {
            applyFilter();
        }
    }

    public void setItem(ObjectProvider<? extends InventoryItem> itemField) {
        setItem(itemField, true);
    }

    public void setItem(ObjectProvider<? extends InventoryItem> itemField, boolean apply) {
        getter.itemProvider = itemField;
        if(apply) {
            applyFilter();
        }
    }

    public void setItemType(InventoryItemType itemType) {
        setItemType(itemType, true);
    }

    public void setItemType(InventoryItemType itemType, boolean apply) {
        getter.itemTypeProvider = itemType == null ? null : () -> itemType;
        if(apply) {
            applyFilter();
        }
    }

    public void setItemType(ObjectProvider<? extends InventoryItemType> itemTypeField) {
        setItemType(itemTypeField, true);
    }

    public void setItemType(ObjectProvider<? extends InventoryItemType> itemTypeField, boolean apply) {
        getter.itemTypeProvider = itemTypeField;
        if(itemTypeField == null) {
            throw new RuntimeException("Item Type Field cannot be null!");
        }
        if(apply) {
            applyFilter();
        }
    }

    private class InventoryFilterProvider implements FilterProvider {

        @Override
        public String getFilterCondition() {
            return getter.storeProvider == null ? null : ("Store=" + getter.storeProvider.getObjectId());
        }
    }

    private static class Getter implements GetProvider<InventoryBin> {

        ObjectProvider<? extends InventoryStore> storeProvider;
        ObjectProvider<? extends InventoryItem> itemProvider;
        ObjectProvider<? extends InventoryItemType> itemTypeProvider;

        void findStore(Component me) {
            if(me == null || storeProvider != null) {
                return;
            }
            Component sf = me.getChildren().
                    filter(f -> f instanceof ObjectField
                            && InventoryStore.class.isAssignableFrom(((ObjectField<?>)f).getObjectClass())).
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
            return listTextObjects(systemEntity, searchText).single(false);
        }

        @Override
        public ObjectIterator<InventoryBin> listTextObjects(SystemEntity systemEntity, String searchText) {
            InventoryStore store = storeProvider == null ? null : storeProvider.getObject();
            if(store == null) {
                return ObjectIterator.create();
            }
            ObjectIterator<InventoryBin> bins = InventoryBin.list(searchText, store);
            if(itemProvider != null || itemTypeProvider != null) {
                return bins.filter(this::filter1);
            }
            return bins;
        }

        boolean filter(InventoryBin bin) {
            if(bin == null || bin instanceof InventoryReservedBin) {
                return false;
            }
            InventoryStore store = storeProvider == null ? null : storeProvider.getObject();
            return store != null && filter1(bin);
        }

        private boolean filter1(InventoryBin bin) {
            if(itemProvider != null) {
                InventoryItem item = itemProvider.getObject();
                if(item != null) {
                    return item.canBin(bin);
                }
            }
            if(itemTypeProvider != null) {
                InventoryItemType itemType = itemTypeProvider.getObject();
                if(itemType != null) {
                    return itemType.canBin(bin);
                }
            }
            return true;
        }
    }
}