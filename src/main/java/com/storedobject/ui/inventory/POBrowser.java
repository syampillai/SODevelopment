package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.storedobject.core.EditorAction.ALL;

public class POBrowser<T extends InventoryPO> extends ObjectBrowser<T> implements ObjectEditorProvider {
    protected final Button switchStore = new Button("Not selected", (String) null, e -> switchStore()).asSmall();
    protected final Button goToGRNs = new Button("GRNs", VaadinIcon.STOCK, e -> processGRN(null));
    private POEditor<T> editor;
    private final List<ProcessButton> processButtons = new ArrayList<>();
    private final GridContextMenu<T> contextMenu;
    String filter = "Status<4";
    private boolean allowSwitchStore = true;
    private boolean searching = false;
    private Search search;
    private final ELabel searchLabel = new ELabel();
    private final ELabel countLabel = new ELabel("0");

    public POBrowser(Class<T> objectClass) {
        this(objectClass, (String)null);
    }

    public POBrowser(Class<T> objectClass, String caption) {
        this(objectClass, ALL, caption);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns) {
        this(objectClass, browseColumns, ALL);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, ALL, filterColumns);
    }

    public POBrowser(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public POBrowser(Class<T> objectClass, int actions, String caption) {
        this(objectClass, null,
                actions, caption);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, null, null);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                     Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, null, caption);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions,
                     Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
                  String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns, caption, allowedActions);
        switchStore.getElement().setAttribute("title", "Click to select the store");
        addConstructedListener(b -> {
            editor();
            setFixedFilter(filter, false);
            if(getObjectClass() == InventoryPO.class) {
                setCaption("Purchase Order");
            }
        });
        contextMenu = new GridContextMenu<>(this);
        GridMenuItem<T> approveOrder = contextMenu.addItem("Approve the Order",
                e -> e.getItem().ifPresent(i -> approveOrder()));
        GridMenuItem<T> placeOrder = contextMenu.addItem("Place the Order",
                e -> e.getItem().ifPresent(i -> placeOrder()));
        GridMenuItem<T> receiveItems = contextMenu.addItem("Receive Items",
                e -> e.getItem().ifPresent(i -> receiveItems()));
        GridMenuItem<T> foreClose = contextMenu.addItem("Foreclose",
                e -> e.getItem().ifPresent(i -> foreclosePO()));
        GridMenuItem<T> close = contextMenu.addItem("Close",
                e -> e.getItem().ifPresent(i -> closePO()));
        GridMenuItem<T> preGRNs = contextMenu.addItem("Process Associated GRNs",
                e -> e.getItem().ifPresent(i -> preProcessGRNs()));
        GridMenuItem<T> viewGRNs = contextMenu.addItem("View Associated GRNs",
                e -> e.getItem().ifPresent(i -> preViewGRNs()));
        contextMenu.setDynamicContentHandler(po -> {
            deselectAll();
            if(po == null) {
                return false;
            }
            select(po);
            processButtons.forEach(b -> b.menu.setVisible(b.check.test(po)));
            int status = po.getStatus();
            viewGRNs.setVisible(status > 1);
            if(status > 0) {
                placeOrder.setVisible(false);
                approveOrder.setVisible(false);
            }
            switch(status) {
                case 0 -> {
                    if(po.getApprovalRequired()) {
                        approveOrder.setVisible(canApprovePO(po));
                        placeOrder.setVisible(false);
                    } else {
                        placeOrder.setVisible(canPlaceOrder(po));
                        approveOrder.setVisible(false);
                    }
                    receiveItems.setVisible(false);
                    foreClose.setVisible(canClosePO(po));
                    close.setVisible(false);
                    preGRNs.setVisible(false);
                }
                case 1 -> {
                    receiveItems.setVisible(canReceiveItems(po));
                    foreClose.setVisible(canClosePO(po));
                    close.setVisible(false);
                    preGRNs.setVisible(false);
                }
                case 2 -> {
                    receiveItems.setVisible(canReceiveItems(po));
                    foreClose.setVisible(canClosePO(po));
                    close.setVisible(false);
                    preGRNs.setVisible(canProcessGRN(po));
                }
                case 3 -> {
                    receiveItems.setVisible(false);
                    foreClose.setVisible(false);
                    close.setVisible(canClosePO(po));
                    preGRNs.setVisible(canProcessGRN(po));
                }
                case 4 -> {
                    receiveItems.setVisible(false);
                    foreClose.setVisible(false);
                    close.setVisible(false);
                    preGRNs.setVisible(canProcessGRN(po));
                }
            }
            return true;
        });
        InventoryStore store = SelectStore.getStore();
        if(store != null) {
            setStore(store, true);
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
        if(isEmpty()) {
            load.setIcon("load");
            load.setText("Load");
        } else {
            load.setIcon("reload");
            load.setText("Reload");
        }
    }

    @Override
    public boolean canSearch() {
        return false;
    }

    @Override
    public String getOrderBy() {
        return "Store,Date DESC,No DESC";
    }

    @Override
    protected void addExtraButtons() {
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : filter));
        buttonPanel.add(actionAllowed("AMEND") ? new ConfirmButton("Amend", e -> amend()) : null,
                actionAllowed("SEARCH") ? new Button("Search", e -> searchPO()) : null,
                h, actionAllowed("GO-TO-GRN") ? goToGRNs : null);
        super.addExtraButtons();
    }

    @Override
    public void createHeaders() {
        ButtonLayout b = new ButtonLayout();
        b.add(new ELabel("Store:"), switchStore, new ELabel().
                append(" | ", Application.COLOR_INFO).
                append("Note: ").
                append("Right-click on the entry for available process options", Application.COLOR_SUCCESS).
                update(), searchLabel, new ELabel("| ", Application.COLOR_INFO).append("Entries:").update(),
                countLabel);
        prependHeader().join().setComponent(b);
    }

    private void searchPO() {
        if(search == null) {
            search = new Search();
        }
        search.execute();
    }

    public void setAllowSwitchStore(boolean allowSwitchStore) {
        this.allowSwitchStore = allowSwitchStore;
        switchStore.setEnabled(allowSwitchStore);
    }

    /**
     * Set a store so that it will not be selectable anymore.
     *
     * @param store Store to set.
     */
    public void setStore(InventoryStore store) {
        setStore(store, false);
    }

    public void setStore(InventoryStore store, boolean allowSwitchStore) {
        this.allowSwitchStore = allowSwitchStore;
        ObjectField<?> storeField = (ObjectField<?>) editor().getAnchorField("Store");
        storeField.setReadOnly(store != null);
        if(store != null) {
            storeField.setObject(store);
            editor().executeAnchorForm();
        }
        switchStore.setVisible(store == null || allowSwitchStore);
    }

    private POEditor<T> editor() {
        if(editor == null) {
            ObjectEditor<T> oe = getObjectEditor();
            if(!(oe instanceof POEditor)) {
                editor = new POEditor<>(getObjectClass());
                setObjectEditor(editor);
            } else {
                editor = (POEditor<T>) oe;
            }
        }
        return editor;
    }

    private void switchStore() {
        editor().resetAnchor();
        switchStore.setText("Not selected");
        load.click();
    }

    @Override
    protected void anchorsSet() {
        switchStore.setText(editor.store.toDisplay());
        goToGRNs.setVisible(true);
        switchStore.setEnabled(allowSwitchStore);
        if(allowSwitchStore) {
            editor().getAnchorField("Store").setReadOnly(false);
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

    @Override
    public boolean canEdit(T po) {
        return editor().canEdit(po) && super.canEdit(po);
    }

    @Override
    public boolean canDelete(T po) {
        return editor().canDelete(po) && super.canDelete(po);
    }

    @Override
    public void doDelete(T po) {
        if(editor().deletePO(po, () -> super.doDelete(po))) {
            super.doDelete(po);
        }
    }

    private T check0(boolean forApproval) {
        clearAlerts();
        T selected = selected();
        if(selected == null) {
            return null;
        }
        if(selected.getStatus() != 0) {
            warning("Status is already '" + selected.getStatusValue() + "'");
            return null;
        }
        if(forApproval) {
            if(!selected.getApprovalRequired()) {
                warning("Already approved");
                return null;
            }
        } else {
            if(selected.getApprovalRequired()) {
                warning("Approval required");
                return null;
            }
        }
        if(!selected.existsLinks(InventoryPOItem.class, true)) {
            warning("Item list is empty");
            return null;
        }
        return selected;
    }

    private void approveOrder() {
        T po = check0(true);
        if(po != null) {
            if(canApprovePO(po) && approve(po)) {
                refresh(po);
                message("Approved");
            }
        }
    }

    private boolean approve(T po) {
        po.setApprovalRequired(false);
        return transact(t -> {
            po.save(t);
            po.addLink(t, UserAction.get(getTransactionManager(), getActionPrefix() + "-APPROVE"));
        });
    }

    private void placeOrder() {
        T po = check0(false);
        if(po != null) {
            if(canPlaceOrder(po) && transact(t -> {
                po.placeOrder(t);
                po.addLink(t, UserAction.get(getTransactionManager(), getActionPrefix() + "-PLACE-ORDER"));
            })) {
                refresh(po);
                message("Order placed");
            }
        }
    }

    /**
     * Check whether this PO can be placed now or not.
     *
     * @param po PO.
     * @return True/false.
     */
    protected boolean canPlaceOrder(T po) {
        return actionAllowed("PLACE-ORDER");
    }

    private void receiveItems() {
        T po = selected();
        if(po == null) {
            return;
        }
        switch(po.getStatus()) {
            case 0 -> {
                message("Order is not yet placed!");
                return;
            }
            case 3 -> {
                message("All items were already received!");
                return;
            }
            case 4 -> {
                message("Order is already closed!");
                return;
            }
            default -> {
            }
        }
        if(!canReceiveItems(po)) {
            return;
        }
        List<InventoryPOItem> items = po.listItems().filter(i -> i.getBalance().isPositive()).toList();
        if(items.isEmpty()) {
            message("No more items to receive.");
            return;
        }
        items.removeIf(i -> !i.canReceive());
        if(items.isEmpty()) {
            message("Can't receive remaining items now.");
            return;
        }
        deselectAll();
        new ReceiveItems(po, items).execute(this.getView());
    }

    /**
     * Check whether items can be received from this PO or not.
     *
     * @param po PO.
     * @return True/false.
     */
    protected boolean canReceiveItems(T po) {
        return actionAllowed("RECEIVE-ITEMS");
    }

    private void closePO() {
        T po = selected();
        if(po == null) {
            return;
        }
        switch(po.getStatus()) {
            case 4 -> {
                message("Already closed");
                return;
            }
            case 3 -> {
            }
            default -> {
                message("Can't proceed with Status = " + po.getStatusValue());
                return;
            }
        }
        if(pendingGRNs(po) || !canClosePO(po)) {
            return;
        }
        closePO(po);
    }

    private void foreclosePO() {
        T po = selected();
        if(po == null) {
            return;
        }
        switch(po.getStatus()) {
            case 4 -> {
                message("Already closed");
                return;
            }
            case 3 -> {
                message("All items were already received, you may close this order.");
                return;
            }
        }
        if(pendingGRNs(po) || !canForeclosePO(po)) {
            return;
        }
        String m = "Status of this order is '" + po.getStatusValue() + "'.\nDo you really want to foreclose this?";
        new ActionForm(m, () -> closePO(po)).execute();
    }

    private void closePO(T po) {
        if(transact(po::closeOrder)) {
            refresh(po);
            message("Closed");
        }
    }

    /**
     * Check whether this PO can be approved or not.
     *
     * @param po PO.
     * @return True/false.
     */
    protected boolean canApprovePO(T po) {
        return actionAllowed("APPROVE");
    }

    /**
     * Check whether this PO can be closed now or not.
     *
     * @param po PO.
     * @return True/false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean canClosePO(T po) {
        return actionAllowed("CLOSE");
    }

    /**
     * Check whether this PO can be foreclosed now or not.
     *
     * @param po PO.
     * @return True/false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean canForeclosePO(T po) {
        return actionAllowed("FORECLOSE");
    }

    private boolean pendingGRNs(T po) {
        if(po.canForeclose()) {
            return false;
        }
        warning("Please process the pending GRNs associated with this order first.");
        return true;
    }

    protected boolean canProcessGRN(T po) {
        return actionAllowed("PROCESS-GRN");
    }

    private void preProcessGRNs() {
        T po = selected();
        if(po == null || !canProcessGRN(po)) {
            return;
        }
        List<InventoryGRN> grns = po.listLinks(InventoryGRN.class).filter(g -> !g.isClosed()).toList();
        if(grns.isEmpty()) {
            message("No open GRN available for processing");
            return;
        }
        if(grns.size() == 1) {
            processGRN(grns.getFirst());
            return;
        }
        new GRNs(grns, this::processGRN).execute();
    }

    private void preViewGRNs() {
        T po = selected();
        if(po == null) {
            return;
        }
        List<InventoryGRN> grns = po.listLinks(InventoryGRN.class).toList();
        if(grns.isEmpty()) {
            message("No GRNs found for this PO");
            return;
        }
        if(grns.size() == 1) {
            processGRN(grns.getFirst(), true);
            return;
        }
        new GRNs(grns, grn -> processGRN(grn, true)).execute();
    }

    private class ReceiveItems extends ListGrid<InventoryPOItem> {

        private final T po;
        private final Map<Id, QField> qFields = new HashMap<>();
        private final Checkbox confirmExcess = new Checkbox("Confirm Excess");
        private final Currency currency;
        private SupplierInvoice supplierInvoice;

        public ReceiveItems(T po, List<InventoryPOItem> items) {
            super(InventoryPOItem.class, items,
                    StringList.create("PartNumber", "SerialNumber AS Serial/Batch Number", "Expected"));
            setCaption("Receive Items - GRN");
            currency = items.getFirst().getUnitPrice().getCurrency();
            this.po = po;
            createHTMLColumn("PartNumber", this::pn);
            GridContextMenu<InventoryPOItem> cm = new GridContextMenu<>(this);
            GridMenuItem<InventoryPOItem> noAPN = cm.addItem("No APNs found!");
            GridMenuItem<InventoryPOItem> createAPN = cm.addItem("Create a new APN",
                    e -> e.getItem().ifPresent(poi -> createAPN(poi.getPartNumber())));
            GridMenuItem<InventoryPOItem> setAPN = cm.addItem("Set APN", e -> e.getItem().ifPresent(this::apn));
            cm.setDynamicContentHandler(item -> {
                deselectAll();
                if(item == null) {
                    return false;
                }
                select(item);
                boolean noAPNs = item.getPartNumber().listAPNs().isEmpty();
                noAPN.setVisible(noAPNs);
                createAPN.setVisible(noAPNs);
                setAPN.setVisible(!noAPNs);
                return true;
            });
        }

        @Override
        public void constructed() {
            super.constructed();
            addComponentColumn(this::receive).setHeader("Receive").setFlexGrow(0).setWidth("200px");
            addComponentColumn(this::excess).setHeader("Excess").setFlexGrow(0).setWidth("200px");
            getColumn("Expected").setFlexGrow(0).setWidth("200px");
        }

        @Override
        public int getRelativeColumnWidth(String columnName) {
            return switch(columnName) {
                case "PartNumber" -> 30;
                case "SerialNumber" -> 10;
                default -> super.getRelativeColumnWidth(columnName);
            };
        }

        private HTMLText pn(InventoryPOItem item) {
            HTMLText h = new HTMLText();
            if(item.getType() == 1) {
                h.append("[APN] ", Application.COLOR_ERROR);
            }
            h.append(item.getPartNumber().toDisplay(), Application.COLOR_SUCCESS);
            h.update();
            return h;
        }

        private void apn(InventoryPOItem item) {
            if(!actionAllowed("SET-APN")) {
                message("Set APN - Not authorized");
                return;
            }
            clearAlerts();
            close();
            setAPN(po, item);
        }

        private void createAPN(InventoryItemType pn) {
            if(!actionAllowed("CREATE-APN")) {
                message("Create APN - Not authorized");
                return;
            }
            clearAlerts();
            close();
            new CreateAPN(pn, getTransactionManager()).execute();
        }

        @SuppressWarnings("unused")
        public Quantity getExpected(InventoryPOItem item) {
            return item.getBalance();
        }

        public QField receive(InventoryPOItem item) {
            QField qf = qFields.get(item.getId());
            if(qf == null) {
                qf = new QField(item);
                qFields.put(item.getId(), qf);
            }
            return qf;
        }

        public QuantityField excess(InventoryPOItem item) {
            return receive(item).exField;
        }

        @Override
        public Component createHeader() {
            confirmExcess.setVisible(false);
            return new ButtonLayout(
                    new ConfirmButton("Create New GRN", VaadinIcon.FILE_PROCESS, e -> process(true)),
                    new ConfirmButton("Add to an Existing GRN", VaadinIcon.FILE_ADD, e -> process(false)),
                    new ConfirmButton("Set Quantities = 0", VaadinIcon.CIRCLE_THIN, e -> zero()),
                    new ConfirmButton("Cancel", e -> close()),
                    confirmExcess,
                    new ELabel("Right-click on the entry to set an APN", Application.COLOR_SUCCESS)
            );
        }

        private void zero() {
            qFields.values().forEach(f -> f.setValue(f.getValue().zero()));
        }

        private void process(boolean createNew) {
            clearAlerts();
            List<InventoryGRN> addToGRNs = null;
            if(!createNew) {
                addToGRNs = StoredObject.list(InventoryGRN.class,
                        "Store=" + po.getStoreId() + " AND Supplier=" + po.getSupplierId() + " AND Status=0")
                        .filter(g -> {
                            InventoryPO p = g.listMasters(InventoryPO.class, true).findFirst();
                            return p != null && p.getType() == po.getType();
                        }).toList();
                if(addToGRNs.isEmpty()) {
                    warning("No open GRNs found for this supplier in this store for this type of PO!");
                    return;
                }
            }
            Map<Id, Quantity> qs = new HashMap<>();
            qFields.forEach((key, value) -> {
                Quantity q = value.getValue();
                if(q.isPositive()) {
                    qs.put(key, q);
                }
            });
            if(qs.isEmpty()) {
                warning("Can't proceed, no non-zero entry!");
                return;
            }
            boolean excess = false;
            Quantity q;
            for(InventoryPOItem item : this) {
                q = qs.get(item.getId());
                if(q == null || q.isLessThanOrEqual(item.getBalance())) {
                    continue;
                }
                excess = true;
                break;
            }
            if(excess) {
                if(!confirmExcess.isVisible()) {
                    confirmExcess.setVisible(true);
                    confirmExcess.setValue(false);
                }
                if(!confirmExcess.getValue()) {
                    message("Please confirm excess quantity!");
                    return;
                }
            } else {
                confirmExcess.setValue(false);
                confirmExcess.setVisible(false);
            }
            if(createNew) {
                supplierInvoice().processGRN(qs, null);
                return;
            }
            SelectGrid<InventoryGRN> selectGrid = new SelectGrid<>(InventoryGRN.class, addToGRNs, g -> {
                if(g.getInvoiceNumber().isBlank()) {
                    supplierInvoice().processGRN(qs, g);
                } else {
                    process(qs, g, null, null, null);
                }
            }) {
                @Override
                public void createHeaders() {
                    ELabel m = new ELabel("Please select the GRN to add the items to", Application.COLOR_SUCCESS);
                    prependHeader().join().setComponent(m);
                }
            };
            selectGrid.execute();
        }

        private SupplierInvoice supplierInvoice() {
            if(supplierInvoice == null) {
                supplierInvoice = new SupplierInvoice();
            } else {
                supplierInvoice.set(getTransactionManager(), currency);
            }
            return supplierInvoice;
        }

        private void process(Map<Id, Quantity> qs, InventoryGRN grn, String invoiceNumber, Date invoiceDate, Rate exchangeRate) {
            close();
            AtomicReference<InventoryGRN> grnCreated = new AtomicReference<>();
            if(transact(t -> grnCreated.set(po.createGRN(t, qs, grn, invoiceNumber, invoiceDate, exchangeRate)))) {
                POBrowser.this.close();
                InventoryGRN g = grnCreated.get();
                g.reload();
                processGRN(g);
            } else {
                po.reload();
            }
        }

        private class SupplierInvoice extends SupplierInvoiceDetail {

            private InventoryGRN grn;
            private Map<Id, Quantity> quantityMap;

            public SupplierInvoice() {
                super(POBrowser.this.getTransactionManager(), currency);
            }

            void processGRN(Map<Id, Quantity> quantityMap, InventoryGRN grn) {
                this.quantityMap = quantityMap;
                this.grn = grn;
                if(grn != null) {
                    refField.setValue(grn.getInvoiceNumber());
                    dateField.setValue(grn.getInvoiceDate());
                }
                execute();
            }

            @Override
            protected boolean process() {
                close();
                String ref = StoredObject.toCode(refField.getText());
                if(ref.isBlank()) {
                    ref = null;
                }
                ReceiveItems.this.process(quantityMap, grn, ref, ref == null ? null : dateField.getValue(),
                        rateField.getValue());
                return true;
            }
        }

        private class QField extends QuantityField {

            private final QuantityField exField = new QuantityField();
            private final Quantity max;

            private QField(InventoryPOItem item) {
                max = item.getBalance();
                exField.setReadOnly(true);
                exField.setTabIndex(-1);
                setValue(max);
                addValueChangeListener(e -> check(e.getValue()));
            }

            private void check(Quantity q) {
                clearAlerts();
                if(!q.isConvertible(max)) {
                    warning(q + " is not compatible with " + max);
                    focus();
                } else if(q.isGreaterThan(max)) {
                    exField.setValue(q.subtract(max));
                }
            }
        }
    }

    private void processGRN(InventoryGRN g) {
        processGRN(g, false);
    }

    private void processGRN(InventoryGRN g, boolean viewOnly) {
        clearAlerts();
        int type;
        if(g == null) {
            T po = editor.createNewInstance();
            if(po == null) {
                return;
            }
            type = po.getGRNType();
        } else {
            type = g.getType();
        }
        GRN grnView = new GRN(type, editor.store);
        grnView.setSource("POs", getClass(), getObjectClass());
        if(g == null) {
            close();
        }
        grnView.execute();
        if(g != null) {
            if(viewOnly) {
                grnView.viewGRN(g);
            } else {
                grnView.processGRN(g);
            }
        }
    }

    public static ObjectBrowser<?> createNew(Class<? extends StoredObject> poClass, InventoryStore store, boolean allowSwitchStore) {
        ObjectBrowser<?> b = ObjectBrowser.create(poClass);
        if(b instanceof POBrowser<?> poBrowser) {
            poBrowser.setStore(store, allowSwitchStore);
        }
        return b;
    }

    private static class GRNs extends SelectGrid<InventoryGRN> {

        public GRNs(List<InventoryGRN> items, Consumer<InventoryGRN> consumer) {
            super(InventoryGRN.class, items, consumer);
        }

        @Override
        public void clean() {
            super.clean();
            clearAlerts();
        }
    }

    private void setAPN(T po, InventoryPOItem item) {
        select(po);
        new SetAPN(po, item).execute(getView());
    }

    private void setAPN(InventoryPOItem item, InventoryItemType apn, Quantity q) {
        if(transact(t -> item.setAPN(t, apn, q))) {
            receiveItems();
        }
    }

    private class SetAPN extends DataForm {

        private final InventoryPOItem item;
        private final ComboField<InventoryItemType> apnField;
        private final QuantityField qField = new QuantityField("Quantity");

        private SetAPN(T po, InventoryPOItem item) {
            super("Set APN - " + po.getReference());
            this.item = item;
            apnField = new ComboField<>("Select APN", item.getPartNumber().listAPNs());
            ELabelField op = new ELabelField("Original P/N");
            op.append(item.getPartNumber().toDisplay(), Application.COLOR_SUCCESS).update();
            ELabelField ef = new ELabelField("Quantity to Receive");
            ef.append(item.getBalance(), Application.COLOR_SUCCESS).update();
            addField(op, ef, apnField, qField);
            setRequired(apnField);
            setRequired(qField);
            qField.setValue(item.getQuantity().zero());
            qField.setAllowedUnits(MeasurementUnit.list(item.getQuantity().getClass()));
        }

        @Override
        protected boolean process() {
            InventoryItemType pn = item.getPartNumber();
            Quantity q = qField.getValue();
            MeasurementUnit uom = pn.getUnitOfMeasurement().getUnit();
            if(!q.isConvertible(uom)) {
                warning(q + " is not compatible with '" + uom + "'");
                qField.focus();
                return false;
            }
            if(q.isGreaterThan(item.getBalance())) {
                warning("Quantity can not be more than " + item.getBalance());
                qField.focus();
                return false;
            }
            clearAlerts();
            InventoryItemType apn = apnField.getValue();
            message("APN " + apn + " set for " + pn);
            close();
            setAPN(item, apn, q);
            return true;
        }
    }

    private static class CreateAPN extends DataForm {

        private final InventoryItemType pn;
        private final TransactionManager tm;
        private final TextField pnField = new TextField("Alternate P/N");

        public CreateAPN(InventoryItemType pn, TransactionManager tm) {
            super("Create APN");
            this.pn = pn;
            this.tm = tm;
            pnField.uppercase();
            pnField.setValue(pn.getPartNumber() + "-ALT");
            pnField.addValueChangeListener(e -> pnField.setValue(StoredObject.toCode(e.getValue())));
            addField(new ELabelField("Warning: Creating APN for", pn.toDisplay(), "red"), pnField);
            setRequired(pnField);
        }

        @Override
        protected boolean process() {
            clearAlerts();
            String p = pnField.getValue();
            if(p.equals(pn.getPartNumber())) {
                warning("Invalid APN!");
                return false;
            }
            try {
                pn.createAPN(p, tm);
            } catch(Exception e) {
                error(e);
            }
            return true;
        }
    }

    /**
     * Add a process button that will be added to the process context menu.
     *
     * @param label Label for the button.
     * @param check Check whether the button should be displayed or not.
     * @param processor The processor.
     */
    protected void addProcessButton(String label, Predicate<T> check, Consumer<T> processor) {
        processButtons.add(new ProcessButton(label, check, processor));
    }

    private class ProcessButton {

        private final Predicate<T> check;
        private final GridMenuItem<T> menu;

        ProcessButton(String label, Predicate<T> check, Consumer<T> processor) {
            this.check = check == null ? (po -> true) : check;
            menu = contextMenu.addItem(label, e -> e.getItem().ifPresent(processor));
        }
    }

    private class Search extends DataForm {

        private final ChoiceField search = new ChoiceField("Search",
                new String[] { "Part Number", "Date Period", "PO No.", "Supplier" });
        private final ObjectGetField<InventoryItemType> pnField =
                new ObjectGetField<>("Part Number", InventoryItemType.class, true);
        private final DatePeriodField periodField = new DatePeriodField("Date Period");
        private final IntegerField noField = new IntegerField("PO No.");
        private final ObjectField<Entity> supplierField;


        public Search() {
            super("Search");
            supplierField = new ObjectField<>("Supplier", GRN.suppliers(0));
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
            POBrowser.this.clearAlerts();
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
                    setLoadFilter(p -> p.existsLinks(InventoryPOItem.class, "PartNumber=" + pnId, true));
                }
                case 1 -> {
                    DatePeriod period = periodField.getValue();
                    filter = "Period = " + period;
                    setLoadFilter(p -> period.inside(p.getDate()));
                }
                case 2 -> {
                    int no = noField.getValue();
                    if(no <= 0) {
                        searching = false;
                        return true;
                    }
                    filter = "PO No. = " + no;
                    setLoadFilter(p -> p.getNo() == no);
                }
                case 3 -> {
                    Entity supplier = supplierField.getObject();
                    if(supplier == null) {
                        searching = false;
                        return true;
                    }
                    filter = "Supplier = " + supplier.toDisplay();
                    Id sid = supplier.getId();
                    setLoadFilter(p -> p.getSupplierId().equals(sid));
                }
            }
            if(filter != null) {
                searchLabel.clearContent().append(" Filter: ", Application.COLOR_ERROR)
                        .append(filter, Application.COLOR_INFO).update();
            }
            return true;
        }
    }

    private void amend() {
        clearAlerts();
        T po = selected();
        if(po == null) {
            return;
        }
        InventoryGRN grn = po.listLinks(InventoryGRN.class).find(g -> !g.isClosed());
        if(grn != null) {
            warning("Please process the GRN - " + grn.getReference() + " related to this PO first!");
            return;
        }
        switch(po.getStatus()) {
            case 0 -> {
                warning("Editing instead of amending!");
                edit.click();
            }
            case 1 -> new AmendAction(po).execute();
            case 2 -> amend(po);
            default -> warning("Can't amend with Status = " + po.getStatusValue());
        }
    }

    private void amend(T po) {
        final AtomicReference<Id> amendedPO = new AtomicReference<>(null);
        if(transact(t -> amendedPO.set(po.amendOrder(t)))) {
            load();
            T poNew = StoredObject.get(getObjectClass(), amendedPO.get());
            scrollTo(poNew);
            select(poNew);
            warning("Amended to " + poNew.getReference());
        }
    }

    private void recall(T po) {
        if(transact(po::recallOrder)) {
            message("Recalled: " + po.getReference());
            refresh(po);
        }
    }

    private class AmendAction extends ActionForm {

        private final T po;
        private final Application a;

        public AmendAction(T po) {
            super("It is possible to recall this PO. Please choose one action carefully." +
                    "\nNote: This action can not be undone!");
            this.po = po;
            a = getApplication();
            setConfirmAction(() -> a.access(() -> recall(po)));
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setText("Recall");
            ok.setIcon("lumo:undo");
            buttonPanel.remove(cancel);
            buttonPanel.add(new Button("Amend", e -> a.access(() -> {
                abort();
                amend(po);
            })));
            buttonPanel.add(cancel);
            cancel.setText("Cancel");
        }
    }
}
