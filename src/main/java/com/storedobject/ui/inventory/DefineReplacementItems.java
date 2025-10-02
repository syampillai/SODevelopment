package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Receive material from another location.
 *
 * @author Syam
 */
public class DefineReplacementItems extends DataForm {

    private static final String[] TYPE_4_STORES = new String[] {
            "Repair/Maintenance Organizations",
            "Lease/Loan/Rental Out Locations",
            "Tool Custodians",
    };
    private boolean valuesSet = false;
    private final LocationField sentFromField;
    private final ChoiceField typeFieldForStores = new ChoiceField("Type", TYPE_4_STORES);

    /**
     * Constructor.
     */
    public DefineReplacementItems() {
        this(null, null, -1);
    }

    /**
     * Constructor.
     *
     * @param locationName Name of the location.
     */
    public DefineReplacementItems(String locationName) {
        this(ParameterParser.location(locationName, true, 0),
                ParameterParser.number(locationName));
    }

    /**
     * Constructor.
     *
     * @param fromStore Location fromStore.
     */
    public DefineReplacementItems(InventoryLocation fromStore) {
        this(fromStore, -1);
    }

    /**
     * Constructor.
     *
     * @param fromStore Location fromStore.
     * @param type Type of receipt.
     */
    public DefineReplacementItems(InventoryLocation fromStore, int type) {
        this(null, fromStore, type);
    }

    private DefineReplacementItems(LocationField sentFromField, InventoryLocation fromStore, int type) {
        super("Define Replacement Items / Quantities");
        if(sentFromField == null) {
            if(fromStore == null) {
                this.sentFromField = LocationField.create("Items Sent from", 0);
            } else {
                this.sentFromField = LocationField.create("Items Sent from", fromStore);
                setFieldReadOnly(this.sentFromField);
            }
            this.sentFromField.setValue((Id)null);
        } else {
            this.sentFromField = sentFromField;
            if(fromStore != null) {
                this.sentFromField.setValue(fromStore);
                setFieldReadOnly(this.sentFromField);
            }
        }
        addField(this.sentFromField, typeFieldForStores);
        setRequired(this.sentFromField);
        toChanged(this.sentFromField.getValue());
        this.sentFromField.addValueChangeListener(e -> toChanged(e.getValue()));
        if(type >= 0 && fromStore != null) {
            if(fromStore instanceof InventoryStoreBin) {
                if(type < TYPE_4_STORES.length) {
                    typeFieldForStores.setValue(type);
                    typeFieldForStores.setReadOnly(true);
                    valuesSet = true;
                } else {
                    showTypeError(type);
                }
            } else {
                throw new SORuntimeException("Not a store - " + fromStore.toDisplay());
            }
        }
    }

    private void showTypeError(int type) {
        HTMLText h = new HTMLText("Incorrect type value: " + type);
        h.newLine().append("Please set one of the following values.");
        for(int i = 0; i < TYPE_4_STORES.length; i++) {
            h.newLine().append(i + ": " + TYPE_4_STORES[i]);
        }
        throw new SORuntimeException(h.getHTML());
    }

    private void toChanged(InventoryLocation location) {
        boolean isStore = location instanceof InventoryStoreBin;
        typeFieldForStores.setVisible(isStore);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(valuesSet) {
            process();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        InventoryLocation fromStore = sentFromField.getValue();
        if(fromStore == null) {
            return false;
        }
        if(fromStore instanceof InventoryStoreBin) {
            close();
            switch(typeFieldForStores.getValue()) {
                case 0 -> new ReplacementItems(3, (InventoryStoreBin) fromStore).execute();
                case 1 -> new ReplacementItems(8, (InventoryStoreBin) fromStore).execute();
                case 2 -> new ReplacementItems(18, (InventoryStoreBin) fromStore).execute();
            }
            return true;
        } else {
            warning("Not a store - " + fromStore.toDisplay());
            return false;
        }
    }

    static class ReplacementItems extends HandleReturnedItems {

        ReplacementItems(int type, InventoryStoreBin storeBin) {
            this(type, storeBin, null);
        }

        ReplacementItems(int type, InventoryStoreBin storeBin, InventoryLocation eo) {
            super(caption(type), type, storeBin, eo, false);
        }

        private static String caption(int type) {
            String caption = switch(type) {
                case 3 -> "Replace Repaired Items";
                case 8 -> "Replace Loan/Rent/Lease Items";
                case 18 -> "Replace Tools Issued";
                default -> null;
            };
            if(caption == null) {
                throw new SORuntimeException("Invalid type: " + InventoryLocation.getTypeValue(type));
            }
            return caption;
        }

        @Override
        protected void processOld() {
        }

        @Override
        protected void proceed(List<InventoryItem> items) {
            new ReplacementGrid(items, eo, getTransactionManager()).execute();
        }
    }

    static class ReplacementGrid extends ObjectListGrid<InventoryItem> implements CloseableView {

        private final List<InventoryItem> items;
        private final InventoryVirtualLocation eo;
        private final InventoryLocation consumption;
        private final TransactionManager tm;
        private final String title;

        public ReplacementGrid(List<InventoryItem> items, InventoryLocation eo, TransactionManager tm) {
            super(InventoryItem.class, StringList.create("PartNumber", "SerialNumberDisplay AS Serial Number", "Quantity", "Location"), true);
            this.tm = tm;
            this.eo = (InventoryVirtualLocation) eo;
            Entity entity = this.eo.getEntity();
            this.consumption = InventoryTransaction.createConsumptionLocation(tm, entity);
            title = entity.getName();
            setCaption("Items at " + title);
            StoredObject.list(MaterialReturned.class,
                    "FromLocation=" + eo.getId() + " AND Status<2", true).forEach(r ->
                    r.listLinks(MaterialReturnedItem.class, true)
                            .map(InventoryTransferItem::getItem)
                            .filter(Objects::nonNull)
                            .forEach(items::remove));
            this.items = items;
            load(items);
            ItemContextMenu<InventoryItem> m = new ItemContextMenu<>(this);
            m.addItem("Replace Item - ", e -> e.getItem().ifPresent(this::replace));
            var markAsConsumed = m.addItem("Mark as Consumed - ", e -> e.getItem().ifPresent(this::markAsConsumed));
            var splitQuantity = m.addItem("Consume Fully/Partially - ", e -> e.getItem().ifPresent(this::splitQuantity));
            var assembly = m.addItem("Manage Assembly - ", e -> e.getItem().ifPresent(this::assembly));
            m.setDynamicContentHandler(i -> {
                markAsConsumed.setVisible(i.isSerialized());
                splitQuantity.setVisible(!i.isSerialized());
                assembly.setVisible(i.getPartNumber().isAssembly());
                return true;
            });
        }

        @Override
        public Component createHeader() {
            ButtonLayout b = new ButtonLayout();
            b.add(
                    new Button("Create New Item", VaadinIcon.PLUS, e -> createNewItem()),
                    new Button("Exit", e -> close())
            );
            return b;
        }

        @Override
        public void createHeaders() {
            prependHeader().join().setComponent(new ButtonLayout(
                    new ELabel("Set Replacement Items at " + title, Application.COLOR_SUCCESS),
                    new ELabel(" | ", Application.COLOR_INFO),
                    new ELabel("Right-click on the respective row to see options", Application.COLOR_SUCCESS)
            ));
        }

        private void replace(InventoryItem item) {
            new ItemReplacementForm(item).execute();
        }

        private void markAsConsumed(InventoryItem item) {
            clearAlerts();
            new ActionForm("Item: " + item.toDisplay()
                    + "\nwill be marked as consumed!"
                    + "\nThis step cannot be undone. Are you sure?",
                    () -> consume(item, item.getQuantity())).execute();
        }

        private void splitQuantity(InventoryItem item) {
            new ConsumptionForm(item).execute();
        }

        private void createNewItem() {
            new CreateNewItemForm().execute();
        }

        private void createNewItem(InventoryItemType pn, String sn, Quantity quantity) {
            InventoryItem item = pn.createItem(sn);
            item.setQuantity(quantity);
            item.setLocation(eo);
            @SuppressWarnings("rawtypes") ObjectEditor itemEditor;
            itemEditor = ObjectEditor.create(item.getClass());
            itemEditor.setCaption("Create New Item - " + StringUtility.makeLabel(item.getPartNumber().getClass()));
            itemEditor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber");
            if(pn.isSerialized()) {
                itemEditor.setFieldReadOnly("SerialNumber");
            }
            //noinspection unchecked
            itemEditor.setSaver(o -> saveItem(itemEditor));
            //noinspection unchecked
            itemEditor.editObject(item, getView());
        }

        private boolean saveItem(@SuppressWarnings("rawtypes") ObjectEditor itemEditor) {
            if(transact(itemEditor::save)) {
                InventoryItem item = (InventoryItem) itemEditor.getObject();
                itemEditor.close();
                addItem(item);
                return true;
            }
            return false;
        }

        private void assembly(InventoryItem item) {
            clearAlerts();
            warning("Not implemented yet!");
        }

        private void addItem(InventoryItem item) {
            items.addFirst(item);
            load(items);
            select(item);
            scrollToStart();
        }

        private void removeItem(InventoryItem item) {
            items.remove(item);
            load(items);
            scrollToStart();
        }

        private void consume(InventoryItem item, Quantity consumed) {
            clearAlerts();
            try {
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today(), "Consumed");
                it.moveTo(item, consumed,null, consumption);
                it.save();
                message(item.toDisplay() + " - Item marked as consumed! Consumed: " + consumed);
                removeItem(item);
            } catch (Throwable e) {
                warning(e);
            }
        }

        private void replace(InventoryItem item, InventoryItemType pn, String sn) {
            clearAlerts();
            InventoryItem newItem = pn.createItem(sn);
            try {
                newItem.loadAttributesFrom(item);
                newItem.setSerialNumber(sn);
                newItem.setGRN((Id)null);
                newItem.setPurchaseDate(DateUtility.today());
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today(), "Replaced");
                tm.transact(t -> {
                    it.moveTo(item,null, consumption);
                    newItem.save(t);
                    it.save(t);
                });
                message(item.toDisplay() + " - Item replaced successfully!");
                items.remove(item);
                addItem(newItem);
            } catch (Throwable e) {
                warning(e);
            }
        }

        private void consume(InventoryItem item, InventoryItemType pn, String sn, Quantity toReturn) {
            Quantity q = item.getQuantity();
            Quantity consumed = toReturn.isZero() ? q : q.subtract(toReturn);
            if(toReturn.isZero() && item.getPartNumberId().equals(pn.getId()) && item.getSerialNumber().equals(sn)) {
                consume(item, consumed);
                return;
            }
            String itemDisplay = item.toDisplay();
            clearAlerts();
            InventoryItem newItem = pn.createItem(sn);
            try {
                newItem.loadAttributesFrom(item);
                newItem.setSerialNumber(sn);
                newItem.setQuantity(toReturn);
                newItem.setGRN((Id)null);
                newItem.setPurchaseDate(DateUtility.today());
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today(), "Consumed");
                tm.transact(t -> {
                    it.moveTo(item, consumed, null, consumption);
                    newItem.save(t);
                    it.save(t);
                });
                message(itemDisplay + " - Item marked as consumed! Consumed: " + consumed + ", To Return: " + toReturn);
                items.remove(item);
                items.addFirst(newItem);
                addItem((InventoryItem) item.reload());
            } catch (Throwable e) {
                warning(e);
            }
        }

        protected abstract class ItemForm extends DataForm {

            final InventoryItem item;

            ItemForm(String caption, InventoryItem item) {
                super(caption + " at " + title);
                this.item = item;
                addField(new ELabelField("Item", item.toDisplay(), Application.COLOR_SUCCESS));
                if(!item.isSerialized()) {
                    addField(new ELabelField("Quantity", item.getQuantity(), Application.COLOR_SUCCESS));
                }
            }
        }

        protected abstract class AbstractReplacementForm extends ItemForm {

            final ObjectComboField<InventoryItemType> pnField;
            final TextField snField;

            AbstractReplacementForm(String caption, InventoryItem item) {
                super(caption, item);
                List<InventoryItemType> pns = new ArrayList<>();
                pns.add(item.getPartNumber());
                pns.addAll(item.getPartNumber().listAPNs());
                pnField = new ObjectComboField<>("Part Number or APN", pns);
                addField(pnField);
                pnField.setValue(item.getPartNumber());
                if(pns.size() == 1) setFieldReadOnly(pnField);
                snField = new TextField(item.isSerialized() ? "Serial Number" : "Batch/Lot Number");
                snField.uppercase();
                snField.addValueChangeListener(e -> {
                    if(e.isFromClient()) snField.setValue(StoredObject.toCode(e.getValue()));
                });
                if(!item.isSerialized()) {
                    snField.setValue(item.getSerialNumber());
                }
                addField(snField);
                setRequired(snField);
                setRequired(pnField);
            }
        }

        private class ItemReplacementForm extends AbstractReplacementForm {

            ItemReplacementForm(InventoryItem item) {
                super("Replace Item", item);
            }

            @Override
            protected boolean process() {
                clearAlerts();
                String sn = snField.getValue();
                InventoryItemType pn = pnField.getValue();
                String w = get(pn, sn);
                if (w != null) {
                    warning(w);
                    return false;
                }
                close();
                new ActionForm("Item: " + item.toDisplay()
                        + "\nwill be replaced with P/N: " + pn.getPartNumber() + ", S/N: " + sn
                        + "\nThis step cannot be undone. Are you sure?",
                        () -> replace(item, pn, sn)).execute();
                return true;
            }
        }

        private class ConsumptionForm extends AbstractReplacementForm {

            private final QuantityField returnField = new QuantityField("Returning Quantity after Consumption");

            ConsumptionForm(InventoryItem item) {
                super("Consumption", item);
                addField(returnField);
                returnField.setValue(item.getQuantity());
            }

            @Override
            protected boolean process() {
                clearAlerts();
                Quantity returnQuantity = returnField.getValue();
                if(returnQuantity.isGreaterThan(item.getQuantity())) {
                    warning("Returning quantity cannot be greater than the existing quantity!");
                    return false;
                }
                String sn = snField.getValue();
                InventoryItemType pn = pnField.getValue();
                close();
                new ActionForm("Item: " + item.toDisplay()
                        + "\nConsumed: " + item.getQuantity().subtract(returnQuantity)
                        + ", Returning quantity: " + returnQuantity
                        + "\nReturning P/N: " + pn.getPartNumber() + ", B/N: " + sn
                        + "\nThis step cannot be undone. Are you sure?",
                        () -> consume(item, pn, sn, returnQuantity)).execute();
                return true;
            }
        }

        private class CreateNewItemForm extends DataForm {

            private final ItemTypeGetField<InventoryItemType> pnField = new ItemTypeGetField<>("Part Number", InventoryItemType.class, true);
            private final TextField snField = new TextField("Serial/Batch/Lot Number");
            private final QuantityField quantityField = new QuantityField("Quantity");

            public CreateNewItemForm() {
                super("Create New Item at " + title);
                addField(pnField);
                addField(snField);
                addField(quantityField);
                snField.uppercase();
                snField.addValueChangeListener(e -> {
                    if(e.isFromClient()) snField.setValue(StoredObject.toCode(e.getValue()));
                });
                pnField.addValueChangeListener(e -> {
                    if(e.isFromClient()) {
                        InventoryItemType pn = pnField.getValue();
                        if(pn == null) {
                            snField.setLabel("Serial/Batch/Lot Number");
                            pnField.focus();
                            return;
                        }
                        snField.setLabel(pn.isSerialized() ? "Serial Number" : "Batch/Lot Number");
                        snField.focus();
                        if(pn.isSerialized()) {
                            quantityField.setValue(Count.ONE);
                            setFieldReadOnly(quantityField);
                        } else {
                            setFieldReadOnly(false, quantityField);
                            Quantity q = quantityField.getValue();
                            if(!pn.getUnitOfMeasurement().isCompatible(q)) {
                                quantityField.setValue(pn.getUnitOfMeasurement());
                            }
                        }
                    }
                });
                setRequired(pnField);
                setRequired(snField);
                setRequired(quantityField);
            }

            @Override
            protected boolean process() {
                clearAlerts();
                String sn = snField.getValue();
                InventoryItemType pn = pnField.getValue();
                if(pn.isSerialized()) {
                    String w = get(pn, sn);
                    if (w != null) {
                        warning(w);
                        return false;
                    }
                }
                Quantity quantity = quantityField.getValue();
                if(!pn.getUnitOfMeasurement().isCompatible(quantity)) {
                    warning("Quantity is not compatible with the unit of measurement of the part number!");
                    return false;
                }
                close();
                String snName = pn.isSerialized() ? "SerialNumber" : "Batch/Lot Number";
                new ActionForm("A new item will be created at " + title
                        + "\nP/N: " + pn.getPartNumber() + ", " + snName + ": " + sn + ", Quantity: " + quantity
                        + "\nThis step cannot be undone. Are you sure?",
                        () -> createNewItem(pn, sn, quantity)).execute();
                return true;
            }
        }

        private static String get(InventoryItemType pn, String sn) {
            InventoryItem ii = StoredObject.list(InventoryItem.class,
                            "PartNumber=" + pn.getId() + " AND SerialNumber='" + sn + "'", true)
                    .filter(i -> !i.getLocation().canResurrect()).findFirst();
            return ii == null ? null : ("Such an item already exists at " + ii.getLocation().toDisplay());
        }
    }
}
