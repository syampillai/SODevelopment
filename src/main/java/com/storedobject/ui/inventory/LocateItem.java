package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DataGrid;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
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
    @SuppressWarnings("rawtypes")
    private ObjectEditor editor;
    private InventoryStore store;
    private boolean allowBreaking, allowEditCost;
    private final ELabel help = new ELabel("Right-click on the row to see more options", Application.COLOR_SUCCESS);
    private GRNEditor grnEditor;

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
        super(InventoryItem.class, StringList.concat(ItemField.COLUMNS, StringList.create(new String[] { "Cost" })));
        allowEditCost = canInspect;
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
        servFilter.addValueChangeListener(e -> loadItems());
        locFilter.addValueChangeListener(e -> loadItems());
        GridContextMenu<InventoryItem> cm = new GridContextMenu<>(this);
        cm.addItem("Item Details", e -> e.getItem().ifPresent(this::view));
        GridMenuItem<InventoryItem> itemAssembly =
                cm.addItem("Item Assembly", e -> e.getItem().ifPresent(ii -> new ViewAssembly<>(ii).execute()));
        GridMenuItem<InventoryItem> parentAssembly =
                cm.addItem("Parent Assembly", e -> e.getItem().ifPresent(
                        ii -> new ViewAssembly<>(((InventoryFitmentPosition)ii.getLocation()).getItem()).execute()));
        GridMenuItem<InventoryItem> viewFitment =
                cm.addItem("Fitment Details", e -> e.getItem().ifPresent(this::viewFitment));
        GridMenuItem<InventoryItem> viewFitmentLocations =
                cm.addItem("Fitment Locations", e -> e.getItem().ifPresent(FitmentLocations::new));
        GridMenuItem<InventoryItem> inspect =
                cm.addItem("Inspect & Bin", e -> e.getItem().ifPresent(this::inspect));
        GridMenuItem<InventoryItem> split =
                cm.addItem("Split Quantity", e -> e.getItem().ifPresent(this::split));
        GridMenuItem<InventoryItem> breakAssembly =
                cm.addItem("Break from Assembly", e -> e.getItem().ifPresent(this::breakAssembly));
        GridMenuItem<InventoryItem> movementDetail = cm.addItem("Movement Details",
                e -> e.getItem().ifPresent(i -> new ItemMovementView(i).execute()));
        cm.addItem("GRN Details", e -> e.getItem().ifPresent(this::viewGRN));
        cm.addItem("Cost Details", e -> e.getItem().ifPresent(item -> new EditCost(item, true).execute()));
        GridMenuItem<InventoryItem> editCost = cm.addItem("Edit Cost",
                e -> e.getItem().ifPresent(item -> {
                    close();
                    new EditCost(item, false, this::loadItems).execute();
                }));
        cm.setDynamicContentHandler(ii -> {
            deselectAll();
            if(ii == null) {
                return false;
            }
            select(ii);
            InventoryLocation loc = ii.getLocation();
            itemAssembly.setVisible(ii.getPartNumber().isAssembly());
            parentAssembly.setVisible(loc instanceof InventoryFitmentPosition);
            inspect.setVisible(canInspect || allowBreaking);
            split.setVisible(canInspect && !ii.isSerialized() && ii.getQuantity().isPositive()
                    && !ii.getQuantity().equals(Count.ONE));
            editCost.setVisible(allowEditCost);
            viewFitment.setVisible(loc instanceof InventoryFitmentPosition);
            viewFitmentLocations.setVisible(loc instanceof InventoryFitmentPosition);
            breakAssembly.setVisible((canInspect || allowBreaking) &&
                    loc instanceof InventoryFitmentPosition);
            movementDetail.setVisible(ii.isSerialized());
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
            b.add(new ELabel("S/N: "), snField);
        }
        if(pnField != null) {
            pnField.setItemLabelGenerator(InventoryItemType::getName);
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
            if(!sb.isEmpty()) {
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
        }).limit(500);
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

    private void split(InventoryItem ii) {
        close();
        new SplitQuantity(ii).execute();
    }

    private void breakAssembly(InventoryItem ii) {
        ELabel m = new ELabel(ii.toDisplay(), Application.COLOR_SUCCESS);
        m.newLine().append("Do you really want to take out this item?", Application.COLOR_ERROR).newLine();
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

    /**
     * Set a store so that the search is limited to the specified store.
     *
     * @param store Store.
     */
    public void setStore(InventoryStore store) {
        this.store = store;
        locFilter.setVisible(false);
    }

    /**
     * Allow breaking of assemblies.
     *
     * @param allowBreaking True/false.
     */
    public void setAllowBreaking(boolean allowBreaking) {
        this.allowBreaking = allowBreaking;
    }

    /**
     * Allow editing of cost. (By default, editing of cost is allowed in the inspection mode).
     *
     * @param allowEditCost True/false.
     */
    public void setAllowEditCost(boolean allowEditCost) {
        this.allowEditCost = allowEditCost;
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
            addField(new ELabelField("Item", item.toDisplay(), Application.COLOR_SUCCESS),
                    new ELabelField("Current location", getLocationDisplay(item), Application.COLOR_SUCCESS),
                    new ELabelField("After removal, it will be available at", location.toDisplay(), Application.COLOR_SUCCESS),
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

    private void viewFitment(InventoryItem item) {
        if(!(item.getLocation() instanceof InventoryFitmentPosition loc)) {
            return;
        }
        InventoryAssembly ia = loc.getAssembly();
        TextView tv = new TextView("Fitment Details");
        tv.append("Assembly Configuration: ").append(ia.toDisplay(), Application.COLOR_SUCCESS).newLine()
                .append("Fitted Item: ").append(item.toDisplay(), Application.COLOR_SUCCESS);
        if(!ia.getItemTypeId().equals(item.getPartNumberId())) {
            tv.append(" (APN)", Application.COLOR_INFO);
        }
        tv.newLine().append("Fitted on: ").append(loc.getItem().toDisplay(), Application.COLOR_SUCCESS);
        tv.popup();
    }

    private void viewGRN(InventoryItem item) {
        clearAlerts();
        InventoryGRN grn = item.getGRN();
        if(grn == null) {
            message("No associated GRN found for " + item.toDisplay());
            return;
        }
        if(grnEditor == null) {
            grnEditor = new GRNEditor();
        }
        grnEditor.viewObject(grn);
    }

    private static class FitmentLocations extends ObjectGrid<InventoryFitmentPosition> implements CloseableView {

        private final InventoryItem item;

        public FitmentLocations(InventoryItem item) {
            super(InventoryFitmentPosition.class);
            this.item = item;
            setCaption("Fitment Locations");
            load(item.listImmediateFitmentPositions());
            execute();
        }

        @Override
        public boolean includeColumn(String columnName) {
            return !"Item".equals(columnName);
        }

        @Override
        public Component createHeader() {
            return new ButtonLayout(new ELabel("Fitment Locations under ").append(item.toDisplay()).update(),
                    new Button("Exit", e -> close()).asSmall());
        }
    }

    private static class SplitQuantity extends DataForm implements Transactional {

        private final InventoryItem item;
        private final QuantityField splitQuantityField;
        private final Quantity quantity;

        public SplitQuantity(InventoryItem item) {
            super("Split Quantity");
            addField(new ELabelField("Item", item.toDisplay(), Application.COLOR_SUCCESS));
            this.item = item;
            quantity = item.getQuantity();
            QuantityField quantityField = new QuantityField("Current Quantity");
            quantityField.setValue(quantity);
            splitQuantityField = new QuantityField("Quantity to Split");
            splitQuantityField.setValue(quantity.zero());
            addField(quantityField, splitQuantityField);
            setFieldReadOnly(quantityField);
            setRequired(splitQuantityField);
        }

        @Override
        protected boolean process() {
            clearAlerts();
            Quantity q = splitQuantityField.getValue();
            try {
                if(!quantity.canConvert(q)) {
                    return false;
                }
            } catch(Invalid_State e) {
                warning(e);
                return false;
            }
            if(q.isGreaterThanOrEqual(quantity)) {
                warning("Invalid quantity specified");
                return false;
            }
            close();
            String reference = "Split";
            InventoryTransaction it = new InventoryTransaction(getTransactionManager(), DateUtility.today(), reference);
            it.splitQuantity(item, q, reference);
            if(transact(it::save)) {
                message("Quantity split successfully");
            }
            return true;
        }
    }
}
