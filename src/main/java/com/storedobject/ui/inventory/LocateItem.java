package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.stream.Stream;

/**
 * For locating stocks of a given "Part Number" and its APNs. The result includes all locations, including items
 * fitted on assemblies, items sent for repair etc. However, items that are already scrapped are not included.
 *
 * @author Syam
 */
public class LocateItem extends ListGrid<InventoryItem> implements CloseableView {

    private final Class<? extends InventoryItem> itemClass;
    private final ObjectField<InventoryItemType> pnField;
    private final TextField snField;
    private final Button inspect;
    @SuppressWarnings("rawtypes")
    private ObjectEditor editor;

    /**
     * Constructor.
     */
    public LocateItem() {
        this((String)null);
    }

    /**
     * Constructor.
     *
     * @param caption This could be "Caption", "Caption|Class Name", "Caption|INSPECT" or "Caption|Class Name|INSPECT".
     */
    public LocateItem(String caption) {
        this(caption(caption), null, itemClass(caption),
                caption != null && caption.contains("INSPECT"));
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
     * @param canInspect Whether the item details can be inspected/re-binned nor not.
     */
    public LocateItem(boolean canInspect) {
        this(null, null, null, canInspect);
    }

    /**
     * Constructor.
     *
     * @param caption Caption.
     * @param canInspect Whether the item details can be inspected/re-binned nor not.
     */
    public LocateItem(String caption, boolean canInspect) {
        this(caption(caption), null, itemClass(caption), canInspect);
    }

    /**
     * Constructor.
     *
     * @param partNumber Part number.
     * @param canInspect Whether the item details can be inspected/re-binned nor not.
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
     * @param canInspect Whether the item details can be inspected/re-binned nor not.
     */
    public LocateItem(String caption, InventoryItemType partNumber, boolean canInspect) {
        this(caption, partNumber, null, canInspect);
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
     * @param canInspect Whether the item details can be inspected/re-binned nor not.
     */
    public LocateItem(String caption, Class<? extends InventoryItem> itemClass, boolean canInspect) {
        this(caption, null, itemClass, canInspect);
    }

    private LocateItem(String caption, InventoryItemType partNumber, Class<? extends InventoryItem> itemClass, boolean canInspect) {
        super(InventoryItem.class, StringList.create(ItemField.COLUMNS));
        this.itemClass = itemClass;
        setCaption(caption == null || caption.isEmpty() ? "Items" : caption);
        if(itemClass == null) {
            pnField = new ObjectField<>(InventoryItemType.class, true);
        } else {
            pnField = null;
        }
        if(partNumber != null && pnField != null) {
            snField = null;
            pnField.setValue(partNumber);
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
        if(canInspect) {
            inspect = new Button("Inspect / Rebin", VaadinIcon.STORAGE, e -> inspect());
            inspect.setVisible(false);
        } else {
            inspect = null;
        }
        addItemDoubleClickListener(e -> view(e.getItem()));
    }

    private static String caption(String caption) {
        if(caption == null) {
            return null;
        }
        caption = caption.replace("INSPECT", "").replace("||", "|");
        caption = caption.trim();
        int p = caption.indexOf('|');
        return p < 0 ? caption : caption.substring(0, p).trim();
    }

    private static Class<? extends InventoryItem> itemClass(String caption) {
        if(caption == null) {
            return null;
        }
        caption = caption.replace("INSPECT", "").replace("||", "|");
        int p = caption.indexOf('|');
        if(p < 0) {
            return null;
        }
        caption = caption.substring(p + 1).trim();
        try {
            Class<?> c = JavaClassLoader.getLogic(caption);
            if(InventoryItem.class.isAssignableFrom(c)) {
                //noinspection unchecked
                return (Class<? extends InventoryItem>) c;
            }
        } catch(ClassNotFoundException ignored) {
        }
        return null;
    }

    @Override
    public Component createHeader() {
        ButtonLayout b = new ButtonLayout(inspect);
        if(snField != null) {
            b.add(new ELabel("Serial Number"), snField);
        }
        if(pnField != null) {
            b.add(new ELabel("Part Number: "), pnField);
        }
        return b;
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

    public String getLocationDisplay(InventoryItem item) {
        String s = item.getLocationDisplay();
        int p = s.indexOf(" \u21D0 ");
        if(p > 0) {
            s = s.substring(0, p);
        }
        return s;
    }

    private void loadItems() {
        String sn = snField == null ? "" : StoredObject.toCode(snField.getValue());
        if(!sn.isEmpty()) {
            snField.setValue(sn);
        }
        if(pnField == null) {
            if(sn.isEmpty()) {
                clear();
                return;
            }
            InventoryItem item = StoredObject.list(itemClass, "SerialNumber='" + sn + "'", true).single(false);
            if(item != null) {
                loadInt(ObjectIterator.create(item));
                return;
            }
            loadInt(StoredObject.list(itemClass, "SerialNumber LIKE '" + sn + "%'", true).map(i -> (InventoryItem)i).stream().limit(500));
            warning("S/N " + sn + " not found!");
            return;
        }
        InventoryItemType pn = pnField.getObject();
        if(pn == null) {
            clear();
            return;
        }
        ObjectIterator<InventoryItem> items = null;
        if(!sn.isEmpty()) {
            InventoryItem item = InventoryItem.getByPartNumberId(sn, pn.getId());
            if(item != null) {
                items = ObjectIterator.create(item);
            } else {
                String c = "PartNumber=" + pn.getId() + " AND SerialNumber LIKE '" + sn + "%'";
                Class<? extends InventoryItem> iClass = itemClass;
                if(iClass == null) {
                    iClass = InventoryItem.class;
                }
                items = StoredObject.list(iClass, c, true).limit(100).map(i -> i);
                for(InventoryItemType apn : pn.listAPNs()) {
                    c = "PartNumber=" + apn.getId() + " AND SerialNumber LIKE '" + sn + "%'";
                    items = items.add(StoredObject.list(iClass, c, true).limit(100).map(i -> i));
                }
                loadInt(items.limit(500));
                warning("S/N " + sn + " not found!");
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

    private void loadInt(Stream<InventoryItem> objects) {
        clear();
        objects.forEach(this::add);
        if(inspect != null) {
            inspect.setVisible(!isEmpty());
        }
    }

    private void loadInt(ObjectIterator<InventoryItem> objects) {
        loadInt(objects.stream());
    }

    @Override
    public void clear() {
        if(inspect != null) {
            inspect.setVisible(false);
        }
        super.clear();
    }

    private void inspect() {
        close();
        new ReceiveAndBin(this).execute();
    }

    private void view(InventoryItem item) {
        if(item != null) {
            //noinspection unchecked
            editor(item).viewObject(item);
        }
    }

    @SuppressWarnings("rawtypes")
    private ObjectEditor editor(InventoryItem item) {
        if(editor != null && editor.getObjectClass() != item.getClass()) {
            editor = null;
        }
        if(editor == null) {
            editor = ObjectEditor.create(item.getClass());
        }
        return editor;
    }
}
