package com.storedobject.ui.inventory;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.EditorAction;
import com.storedobject.core.InventoryItem;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStore;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectGetField;
import com.storedobject.ui.ObjectProvider;

/**
 * Field to accept an instance of the {@link InventoryItem} with provision to input part number.
 *
 * @param <I> Type of inventory item to accept.
 * @author Syam
 */
public class ItemGetField<I extends InventoryItem> extends ObjectGetField<I> implements ItemInput<I> {

    private ObjectProvider<? extends InventoryStore> storeField;
    private ObjectProvider<? extends InventoryLocation> locationField;
    private FilterProvider extraFilterProvider;
    private final InventoryFilterProvider filterProvider;

    /**
     * Constructor.
     *
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     */
    public ItemGetField(Class<I> objectClass) {
        this(null, objectClass);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     */
    public ItemGetField(String label, Class<I> objectClass) {
        this(label, objectClass, false);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public ItemGetField(Class<I> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    /**
     * Constructor.
     *
     * @param label Label for the field.
     * @param objectClass Class of the {@link InventoryItem} objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public ItemGetField(String label, Class<I> objectClass, boolean allowAny) {
        super(objectClass, allowAny);
        filterProvider = new InventoryFilterProvider();
        setLabel(label);
    }

    @Override
    public void setStore(ObjectProvider<? extends InventoryStore> storeField) {
        this.storeField = storeField;
    }

    @Override
    public void setStore(InventoryStore store) {
        this.storeField = store == null ? null : () -> store;
    }

    @Override
    public void setLocation(ObjectProvider<? extends InventoryLocation> locationField) {
        this.locationField = locationField;
    }

    @Override
    public void setLocation(InventoryLocation location) {
        this.locationField = location == null ? null : () -> location;
    }

    @Override
    public void setExtraFilterProvider(FilterProvider extraFilterProvider) {
        this.extraFilterProvider = extraFilterProvider;
    }

    @Override
    protected ObjectBrowser<I> createSearcher() {
        ObjectBrowser<I> s = ObjectBrowser.create(getObjectClass(), ItemField.COLUMNS,
                EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0),
                null);
        s.setFixedFilter(filterProvider);
        return s;
    }

    private class InventoryFilterProvider implements FilterProvider {

        @Override
        public String getFilterCondition() {
            final StringBuilder f = new StringBuilder("(T.Quantity).Quantity>0");
            if(extraFilterProvider != null) {
                f.append(" AND (").append(extraFilterProvider.getFilterCondition()).append(')');
            }
            if(locationField != null) {
                f.append(" AND T.Location=").append(locationField.getObjectId());
            } else if(storeField != null) {
                f.append(" AND T.Store=").append(storeField.getObjectId());
            }
            return f.toString();
        }
    }
}
