package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.*;
import java.util.function.Consumer;

/**
 * GRN - Create, edit and process GRNs.
 *
 * @author Syam
 */
public class GRN extends ObjectBrowser<InventoryGRN> {

    private ObjectEditor<InventoryGRN> viewer;
    private final GRNEditor editor;
    private final ELabel storeDisplay = new ELabel("Store: Not selected");
    private final Button switchStore = new Button("Switch Store", VaadinIcon.STORAGE, e -> switchStore());
    private final int type;
    private ProducesGRN grnProducer;

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
        this(actions, (String)null);
    }

    /**
     * Constructor.
     *
     * @param actions Allowed edit actions (See {@link EditorAction}).
     * @param caption Caption.
     */
    public GRN(int actions, String caption) {
        this(null, actions, caption);
    }

    /**
     * Constructor.
     */
    public GRN(InventoryStore store) {
        this(store, EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param actions Allowed edit actions (See {@link EditorAction}).
     */
    public GRN(InventoryStore store, int actions) {
        this(store, actions, null);
    }

    /**
     * Constructor.
     *
     * @param actions Allowed edit actions (See {@link EditorAction}).
     * @param caption Caption.
     */
    public GRN(InventoryStore store, int actions, String caption) {
        this(0, InventoryItemType.class, actions, caption, store);
    }

    /**
     * Constructor.
     *
     * @param classNames Class names to be used. "Class Name of P/N|Store Name".
     */
    public GRN(String classNames) {
        this(0, ParameterParser.itemTypeClass(classNames), EditorAction.ALL, null,
                ParameterParser.store(classNames));
    }

    /**
     * Constructor.
     *
     * @param type Type.
     * @param actions Allowed edit actions (See {@link EditorAction}).
     */
    public GRN(int type, int actions) {
        this(type, actions == 0 ? EditorAction.ALL : actions, null);
    }

    /**
     * Constructor.
     *
     * @param type Type.
     * @param actions Allowed edit actions (See {@link EditorAction}).
     * @param caption Caption.
     */
    public GRN(int type, int actions, String caption) {
        this(type, null, actions, caption);
    }

    /**
     * Constructor.
     *
     * @param type Type.
     */
    public GRN(int type, InventoryStore store) {
        this(type, store, EditorAction.ALL);
    }

    /**
     * Constructor.
     *
     * @param type Type.
     * @param actions Allowed edit actions (See {@link EditorAction}).
     */
    public GRN(int type, InventoryStore store, int actions) {
        this(type, store, actions, null);
    }

    /**
     * Constructor.
     *
     * @param type Type.
     * @param actions Allowed edit actions (See {@link EditorAction}).
     * @param caption Caption.
     */
    public GRN(int type, InventoryStore store, int actions, String caption) {
        this(type, InventoryItemType.class, actions, caption, store);
    }

    private GRN(int type, Class<? extends InventoryItemType> pnClass, int actions, String caption,
                InventoryStore store) {
        super(InventoryGRN.class, actions, caption);
        this.type = type;
        editor = new GRNEditor(type, pnClass, actions, caption);
        if(store != null) {
            addConstructedListener(f -> setStore(store));
        } else {
            setFixedFilter("Status<2 AND Type=" + type, false);
        }
        setCaption("GRN (" + InventoryGRN.getTypeValues()[type] + ")");
        GridContextMenu<InventoryGRN> cm = new GridContextMenu<>(this);
        cm.addItem("Receive/Process", e -> e.getItem().ifPresent(i -> edit.click()));
        cm.setDynamicContentHandler(grn -> {
            deselectAll();
            select(grn);
            return grn != null && grn.getStatus() <= 1;
        });
    }

    public void setGRNProducer(ProducesGRN grnProducer) {
        this.grnProducer = grnProducer;
    }

    public ProducesGRN getGRNProducer() {
        return grnProducer;
    }

    public void setStore(InventoryStore store) {
        ObjectField<?> storeField = (ObjectField<?>) editor.getAnchorField("Store");
        storeField.setReadOnly(store != null);
        if(store != null) {
            storeField.setObject(store);
            editor.executeAnchorForm();
        }
        switchStore.setVisible(store == null);
        setFixedFilter("Status<2 AND Type=" + type);
    }

    public void processGRN(InventoryGRN grn) {
        select(grn);
        edit.click();
    }

    @Override
    public void constructed() {
        super.constructed();
        edit.setText("Receive / Process");
    }

    @Override
    public int getRelativeColumnWidth(String columnName) {
        return "Supplier".equals(columnName) ? 5 : super.getRelativeColumnWidth(columnName);
    }

    @Override
    public boolean canRowEdit(InventoryGRN item) {
        select(item);
        edit.click();
        return false;
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(storeDisplay);
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("Supplier".equals(columnName)) {
            return InventoryGRN.getTypeValues()[type];
        }
        return super.getColumnCaption(columnName);
    }

    @Override
    protected final GRNEditor createObjectEditor() {
        return editor;
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(switchStore);
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter("Type=" + type + (e.getValue() ? "" : " AND Status<2")));
        buttonPanel.add(h);
        super.addExtraButtons();
    }

    @Override
    public boolean includeColumn(String columnName) {
        return !"Store".equals(columnName);
    }

    @Override
    public String getOrderBy() {
        return "Store,Date DESC,No DESC";
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
        resetAnchor();
        storeDisplay.clearContent().append("Store: Not selected").update();
        load.click();
    }

    private void displayStore() {
        this.storeDisplay.clearContent().append("Store: ").
                append(editor.store, "blue").
                append(" | ", "green").
                append("Note: ").
                append("Double-click or right-click on the entry to receive/process items", "blue").
                update();
    }

    @Override
    protected void anchorsSet() {
        displayStore();
    }

    @Override
    protected void anchorsCancelled() {
        if(editor.store == null) {
            close();
        } else {
            anchorsSet();
        }
    }

    static Collection<Entity> suppliers(int type) {
        type = switch(type) {
            case 0 -> 1;
            case 1 -> 17;
            case 2 -> 9;
            default -> -1;
        };
        return StoredObject.list(InventoryVirtualLocation.class, "Type=" + type).
                map(InventoryVirtualLocation::getEntity).toList();
    }

    public void setEditorProvider(ObjectEditorProvider editorProvider) {
        if(editorProvider != null) {
            editor.editorProvider = editorProvider;
        }
    }

    /**
     * This is invoked whenever the GRN status is changed.
     *
     * @param grn Current GRN.
     */
    protected void statusChanged(InventoryGRN grn) {
    }

    private void changedStatus(InventoryGRN grn) {
        statusChanged(grn);
        if(grnProducer != null) {
            grnProducer.statusOfGRNChanged(grn);
        }
    }

    private class GRNEditor extends ObjectEditor<InventoryGRN> {

        private ObjectEditorProvider editorProvider = new ObjectEditorProvider() {};
        private final int type;
        private ObjectField<Entity> supplierField;
        private ObjectLinkField<InventoryGRNItem> grnItemsField;
        private GRNItemGrid grnItemGrid;
        private InventoryStore store;
        private final NewGRNItemForm newGRNItemForm = new NewGRNItemForm();
        private final Button process = new Button("Mark as Inspected", VaadinIcon.CHECK, e -> process());
        private final Button close = new Button("Mark as Received", VaadinIcon.THUMBS_UP_O, e -> process());
        private final Button inspect = new Button("Inspect", VaadinIcon.STOCK, e -> grnItemGrid.inspectSel())
                .asSmall();
        private final Button bin = new Button("Bin", VaadinIcon.STORAGE, e -> grnItemGrid.binSel()).asSmall();
        private final Button assemble = new Button("Assemble", VaadinIcon.COMPILE, e -> grnItemGrid.assembleSel())
                .asSmall();
        private final ELabel hint = new ELabel("You may also right-click on the entry to inspect/bin/assemble.",
                "blue");
        private final Class<? extends InventoryItemType> pnClass;

        GRNEditor(int type, Class<? extends InventoryItemType> pnClass, int actions, String caption) {
            super(InventoryGRN.class, actions & (~EditorAction.SEARCH), caption);
            this.type = type;
            this.pnClass = pnClass;
            Collection<Entity> suppliers = suppliers(type);
            if(suppliers != null) {
                if(suppliers.isEmpty()) {
                    throw new SORuntimeException("Can't find any \"" + InventoryGRN.getTypeValues()[type] +
                            "\" entities configured!");
                }
                supplierField = new ObjectField<>(suppliers);
            }
            addField("ReferenceNumber");
            setCaption("Edit / Process GRN");
            setNewObjectGenerator(() -> {
                InventoryGRN grn = new InventoryGRN();
                grn.setType(type);
                return grn;
            });
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            add.setVisible(false);
            setFieldReadOnly("ReferenceNumber", "Items.l");
            if(edit != null) {
                edit.setVisible(getExtraInfoField() != null);
            }
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if(supplierField != null && "Supplier".equals(fieldName)) {
                supplierField.setLabel(InventoryGRN.getTypeValues()[type]);
                return supplierField;
            }
            return super.createField(fieldName, label);
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
            store = (InventoryStore) ((ObjectField<?>)getAnchorField("Store")).getObject();
            newGRNItemForm.bField.setStore(store);
            if(grnItemGrid.binEditor != null) {
                grnItemGrid.binEditor.binField.setStore(store);
            }
        }

        @Override
        protected void addExtraButtons() {
            InventoryGRN grn = getObject();
            if(grn == null) {
                return;
            }
            switch(grn.getStatus()) {
                case 0 -> buttonPanel.add(process);
                case 1 -> buttonPanel.add(close);
            }
        }

        @Override
        public boolean canEdit() {
            InventoryGRN grn = getObject();
            if(grn == null) {
                return false;
            }
            if(grn.getStatus() > 1) {
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
                    "Entry will be removed but this will not affect the items received via this GRN." +
                            "\nAre you sure?",
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
            InventoryGRNItem grnItem = grn.listLinks(InventoryGRNItem.class).filter(gi -> Id.isNull(gi.getItemId())).
                    findFirst();
            if(grnItem != null) {
                warning("Kindly inspect - " + grnItem.toDisplay());
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
                ((GRN)getGrid()).refresh(grn);
                if(grn.getStatus() == 2) {
                    close();
                }
            }
            changedStatus(grn);
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
            hint.setVisible(status == 0 || status == 1);
        }

        private void disableExtraButtons() {
            inspect.setVisible(false);
            bin.setVisible(false);
            assemble.setVisible(false);
            hint.setVisible(false);
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

        @Override
        public void extraInfoCreated(StoredObject extraInfo) {
            if(grnProducer != null) {
                grnProducer.extraGRNInfoCreated(getObject(), extraInfo);
            }
        }

        @Override
        public void extraInfoLoaded(StoredObject extraInfo) {
            if(grnProducer != null) {
                grnProducer.extraGRNInfoLoaded(getObject(), extraInfo);
            }
        }

        @Override
        public void savingExtraInfo(StoredObject extraInfo) throws Exception {
            if(grnProducer != null) {
                grnProducer.savingGRNExtraInfo(getObject(), extraInfo);
            }
        }

        private class GRNItemGrid extends DetailLinkGrid<InventoryGRNItem> {

            @SuppressWarnings("rawtypes")
            private ObjectEditor itemEditor;
            private BinEditor binEditor;
            private SNEditor snEditor;
            private InventoryGRNItem grnItem;
            private boolean invokeBin = false;

            public GRNItemGrid() {
                super(grnItemsField, StringList.create("Item", "PartNumber", "SerialNumber", "Inspected", "Quantity",
                        "UnitCost", "Bin"));
                setObjectEditor(new GRNItemEditor());
                getButtonPanel().add(inspect, bin, assemble, hint);
                GridContextMenu<InventoryGRNItem> contextMenu = new GridContextMenu<>(this);
                contextMenu.addItem("Inspect", e -> e.getItem().ifPresent(x -> inspect()));
                contextMenu.addItem("Bin", e -> e.getItem().ifPresent(x -> bin()));
                GridMenuItem<InventoryGRNItem> assembleRC = contextMenu.addItem("Assemble", e -> e.getItem().
                        ifPresent(x -> assemble()));
                contextMenu.setDynamicContentHandler(r -> {
                    deselectAll();
                    if(r == null || !inspect.isVisible()) {
                        return false;
                    }
                    select(r);
                    grnItem = r;
                    invokeBin = false;
                    assembleRC.setVisible(assemble.isVisible());
                    return true;
                });
            }

            public String getItem(InventoryGRNItem grnItem) {
                return grnItem.getPartNumber().getName();
            }

            @SuppressWarnings("unused")
            public String getPartNumber(InventoryGRNItem grnItem) {
                return grnItem.getPartNumber().getPartNumber();
            }

            @SuppressWarnings("unused")
            public String getBin(InventoryGRNItem grnItem) {
                InventoryLocation bin = grnItem.getBin();
                return bin == null ? "[Not set]" : bin.toDisplay();
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
                grnItem = super.selected();
                if(grnItem == null) {
                    if(size() == 0) {
                        warning("No entries. Please click the 'Edit' button to add items first.");
                    } else {
                        warning("Please select an entry first");
                    }
                }
                return grnItem;
            }

            private void inspectSel() {
                clearAlerts();
                if(selected() == null) {
                    return;
                }
                invokeBin = false;
                inspect();
            }

            private void inspect() {
                InventoryItemType itemType = grnItem.getPartNumber();
                if(itemType.isSerialized() && grnItem.getSerialNumber().isEmpty()) {
                    warning("S/N not set, please enter the S/N for this entry");
                    snEditor().editObject();
                    return;
                }
                InventoryItem item = grnItem.getItem();
                if(item == null) {
                    if(itemType.isSerialized()) {
                        item = InventoryItem.get(grnItem.getSerialNumber(), itemType);
                        if(item != null && item.getSerialNumber().equals(grnItem.getSerialNumber())) {
                            warning("An item with the same " + itemType.getPartNumberShortName() + " already exists: "
                                    + item.toDisplay());
                            snEditor().editObject();
                            return;
                        }
                    }
                    item = itemType.createItem();
                    item.setSerialNumber(grnItem.getSerialNumber());
                    Quantity q = grnItem.getQuantity();
                    item.setQuantity(q);
                    item.setCost(grnItem.getUnitCost().multiply(q));
                    item.setLocation(switch(type) {
                        case 0 -> InventoryTransaction.createSupplierLocation(getTransactionManager(),
                                getObject().getSupplier());
                        case 1 -> InventoryTransaction.createExternalOwnerLocation(getTransactionManager(),
                                getObject().getSupplier());
                        case 2 -> InventoryTransaction.createLoanFromLocation(getTransactionManager(),
                                getObject().getSupplier());
                        default -> null;
                    });
                }
                clearAlerts();
                if(itemEditor != null && itemEditor.getObjectClass() != item.getClass()) {
                    itemEditor = null;
                }
                if(itemEditor == null) {
                    itemEditor = editorProvider.createEditor(item.getClass());
                    itemEditor.setCaption("Inspect Item");
                    itemEditor.setFieldHidden("Location");
                    itemEditor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber", "SerialNumber");
                    //noinspection unchecked
                    itemEditor.setSaver(e -> saveItem());
                }
                //noinspection unchecked
                itemEditor.editObject(item,GRNEditor.this);
            }

            private boolean saveItem() {
                InventoryLocation bin = grnItem.getBin();
                if(!transact(t -> {
                    InventoryItem item;
                    itemEditor.save(t);
                    item = (InventoryItem) itemEditor.getObject();
                    if(!item.getId().equals(grnItem.getItemId())) {
                        grnItem.setItem(item.getId());
                        if(bin != null && !(bin instanceof InventoryStoreBin) && !bin.canBin(item)) {
                            grnItem.setBin(Id.ZERO);
                        }
                        grnItem.save(t);
                    }
                })) {
                    return false;
                }
                itemEditor.close();
                refresh(grnItem);
                InventoryItem ii = grnItem.getItem();
                if(ii.isBlocked()) {
                    if(ii.isServiceable()) {
                        warning("This is a blocked item and its status is still set as serviceable. " +
                                "GRN processing will fail unless the status is changed!");
                    } else {
                        warning("This is a blocked item! Please take appropriate steps to return it.");
                    }
                }
                if(invokeBin) {
                    bin();
                } else {
                    if(Id.isNull(grnItem.getBinId()) && bin != null) {
                        warning("This item can't be stored at the previously selected location '" + bin.toDisplay() +
                                "', Kindly set the correct bin.");
                    }
                }
                return true;
            }

            private void binSel() {
                clearAlerts();
                if(selected() == null) {
                    return;
                }
                bin();
            }

            private void bin() {
                InventoryItem item = grnItem.getItem();
                if(item == null) {
                    warning("Please inspect the item before binning");
                    invokeBin = true;
                    inspect();
                    return;
                }
                clearAlerts();
                if(binEditor == null) {
                    binEditor = new BinEditor();
                }
                binEditor.setGRNItem(grnItem);
                invokeBin = false;
            }

            private void assembleSel() {
                clearAlerts();
                if(selected() == null) {
                    return;
                }
                assemble();
            }

            private void assemble() {
                clearAlerts();
                InventoryItem item = grnItem.getItem();
                if(item.getPartNumber().listAssemblies().findFirst() == null) {
                    warning("Not an assembly: " + item.toDisplay());
                    return;
                }
                new AssemblyReceipt<>(grnItem).execute(GRNEditor.this);
            }

            private SNEditor snEditor() {
                if(snEditor == null) {
                    snEditor = new SNEditor();
                }
                return snEditor;
            }

            private class SNEditor extends GRNItemEditor {

                public SNEditor() {
                    setColumns(1);
                    setWindowMode(true);
                }

                private void editObject() {
                    super.editObject(grnItem);
                }

                @Override
                public void saved(InventoryGRNItem object) {
                    super.saved(object);
                    refresh(object);
                    if(!object.getSerialNumber().isBlank()) {
                        inspect();
                    }
                }
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
                InventoryBin bin = grnItem.getBin();
                if(bin == null) {
                    bin = store.findBin(grnItem.getItem());
                    if(bin == null) {
                        message("Unable to suggest a suitable storage location from prior experience!");
                    }
                }
                binField.setValue(bin);
                execute();
            }

            @Override
            protected void cancel() {
                super.cancel();
                clearAlerts();
            }

            @Override
            protected boolean process() {
                clearAlerts();
                InventoryBin bin = binField.getValue();
                if(bin.getId().equals(grnItem.getBinId())) {
                    return true;
                }
                InventoryItem item = grnItem.getItem();
                if(!item.canBin(bin)) {
                    warning("This item can't be stored at '" + bin.toDisplay() + "'");
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
                cField = new MoneyField("Unit Cost");
            }

            @Override
            protected void buildFields() {
                pnField = new ObjectField<>("Part Number", pnClass, true);
                addField(pnField, snField, qField, cField, bField);
                pnField.addValueChangeListener(e -> pnChanged());
                setRequired(pnField);
                setRequired(qField);
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
                    InventoryBin bin = store.findBin(pn);
                    if(bin != null) {
                        bField.setValue(bin);
                    }
                }
            }

            @Override
            protected boolean process() {
                clearAlerts();
                InventoryItemType pn = pnField.getObject();
                Quantity q = qField.getValue();
                String sn = StoredObject.toCode(snField.getValue());
                if(pn.isSerialized() && q.equals(Count.ONE) && sn.isEmpty()) {
                    warning("Please enter the S/N of the item");
                    return false;
                }
                if(!q.isCompatible(pn.getUnitOfMeasurement())) {
                    warning("Unit used in quantity (" + q + ") is not compatible with the unit of measurement of "
                            + pn.toDisplay());
                    return false;
                }
                InventoryBin bin = bField.getValue();
                if(bin != null && !bin.canBin(pn)) {
                    warning("Storage location selected is not suitable");
                    return false;
                }
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
                qField.setValue(Count.ZERO);
                super.execute(parent, doNotLock);
            }
        }

        private static class ConfirmGrid extends ActionGrid<InventoryGRNItem> {

            public ConfirmGrid(List<InventoryGRNItem> items, String message, Runnable action) {
                super(InventoryGRNItem.class, items, StringList.create("Item.PartNumber", "Item.SerialNumber"),
                        message, action);
                execute();
            }
        }
    }
}
