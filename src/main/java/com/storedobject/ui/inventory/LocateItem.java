package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DataGrid;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * For locating stocks of a given "Part Number" and its APNs. The result includes all locations, including items
 * fitted on assemblies, items sent for repair etc. However, items that are already scrapped are not included.
 *
 * @author Syam
 */
public class LocateItem extends DataGrid<InventoryItem> implements CloseableView {

    private static final String INSPECT = "INSPECT", EDIT_COST = "EDIT_COST", ASSEMBLE = "ASSEMBLE", BREAK = "BREAK";
    private final ChoiceField servFilter = new ChoiceField(new String[] {
            "Serviceable",
            "Unserviceable",
            "All",
    });
    private final ChoiceField locFilter = new ChoiceField(new String[] {
            "Stores only",
            "Other locations",
            "Fitted on assembly",
            "Everywhere"
    });
    private final Class<? extends InventoryItem> itemClass;
    private final ObjectField<? extends InventoryItemType> pnField;
    private final TextField snField;
    private InventoryStore store;
    private final ItemContextMenu<InventoryItem> contextMenu;
    private final ELabel help = new ELabel("Right-click on the row to see more options", Application.COLOR_SUCCESS);
    private final Button selectStore = new Button("Select Store", (String)null, e -> selectStore());

    /**
     * Constructor.
     */
    public LocateItem() {
        this((String)null);
    }

    /**
     * Constructor.
     *
     * @param caption This could be "Caption", "Caption|Class Name", "Caption|Options", "Caption|Class Name|Options",
     *                "Options" could be comma-separated string of "INSPECT", "EDIT_COST", "BREAK", "ASSEMBLE".
     */
    public LocateItem(String caption) {
        this(caption(caption), null, itemClass(caption), itemTypeClass(caption), false, caption);
    }

    /**
     * Constructor.
     *
     * @param partNumber Part number.
     */
    public LocateItem(InventoryItemType partNumber) {
        this(null, partNumber);
    }

    /**
     * Constructor.
     *
     * @param canInspect Whether the item details can be inspected/re-binned or not.
     */
    public LocateItem(boolean canInspect) {
        this(null, null, null, null,
                canInspect, null);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param canInspect Whether the item details can be inspected/re-binned or not.
     */
    public LocateItem(String caption, boolean canInspect) {
        this(caption(caption), null, itemClass(caption), itemTypeClass(caption), canInspect, caption);
    }

    /**
     * Constructor.
     *
     * @param partNumber Part number.
     * @param canInspect Whether the item details can be inspected/re-binned or not.
     */
    public LocateItem(InventoryItemType partNumber, boolean canInspect) {
        this(null, partNumber, canInspect);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param partNumber Part number.
     */
    public LocateItem(String caption, InventoryItemType partNumber) {
        this(caption, partNumber, false);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param partNumber Part number.
     * @param canInspect Whether the item details can be inspected/re-binned or not.
     */
    public LocateItem(String caption, InventoryItemType partNumber, boolean canInspect) {
        this(caption, partNumber, null, null, canInspect, caption);
    }

    /**
     * Constructor.
     *
     * @param itemClass Item class.
     */
    public LocateItem(Class<? extends InventoryItem> itemClass) {
        this(null, itemClass, false);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param itemClass Item class.
     */
    public LocateItem(String caption, Class<? extends InventoryItem> itemClass) {
        this(caption, itemClass, false);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param itemClass Item class.
     * @param canInspect Whether the item details can be inspected/re-binned or not.
     */
    public LocateItem(String caption, Class<? extends InventoryItem> itemClass, boolean canInspect) {
        this(caption, null, itemClass, null, canInspect, null);
    }

    private LocateItem(String caption, InventoryItemType partNumber, Class<? extends InventoryItem> itemClass,
                       Class<? extends InventoryItemType> itemTypeClass, boolean canInspect, String originalCaption) {
        super(InventoryItem.class, ItemField.COLUMNS);
        selectStore.asSmall().setVisible(false);
        if(originalCaption == null) {
            originalCaption = "";
        }
        setCaption(caption == null || caption.isEmpty() ? "Items" : caption);
        if(!canInspect && originalCaption.contains(INSPECT)) {
            canInspect = true;
        }
        if(originalCaption.contains("|")) {
            caption = originalCaption.substring(originalCaption.indexOf('|') + 1)
                    .replace(INSPECT, "").replace(EDIT_COST, "")
                    .replace(BREAK, "").replace(ASSEMBLE, "")
                    .replace(",", "").replace("|", "");
            if (!caption.isEmpty() && itemClass == null && itemTypeClass == null) {
                throw new SORuntimeException("Unable to determine item or item type class from '" +
                        originalCaption + "'");
            }
        }
        this.itemClass = itemClass;
        if(itemClass == null) {
            if(itemTypeClass == null) {
                itemTypeClass = InventoryItemType.class;
            }
            pnField = new ObjectField<>(itemTypeClass, true);
        } else {
            pnField = null;
        }
        if(partNumber != null && pnField != null) {
            snField = null;
            pnField.setValue(partNumber.getId());
            pnField.setReadOnly(true);
            loadItems();
        } else {
            snField = new TextField();
            if(pnField != null) {
                snField.setPlaceholder("Optional");
            }
            snField.addValueChangeListener(e -> {
                if(e.isFromClient()) {
                    loadItems();
                }
            });
            if(pnField != null) {
                pnField.addValueChangeListener(e -> loadItems());
            }
        }
        servFilter.addValueChangeListener(e -> loadItems());
        locFilter.addValueChangeListener(e -> loadItems());
        contextMenu = new ItemContextMenu<>(this, canInspect, false,
                originalCaption.contains(EDIT_COST), this::loadItems);
        contextMenu.setAllowBreaking(originalCaption.contains(BREAK));
        contextMenu.setAllowAssemble(originalCaption.contains(ASSEMBLE));
        contextMenu.setHideViewStock(true);
    }

    private static String caption(String caption) {
        if(caption == null) {
            return null;
        }
        caption = caption.replace(INSPECT, "").replace(EDIT_COST, "")
                .replace(BREAK, "").replace(ASSEMBLE, "")
                .replace(",", "").replace("||", "|").
                replace("_", "");
        caption = caption.trim();
        int p = caption.indexOf('|');
        return p < 0 ? caption : caption.substring(0, p).trim();
    }

    private static Class<? extends InventoryItem> itemClass(String caption) {
        Class<?> c = klass(caption);
        //noinspection unchecked
        return c != null && InventoryItem.class.isAssignableFrom(c) ? (Class<? extends InventoryItem>) c : null;
    }

    private static Class<? extends InventoryItemType> itemTypeClass(String caption) {
        Class<?> c = klass(caption);
        //noinspection unchecked
        return c != null && InventoryItemType.class.isAssignableFrom(c) ? (Class<? extends InventoryItemType>) c : null;
    }

    private static Class<? extends StoredObject> klass(String caption) {
        if(caption == null) {
            return null;
        }
        caption = caption.replace(INSPECT, "").replace(EDIT_COST, "")
                .replace(",", "").replace("||", "|");
        if(caption.endsWith("|")) {
            caption = caption.substring(0, caption.length() - 1);
        }
        int p = caption.indexOf('|');
        if(p < 0) {
            return null;
        }
        caption = caption.substring(p + 1).trim();
        try {
            Class<?> c = JavaClassLoader.getLogic(caption);
            if(StoredObject.class.isAssignableFrom(c)) {
                //noinspection unchecked
                return (Class<? extends StoredObject>) c;
            }
        } catch(ClassNotFoundException ignored) {
        }
        return null;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(pnField != null) {
            pnField.focus();
        } else if(snField != null) {
            snField.focus();
        }
    }

    @Override
    public void constructed() {
        super.constructed();
        getColumnDetail("Owner").setVisible(false);
        getColumnDetail("Cost").setVisible(false);
    }

    @Override
    public Component createHeader() {
        servFilter.setWidth("130px");
        locFilter.setWidth("155px");
        ButtonLayout b = new ButtonLayout(getConfigureButton(), servFilter, locFilter);
        if(snField != null) {
            b.add(new ELabel("Serial/Batch: "), snField);
        }
        if(pnField != null) {
            pnField.setItemLabelGenerator(iit -> iit.getPartNumber() + " - " + iit.getName());
            b.add(new ELabel("P/N: "), pnField);
        }
        b.add(new Button("Exit", e -> close()));
        return b;
    }

    @Override
    public void createFooters() {
        ButtonLayout b = new ButtonLayout(selectStore, help);
        appendFooter().join().setComponent(b);
        help.setVisible(!isEmpty());
    }

    @Override
    public void clear() {
        super.clear();
        help.setVisible(false);
    }

    @Override
    public void clean() {
        super.clean();
        clearAlerts();
    }

    public String getLocationDisplay(InventoryItem item) {
        return ItemContext.locationDisplay(item);
    }

    void loadItems() {
        clearAlerts();
        String sn = snField == null ? "" : StoredObject.toCode(snField.getValue());
        if(!sn.isEmpty()) {
            snField.setValue(sn);
        }
        if(pnField == null) {
            if(sn.isEmpty()) {
                clear();
                return;
            }
            InventoryItem item = StoredObject.list(itemClass, "SerialNumber='" + sn + "'", true)
                    .single(false);
            if(item != null) {
                loadInt(ObjectIterator.create(item));
                return;
            }
            loadInt(StoredObject.list(itemClass, "SerialNumber LIKE '" + sn + "%'", true)
                    .map(i -> (InventoryItem)i));
            if(isEmpty()) warning("S/N " + sn + " not found!");
            return;
        }
        InventoryItemType pn = pnField.getObject();
        if(pn == null) {
            clear();
            return;
        }
        ObjectIterator<InventoryItem> items = null;
        if(!sn.isEmpty()) {
            InventoryItem item = InventoryItem.get(sn, pn);
            if(item != null) {
                items = ObjectIterator.create(item);
            } else {
                String c = "PartNumber=" + pn.getId() + " AND SerialNumber LIKE '" + sn + "%'";
                Class<? extends InventoryItem> iClass = itemClass;
                if(iClass == null) {
                    iClass = pn.createItem().getClass();
                }
                items = StoredObject.list(iClass, c, true).limit(100).map(i -> i);
                for(InventoryItemType apn : pn.listAPNs()) {
                    c = "PartNumber=" + apn.getId() + " AND SerialNumber LIKE '" + sn + "%'";
                    items = items.add(StoredObject.list(iClass, c, true).limit(100).map(i -> i));
                }
                loadInt(items);
                if(isEmpty()) warning("S/N " + sn + " not found!");
                return;
            }
        }
        if(items == null) {
            items = InventoryItem.listItems(pn);
            for(InventoryItemType apn : pn.listAPNs()) {
                items = items.add(InventoryItem.listItems(apn));
            }
        }
        loadInt(items);
    }

    private void loadInt(ObjectIterator<InventoryItem> objects) {
        clear();
        if(store != null) {
            objects = objects.filter(ii -> {
                InventoryLocation loc = ii.getLocation();
                return loc instanceof InventoryBin && ((InventoryBin)loc).getStoreId().equals(store.getId());
            });
        }
        int filter = this.locFilter.getValue();
        switch(filter) {
            case 0 -> objects = objects.filter(ii -> ii.getLocation() instanceof InventoryBin);
            case 1 -> objects = objects.filter(ii -> {
                InventoryLocation loc = ii.getLocation();
                return !(loc instanceof InventoryBin || loc instanceof InventoryFitmentPosition);
            });
            case 2 -> objects = objects.filter(ii -> ii.getLocation() instanceof InventoryFitmentPosition);
        }
        switch(servFilter.getValue()) {
            case 0 -> objects = objects.filter(InventoryItem::isServiceable);
            case 1 -> objects = objects.filter(ii -> !ii.isServiceable());
        }
        objects = objects.filter(ii -> switch(ii.getLocation().getType()) {
            case 1, 2, 7, 9, 12, 15, 16, 17 -> false;
            default -> true;
        }).limit(501);
        AtomicInteger n = new AtomicInteger(0);
        objects.forEach(ii -> {
            if(n.getAndIncrement() <= 500) {
                add(ii);
            }
        });
        objects.close();
        help.setVisible(n.get() > 0);
        if(n.get() > 500) {
            warning("More than 500 items found but only 500 are displayed!");
        } else if(n.get() == 0) {
            message("No items found!");
        }
    }

    /**
     * Set a store so that the search is limited to the specified store.
     *
     * @param store Store.
     */
    public void setStore(InventoryStore store) {
        this.store = store;
        locFilter.setVisible(false);
        selectStore.setText(store.toDisplay());
        selectStore.setVisible(true);
    }

    /**
     * Allow breaking of assemblies.
     *
     * @param allowBreaking True/false.
     */
    public void setAllowBreaking(boolean allowBreaking) {
        contextMenu.setAllowBreaking(allowBreaking);
    }

    /**
     * Allow editing of cost. (By default, editing of cost is allowed in the inspection mode).
     *
     * @param allowEditCost True/false.
     */
    public void setAllowEditCost(boolean allowEditCost) {
        contextMenu.setAllowEditCost(allowEditCost);
    }

    /**
     * Sets whether inspection is allowed or not.
     *
     * @param allowInspection A boolean flag indicating whether inspection is allowed. (By default, it is not allowed).
     */
    public void setAllowInspection(boolean allowInspection) {
        contextMenu.setAllowInspection(allowInspection);
    }

    private void selectStore() {
        new SelectStore(s -> {
            setStore(s);
            loadItems();
        }).execute();
    }
}
