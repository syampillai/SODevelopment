package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * GRN - Create, edit and process GRNs.
 *
 * @author Syam
 */
public class GRN extends ObjectBrowser<InventoryGRN> {

    private ObjectEditor<InventoryGRN> viewer;
    private final GRNEditor editor;
    private final ELabel store = new ELabel("Store: Not selected");
    private final Button switchStore = new Button("Switch Store", VaadinIcon.STORAGE, e -> switchStore());

    /**
     * Constructor.
     */
    public GRN() {
        this(EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param actions Allowed edit actions (See {@link EditorAction}).
     */
    public GRN(int actions) {
        this(actions, null);
    }

    /**
     * Constructor.
     *
     * @param actions Allowed edit actions (See {@link EditorAction}).
     * @param caption Caption.
     */
    public GRN(int actions, String caption) {
        this(InventoryItemType.class, null, actions, caption);
    }

    /**
     * Constructor.
     *
     * @param classNames Class names of to be used. "Class Name of P/N|Class Name of Supplier".
     */
    public GRN(String classNames) {
        this(itemTypeClass(classNames), suppliers(classNames), EditorAction.ALL, null);
        setStore(storeName(classNames));
    }

    private GRN(Class<? extends InventoryItemType> pnClass, Collection<Entity> suppliers, int actions, String caption) {
        super(InventoryGRN.class, actions, caption);
        editor = new GRNEditor(pnClass, suppliers, actions, caption);
        editor.grnBrowser = this;
    }

    void showStore(InventoryStore store) {
        this.store.clearContent().append("Store: ").append(store.toDisplay(), "blue").update();
    }

    @Override
    public void constructed() {
        super.constructed();
        edit.setText("Receive / Process");
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(store);
    }

    @Override
    protected final GRNEditor createObjectEditor() {
        return editor;
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(switchStore);
        super.addExtraButtons();
    }

    @Override
    public boolean includeColumn(String columnName) {
        return !"Store".equals(columnName);
    }

    @Override
    public String getOrderBy() {
        return "Store,Date DESC";
    }

    @Override
    public void doEdit(InventoryGRN object) {
        if(object == null) {
            return;
        }
        editor.setObject(object);
        if(!editor.canEdit()) {
            return;
        }
        editor.execute(getView());
    }

    @Override
    public void doView(InventoryGRN object) {
        if(object == null) {
            return;
        }
        if(viewer == null) {
            viewer = ObjectEditor.create(InventoryGRN.class);
        }
        viewer.setCaption("GRN: " + object.getReferenceNumber());
        viewer.viewObject(object);
    }

    @Override
    public void doDelete(InventoryGRN object) {
        editor.deleteMe(object, super::doDelete);
    }

    private void switchStore() {
        editor.resetAnchor();
        store.clearContent().append("Store: Not selected").update();
        load.click();
    }

    /**
     * Set the store programmatically.
     *
     * @param store Store to be set.
     */
    public void setStore(InventoryStore store) {
        editor.resetAnchor();
        if(store == null) {
            return;
        }
        HasValue<?, ?> storeField = editor.getAnchorField("Store");
        //noinspection unchecked
        ((IdInput<InventoryStore>)storeField).setValue(store);
        storeField.setReadOnly(true);
        load.click();
    }

    private static Class<? extends InventoryItemType> itemTypeClass(String classNames) {
        int p = classNames.indexOf('|');
        if(p >= 0) {
            classNames = classNames.substring(0, p);
        }
        if(classNames.isEmpty()) {
            return InventoryItemType.class;
        }
        try {
            //noinspection unchecked
            return (Class<? extends InventoryItemType>) JavaClassLoader.getLogic(classNames);
        } catch(Throwable e) {
            throw new SORuntimeException("Unable to determine item type from '" + classNames + "'");
        }
    }

    private static Collection<Entity> suppliers(String classNames) {
        int p = classNames.indexOf('|');
        if(p >= 0) {
            classNames = classNames.substring(p + 1).trim();
            p = classNames.indexOf('|');
            if(p >= 0) {
                classNames = classNames.substring(0, p).trim();
            }
        } else {
            return null;
        }
        if(classNames.isEmpty()) {
            return null;
        }
        try {
            //noinspection unchecked
            return StoredObject.list((Class<? extends EntityRole>) JavaClassLoader.getLogic(classNames)).
                    convert(EntityRole::getOrganization).collectAll();
        } catch(Throwable e) {
            throw new SORuntimeException("Unable to determine suppliers from '" + classNames + "'");
        }
    }

    private static InventoryStore storeName(String classNames) {
        int p = classNames.indexOf('|');
        if(p >= 0) {
            classNames = classNames.substring(p + 1).trim();
            p = classNames.indexOf('|');
            if(p >= 0) {
                classNames = classNames.substring(p + 1).trim();
            } else {
                return null;
            }
        } else {
            return null;
        }
        if(classNames.isEmpty()) {
            return null;
        }
        InventoryStore store = LocationField.getStore(classNames);
        if(store == null) {
            throw new SORuntimeException("Unable to determine the store from '" + classNames + "'");
        }
        return store;
    }

    private static class GRNEditor extends ObjectEditor<InventoryGRN> {

        private ObjectField<Entity> supplierField;
        private ObjectLinkField<InventoryGRNItem> grnItemsField;
        private GRNItemGrid grnItemGrid;
        private ObjectField<InventoryStore> storeField;
        private InventoryStore store;
        private final NewGRNItemForm newGRNItemForm = new NewGRNItemForm();
        private GRN grnBrowser;
        private final Button process = new Button("Mark as Inspected", VaadinIcon.CHECK, e -> process());
        private final Button close = new Button("Mark as Received", VaadinIcon.THUMBS_UP_O, e -> process());
        private final Button inspect = new Button("Inspect", VaadinIcon.STOCK, e -> grnItemGrid.inspect(false)).asSmall();
        private final Button bin = new Button("Bin", VaadinIcon.STORAGE, e -> grnItemGrid.bin()).asSmall();
        private final Button assemble = new Button("Assemble", VaadinIcon.COMPILE, e -> grnItemGrid.assemble()).asSmall();
        private final Class<? extends InventoryItemType> pnClass;

        GRNEditor(Class<? extends InventoryItemType> pnClass, Collection<Entity> suppliers, int actions, String caption) {
            super(InventoryGRN.class, actions & (~EditorAction.SEARCH), caption);
            this.pnClass = pnClass;
            if(suppliers != null) {
                if(suppliers.isEmpty()) {
                    throw new SORuntimeException("No suppliers found!");
                }
                supplierField = new ObjectField<>(suppliers);
            }
            setCaption("Edit / Process GRN");
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            add.setVisible(false);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if(supplierField != null && "Supplier".equals(fieldName)) {
                supplierField.setLabel(label);
                return supplierField;
            }
            return super.createField(fieldName, label);
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Store".equals(fieldName)) {
                //noinspection unchecked
                storeField = (ObjectField<InventoryStore>) field;
            }
            super.customizeField(fieldName, field);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
            if("Items.l".equals(fieldName)) {
                grnItemsField = (ObjectLinkField<InventoryGRNItem>) field;
                grnItemGrid = new GRNItemGrid();
                return grnItemGrid;
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        protected void anchorsSet() throws Exception {
            super.anchorsSet();
            store = storeField.getObject();
            newGRNItemForm.bField.setStore(store);
            if(grnItemGrid.binEditor != null) {
                grnItemGrid.binEditor.binField.setStore(store);
            }
            if(grnBrowser != null) {
                grnBrowser.showStore(store);
            }
        }

        @Override
        protected void addExtraButtons() {
            InventoryGRN grn = getObject();
            if(grn == null) {
                return;
            }
            switch(grn.getStatus()) {
                case 0:
                    buttonPanel.add(process);
                    break;
                case 1:
                    buttonPanel.add(close);
                    break;
            }
        }

        @Override
        public boolean canEdit() {
            InventoryGRN grn = getObject();
            if(grn == null) {
                return false;
            }
            if(grn.getStatus() > 0) {
                warning("Status: " + grn.getStatusValue() + ". Can't edit");
                return false;
            }
            return true;
        }

        @Override
        public void doDelete() {
            deleteMe(getObject(), o -> {
                super.doDelete();
                close();
            });
        }

        void deleteMe(InventoryGRN object, Consumer<InventoryGRN> how) {
            if(object == null) {
                return;
            }
            if(object.getStatus() == 0) {
                how.accept(object);
                return;
            }
            if(object.getStatus() == 1) {
                warning("Entry is already processed. Deletion not possible at this stage.");
                return;
            }
            ActionForm af = new ActionForm(
                    "Entry will be removed but this will not affect the items received via this GRN.\nAre you sure?",
                    () -> how.accept(object));
            af.execute();
        }

        private void process() {
            InventoryGRN grn = getObject();
            if(grn.getStatus() == 2) {
                message("Already closed, no further action possible");
                return;
            }
            if(grn.getStatus() == 1) {
                preCloseGRN(grn);
            } else {
                preProcessGRN(grn);
            }
        }

        private void preProcessGRN(InventoryGRN grn) {
            InventoryGRNItem grnItem = grn.listLinks(InventoryGRNItem.class).filter(gi -> Id.isNull(gi.getItemId())).findFirst();
            if(grnItem != null) {
                warning("Item not inspected for " + grnItem.toDisplay());
                return;
            }
            clearAlerts();
            List<InventoryGRNItem> list = new ArrayList<>();
            grn.listLinks(InventoryGRNItem.class).filter(gi -> Id.isNull(gi.getBinId())).collectAll(list);
            if(list.isEmpty()) {
                processGRN(grn);
                return;
            }
            String m = "Bin location";
            if(list.size() == 1) {
                m += " is";
            } else {
                m += "s are";
            }
            m += " not specified for th";
            if(list.size() == 1) {
                m += "is. This";
            } else {
                m += "ese. These";
            }
            m += " will be stored without specifying any bin";
            if(list.size() > 1) {
                m += "s";
            }
            m += "!";
            new ConfirmGrid(list, m, () -> processGRN(grn));
        }

        private void preCloseGRN(InventoryGRN grn) {
            clearAlerts();
            List<InventoryGRNItem> list = new ArrayList<>();
            grn.listLinks(InventoryGRNItem.class).filter(gi -> gi.getItem().isAssemblyIncomplete()).collectAll(list);
            if(list.isEmpty()) {
                closeGRN(grn);
                return;
            }
            String m = "Assembly for the following item";
            if(list.size() == 1) {
                m += " is";
            } else {
                m += "s are";
            }
            m += " incomplete. Th";
            if(list.size() == 1) {
                m += "is";
            } else {
                m += "ese";
            }
            m += " will be accepted as such!";
            new ConfirmGrid(list, m, () -> overrideAndCloseGRN(grn));
        }

        private void processGRN(InventoryGRN grn) {
            grnChanged(grn, transact(grn::process));
        }

        private void closeGRN(InventoryGRN grn) {
            grnChanged(grn, transact(grn::close));
        }

        private void overrideAndCloseGRN(InventoryGRN grn) {
            List<InventoryGRNItem> list = new ArrayList<>();
            grn.listLinks(InventoryGRNItem.class).
                    filter(gi -> !gi.getInspected() && gi.getItem().isAssemblyIncomplete()).collectAll(list);
            if(!list.isEmpty()) {
                if(!transact(t -> {
                    for(InventoryGRNItem gi: list) {
                        gi.inspect(t);
                    }
                })) {
                    return;
                }
            }
            closeGRN(grn);
        }

        private void grnChanged(InventoryGRN grn, boolean showMessage) {
            grn.reload();
            setObject(grn);
            if(showMessage) {
                message("Status changed to: " + grn.getStatusValue());
                grnBrowser.refresh(grn);
            }
        }

        @Override
        public void setObject(InventoryGRN grn, boolean load) {
            super.setObject(grn, load);
            enableExtraButtons();
        }

        private void enableExtraButtons() {
            InventoryGRN grn = getObject();
            if(grn == null) {
                return;
            }
            int status = grn.getStatus();
            grnItemGrid.setAllowAdd(status == 0);
            inspect.setVisible(status == 0);
            bin.setVisible(status == 0);
            assemble.setVisible(status == 1);
        }

        private void disableExtraButtons() {
            inspect.setVisible(false);
            bin.setVisible(false);
            assemble.setVisible(false);
        }

        @Override
        public void editingStarted() {
            super.editingStarted();
            disableExtraButtons();
        }

        @Override
        public void editingEnded() {
            super.editingEnded();
            enableExtraButtons();
        }

        @Override
        public void editingCancelled() {
            super.editingCancelled();
            enableExtraButtons();
        }

        private class GRNItemGrid extends DetailLinkGrid<InventoryGRNItem> {

            @SuppressWarnings("rawtypes")
            private ObjectEditor itemEditor;
            private BinEditor binEditor;

            public GRNItemGrid() {
                super(grnItemsField, StringList.create("PartNumber", "SerialNumber", "Inspected", "Quantity", "UnitCost", "Bin"));
                setObjectEditor(new GRNItemEditor());
                getButtonPanel().add(inspect, bin, assemble);
            }

            public boolean getInspected(InventoryGRNItem grnItem) {
                return !Id.isNull(grnItem.getItemId());
            }

            @Override
            public boolean isColumnEditable(String columnName) {
                if("Inspected".equals(columnName)) {
                    return false;
                }
                return super.isColumnEditable(columnName);
            }

            @Override
            public void add() {
                newGRNItemForm.execute(GRNEditor.this);
            }

            @Override
            public InventoryGRNItem selected() {
                InventoryGRNItem grnItem = super.selected();
                if(grnItem == null) {
                    if(size() == 0) {
                        warning("No entries. Please click the 'Edit' button to add items first.");
                    } else {
                        warning("Please select an entry first");
                    }
                }
                return grnItem;
            }

            private void inspect(boolean invokeBin) {
                if(!invokeBin) {
                    clearAlerts();
                }
                InventoryGRNItem grnItem = selected();
                if(grnItem == null) {
                    return;
                }
                InventoryItemType itemType = grnItem.getPartNumber();
                if(itemType.isSerialized() && grnItem.getSerialNumber().isEmpty()) {
                    warning("S/N for item not set");
                    return;
                }
                InventoryItem item = grnItem.getItem();
                if(item == null) {
                    if(itemType.isSerialized()) {
                        item = InventoryItem.get(grnItem.getSerialNumber(), itemType);
                        if(item != null) {
                            warning("An item with the same " + itemType.getPartNumberShortName() + " already exists: " + item.toDisplay());
                            edit();
                            return;
                        }
                    }
                    item = itemType.createItem();
                    item.setSerialNumber(grnItem.getSerialNumber());
                    Quantity q = grnItem.getQuantity();
                    item.setQuantity(q);
                    item.setCost(grnItem.getUnitCost().multiply(q));
                    item.setLocation(InventoryTransaction.createSupplierLocation(getTransactionManager(), getObject().getSupplier()));
                }
                if(itemEditor != null && itemEditor.getObjectClass() != item.getClass()) {
                    itemEditor = null;
                }
                if(itemEditor == null) {
                    itemEditor = ObjectEditor.create(item.getClass());
                    itemEditor.setCaption("Inspect Item");
                    itemEditor.setFieldHidden("Location");
                    itemEditor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber", "SerialNumber");
                    //noinspection unchecked
                    itemEditor.setSaver(e -> saveItem(invokeBin));
                }
                //noinspection unchecked
                itemEditor.editObject(item,GRNEditor.this);
            }

            private boolean saveItem(boolean invokeBin) {
                return transact(t -> {
                    InventoryGRNItem grnItem = getSelected();
                    InventoryItem item;
                    itemEditor.save(t);
                    item = (InventoryItem) itemEditor.getObject();
                    if(!item.getId().equals(grnItem.getItemId())) {
                        grnItem.setItem(item.getId());
                        grnItem.save(t);
                    }
                    refresh(grnItem);
                    if(invokeBin) {
                        bin();
                    }
                });
            }

            private void bin() {
                clearAlerts();
                InventoryGRNItem grnItem = selected();
                if(grnItem == null) {
                    return;
                }
                InventoryItem item = grnItem.getItem();
                if(item == null) {
                    warning("Please inspect the item before binning");
                    inspect(true);
                    return;
                }
                if(binEditor == null) {
                    binEditor = new BinEditor();
                }
                binEditor.setGRNItem(grnItem);
                binEditor.execute();
            }

            private void assemble() {
                clearAlerts();
                InventoryGRNItem grnItem = selected();
                if(grnItem == null) {
                    return;
                }
                InventoryItem item = grnItem.getItem();
                if(item.getPartNumber().listAssemblies().findFirst() == null) {
                    warning("Not an assembly: " + item.toDisplay());
                    return;
                }
                new AssemblyReceipt<>(grnItem).execute(GRNEditor.this);
            }
        }

        private class BinEditor extends DataForm {

            private InventoryGRNItem grnItem;
            private final ELabelField itemField = new ELabelField("Item");
            private final BinField binField = new BinField("Bin");

            BinEditor() {
                super("Select Bin");
                addField(itemField, binField);
                binField.setStore(store);
                setRequired(binField);
            }

            public void setGRNItem(InventoryGRNItem grnItem) {
                this.grnItem = grnItem;
                itemField.clearContent().append(grnItem.getItem().toDisplay()).update();
                binField.setValue(grnItem.getBin());
            }

            @Override
            protected boolean process() {
                InventoryBin bin = binField.getValue();
                if(bin.getId().equals(grnItem.getBinId())) {
                    return true;
                }
                InventoryItem item = grnItem.getItem();
                if(!item.canStore(bin)) {
                    warning("The item can't be binned at that location");
                    return false;
                }
                grnItem.setBin(bin);
                if(transact(t -> grnItem.save(t))) {
                    grnItemGrid.refresh(grnItem);
                    return true;
                }
                return false;
            }
        }

        private class GRNItemEditor extends ObjectEditor<InventoryGRNItem> {

            public GRNItemEditor() {
                super(InventoryGRNItem.class);
            }

            @Override
            protected void formConstructed() {
                super.formConstructed();
                setFieldReadOnly("PartNumber");
                setFieldHidden("Inspected");
            }

            @Override
            protected void customizeField(String fieldName, HasValue<?, ?> field) {
                if("Bin".equals(fieldName)) {
                    ((BinField)((ObjectField<?>)field).getField()).setStore(store);
                }
                super.customizeField(fieldName, field);
            }

            @Override
            public boolean isFieldEditable(String fieldName) {
                InventoryGRNItem gi = getObject();
                if(gi == null) {
                    return false;
                }
                if("Quantity".equals(fieldName)) {
                    return !gi.getPartNumber().isSerialized();
                }
                return super.isFieldEditable(fieldName);
            }
        }

        private class NewGRNItemForm extends DataForm {

            private ObjectField<? extends InventoryItemType> pnField;
            private final TextField snField;
            private final QuantityField qField;
            private final BinField bField;
            private final MoneyField cField;

            public NewGRNItemForm() {
                super("GRN Item");
                bField = new BinField("Bin");
                snField = new TextField("Serial/Batch Number");
                qField = new QuantityField("Quantity");
                setRequired(qField);
                cField = new MoneyField("Unit Cost");
                setRequired(cField);
            }

            @Override
            protected void buildFields() {
                pnField = new ObjectField<>("Part Number", pnClass, true);
                addField(pnField, snField, qField, cField, bField);
                pnField.addValueChangeListener(e -> pnChanged());
            }

            private void pnChanged() {
                InventoryItemType pn = pnField.getObject();
                if(pn != null) {
                    if(pn.isSerialized()) {
                        qField.setValue(Count.ONE);
                    } else {
                        qField.setValue(pn.getUnitOfMeasurement());
                    }
                    cField.setValue(pn.getUnitCost());
                }
            }

            @Override
            protected boolean process() {
                InventoryItemType pn = pnField.getObject();
                Quantity q = qField.getValue();
                String sn = StoredObject.toCode(snField.getValue());
                if(pn.isSerialized() && q.equals(Count.ONE) && sn.isEmpty()) {
                    warning("Please enter the S/N of the item");
                    return false;
                }
                if(!q.isCompatible(pn.getUnitOfMeasurement())) {
                    warning("Unit used in quantity (" + q + ") is not compatible with the unit of measurement of " + pn.toDisplay());
                    return false;
                }
                InventoryBin bin = bField.getValue();
                Money cost = cField.getValue();
                int n;
                if(pn.isSerialized() && !q.equals(Count.ONE)) {
                    n = q.convert(Count.ONE.getUnit()).getValue().intValue();
                    q = Count.ONE;
                } else {
                    n = 1;
                }
                InventoryGRNItem grnItem;
                for(int i = 0; i < n; i++) {
                    grnItem = new InventoryGRNItem();
                    grnItem.setPartNumber(pn);
                    grnItem.setSerialNumber(i == 0 ? sn : "");
                    grnItem.setQuantity(q);
                    grnItem.setUnitCost(cost);
                    grnItem.setBin(bin);
                    grnItemsField.add(grnItem);
                }
                return true;
            }

            @Override
            protected void execute(View parent, boolean doNotLock) {
                getComponent();
                pnField.setValue((Id)null);
                snField.setValue("");
                bField.setValue((Id)null);
                cField.setValue(null);
                super.execute(parent, doNotLock);
            }
        }

        private static class ConfirmGrid extends ActionGrid<InventoryGRNItem> {

            public ConfirmGrid(List<InventoryGRNItem> items, String message, Runnable action) {
                super(InventoryGRNItem.class, items, StringList.create("Item.PartNumber", "Item.SerialNumber"), message, action);
                execute();
            }
        }
    }
}
