package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.core.ObjectIterator;
import com.storedobject.vaadin.MultiSelectGrid;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Select stock multiple entries from a list of given stock items.
 *
 * @author Syam
 */
public class MultiSelectStock extends MultiSelectGrid<InventoryItem> {

    /**
     * Constructor.
     *
     * @param stockItems Stock items.
     * @param consumer Consumer to consume the selected entries.
     */
    public MultiSelectStock(ObjectIterator<InventoryItem> stockItems, Consumer<Set<InventoryItem>> consumer) {
        super(InventoryItem.class, stockItems.toList(), ItemField.COLUMNS, consumer);
        setCaption("Select Items");
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("SerialNumberDisplay".equals(columnName)) {
            return "Serial/Batch Number";
        }
        if("LocationDisplay".equals(columnName)) {
            return "Location";
        }
        return super.getColumnCaption(columnName);
    }
}
