package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.storedobject.core.EditorAction.ALL;

public class POBrowser<T extends InventoryPO> extends ObjectBrowser<T> implements ObjectEditorProvider, ProducesGRN {

    private final ELabel storeDisplay = new ELabel("Store: Not selected");
    protected final Button switchStore = new Button("Switch Store", VaadinIcon.STORAGE, e -> switchStore());
    private POEditor<T> editor;
    private final List<ProcessButton> processButtons = new ArrayList<>();
    private final GridContextMenu<T> contextMenu;

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
        addConstructedListener(b -> {
            editor();
            setFixedFilter("Status<4", false);
            if(getObjectClass() == InventoryPO.class) {
                setCaption("Purchase Order");
            }
        });
        contextMenu = new GridContextMenu<>(this);
        GridMenuItem<T> placeOrder = contextMenu.addItem("Place the Order", e -> e.getItem().ifPresent(i -> placeOrder()));
        GridMenuItem<T> receiveItems = contextMenu.addItem("Receive Items", e -> e.getItem().ifPresent(i -> receiveItems()));
        GridMenuItem<T> foreClose = contextMenu.addItem("Foreclose", e -> e.getItem().ifPresent(i -> foreclosePO()));
        GridMenuItem<T> close = contextMenu.addItem("Close", e -> e.getItem().ifPresent(i -> closePO()));
        GridMenuItem<T> preGRNs = contextMenu.addItem("Associated GRNs", e -> e.getItem().ifPresent(i -> preGRNs()));
        contextMenu.setDynamicContentHandler(po -> {
            deselectAll();
            if(po == null) {
                return false;
            }
            select(po);
            processButtons.forEach(b -> b.menu.setVisible(b.check.test(po)));
            switch(po.getStatus()) {
                case 0 -> {
                    placeOrder.setVisible(canPlaceOrder(po));
                    receiveItems.setVisible(false);
                    foreClose.setVisible(canClosePO(po));
                    close.setVisible(false);
                    preGRNs.setVisible(false);
                }
                case 1 -> {
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(canReceiveItems(po));
                    foreClose.setVisible(canClosePO(po));
                    close.setVisible(false);
                    preGRNs.setVisible(false);
                }
                case 2 -> {
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(canReceiveItems(po));
                    foreClose.setVisible(canClosePO(po));
                    close.setVisible(false);
                    preGRNs.setVisible(canProcessGRN(po));
                }
                case 3 -> {
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(false);
                    foreClose.setVisible(false);
                    close.setVisible(canClosePO(po));
                    preGRNs.setVisible(canProcessGRN(po));
                }
                case 4 -> {
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(false);
                    foreClose.setVisible(false);
                    close.setVisible(false);
                    preGRNs.setVisible(canProcessGRN(po));
                }
            }
            return true;
        });
    }

    public POBrowser() {
        this((String)null);
    }

    public POBrowser(String caption) {
        this(ALL, caption);
    }

    public POBrowser(Iterable<String> browseColumns) {
        this(browseColumns, ALL);
    }

    public POBrowser(Iterable<String> browseColumns, Iterable<String> filterColumns) {
        this(browseColumns, ALL, filterColumns);
    }

    public POBrowser(int actions) {
        this(actions, null);
    }

    public POBrowser(int actions, String caption) {
        this((Iterable<String>)null, actions, caption);
    }

    public POBrowser(Iterable<String> browseColumns, int actions) {
        this(browseColumns, actions, null, null);
    }

    public POBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(browseColumns, actions, filterColumns, null);
    }

    public POBrowser(Iterable<String> browseColumns, int actions, String caption) {
        this(browseColumns, actions, null, caption);
    }

    public POBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        //noinspection unchecked
        this((Class<T>) InventoryPO.class, browseColumns, actions, filterColumns, caption, null);
    }

    @Override
    public String getOrderBy() {
        return "Store,Date DESC,No DESC";
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(switchStore);
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<4"));
        buttonPanel.add(h);
        super.addExtraButtons();
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(storeDisplay);
    }

    /**
     * Set a store so that it will not be selectable anymore.
     *
     * @param store Store to set.
     */
    public void setStore(InventoryStore store) {
        ObjectField<?> storeField = (ObjectField<?>) editor().getAnchorField("Store");
        storeField.setReadOnly(store != null);
        if(store != null) {
            storeField.setObject(store);
            editor().executeAnchorForm();
        }
        switchStore.setVisible(store == null);
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
        storeDisplay.clearContent().append("Store: Not selected").update();
        load.click();
    }

    @Override
    protected void anchorsSet() {
        this.storeDisplay.clearContent().append("Store: ").
                append(editor.store, "blue").
                append(" | ", "green").
                append("Note: ").
                append("Right-click on the entry for available process options", "blue").
                update();
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

    private void placeOrder() {
        T po = selected();
        if(po == null) {
            return;
        }
        if(po.getStatus() != 0) {
            warning("Status is already '" + po.getStatusValue() + "'");
            return;
        }
        clearAlerts();
        if(canPlaceOrder(po) && transact(po::placeOrder)) {
            refresh(po);
            message("Order placed");
        }
    }

    /**
     * Check whether this PO can be placed now or not. Default implementation returns ture but this can be
     * overridden to implement access control as per the organization's policy.
     *
     * @param po PO.
     * @return True/false.
     */
    protected boolean canPlaceOrder(T po) {
        return true;
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
        deselectAll();
        new ReceiveItems(po, items).execute(this.getView());
    }


    /**
     * Check whether items can be received form this PO or not. Default implementation returns ture but this can be
     * overridden to implement access control as per the organization's policy.
     *
     * @param po PO.
     * @return True/false.
     */
    protected boolean canReceiveItems(T po) {
        return true;
    }

    private void closePO() {
        T po = selected();
        if(po == null) {
            return;
        }
        switch(po.getStatus()) {
            case 4:
                message("Already closed");
                return;
            case 3:
                break;
            default:
                message("Can't proceed with Status = " + po.getStatusValue());
                return;
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
        if(pendingGRNs(po) || !canClosePO(po)) {
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
     * Check whether this PO can be closed now or not. Default implementation returns ture but this can be
     * overridden to implement access control as per the organization's policy.
     *
     * @param po PO.
     * @return True/false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean canClosePO(T po) {
        return true;
    }

    private boolean pendingGRNs(T po) {
        if(po.canForeclose()) {
            return false;
        }
        warning("Please process the pending GRNs associated with this order first.");
        return true;
    }

    protected boolean canProcessGRN(T po) {
        return true;
    }

    private void preGRNs() {
        T po = selected();
        if(po == null || !canProcessGRN(po)) {
            return;
        }
        List<InventoryGRN> grns = po.listLinks(InventoryGRN.class).filter(g -> !g.isClosed()).toList();
        if(grns.isEmpty()) {
            message("No open GRN available for processing");
            return;
        }
        new GRNs(grns, this::processGRN).execute();
    }

    private class ReceiveItems extends ListGrid<InventoryPOItem> {

        private final T po;
        private final Map<Id, QField> qFields = new HashMap<>();
        private final Checkbox confirmExcess = new Checkbox("Confirm Excess");
        private SupplierInvoice supplierInvoice;

        public ReceiveItems(T po, List<InventoryPOItem> items) {
            super(InventoryPOItem.class, items, StringList.create("PartNumber", "SerialNumber", "Expected"));
            setCaption("Receive Items - GRN");
            this.po = po;
            createHTMLColumn("PartNumber", this::pn);
            GridContextMenu<InventoryPOItem> cm = new GridContextMenu<>(this);
            GridMenuItem<InventoryPOItem> noAPN = cm.addItem("No APNs found!");
            GridMenuItem<InventoryPOItem> setAPN = cm.addItem("Set APN", e -> e.getItem().ifPresent(this::apn));
            cm.setDynamicContentHandler(item -> {
                deselectAll();
                if(item == null) {
                    return false;
                }
                select(item);
                boolean noAPNs = item.getPartNumber().listAPNs().isEmpty();
                noAPN.setVisible(noAPNs);
                setAPN.setVisible(!noAPNs);
                return true;
            });
        }

        @Override
        public void constructed() {
            super.constructed();
            addComponentColumn(this::receive).setHeader("Receive");
            addComponentColumn(this::excess).setHeader("Excess");
        }

        private HTMLText pn(InventoryPOItem item) {
            HTMLText h = new HTMLText();
            if(item.getType() == 1) {
                h.append("[APN] ", "red");
            }
            h.append(item.getPartNumber().toDisplay(), "blue");
            h.update();
            return h;
        }

        private void apn(InventoryPOItem item) {
            close();
            setAPN(po, item);
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
                    new ConfirmButton("Cancel", e -> close()),
                    confirmExcess,
                    new ELabel("Right-click on the entry to set an APN", "blue")
            );
        }

        private void process(boolean createNew) {
            clearAlerts();
            List<InventoryGRN> addToGRNs = null;
            if(!createNew) {
                addToGRNs = StoredObject.list(InventoryGRN.class,
                        "Store=" + po.getStoreId() + " AND Supplier=" + po.getSupplierId() + " AND Status<2").
                        toList();
                if(addToGRNs.isEmpty()) {
                    warning("No open GRNs found for this supplier in this store!");
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
                if(g.getReferenceNumber().startsWith("GRN")) {
                    supplierInvoice().processGRN(qs, g);
                } else {
                    process(qs, g, null, null);
                }
            }) {
                @Override
                public void createHeaders() {
                    ELabel m = new ELabel("Please select the GRN to add the items to", "blue");
                    prependHeader().join().setComponent(m);
                }
            };
            selectGrid.execute();
        }

        private SupplierInvoice supplierInvoice() {
            if(supplierInvoice == null) {
                supplierInvoice = new SupplierInvoice();
            }
            return supplierInvoice;
        }

        private void process(Map<Id, Quantity> qs, InventoryGRN grn, String invoiceNumber, Date invoiceDate) {
            close();
            AtomicReference<InventoryGRN> grnCreated = new AtomicReference<>();
            if(transact(t -> grnCreated.set(po.createGRN(t, qs, grn, invoiceNumber, invoiceDate)))) {
                POBrowser.this.close();
                InventoryGRN g = grnCreated.get();
                g.reload();
                processGRN(g);
            } else {
                po.reload();
            }
        }

        private class SupplierInvoice extends DataForm {

            private final TextField refField = new TextField("Invoice Number");
            private final DateField dateField = new DateField("Invoice Date");
            private InventoryGRN grn;
            private Map<Id, Quantity> quantityMap;

            public SupplierInvoice() {
                super("Supplier Invoice Details");
                refField.uppercase();
                refField.addValueChangeListener(e -> {
                    if(e.isFromClient()) {
                        refField.setValue(StoredObject.toCode(refField.getValue()));
                    }
                });
                addField(refField, dateField);
                refField.setHelperText("Leave it blank if not available");
            }

            void processGRN(Map<Id, Quantity> quantityMap, InventoryGRN grn) {
                this.quantityMap = quantityMap;
                this.grn = grn;
                if(grn != null) {
                    refField.setValue(grn.getReferenceNumber());
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
                ReceiveItems.this.process(quantityMap, grn, ref, ref == null ? null : dateField.getValue());
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
        clearAlerts();
        GRN grnView = getGRNBrowser(g.getStore());
        grnView.setGRNProducer(this);
        grnView.setEditorProvider(this);
        grnView.execute();
        grnView.processGRN(g);
    }

    public GRN getGRNBrowser(InventoryStore store) {
        return new GRN(store);
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
            super("Set APN - PO " + po.getReferenceNumber());
            this.item = item;
            apnField = new ComboField<>("Select APN", item.getPartNumber().listAPNs());
            ELabelField op = new ELabelField("Original P/N");
            op.append(item.getPartNumber().toDisplay(), "blue").update();
            ELabelField ef = new ELabelField("Quantity to Receive");
            ef.append(item.getBalance(), "blue").update();
            addField(op, ef, apnField, qField);
            setRequired(apnField);
            setRequired(qField);
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
}
