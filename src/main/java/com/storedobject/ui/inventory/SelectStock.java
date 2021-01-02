package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryItem;
import com.storedobject.core.ObjectIterator;
import com.storedobject.vaadin.SelectGrid;

import java.util.function.Consumer;

/**
 * Select stock entry from a list of given stock items.
 *
 * @author Syam
 */
public class SelectStock extends SelectGrid<InventoryItem> {

    /**
     * Constructor.
     *
     * @param stockItems Stock items.
     * @param consumer Consumer to consume the selected entry.
     */
    public SelectStock(ObjectIterator<InventoryItem> stockItems, Consumer<InventoryItem> consumer) {
        super(InventoryItem.class, stockItems.toList(), ItemField.COLUMNS, consumer);
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
