package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        super("Define Replacements / Consumption");
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
            super(caption(type), type, storeBin, null, false, false);
        }

        ReplacementItems(int type, InventoryStoreBin storeBin, InventoryLocation eo) {
            super(caption(type), type, storeBin, eo, false, true);
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
            new ReplacementGrid(items, eo, getTransactionManager(), storeBin).execute();
        }
    }

    static class ReplacementGrid extends ObjectListGrid<InventoryItem> implements CloseableView {

        private final List<InventoryItem> items;
        private final InventoryStoreBin storeBin;
        private final InventoryVirtualLocation eo;
        private final InventoryLocation consumption;
        private final TransactionManager tm;
        private final String title;

        public ReplacementGrid(List<InventoryItem> items, InventoryLocation eo, TransactionManager tm, InventoryStoreBin storeBin) {
            super(InventoryItem.class, StringList.create("PartNumber", "SerialNumberDisplay AS Serial Number", "Quantity", "Location"), true);
            this.tm = tm;
            this.storeBin = storeBin;
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
            var markAsConsumed = m.addItem("Mark as Consumed - ", e -> e.getItem().ifPresent(this::consume));
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
            Button switchOrg = new Button(title, (String)null, e -> {
                close();
                new ReceiveReturnedItems(eo.getType(), storeBin, eo, false).execute();
            }).asSmall();
            switchOrg.getElement().setAttribute("title", "Click to change");
            prependHeader().join().setComponent(new ButtonLayout(
                    new ELabel("Define Replacements / Consumption at ", Application.COLOR_SUCCESS),
                    switchOrg,
                    new ELabel(" | ", Application.COLOR_INFO),
                    new ELabel("Right-click on the respective row to see options", Application.COLOR_SUCCESS)
            ));
        }

        private void replace(InventoryItem item) {
            new ItemReplacementForm(item).execute();
        }

        private void markAsConsumed(InventoryItem item, String remarks) {
            clearAlerts();
            new ActionForm("Item: " + item.toDisplay()
                    + "\nwill be marked as consumed!"
                    + "\nRemarks: " + remarks
                    + "\nThis step cannot be undone. Are you sure?",
                    () -> consume(item, item.getQuantity(), remarks)).execute();
        }

        private void splitQuantity(InventoryItem item) {
            new ConsumptionForm(item).execute();
        }

        private void consume(InventoryItem item) {
            new SerializedConsumptionForm(item).execute();
        }

        private void createNewItem() {
            new CreateNewItemForm().execute();
        }

        private void createNewItem(InventoryItemType pn, String sn, Quantity quantity, String remarks) {
            InventoryItem item = getItem(pn, sn);
            if(item == null) item = pn.createItem(sn);
            if(item == null) {
                warning("Unable to create item with P/N = " + pn.getPartNumber() + ", S/N = " + sn);
                return;
            }
            item.setQuantity(quantity);
            item.setLocation(eo);
            item.setOwner(eo.getEntityId());
            @SuppressWarnings("rawtypes") ObjectEditor itemEditor;
            itemEditor = ObjectEditor.create(item.getClass());
            itemEditor.setCaption("Create New Item - " + StringUtility.makeLabel(item.getPartNumber().getClass()));
            itemEditor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber");
            if(pn.isSerialized()) {
                itemEditor.setFieldReadOnly("SerialNumber");
            }
            //noinspection unchecked
            itemEditor.setSaver(o -> saveItem(itemEditor, remarks));
            //noinspection unchecked
            itemEditor.editObject(item, getView());
        }

        private boolean saveItem(@SuppressWarnings("rawtypes") ObjectEditor itemEditor, String remarks) {
            if(transact(itemEditor::save)) {
                InventoryItem item = (InventoryItem) itemEditor.getObject();
                itemEditor.close();
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today(), remarks(remarks, "Created"));
                it.changeOwner(item, null, StoredObject.get(Entity.class, storeBin.getStore().getSystemEntity().getEntityId()));
                try {
                    it.save();
                    item = StoredObject.get(item.getClass(), item.getId());
                } catch (Exception e) {
                    tm.log(e);
                    error("Unable to change ownership of " + item.toDisplay());
                    return false;
                }
                addItem(item);
                return true;
            }
            return false;
        }

        private void assembly(InventoryItem item) {
            clearAlerts();
            items.remove(item);
            ManageAssembly<?, ?> a = new ManageAssembly<>(item);
            a.setExitAction(() -> addItem(StoredObject.get(item.getClass(), item.getId())));
            a.execute(this.getView());
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

        private static String remarks(String remarks, String reference) {
            if(reference == null || reference.isEmpty()) return remarks;
            if(remarks.isEmpty()) return reference;
            return reference + " - " + remarks;
        }

        private void consume(InventoryItem item, Quantity consumed, String remarks) {
            clearAlerts();
            try {
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today(), remarks(remarks, "Consumed"));
                it.moveTo(item, consumed,null, consumption);
                it.save();
                message(item.toDisplay() + " - Item marked as consumed! Consumed: " + consumed);
                removeItem(item);
            } catch (Throwable e) {
                warning(e);
            }
        }

        private void replace(InventoryItem item, InventoryItemType pn, String sn, String remarks) {
            clearAlerts();
            String sOld = "S/N = " + item.getSerialNumber(), sNew = "S/N = " + sn;
            InventoryItem newItem, ii = getItem(pn, sn);
            boolean isNew = ii == null;
            if(isNew) ii = pn.createItem(sn);
            newItem = ii;
            try {
                if(newItem == null) {
                    warning("Unable to create item with P/N = " + pn.getPartNumber() + ", " + sOld);
                    return;
                }
                if(isNew) {
                    newItem.loadAttributesFrom(item);
                }
                newItem.setSerialNumber(sn);
                newItem.setGRN((Id)null);
                newItem.setPurchaseDate(DateUtility.today());
                newItem.setOwner(eo.getEntityId());
                if(tm.transact(newItem::save) != 0) {
                    error("Unable to save new item with P/N = " + pn.getPartNumber() + ", " + sOld);
                    return;
                }
                if(!pn.getId().equals(item.getPartNumberId())) {
                    sOld = " P/N = " + item.getPartNumber().getPartNumber() + ", " + sOld;
                    sNew = " P/N = " + pn.getPartNumber() + ", " + sNew;
                }
                String finalSOld = sOld, finalSNew = sNew;
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today());
                tm.transact(t -> {
                    it.moveTo(item, remarks(remarks, "Replaced with " + finalSNew), consumption);
                    it.changeOwner(StoredObject.get(newItem.getClass(), newItem.getId()),
                            remarks(remarks, "Replacement of " + finalSOld),
                            StoredObject.get(Entity.class, storeBin.getStore().getSystemEntity().getEntityId()));
                    it.save(t);
                });
                message(item.toDisplay() + " - Item replaced successfully! Remarks: " + remarks);
                items.remove(item);
                addItem(newItem);
            } catch (Throwable e) {
                warning(e);
            }
        }

        private void consume(InventoryItem item, InventoryItemType pn, String sn, Quantity toReturn, String remarks) {
            Quantity q = item.getQuantity();
            Quantity consumed = toReturn.isZero() ? q : q.subtract(toReturn);
            if(toReturn.isZero() && item.getPartNumberId().equals(pn.getId()) && item.getSerialNumber().equals(sn)) {
                consume(item, consumed, remarks);
                return;
            }
            String itemDisplay = item.toDisplay();
            clearAlerts();
            try {
                InventoryTransaction it = new InventoryTransaction(tm, DateUtility.today(), remarks(remarks, "Consumed"));
                tm.transact(t -> {
                    it.moveTo(item, consumed, null, consumption);
                    it.save(t);
                });
                message(itemDisplay + " - Item marked as consumed! Consumed: " + consumed + ", To Return: " + toReturn
                        + ", Remarks: " + remarks);
                items.remove(item);
                items.addFirst(StoredObject.get(item.getClass(), item.getId()));
            } catch (Throwable e) {
                warning(e);
            }
        }

        protected abstract class ItemForm extends DataForm {

            final InventoryItem item;
            final TextField remarksField = new TextField("Remarks");

            ItemForm(String caption, InventoryItem item) {
                super(caption + " at " + title);
                this.item = item;
                addField(new ELabelField("Item", item.toDisplay(), Application.COLOR_SUCCESS));
                if(!item.isSerialized()) {
                    addField(new ELabelField("Quantity", item.getQuantity(), Application.COLOR_SUCCESS));
                }
                addField(remarksField);
                setRequired(remarksField);
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
                String r = remarksField.getValue().trim();
                new ActionForm("Item: " + item.toDisplay()
                        + "\nwill be replaced with P/N: " + pn.getPartNumber() + ", S/N: " + sn
                        + "\nRemarks: " + r
                        + "\nThis step cannot be undone. Are you sure?",
                        () -> replace(item, pn, sn, r)).execute();
                return true;
            }
        }

        private class SerializedConsumptionForm extends AbstractReplacementForm {

            SerializedConsumptionForm(InventoryItem item) {
                super("Consumption", item);
                snField.setValue(item.getSerialNumber());
                setFieldReadOnly(snField);
            }

            @Override
            protected boolean process() {
                clearAlerts();
                String sn = snField.getValue();
                InventoryItemType pn = pnField.getValue();
                close();
                String r = remarksField.getValue().trim();
                new ActionForm("Item: " + item.toDisplay()
                        + "\nReturning P/N: " + pn.getPartNumber() + ", S/N: " + sn
                        + "\nRemarks: " + r
                        + "\nThis step cannot be undone. Are you sure?",
                        () -> markAsConsumed(item, r)).execute();
                return true;
            }
        }

        private class ConsumptionForm extends AbstractReplacementForm {

            private final QuantityField consumptionField = new QuantityField("Consumption");
            private final QuantityField returnField = new QuantityField("Returning Quantity");

            ConsumptionForm(InventoryItem item) {
                super("Consumption", item);
                addField(consumptionField, returnField);
                consumptionField.setValue(item.getQuantity());
                setFieldReadOnly(snField);
                consumptionField.addValueChangeListener(e -> {
                    if(e.isFromClient()) {
                        Quantity total = item.getQuantity();
                        Quantity q = consumptionField.getValue();
                        if(q.isZero()) {
                            warning("Consumption cannot be zero!");
                            consumptionField.focus();
                            return;
                        }
                        if(q.isGreaterThan(total)) {
                            warning("Consumption cannot be greater than the available quantity!");
                            consumptionField.focus();
                            return;
                        }
                        if(q.equals(total)) returnField.clear(); else returnField.setValue(total.subtract(q));
                    }
                });
                returnField.addValueChangeListener(e -> {
                    if(e.isFromClient()) {
                        Quantity total = item.getQuantity();
                        Quantity q = returnField.getValue();
                        if(q.isGreaterThan(total)) {
                            warning("Returning quantity cannot be greater than the available quantity!");
                            returnField.focus();
                            return;
                        }
                        if(q.equals(total)) consumptionField.clear(); else consumptionField.setValue(total.subtract(q));
                    }
                });
            }

            @Override
            protected boolean process() {
                clearAlerts();
                Quantity totalQuantity = item.getQuantity(), returnQuantity = returnField.getValue();
                if(returnQuantity.isGreaterThan(totalQuantity)) {
                    warning("Returning quantity cannot be greater than the existing quantity!");
                    return false;
                }
                if(totalQuantity.equals(returnQuantity)) {
                    close();
                    message("No changes made!");
                    return true;
                }
                String sn = snField.getValue();
                InventoryItemType pn = pnField.getValue();
                close();
                String r = remarksField.getValue().trim();
                new ActionForm("Item: " + item.toDisplay()
                        + "\nConsumed: " + totalQuantity.subtract(returnQuantity)
                        + ", Returning quantity: " + returnQuantity
                        + "\nReturning P/N: " + pn.getPartNumber() + ", B/N: " + sn
                        + "\nRemarks: " + r
                        + "\nThis step cannot be undone. Are you sure?",
                        () -> consume(item, pn, sn, returnQuantity, r)).execute();
                return true;
            }
        }

        private class CreateNewItemForm extends DataForm {

            private final ItemTypeGetField<InventoryItemType> pnField = new ItemTypeGetField<>("Part Number", InventoryItemType.class, true);
            private final TextField snField = new TextField("Serial/Batch/Lot Number");
            private final QuantityField quantityField = new QuantityField("Quantity");
            private final TextArea remarksField = new TextArea("Remarks");

            public CreateNewItemForm() {
                super("Create New Item at " + title);
                addField(pnField, snField, quantityField, remarksField);
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
                            quantityField.setValue(Quantity.create(quantityField.getValue().getValue(), pn.getUnitOfMeasurement().getUnit()));
                        }
                    }
                });
                setRequired(pnField);
                setRequired(snField);
                setRequired(quantityField);
                setRequired(remarksField);
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
                        () -> createNewItem(pn, sn, quantity, remarksField.getValue().trim())).execute();
                return true;
            }
        }

        private static InventoryItem getItem(InventoryItemType pn, String sn) {
            return StoredObject.list(InventoryItem.class,
                            "PartNumber=" + pn.getId() + " AND SerialNumber='" + sn + "'", true)
                    .filter(i -> !i.getLocation().canResurrect()).findFirst();
        }

        private String get(InventoryItemType pn, String sn) {
            InventoryItem ii = getItem(pn, sn);
            return ii == null || ii.getOwnerId().equals(eo.getEntityId()) ? null
                    : ("Such an item already exists at " + ii.getLocation().toDisplay());
        }

        private class ManageAssembly<T extends InventoryItem, C extends InventoryItem> extends AbstractAssembly<T, C> {

            public ManageAssembly(T item) {
                //noinspection unchecked
                this(null, item, (Class<T>) item.getClass(), null);
            }

            private ManageAssembly(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass) {
                super(location, item, itemClass, componentClass);
                if(item == null) {
                    Application a = Application.get();
                    if(a != null) {
                        String caption = a.getLogicTitle(null);
                        if(caption != null) {
                            setCaption(caption);
                        }
                    }
                }
            }

            @Override
            FitItem createFitItem(Class<C> itemClass) {
                return new ItemFit(itemClass);
            }

            @Override
            RemoveItem createRemoveItem() {
                return new ItemRemove();
            }

            private class ItemFit extends AbstractAssemblyFit {

                public ItemFit(Class<C> itemClass) {
                    super(new ItemComboField<>(itemClass, new ArrayList<>()));
                    itemField.setEnabled(true);
                    addField(partNumbersField, (HasValue<?, ?>) itemField, requiredQuantityField,
                            availableQuantityField, toFitQuantityField);
                    setRequired((HasValue<?, ?>) itemField);
                }

                @Override
                protected boolean process() {
                    return process(date, reference("Fitted"));
                }

                public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity quantityAlreadyFitted) {
                    super.setAssemblyPosition(fitmentPosition, quantityAlreadyFitted);
                    availableQuantityField.setValue(quantityRequired.zero());
                    toFitQuantityField.setEnabled(!itemType.isSerialized());
                    toFitQuantityField.setValue(itemType.isSerialized() ? Count.ONE : quantityRequired.zero());
                }

                @Override
                protected void pnChanged() {
                    InventoryItemType pn = partNumbersField.getValue();
                    if(pn == null) {
                        itemField.clear();
                        partNumbersField.focus();
                        return;
                    }
                    @SuppressWarnings("unchecked") List<C> items = StoredObject.list(InventoryItem.class,
                                    "PartNumber=" + pn.getId() + " AND Location=" + eo.getId(), true)
                            .map(i -> (C)i).toList();
                    ((ItemComboField<C>)itemField).setItems(items);
                }

                @Override
                public void valueChanged(ChangedValues changedValues) {
                    if(changedValues.isFromClient() && changedValues.getChanged() == partNumbersField) {
                        pnChanged();
                        return;
                    }
                    if(changedValues.getChanged() == itemField) {
                        itemValueChanged();
                    }
                }
            }

            private class ItemRemove extends AbstractItemRemove {

                private final QuantityField qToRemoveField = new QuantityField("Quantity to Remove");

                public ItemRemove() {
                    addField(itemField, fittedQuantityField, qToRemoveField);
                    fittedQuantityField.setEnabled(false);
                    setRequired(qToRemoveField);
                }

                @Override
                protected boolean process() {
                    Quantity qToRemove = qToRemoveField.getValue();
                    if(!item.isSerialized()) {
                        if(qToRemove.isGreaterThan(item.getQuantity())) {
                            warning("Fitted quantity is only " + item.getQuantity() + ", can't remove " + qToRemove);
                            return false;
                        }
                    }
                    if(inventoryTransaction == null || !inventoryTransaction.getDate().equals(date)) {
                        inventoryTransaction = new InventoryTransaction(getTransactionManager(), date);
                    } else {
                        inventoryTransaction.abandon();
                    }
                    inventoryTransaction.moveTo(item, qToRemove, reference("Removed"), eo);
                    if(transact(t -> inventoryTransaction.save(t))) {
                        refresh();
                        return true;
                    }
                    return false;
                }

                @Override
                public void setAssemblyPosition(InventoryFitmentPosition fitmentPosition) {
                    super.setAssemblyPosition(fitmentPosition);
                    Quantity q = item.getQuantity();
                    qToRemoveField.setValue(q);
                    qToRemoveField.setEnabled(!item.isSerialized() && !item.isConsumable());
                }
            }
        }
    }
}
