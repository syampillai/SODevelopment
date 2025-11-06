package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiFunction;

/**
 * GRN - Create, edit and process GRNs.
 *
 * @author Syam
 */
public class GRN extends ObjectBrowser<InventoryGRN> {

    private BiFunction<InventoryStore, Boolean, ObjectBrowser<?>> source;
    private ObjectEditor<InventoryGRN> viewer;
    private final GRNEditor editor;
    private final ELabel storeDisplay = new ELabel("Store: Not selected");
    private final Button switchStore = new Button("Select", (String) null, e -> switchStore()).asSmall();
    private boolean allowSwitchStore = true;
    private final int type;
    private final boolean landedCostModule;
    private boolean searching = false;
    private Search search;
    private final ELabel searchLabel = new ELabel();
    private final ELabel countLabel = new ELabel("0");

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
     * @param store Store.
     * @param actions Allowed edit actions (See {@link EditorAction}).
     * @param caption Caption.
     */
    public GRN(InventoryStore store, int actions, String caption) {
        this(0, InventoryItemType.class, actions, caption, store);
    }

    /**
     * Constructor.
     *
     * @param classNames Class names to be used.
     *                   "Class Name of P/N|Store Name".
     */
    public GRN(String classNames) {
        this(type(classNames), ParameterParser.itemTypeClass(classNames), EditorAction.ALL, null,
                ParameterParser.store(classNames));
    }

    private static int type(String classNames) {
        int type = ParameterParser.number(classNames, 2);
        return switch (type) {
            case 3, 5 -> type;
            default -> 0;
        };
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
        landedCostModule = StoredObject.exists(LandedCostType.class, "true");
        this.type = type;
        editor = new GRNEditor(type, pnClass, actions, caption);
        if(store == null) {
            store = SelectStore.getStore();
        }
        if(store != null) {
            InventoryStore finalStore = store;
            addConstructedListener(f -> setStore(finalStore, true));
        } else {
            setFixedFilter("Status<2 AND Type=" + type, false);
        }
        addConstructedListener(f -> con());
        setCaption("GRN (" + InventoryGRN.getTypeValues()[type] + ")");
        GridContextMenu<InventoryGRN> cm = new GridContextMenu<>(this);
        GridMenuItem<InventoryGRN> process =
                cm.addItem("Receive/Process", e -> e.getItem().ifPresent(i -> edit.click()));
        GridMenuItem<InventoryGRN> landedCost =
                cm.addItem("Landed Cost", e -> e.getItem().ifPresent(this::computeLandedCost));
        cm.setDynamicContentHandler(grn -> {
            deselectAll();
            if(grn == null) {
                return false;
            }
            select(grn);
            process.setVisible(grn.getStatus() <= 1);
            landedCost.setVisible(landedCostModule && grn.getStatus() > 0);
            return process.isVisible() || landedCost.isVisible();
        });
    }

    private void computeLandedCost(InventoryGRN grn) {
        clearAlerts();
        try {
            new ComputeLandedCost(grn, this).execute(getView());
        } catch(SOException e) {
            warning(e);
        }
    }

    public void setAllowSwitchStore(boolean allowSwitchStore) {
        this.allowSwitchStore = allowSwitchStore;
        switchStore.setVisible(allowSwitchStore);
    }

    public void setStore(InventoryStore store) {
        setStore(store, false);
    }

    public void setStore(InventoryStore store, boolean allowSwitchStore) {
        ObjectField<?> storeField = (ObjectField<?>) editor.getAnchorField("Store");
        storeField.setReadOnly(store != null);
        if(store != null) {
            storeField.setObject(store);
            editor.executeAnchorForm();
        }
        switchStore.setVisible(store == null || allowSwitchStore);
        setFixedFilter("Status<2 AND Type=" + type);
    }

    public void processGRN(InventoryGRN grn) {
        if(type != grn.getType()) {
            return;
        }
        select(grn);
        doEdit(grn, true);
    }

    public void viewGRN(InventoryGRN grn) {
        if(type != grn.getType()) {
            return;
        }
        close();
        doView(grn);
    }

    private void con() {
        if(edit != null) {
            edit.setText("Receive / Process");
        }
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
        ButtonLayout b = new ButtonLayout();
        b.add(storeDisplay, switchStore, new ELabel().
                append(" | ", Application.COLOR_INFO).
                append("Note: ").
                append("Double-click or right-click on the entry to receive/process items",
                        Application.COLOR_SUCCESS).
                update(), searchLabel, new ELabel("| ", Application.COLOR_INFO).append("Entries:").update(),
                countLabel);
        prependHeader().join().setComponent(b);
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("Supplier".equals(columnName)) {
            return InventoryGRN.getTypeValues()[type];
        }
        return super.getColumnCaption(columnName);
    }

    @Override
    protected final ObjectEditor<InventoryGRN> createObjectEditor() {
        return editor;
    }

    @Override
    protected void addExtraButtons() {
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter("Type=" + type + (e.getValue() ? "" : " AND Status<2")));
        buttonPanel.add(new Button("Search", e -> searchFilter()), h);
        buttonPanel.add(new Button("View Source", e -> viewSource()));
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
    public void doAdd() {
        if(source == null) {
            return;
        }
        ObjectBrowser<?> source = this.source.apply(editor.store, allowSwitchStore);
        if(source == null) {
            return;
        }
        close();
        source.execute();
        source.load();
    }

    @Override
    public void doEdit(InventoryGRN object) {
        doEdit(object, false);
    }

    private void doEdit(InventoryGRN object, boolean close) {
        if(object == null) {
            return;
        }
        editor.setObject(object);
        if(!editor.canEdit()) {
            return;
        }
        if(close) {
            close();
            editor.execute();
        } else {
            editor.execute(getView());
        }
    }

    @Override
    public void doView(InventoryGRN object) {
        if(object == null) {
            return;
        }
        if(viewer == null) {
            viewer = ObjectEditor.create(InventoryGRN.class);
        }
        viewer.setCaption("GRN: " + object.getReference());
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
        switchStore.setText("Change");
        this.storeDisplay.clearContent().append("Store: ").append(editor.store, Application.COLOR_SUCCESS).update();
    }

    @Override
    protected void anchorsSet() {
        displayStore();
        if(allowSwitchStore) {
            editor.getAnchorField("Store").setReadOnly(false);
        }
    }

    @Override
    protected void anchorsCancelled() {
        if(editor.store == null) {
            close();
        } else {
            anchorsSet();
        }
    }

    /**
     * Create a supplier list for the given type of GRN.
     *
     * @param type Type of GRN.
     * @return List of supplier entities.
     */
    public static List<Entity> suppliers(int type) {
        type = switch(type) {
            case 0 -> 1;
            case 1 -> 17;
            case 2 -> 9;
            case 3 -> 3;
            case 4 -> 2;
            default -> -1;
        };
        return StoredObject.list(InventoryVirtualLocation.class, "Type=" + type + " AND Status=0").
                map(InventoryVirtualLocation::getEntity).toList();
    }

    /**
     * This method is invoked when the button is pressed to mark the GRN as inspected/received. This method may show
     * appropriate messages and may return <code>false</code> if some other associated data is incomplete.
     *
     * @param grn Current GRN.
     * @return True/false.
     */
    protected boolean canFinalize(InventoryGRN grn) {
        return true;
    }

    private class GRNEditor extends ObjectEditor<InventoryGRN> {

        private final int type;
        private ObjectField<Entity> supplierField;
        private ObjectLinkField<InventoryGRNItem> grnItemsField;
        private DateField dateField;
        private GRNItemGrid grnItemGrid;
        private InventoryStore  store;
        private final NewGRNItemForm newGRNItemForm = new NewGRNItemForm();
        private final Button process = new Button("Mark as Inspected", VaadinIcon.CHECK, e -> process());
        private final Button close = new Button("Mark as Received", VaadinIcon.THUMBS_UP_O, e -> process());
        private final Button editItem = new Button("Edit", e -> grnItemGrid.editGRNItemSel()).asSmall();
        private final Button splitQty = new Button("Split Quantity", VaadinIcon.SPLIT,
                e -> grnItemGrid.splitQuantitySel()).asSmall();
        private final Button inspect = new Button("Inspect", VaadinIcon.STOCK, e -> grnItemGrid.inspectSel())
                .asSmall();
        private final Button bin = new Button("Bin", VaadinIcon.STORAGE, e -> grnItemGrid.binSel()).asSmall();
        private final Button assemble = new Button("Assemble", VaadinIcon.COMPILE, e -> grnItemGrid.assembleSel())
                .asSmall();
        private final ELabel hint =
                new ELabel("You may also right-click on the entry to edit/inspect/bin/assemble.", Application.COLOR_SUCCESS);
        private final Class<? extends InventoryItemType> pnClass;

        GRNEditor(int type, Class<? extends InventoryItemType> pnClass, int actions, String caption) {
            super(InventoryGRN.class, actions & (~EditorAction.SEARCH), caption);
            addField("Reference");
            addField("Status", InventoryGRN::getStatus, (grn, v) -> {});
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
            setFieldReadOnly("Type", "Items.l");
        }

        @Override
        public boolean isFieldEditable(String fieldName) {
            if("Date".equals(fieldName)) {
                return getObject().getStatus() == 0;
            }
            return super.isFieldEditable(fieldName);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if(supplierField != null && "Supplier".equals(fieldName)) {
                supplierField.setLabel(InventoryGRN.getTypeValues()[type]);
                return supplierField;
            }
            return super.createField(fieldName, label);
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Date".equals(fieldName)) {
                dateField = (DateField) field;
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
            store = (InventoryStore) ((ObjectField<?>)getAnchorField("Store")).getObject();
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
            if(grn.getStatus() == 2) {
                warn("Status: " + grn.getStatusValue() + ". Can't edit");
                return false;
            }
            setFieldReadOnly(grn.getStatus() == 1, dateField);
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
                warn("Entry is already processed. Deletion not possible at this stage.");
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
            if(canFinalize(grn)) {
                if(grn.getStatus() == 1) {
                    preCloseGRN(grn);
                } else {
                    preProcessGRN(grn);
                }
            }
        }

        private void preProcessGRN(InventoryGRN grn) {
            preProcessGRN(grn, false);
        }

        private void preProcessGRN(InventoryGRN grn, boolean skipInspection) {
            clearAlert();
            List<InventoryGRNItem> list = grn.listLinks(InventoryGRNItem.class).toList();
            for(InventoryGRNItem gi1 : list) {
                InventoryItemType iit = gi1.getPartNumber();
                if(!iit.isSerialized()) {
                    continue;
                }
                String s1 = StoredObject.toCode(gi1.getSerialNumber());
                for(InventoryGRNItem gi2 : list) {
                    if(gi1 == gi2 || !gi1.getPartNumberId().equals(gi2.getPartNumberId())) {
                        continue;
                    }
                    if(s1.equals(StoredObject.toCode(gi2.getSerialNumber()))) {
                        warn("Duplicate S/N " + s1 + " for " + iit.toDisplay());
                        gi2.setItem((InventoryItem) null);
                        transact(gi2::save);
                        reload();
                        message("One of them marked as 'Not Inspected' now");
                        return;
                    }
                }
            }
            if(skipInspection) {
                InventoryItem item;
                Transaction transaction = null;
                try {
                    transaction = getTransactionManager().createTransaction();
                    for(InventoryGRNItem gi : list) {
                        InventoryBin bin = Id.isNull(gi.getBinId()) ? store.findBin(gi.getPartNumber()) : null;
                        if(Id.isNull(gi.getItemId())) {
                            item = createItem(gi);
                            item.save(transaction);
                            gi.setItem(item);
                            if(bin != null) {
                                gi.setBin(bin);
                            }
                            gi.save(transaction);
                        } else if(bin != null) {
                            gi.setBin(bin);
                            gi.save(transaction);
                        }
                    }
                    transaction.commit();
                    transaction = null;
                } catch(Exception e) {
                    error(e);
                } finally {
                    if(transaction != null) {
                        transaction.rollback();
                    }
                }
            } else {
                InventoryGRNItem grnItem = list.stream()
                        .filter(gi -> Id.isNull(gi.getItemId()) && gi.getPartNumber().isSerialized())
                        .findAny().orElse(null);
                if(grnItem != null) {
                    warn("Kindly inspect - " + grnItem.toDisplay());
                    return;
                }
                if(list.stream()
                        .anyMatch(gi -> Id.isNull(gi.getItemId()) && !gi.getPartNumber().isSerialized())) {
                    list.removeIf(gi -> !Id.isNull(gi.getItemId()));
                    new ConfirmGrid(list, inspectPrompt(list), () -> preProcessGRN(grn, true));
                    return;
                }
            }
            list.removeIf(gi -> !Id.isNull(gi.getBinId()));
            if(list.isEmpty()) {
                processGRN(grn);
                return;
            }
            new ConfirmGrid(list, noBinPrompt(list), () -> processGRN(grn));
        }

        private static String noBinPrompt(List<InventoryGRNItem> list) {
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
            return m;
        }

        private static String inspectPrompt(List<InventoryGRNItem> list) {
            String m = "The following item";
            if(list.size() > 1) {
                m += "s are";
            } else {
                m += "is";
            }
            m += " not inspected and binned. Th";
            if(list.size() > 1) {
                m += "ese";
            } else {
                m += "is";
            }
            m += " will be auto-created and auto-binned (if possible)!";
            return m;
        }

        private boolean isAssemblyIncomplete(InventoryGRNItem grnItem) {
            InventoryItem item = grnItem.getItem();
            return item != null && item.isAssemblyIncomplete();
        }

        private void preCloseGRN(InventoryGRN grn) {
            clearAlert();
            List<InventoryGRNItem> list = new ArrayList<>();
            grn.listLinks(InventoryGRNItem.class).filter(this::isAssemblyIncomplete).collectAll(list);
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
                    filter(gi -> !gi.getInspected() && isAssemblyIncomplete(gi)).collectAll(list);
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
            editItem.setVisible(status == 0);
            splitQty.setVisible(status == 0);
            inspect.setVisible(status == 0);
            bin.setVisible(status == 0);
            assemble.setVisible(status == 1);
            hint.setVisible(status == 0 || status == 1);
        }

        private void disableExtraButtons() {
            editItem.setVisible(false);
            splitQty.setVisible(false);
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

        private void clearAlert() {
            clearAlerts();
        }
        
        private void warn(Object any) {
            warning(any);
        }

        private InventoryItem createItem(InventoryGRNItem grnItem) {
            InventoryItem item = grnItem.getPartNumber().createItem();
            item.setSerialNumber(grnItem.getSerialNumber());
            Quantity q = grnItem.getQuantity();
            item.setQuantity(q);
            item.setCost(grnItem.getUnitCost().multiply(q));
            item.setLocation(location());
            return item;
        }

        private InventoryLocation location() {
            return switch(type) {
                case 0 -> InventoryTransaction.createSupplierLocation(getTransactionManager(),
                        getObject().getSupplier());
                case 1 -> InventoryTransaction.createExternalOwnerLocation(getTransactionManager(),
                        getObject().getSupplier());
                case 2 -> InventoryTransaction.createLoanFromLocation(getTransactionManager(),
                        getObject().getSupplier());
                case 3 -> InventoryTransaction.createRepairLocation(getTransactionManager(),
                        getObject().getSupplier());
                case 4 -> InventoryTransaction.createConsumerLocation(getTransactionManager(),
                        getObject().getSupplier());
                default -> null;
            };
        }

        private class GRNItemGrid extends DetailLinkGrid<InventoryGRNItem> {

            @SuppressWarnings("rawtypes")
            private ObjectEditor itemEditor;
            private BinEditor binEditor;
            private SNEditor snEditor;
            private InventoryGRNItem grnItem;
            private boolean invokeBin = false;
            private EditGRNItem editGRNItem;
            private SplitQuantity splitQuantity;

            public GRNItemGrid() {
                super(grnItemsField, false);
                setObjectEditor(new GRNItemEditor());
                getButtonPanel().add(editItem, splitQty, inspect, bin, assemble, hint);
                ItemContextMenu<InventoryGRNItem> contextMenu = new ItemContextMenu<>(this);
                contextMenu.setHideGRNDetails(true);
                GridMenuItem<InventoryGRNItem> split = contextMenu.addItem("Split Quantity",
                        e -> e.getItem().ifPresent(x -> splitQuantity()));
                GridMenuItem<InventoryGRNItem> inspectRC = contextMenu.addItem("Inspect",
                        e -> e.getItem().ifPresent(x -> inspect()));
                GridMenuItem<InventoryGRNItem> binRC = contextMenu.addItem("Bin",
                        e -> e.getItem().ifPresent(x -> bin()));
                GridMenuItem<InventoryGRNItem> assembleRC = contextMenu.addItem("Assemble",
                        e -> e.getItem().ifPresent(x -> assemble()));
                GridMenuItem<InventoryGRNItem> editGRNItem = contextMenu.addItem("Edit",
                        e -> e.getItem().ifPresent(x -> editGRNItem()));
                contextMenu.setDynamicContentHandler(r -> {
                    if(r == null) {
                        return false;
                    }
                    select(r);
                    grnItem = r;
                    split.setVisible(splitQty.isVisible() && canSplitQty());
                    inspectRC.setVisible(inspect.isVisible());
                    binRC.setVisible(bin.isVisible());
                    assembleRC.setVisible(assemble.isVisible());
                    invokeBin = false;
                    editGRNItem.setVisible(editItem.isVisible() && grnItem.getItem() == null);
                    return inspectRC.isVisible() || binRC.isVisible() || assembleRC.isVisible()
                            || editGRNItem.isVisible() || split.isVisible();
                });
            }

            @SuppressWarnings("unused")
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
                        warn("No entries.");
                    } else {
                        warn("Please select an entry first");
                    }
                }
                return grnItem;
            }

            private void editGRNItemSel() {
                clearAlert();
                if(selected() == null) {
                    return;
                }
                editGRNItem();
            }

            private void editGRNItem() {
                if(editGRNItem == null) {
                    editGRNItem = new EditGRNItem();
                }
                editGRNItem.execute();
            }

            private class EditGRNItem extends DataForm {

                private final ELabelField itemField = new ELabelField("Item");
                private final TextField snField = new TextField("Serial/Batch Number");
                private final QuantityField quantityField = new QuantityField("Quantity");
                private final MoneyField ucField = new MoneyField("Unit Cost");

                public EditGRNItem() {
                    super("Edit Details");
                    snField.capitalize();
                    snField.addValueChangeListener(e -> {
                        if(e.isFromClient()) {
                            snField.setValue(StoredObject.toCode(snField.getValue()));
                        }
                    });
                    quantityField.addValueChangeListener(e -> {
                        if(e.isFromClient()) {
                            qtyChanged(e.getOldValue());
                        }
                    });
                    addField(itemField, snField, quantityField, ucField);
                }

                @Override
                protected void execute(View parent, boolean doNotLock) {
                    clearAlert();
                    if(grnItem.getItem() != null) {
                        warn("Can't edit, item was already created!");
                        return;
                    }
                    InventoryPOItem poItem;
                    if((poItem = grnItem.getPOItem()) == null) {
                        warn("Can't determine the corresponding PO!");
                        return;
                    }
                    quantityField.setMaximumAllowed(poItem.getQuantity().subtract(poItem.getReceived())
                            .add(grnItem.getQuantity()));
                    itemField.clearContent().append(grnItem.getPartNumber()).update();
                    snField.setValue(grnItem.getSerialNumber());
                    quantityField.setValue(grnItem.getQuantity());
                    ucField.setValue(grnItem.getUnitCost());
                    super.execute(parent, doNotLock);
                }

                private void qtyChanged(Quantity oldValue) {
                    Quantity qty = quantityField.getValue();
                    if(qty.getUnit().equals(oldValue.getUnit())) {
                        return;
                    }
                    UnitCost uc = new UnitCost(grnItem.getUnitCost(), grnItem.getQuantity().getUnit());
                    ucField.setValue(uc.getUnitCost(qty.getUnit()).getCost());
                }

                @Override
                protected boolean process() {
                    close();
                    try {
                        Quantity q = quantityField.getValue();
                        if(grnItem.updateValues(getTransactionManager(), quantityField.getValue(), ucField.getValue(),
                                snField.getValue())) {
                            if(q.isZero()) {
                                GRNEditor.this.reload();
                            } else {
                                refresh(grnItem);
                            }
                        } else {
                            message("No changes specified");
                        }
                    } catch(Exception e) {
                        message("Changes rejected");
                        warn(e);
                    }
                    return true;
                }
            }

            private void inspectSel() {
                clearAlert();
                if(selected() == null) {
                    return;
                }
                invokeBin = false;
                inspect();
            }

            private void inspect() {
                clearAlert();
                InventoryItemType itemType = grnItem.getPartNumber();
                if(itemType.isSerialized() && grnItem.getSerialNumber().isEmpty()) {
                    warn("S/N not set, please enter the S/N for this entry");
                    snEditor().editObject();
                    return;
                }
                InventoryItem item = grnItem.getItem();
                if(item == null) {
                    if(itemType.isSerialized()) {
                        item = InventoryItem.get(grnItem.getSerialNumber(), itemType);
                        if(item != null && item.getSerialNumber().equals(grnItem.getSerialNumber())) {
                            if(!item.getLocation().canResurrect()) {
                                warn("An item with the same " + itemType.getPartNumberShortName()
                                        + " already exists: " + item.toDisplay());
                                snEditor().editObject();
                                return;
                            }
                            item.resurrect(grnItem.getUnitCost(), location());
                        } else {
                            item = null;
                        }
                    }
                    if(item == null) {
                        item = createItem(grnItem);
                    }
                }
                if(itemEditor != null && itemEditor.getObjectClass() != item.getClass()) {
                    itemEditor = null;
                }
                if(itemEditor == null) {
                    itemEditor = ObjectEditor.create(item.getClass());
                    itemEditor.setCaption("Inspect " + StringUtility.makeLabel(item.getPartNumber().getClass()));
                    itemEditor.setFieldHidden("Location");
                    itemEditor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber");
                    if(itemType.isSerialized()) {
                        itemEditor.setFieldReadOnly("SerialNumber");
                    }
                    //noinspection unchecked
                    itemEditor.setSaver(o -> saveItem());
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
                if(ii != null && ii.isBlocked()) {
                    if(ii.isServiceable()) {
                        warn("This is a blocked item and its status is still set as serviceable. " +
                                "GRN processing will fail unless the status is changed!");
                    } else {
                        warn("This is a blocked item! Please take appropriate steps to return it.");
                    }
                }
                if(invokeBin) {
                    bin();
                } else {
                    if(Id.isNull(grnItem.getBinId()) && bin != null) {
                        warn("This item can't be stored at the previously selected location '"
                                + bin.toDisplay() + "', Kindly set the correct bin.");
                    }
                }
                return true;
            }

            private void binSel() {
                clearAlert();
                if(selected() == null) {
                    return;
                }
                bin();
            }

            private void bin() {
                InventoryItem item = grnItem.getItem();
                if(item == null) {
                    warn("Please inspect the item before binning");
                    invokeBin = true;
                    inspect();
                    return;
                }
                clearAlert();
                if(binEditor == null) {
                    binEditor = new BinEditor();
                }
                binEditor.setGRNItem(grnItem);
                invokeBin = false;
            }

            private void assembleSel() {
                clearAlert();
                if(selected() == null) {
                    return;
                }
                assemble();
            }

            private void assemble() {
                clearAlert();
                InventoryItem item = grnItem.getItem();
                if(item != null && item.getPartNumber().listAssemblies().findFirst() == null) {
                    warn("Not an assembly: " + item.toDisplay());
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

            private void splitQuantitySel() {
                clearAlert();
                if(selected() == null) {
                    return;
                }
                if(!canSplitQty()) {
                    if(grnItem.getPartNumber().isSerialized()) {
                        warn("Can't split quantity for that item");
                    } else {
                        warn("Can't split quantity for that because the item was already created");
                    }
                    return;
                }
                splitQuantity();
            }

            private void splitQuantity() {
                if(splitQuantity == null) {
                    splitQuantity = new SplitQuantity();
                }
                splitQuantity.execute();
            }

            private boolean canSplitQty() {
                return grnItem != null && !grnItem.getPartNumber().isSerialized() && Id.isNull(grnItem.getItemId());
            }

            private class SplitQuantity extends DataForm {

                private final QuantityField qFieldCurrent = new QuantityField("Current Quantity");
                private final QuantityField qField = new QuantityField("Quantity to Split");

                public SplitQuantity() {
                    super("Split Quantity");
                    addField(qFieldCurrent, qField);
                    setFieldReadOnly(qFieldCurrent);
                    setRequired(qField);
                }

                @Override
                protected void execute(View parent, boolean doNotLock) {
                    qFieldCurrent.setValue(grnItem.getQuantity());
                    qField.setMaximumAllowed(grnItem.getQuantity());
                    super.execute(parent, doNotLock);
                }

                @Override
                protected boolean process() {
                    clearAlert();
                    Quantity q = qField.getValue(), gq = grnItem.getQuantity();
                    if(q.isZero() || q.isGreaterThan(gq) || !q.isCompatible(gq)
                            || (q.equals(gq) && q.getUnit().equals(gq.getUnit()))) {
                        warn("Please check the quantity");
                        return false;
                    }
                    if(GRN.this.transact(t -> grnItem.splitQuantity(t, q))) {
                        GRNEditor.this.reload();
                        return true;
                    }
                    return false;
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
                binField.setStore(() -> store, false);
                binField.setItem(() -> grnItem == null ? null : grnItem.getItem(), false);
                setRequired(binField);
            }

            public void setGRNItem(InventoryGRNItem grnItem) {
                this.grnItem = grnItem;
                InventoryItem item = grnItem.getItem();
                itemField.clearContent().append(item == null ? "<None>" : item.toDisplay()).update();
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
                clearAlert();
            }

            @Override
            protected boolean process() {
                clearAlert();
                InventoryBin bin = binField.getValue();
                if(bin.getId().equals(grnItem.getBinId())) {
                    return true;
                }
                InventoryItem item = grnItem.getItem();
                if(item == null || !item.canBin(bin)) {
                    warn("This item can't be stored at '" + bin.toDisplay() + "'");
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
                setCaption("GRN Item");
            }

            @Override
            protected void formConstructed() {
                super.formConstructed();
                setFieldReadOnly("PartNumber");
                setFieldHidden("Inspected");
                BinField bf = (BinField)(((ObjectField<?>)getField("Bin")).getField());
                bf.setStore(() -> store, false);
                @SuppressWarnings("unchecked") ObjectField<? extends InventoryItem> iField =
                        (ObjectField<? extends InventoryItem>) getField("Item");
                bf.setItem(iField, false);
                @SuppressWarnings("unchecked") ObjectField<? extends InventoryItemType> itField =
                        (ObjectField<? extends InventoryItemType>) getField("PartNumber");
                bf.setItemType(itField, false);
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

            @Override
            public void clean() {
                super.clean();
                clearAlert();
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
                bField.setStore(() -> store, false);
                bField.setItemType(pnField, false);
                pnField.addValueChangeListener(e -> pnChanged());
                setRequired(pnField);
                setRequired(qField);
            }

            private void pnChanged() {
                InventoryItemType pn = pnField.getObject();
                if(pn != null) {
                    bField.reload();
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
                clearAlert();
                InventoryItemType pn = pnField.getObject();
                Quantity q = qField.getValue();
                String sn = StoredObject.toCode(snField.getValue());
                if(pn.isSerialized() && q.equals(Count.ONE) && sn.isEmpty()) {
                    warn("Please enter the S/N of the item");
                    return false;
                }
                if(!q.isCompatible(pn.getUnitOfMeasurement())) {
                    warn("Unit used in quantity (" + q +
                            ") is not compatible with the unit of measurement of " + pn.toDisplay());
                    return false;
                }
                InventoryBin bin = bField.getValue();
                if(bin != null && !bin.canBin(pn)) {
                    warn("Storage location selected is not suitable");
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
                InventoryGRNItem newGrnItem;
                for(int i = 0; i < n; i++) {
                    newGrnItem = new InventoryGRNItem();
                    newGrnItem.setPartNumber(pn);
                    newGrnItem.setSerialNumber(i == 0 ? sn : "");
                    newGrnItem.setQuantity(q);
                    newGrnItem.setUnitCost(cost);
                    newGrnItem.setBin(bin);
                    grnItemsField.add(newGrnItem);
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
                super(InventoryGRNItem.class, items,
                        StringList.create("Item", "PartNumber", "SerialNumber AS Serial/Batch", "Quantity"),
                        message, action);
                execute();
            }

            @SuppressWarnings("unused")
            public String getItem(InventoryGRNItem gi) {
                return gi.getPartNumber().getName();
            }

            @SuppressWarnings("unused")
            public String getPartNumber(InventoryGRNItem gi) {
                return gi.getItem() == null ? gi.getPartNumber().getPartNumber() :
                        gi.getItem().getPartNumber().getPartNumber();
            }

            @SuppressWarnings("unused")
            public String getSerialNumber(InventoryGRNItem gi) {
                String sn = gi.getItem() == null ? null :  gi.getItem().getSerialNumberDisplay();
                if(sn == null) {
                    sn = gi.getPartNumber().getSerialNumberShortName() + ": " + gi.getSerialNumber();
                }
                return sn;
            }
        }
    }

    @Override
    public void loaded() {
        if(searching) {
            searching = false;
            setLoadFilter(null, false);
        } else {
            searchLabel.clearContent().update();
        }
        countLabel.clearContent().append(String.valueOf(size()), Application.COLOR_SUCCESS).update();
    }

    private void viewSource() {
        InventoryGRN grn = selected();
        if(grn == null) {
            return;
        }
        Application a = Application.get();
        grn.listMasters(StoredObject.class, true)
                .forEach(m -> a.view(grn.getReference() + " - " + StringUtility.makeLabel(m.getClass()), m));
    }

    @Override
    public boolean canSearch() {
        return false;
    }

    private void searchFilter() {
        if(search == null) {
            search = new Search();
        }
        search.execute();
    }

    private class Search extends DataForm {

        private final ChoiceField search = new ChoiceField("Search",
                new String[] { "Part Number", "Date Period", "No.", InventoryGRN.getTypeValues()[type] });
        private final ObjectGetField<InventoryItemType> pnField =
                new ObjectGetField<>("Part Number", InventoryItemType.class, true);
        private final DatePeriodField periodField = new DatePeriodField("Date Period");
        private final IntegerField noField = new IntegerField("No.");
        private final ObjectField<Entity> supplierField;

        public Search() {
            super("Search");
            supplierField = new ObjectField<>(InventoryGRN.getTypeValues()[type], suppliers(type));
            supplierField.setVisible(false);
            noField.setVisible(false);
            periodField.setVisible(false);
            search.addValueChangeListener(e -> vis());
            addField(search, pnField, periodField, noField, supplierField);
        }

        private void vis() {
            int s = search.getValue();
            pnField.setVisible(s == 0);
            periodField.setVisible(s == 1);
            noField.setVisible(s == 2);
            supplierField.setVisible(s == 3);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            vis();
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            GRN.this.clearAlerts();
            int s = search.getValue();
            searching = true;
            String filter = null;
            switch(s) {
                case 0 -> {
                    InventoryItemType pn = pnField.getValue();
                    if(pn == null) {
                        searching = false;
                        return true;
                    }
                    filter = "Contains " + pn.toDisplay();
                    Id pnId = pn.getId();
                    setLoadFilter(p -> p.existsLinks(InventoryGRNItem.class, "PartNumber=" + pnId, true));
                }
                case 1 -> {
                    DatePeriod period = periodField.getValue();
                    filter = "Period = " + period;
                    setLoadFilter(p -> period.inside(p.getDate()));
                }
                //noinspection DuplicatedCode
                case 2 -> {
                    int no = noField.getValue();
                    if(no <= 0) {
                        searching = false;
                        return true;
                    }
                    filter = "No. = " + no;
                    setLoadFilter(p -> p.getNo() == no);
                }
                case 3 -> {
                    Entity supplier = supplierField.getObject();
                    if(supplier == null) {
                        searching = false;
                        return true;
                    }
                    filter = "From " + supplier.toDisplay();
                    Id sid = supplier.getId();
                    setLoadFilter(p -> p.getSupplierId().equals(sid));
                }
            }
            if(filter != null) {
                searchLabel.clearContent().append(" | ", Application.COLOR_INFO)
                        .append(" Filter: ", Application.COLOR_ERROR)
                        .append(filter, Application.COLOR_INFO).update();
            }
            return true;
        }
    }

    public void setSource(String label, Class<?> browserClass, Class<? extends StoredObject> soClass) {
        this.source = null;
        if(browserClass == null || label == null) {
            return;
        }
        String sourceName = browserClass.getName();
        Method m = null;
        while (m == null) {
            if(browserClass == Object.class) {
                break;
            }
            try {
                m = browserClass.getDeclaredMethod("createNew", Class.class, InventoryStore.class, boolean.class);
                int modifier = m.getModifiers();
                if(Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
                    break;
                }
            } catch (NoSuchMethodException ignored) {
            }
            browserClass = browserClass.getSuperclass();
        }
        if(m == null) {
            error(label + " - Method not found:  public static void createNew(InventoryStore store, boolean allowSwitching) in "
                    + sourceName);
            return;
        }
        try {
            Method finalM = m;
            this.source = (store, allowed) -> {
                Application a = Application.get();
                try {
                    Object ob = finalM.invoke(null, soClass, store, allowed);
                    return (ObjectBrowser<?>) ob;
                } catch (Throwable e) {
                    log(e);
                    a.access(() -> error("Unable to jump to " + label));
                }
                return null;
            };
        } catch (Exception e) {
            error(e);
        }
        if(add != null) {
            add.setVisible(true);
            add.setText(label);
            add.setIcon(VaadinIcon.FILE_TABLE);
        }
    }
}
