package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.DataGrid;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * For locating stocks of a given "Part Number" and its APNs. The result includes all locations, including items
 * fitted on assemblies, items sent for repair etc. However, items that are already scrapped are not included.
 *
 * @author Syam
 */
public class LocateItem extends DataGrid<InventoryItem> implements CloseableView {

    private static final String INSPECT = "INSPECT";
    private final ChoiceField filter = new ChoiceField(new String[] {
            "Stores only",
            "Other locations",
            "Fitted on assembly",
            "Everywhere"
    });
    private final Class<? extends InventoryItem> itemClass;
    private final ObjectField<? extends InventoryItemType> pnField;
    private final TextField snField;
    private final Checkbox serviceableOnly = new Checkbox("Serviceable");
    @SuppressWarnings("rawtypes")
    private ObjectEditor editor;
    private InventoryStore store;
    private boolean allowBreaking;
    private final ELabel help = new ELabel("Right-click on the row to see more options", "blue");

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
        this(caption(caption), null, itemClass(caption), itemTypeClass(caption),
                caption != null && caption.contains(INSPECT), caption);
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
        super(InventoryItem.class, StringList.create(ItemField.COLUMNS));
        setCaption(caption == null || caption.isEmpty() ? "Items" : caption);
        if(originalCaption != null && originalCaption.contains("|")) {
            caption = originalCaption.substring(originalCaption.indexOf('|') + 1).
                    replace(INSPECT, "").replace("|", "");
            if(!caption.isEmpty() && itemClass == null && itemTypeClass == null) {
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
        serviceableOnly.setValue(true);
        serviceableOnly.addValueChangeListener(e -> loadItems());
        filter.addValueChangeListener(e -> loadItems());
        GridContextMenu<InventoryItem> cm = new GridContextMenu<>(this);
        cm.addItem("View Details", e -> e.getItem().ifPresent(this::view));
        GridMenuItem<InventoryItem> inspect =
                cm.addItem("Inspect & Bin", e -> e.getItem().ifPresent(this::inspect));
        GridMenuItem<InventoryItem> breakAssembly =
                cm.addItem("Break from Assembly", e -> e.getItem().ifPresent(this::breakAssembly));
        GridMenuItem<InventoryItem> movementReport = cm.addItem("Movement Detail",
                e -> e.getItem().ifPresent(i -> new ItemMovementView(i).execute()));
        cm.setDynamicContentHandler(ii -> {
            deselectAll();
            if(ii == null) {
                return false;
            }
            select(ii);
            inspect.setVisible(canInspect || allowBreaking);
            breakAssembly.setVisible((canInspect || allowBreaking) &&
                    ii.getLocation() instanceof InventoryFitmentPosition);
            movementReport.setVisible(ii.isSerialized());
            return true;
        });
    }

    private static String caption(String caption) {
        if(caption == null) {
            return null;
        }
        caption = caption.replace(INSPECT, "").replace("||", "|").
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
        caption = caption.replace(INSPECT, "").replace("||", "|");
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
    public Component createHeader() {
        ButtonLayout b = new ButtonLayout(new ELabel("Filter:"), serviceableOnly, filter);
        if(snField != null) {
            b.add(new ELabel("S/N: "), snField);
        }
        if(pnField != null) {
            b.add(new ELabel("P/N: "), pnField);
        }
        b.add(new Button("Exit", e -> close()));
        return b;
    }

    @Override
    public void createFooters() {
        appendFooter().join().setComponent(help);
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

    public static String getLocationDisplay(InventoryItem item) {
        InventoryLocation loc = item.getLocation();
        if(!(loc instanceof InventoryFitmentPosition)) {
            return item.getLocationDisplay();
        }
        InventoryLocation location;
        String s;
        StringBuilder sb = new StringBuilder();
        while(item != null) {
            location = item.getLocation();
            s = location instanceof InventoryFitmentPosition ?
                    ((InventoryFitmentPosition) location).toDisplay(false) :
                    item.getLocationDisplay();
            if(sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(s);
            if(!(location instanceof InventoryFitmentPosition)) {
                break;
            }
            item = item.getParentItem();
        }
        return sb.toString();
    }

    private void loadItems() {
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
                loadInt(items);
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

    private void loadInt(ObjectIterator<InventoryItem> objects) {
        clear();
        if(store != null) {
            objects = objects.filter(ii -> {
                InventoryLocation loc = ii.getLocation();
                return loc instanceof InventoryBin && ((InventoryBin)loc).getStoreId().equals(store.getId());
            });
        }
        int filter = this.filter.getValue();
        switch(filter) {
            case 0 -> objects = objects.filter(ii -> ii.getLocation() instanceof InventoryBin);
            case 1 -> objects = objects.filter(ii -> {
                InventoryLocation loc = ii.getLocation();
                return !(loc instanceof InventoryBin || loc instanceof InventoryFitmentPosition);
            });
            case 2 -> objects = objects.filter(ii -> ii.getLocation() instanceof InventoryFitmentPosition);
        }
        if(serviceableOnly.getValue()) {
            objects = objects.filter(InventoryItem::isServiceable);
        }
        objects = objects.limit(500);
        objects.forEach(this::add);
        objects.close();
        help.setVisible(!isEmpty());
    }

    private void inspect(InventoryItem ii) {
        close();
        List<InventoryItem> list = new ArrayList<>();
        list.add(ii);
        new ReceiveAndBin(list).execute();
    }

    private void breakAssembly(InventoryItem ii) {
        ELabel m = new ELabel(ii.toDisplay(), "blue");
        m.newLine().append("Do you really want to take out this item?", "red").newLine();
        m.append("Fitment location:").newLine();
        m.append(getLocationDisplay(ii));
        deselectAll();
        new ActionForm(m.update(), () -> new DetachFromAssembly(ii).execute()).execute();
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

    public void setStore(InventoryStore store) {
        this.store = store;
        filter.setVisible(false);
    }

    public void setAllowBreaking(boolean allowBreaking) {
        this.allowBreaking = allowBreaking;
    }

    private class DetachFromAssembly extends DataForm implements Transactional {

        private final InventoryItem item;
        private final DateField dateField = new DateField("Date");
        private final TextField referenceField = new TextField("Reference");
        private final InventoryLocation location;

        public DetachFromAssembly(InventoryItem item) {
            super("Detach from Assembly");
            this.item = item;
            location = item.getRealLocation();
            addField(new ELabelField("Item", item.toDisplay(), "blue"),
                    new ELabelField("Current location", getLocationDisplay(item), "blue"),
                    new ELabelField("After removal, it will be available at", location.toDisplay(), "blue"),
                    dateField, referenceField);
            setRequired(referenceField);
        }

        @Override
        protected boolean process() {
            Date d = dateField.getValue();
            if(!d.before(DateUtility.tomorrow())) {
                warning("Invalid date!");
                dateField.focus();
                return false;
            }
            String ref = referenceField.getValue().trim();
            if(ref.isEmpty()) {
                referenceField.setValue("");
                referenceField.focus();
                return false;
            }
            close();
            InventoryTransaction it = new InventoryTransaction(getTransactionManager(), dateField.getValue());
            it.moveTo(item, ref, location);
            if(transact(it::save)) {
                message("Item '" + item.toDisplay() +
                        "' is removed from the assembly and is available at '" + location + "' now!");
                loadItems();
            }
            LocateItem.this.select(item);
            return true;
        }
    }
}
