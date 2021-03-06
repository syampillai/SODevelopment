package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.storedobject.core.EditorAction.*;

public class POBrowser<T extends InventoryPO> extends ObjectBrowser<T> {

    private final ELabel storeDisplay = new ELabel("Store: Not selected");
    private final Button switchStore = new Button("Switch Store", VaadinIcon.STORAGE, e -> switchStore());
    private POEditor<T> editor;

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
        this(objectClass, null, actions, caption);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions) {
        this(objectClass, browseColumns, actions, null, null);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        this(objectClass, browseColumns, actions, filterColumns, null);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, String caption) {
        this(objectClass, browseColumns, actions, null, caption);
    }

    public POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        this(objectClass, browseColumns, actions, filterColumns, caption, null);
    }

    POBrowser(Class<T> objectClass, Iterable<String> browseColumns, int actions, Iterable<String> filterColumns,
                  String caption, String allowedActions) {
        super(objectClass, browseColumns, actions, filterColumns, caption, allowedActions);
        addConstructedListener(b -> {
            editor();
            setExtraFilter("Status<4", false);
            if(getObjectClass() == InventoryPO.class) {
                setCaption("Purchase Order");
            }
        });
        GridContextMenu<T> cm = new GridContextMenu<>(this);
        GridMenuItem<T> placeOrder = cm.addItem("Place the Order", e -> e.getItem().ifPresent(i -> placeOrder()));
        GridMenuItem<T> receiveItems = cm.addItem("Receive Items", e -> e.getItem().ifPresent(i -> receiveItems()));
        GridMenuItem<T> foreClose = cm.addItem("Foreclose", e -> e.getItem().ifPresent(i -> foreclosePO()));
        GridMenuItem<T> close = cm.addItem("Close", e -> e.getItem().ifPresent(i -> closePO()));
        GridMenuItem<T> preGRNs = cm.addItem("Associated GRNs", e -> e.getItem().ifPresent(i -> preGRNs()));
        cm.setDynamicContentHandler(po -> {
            deselectAll();
            if(po == null) {
                return false;
            }
            select(po);
            switch(po.getStatus()) {
                case 0:
                    placeOrder.setVisible(true);
                    receiveItems.setVisible(false);
                    foreClose.setVisible(true);
                    close.setVisible(false);
                    preGRNs.setVisible(false);
                    break;
                case 1:
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(true);
                    foreClose.setVisible(true);
                    close.setVisible(false);
                    preGRNs.setVisible(false);
                    break;
                case 2:
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(true);
                    foreClose.setVisible(true);
                    close.setVisible(false);
                    preGRNs.setVisible(true);
                    break;
                case 3:
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(false);
                    foreClose.setVisible(false);
                    close.setVisible(true);
                    preGRNs.setVisible(true);
                    break;
                case 4:
                    placeOrder.setVisible(false);
                    receiveItems.setVisible(false);
                    foreClose.setVisible(false);
                    close.setVisible(false);
                    preGRNs.setVisible(true);
                    break;
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
        PopupButton p = new PopupButton("Process");
        p.add(new Button("Place the Order", VaadinIcon.PAPERPLANE_O, e -> placeOrder()));
        p.add(new Button("Receive Items", VaadinIcon.STORAGE, e -> receiveItems()));
        p.add(new Button("Foreclose the Order", "close", e -> foreclosePO()));
        p.add(new Button("Close the Order", "close", e -> closePO()));
        p.add(new Button("Associated GRNs", VaadinIcon.FILE_PROCESS, e -> preGRNs()));
        buttonPanel.add(p, switchStore);
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status<4"));
        buttonPanel.add(h);
        super.addExtraButtons();
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(storeDisplay);
    }

    private POEditor<T> editor() {
        if(editor == null) {
            ObjectEditor<T> oe = getObjectEditor();
            if(!(oe instanceof POEditor)) {
                editor = new POEditor<>(getObjectClass());
                setObjectEditor(editor);
            } else {
                editor = (POEditor<T>) getObjectEditor();
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
        if(transact(po::placeOrder)) {
            refresh(po);
            message("Order placed");
        }
    }

    private void receiveItems() {
        T po = selected();
        if(po == null) {
            return;
        }
        switch(po.getStatus()) {
            case 0:
                message("Order is not yet placed!");
                return;
            case 3:
                message("All items were already received!");
                return;
            case 4:
                message("Order is already closed!");
                return;
            default:
                break;
        }
        List<InventoryPOItem> items = po.listItems().filter(i -> i.getBalance().isPositive()).toList();
        if(items.isEmpty()) {
            message("No more items to receive.");
            return;
        }
        deselectAll();
        new ReceiveItems(po, items).execute(this.getView());
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
        if(pendingGRNs(po)) {
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
            case 4:
                message("Already closed");
                return;
            case 3:
                message("All items were already received, you may close this order.");
                return;
        }
        if(pendingGRNs(po)) {
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

    private boolean pendingGRNs(T po) {
        if(po.canForeclose()) {
            return false;
        }
        warning("Please process the pending GRNs associated with this order first.");
        return true;
    }

    private void preGRNs() {
        T po = selected();
        if(po == null) {
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
                process(qs, null);
                return;
            }
            SelectGrid<InventoryGRN> selectGrid = new SelectGrid<>(InventoryGRN.class, addToGRNs, g -> process(qs, g)) {
                @Override
                public void createHeaders() {
                    ELabel m = new ELabel("Please select the GRN to add the items to", "blue");
                    prependHeader().join().setComponent(m);
                }
            };
            selectGrid.execute();
        }

        private void process(Map<Id, Quantity> qs, InventoryGRN grn) {
            close();
            AtomicReference<InventoryGRN> grnCreated = new AtomicReference<>();
            if(transact(t -> grnCreated.set(po.createGRN(t, qs, grn)))) {
                POBrowser.this.close();
                InventoryGRN g = grnCreated.get();
                g.reload();
                processGRN(g);
            } else {
                po.reload();
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
        GRN grnView = new GRN(g.getStore());
        grnView.execute();
        grnView.processGRN(g);
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
}
