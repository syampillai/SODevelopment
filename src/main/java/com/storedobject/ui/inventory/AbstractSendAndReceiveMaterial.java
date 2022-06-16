package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.MultiSelectGrid;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractSendAndReceiveMaterial<T extends InventoryTransfer, L extends InventoryTransferItem>
        extends ObjectBrowser<T> {

    private static final String LABEL_TOOL = "Tool/Item under Custody";
    private static final String LABEL_TOOLS = "Tools/Items under Custody";
    static final int[] ALL_TYPES = new int[] { 0, 3, 4, 5, 8, 10, 11 };
    private final Button send = new Button("Send", VaadinIcon.TRUCK, e -> send());
    private final Button receive = new Button("Receive", VaadinIcon.STORAGE, e -> receive());
    private final Button grnButton;
    private final ObjectField<InventoryLocation> fromField, toField;
    private InventoryLocation fromOrTo;
    private InventoryLocation otherLocation;
    private final boolean receiveMode;
    private final Class<L> transferItemClass;
    Class<? extends InventoryItem> itemClass;
    private GRNEditor grnEditor;
    private InventoryGRN grn;

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, boolean receiveMode) {
        this(transferClass, itemClass, (String) null, receiveMode);
    }

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, boolean receiveMode,
                                          InventoryLocation otherLocation) {
        this(transferClass, itemClass, (String) null, receiveMode, otherLocation);
    }

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, String fromOrTo,
                                          boolean receiveMode) {
        this(transferClass, itemClass, fromOrTo, receiveMode,
                ParameterParser.location(1, fromOrTo, true, ALL_TYPES));
    }

    private AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, String fromOrTo,
                                           boolean receiveMode, InventoryLocation otherLocation) {
        this(transferClass, itemClass, fromOrToField(fromOrTo, receiveMode, transferClass),
                receiveMode, otherLocation);
        this.itemClass = ParameterParser.itemClass(fromOrTo);
    }

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, InventoryLocation fromOrTo,
                                          boolean receiveMode) {
        this(transferClass, itemClass, LocationField.create(fromOrTo), receiveMode);
    }

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, InventoryLocation fromOrTo,
                                          boolean receiveMode, InventoryLocation otherLocation) {
        this(transferClass, itemClass, LocationField.create(fromOrTo), receiveMode, otherLocation);
    }

    private AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, LocationField fromOrToField,
                                           boolean receiveMode) {
        this(transferClass, itemClass, fromOrToField, receiveMode, null);
    }

    private AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, LocationField fromOrToField,
                                           boolean receiveMode, InventoryLocation otherLocation) {
        super(transferClass,
                receiveMode ?
                        StringList.create("Date", "Reference",
                                "FromLocation AS From", "Received") :
                        StringList.create("Date", "Reference",
                                "ToLocation AS " + (transferClass == MaterialReturned.class ? "Return" :
                                        (transferClass == InventoryRO.class ? "Send" : "Transfer")) + " to",
                                "Status"));
        addConstructedListener(v -> created());
        this.transferItemClass = itemClass;
        this.receiveMode = receiveMode;
        if(transferClass == InventoryRO.class) {
            setCaption("Send Items for Repair");
        } else {
            setCaption((receiveMode ? "Receive" : (transferClass == MaterialReturned.class ? "Return" : "Transfer")) +
                    " Materials/Tools");
        }
        this.fromOrTo = fromOrToField.getValue();
        if(this.fromOrTo == null) {
            throw new LogicRedirected(this::selectLocation);
        }
        send.setVisible(!receiveMode);
        receive.setVisible(receiveMode);
        this.otherLocation = otherLocation;
        if(this.otherLocation != null && this.otherLocation.equals(this.fromOrTo)) {
            throw new SORuntimeException("Both locations can't be the same - " + this.fromOrTo.toDisplay());
        }
        LocationField lf;
        if(receiveMode) {
            this.toField = new ObjectField<>("Receive at", fromOrToField);
            lf = this.otherLocation == null ? new LocationField(ALL_TYPES).remove(this.fromOrTo) :
                    LocationField.create(this.otherLocation);
            this.fromField = new ObjectField<>("Sent from", lf);
        } else {
            this.fromField = new ObjectField<>("Sent from", fromOrToField);
            if(transferClass == MaterialReturned.class) {
                lf = this.otherLocation == null ? new LocationField(0).remove(this.fromOrTo) :
                        LocationField.create(this.otherLocation);
                this.toField = new ObjectField<>("Return to", lf);
            } else {
                if(this.otherLocation == null) {
                    if(transferClass == InventoryRO.class) {
                        lf = new LocationField(3);
                    } else {
                        lf = new LocationField(ALL_TYPES).remove(this.fromOrTo);
                    }
                } else {
                    lf = LocationField.create(this.otherLocation);
                }
                this.toField = new ObjectField<>((transferClass == InventoryRO.class ? "Sent" : "Transferred") +
                        " to", lf);
            }
        }
        setOrderBy("Date DESC,No DESC");
        GridContextMenu<T> cm = new GridContextMenu<>(this);
        cm.addItem(receiveMode ? "Receive" : "Send", e -> e.getItem().ifPresent(this::rowDoubleClicked));
        GridMenuItem<T> grnMenu = cm.addItem("Associated GRN", e ->  grn());
        cm.setDynamicContentHandler(o -> {
            if(o == null) {
                return false;
            }
            grn = receiveMode ? o.listLinks(InventoryGRN.class).findFirst() : null;
            grnMenu.setVisible(grn != null);
            return grn != null || (receiveMode ? o.getStatus() == 1 : o.getStatus() == 2);
        });
        if(receiveMode) {
            grnButton = new Button("GRN", VaadinIcon.FILE_TABLE, e -> greSel());
        } else {
            grnButton = null;
        }
    }

    void created() {
    }

    protected void selectLocation() {
    }

    private GRNEditor grnEditor() {
        if(grnEditor == null) {
            grnEditor = new GRNEditor();
        }
        return grnEditor;
    }

    private void grn() {
        if(grn == null) {
            return;
        }
        grnEditor().setObject(grn);
        grnEditor.execute();
    }

    private void greSel() {
        if(!receiveMode) {
            return;
        }
        T mt = selected();
        if(mt == null) {
            return;
        }
        grn = mt.listLinks(InventoryGRN.class).findFirst();
        if(grn == null) {
            message("No GRN found for the selected entry");
        } else {
            grn();
        }
    }

    public final InventoryLocation getLocationFrom() {
        return fromField.getObject();
    }

    public final InventoryLocation getLocationTo() {
        return toField.getObject();
    }

    @Override
    public void setFixedFilter(String fixedFilter) {
        String f = getFixedSide() + "Location=" + this.fromOrTo.getId();
        if(otherLocation != null) {
            f += " AND " + ("To".equals(getFixedSide()) ? "From" : "To") + "Location=" + this.otherLocation.getId();
        }
        if(fixedFilter != null) {
            f += " AND (" + fixedFilter + ")";
        }
        super.setFixedFilter(f);
    }

    String getFixedSide() {
        return receiveMode ? "To" : "From";
    }

    void receive(T entry) {
        if(receiveMode) {
            select(entry);
            receive.click();
        }
    }

    private static LocationField fromOrToField(String fromOrTo, boolean receiveMode, Class<?> transferClass) {
        if(receiveMode) {
            return LocationField.create(null, fromOrTo, 0);
        }
        if(transferClass == MaterialReturned.class) {
            return LocationField.create(null, fromOrTo, 4, 5, 11);
        }
        return LocationField.create(null, fromOrTo, ALL_TYPES);
    }

    @Override
    public void createHeaders() {
        ELabel e = new ELabel((receiveMode ? "Receive at" : "From") + ": ").
                append(fromOrTo.toDisplay(), Application.COLOR_SUCCESS);

        Button locSwitch = getSwitchLocationButton();
        ButtonLayout buttonLayout = null;
        if(locSwitch != null) {
            e.update();
            buttonLayout = new ButtonLayout(e, locSwitch.asSmall());
        }
        if(locSwitch != null) {
            e = new ELabel();
        } else {
            e.append(" ");
        }
        if(otherLocation != null) {
            e.append((receiveMode ? "From" : "To") + ": ").append(otherLocation.toDisplay(), Application.COLOR_SUCCESS);
        }
        if(receiveMode) {
            e.newLine();
        } else {
            e.append(" | ", Application.COLOR_INFO);
        }
        e.append("Note: ").
                append("Double-click " + (receiveMode ? "" : "or right-click") + "on the entry to " +
                        (receiveMode ? "receive" : "send")
                        + (receiveMode ? ". Right-click on the entry for more options." : ""), Application.COLOR_SUCCESS);
        e.update();
        if(buttonLayout != null) {
            buttonLayout.add(e);
        }
        prependHeader().join().setComponent(buttonLayout == null ? e : buttonLayout);
    }

    protected Button getSwitchLocationButton() {
        return null;
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        if(receiveMode) {
            add.setVisible(false);
            edit.setVisible(false);
        }
        buttonPanel.add(send, receive, grnButton);
    }

    @Override
    public void rowDoubleClicked(T object) {
        if(object != null) {
            select(object);
            (receiveMode ? receive : send).click();
        }
    }

    public String getReceived(T mt) {
        return mt != null && mt.getStatus() == 2 ? "Yes" : "No";
    }

    @Override
    protected ObjectEditor<T> createObjectEditor() {
        return new MTEditor();
    }

    @Override
    public boolean canEdit(T object) {
        if(object.getStatus() > 0) {
            warning("Changes not possible with status = " + object.getStatusValue());
            return false;
        }
        return super.canEdit(object);
    }

    @Override
    public boolean canDelete(T object) {
        if(object.getStatus() == 1) {
            if(receiveMode) {
                warning("Please receive the items first.");
            } else {
                warning("Items are already sent, please ask someone at '" + object.getToLocation().toDisplay() +
                        "' to receive it first.");
            }
            return false;
        }
        return super.canDelete(object);
    }

    private void send() {
        T mt = selected();
        if(mt == null) {
            return;
        }
        if(mt.getStatus() != 0) {
            warning("Already sent!");
            return;
        }
        if(transact(mt::send)) {
            refresh(mt);
            message("Sent successfully");
        }
    }

    private void receive() {
        T mt = selected();
        if(mt == null) {
            return;
        }
        switch(mt.getStatus()) {
            case 0:
                warning("Not yet dispatched");
                return;
            case 1:
                break;
            case 2:
                warning("Already received!");
                return;
            default:
                return;
        }
        List<InventoryItem> items = mt.listLinks(transferItemClass).map(InventoryTransferItem::getItem).toList();
        List<InventoryItem> moved = new ArrayList<>();
        InventoryLocation to = mt.getToLocation();
        Id storeId = to instanceof InventoryStoreBin ? ((InventoryStoreBin) to).getStoreId() : null;
        items.removeIf(i -> {
            InventoryLocation loc = i.getLocation();
            if(loc.getId().equals(to.getId())) {
                return false;
            }
            if(storeId != null && loc instanceof InventoryBin && ((InventoryBin) loc).getStoreId().equals(storeId)) {
                return false;
            }
            moved.add(i);
            return true;
        });
        if(moved.isEmpty()) {
            receive(mt, items);
        } else {
            String m1, m2 = "You may ";
            if(items.isEmpty()) {
                m1 = "All";
                m2 += "just mark it as received";
            } else {
                m1 = "The following";
                m2 += "inspect/bin the remaining items";
            }
            m1 +=  " items are already moved!";
            m2 += ". Proceed?";
            ActionGrid<InventoryItem> ac;
            ac = new ActionGrid<>(InventoryItem.class, moved, m1, () -> receive(mt, items));
            ac.getConfirmMessage().clearContent().append(m2).update();
            ac.execute();
        }
    }

    private void receive(T mt, List<InventoryItem> items) {
        grn = mt.listLinks(InventoryGRN.class).single(false);
        GRNEditor gEd = grn == null ? null : grnEditor();
        if(grn != null) {
            grnEditor.setObject(grn);
        }
        new ReceiveAndBin(mt.getDate(), "Receipt " + mt.getReference(), items, mt::receive,
                () -> refresh(mt), gEd).execute(getView());
    }

    private class MTEditor extends ObjectEditor<T> {

        private MTItemGrid mtItemGrid;

        public MTEditor() {
            super(AbstractSendAndReceiveMaterial.this.getObjectClass());
            if(!receiveMode) {
                setNewObjectGenerator(() -> {
                    T mt = getObjectClass().getDeclaredConstructor().newInstance();
                    mt.setFromLocation(fromOrTo);
                    if(otherLocation != null) {
                        mt.setToLocation(otherLocation);
                    }
                    return mt;
                });
            }
            if(getObjectClass() == InventoryRO.class) {
                setCaption("Send Items for Repair");
            }
            setColumns(3);
            addField("Reference");
        }

        @Override
        protected int getFieldOrder(String fieldName) {
            if("SystemEntity".equals(fieldName)) {
                return 1;
            }
            if("Reference".equals(fieldName)) {
                return 2;
            }
            return super.getFieldOrder(fieldName);
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            if(receiveMode) {
                toField.setValue(fromOrTo);
                setFieldReadOnly(toField);
                if(otherLocation != null) {
                    fromField.setValue(otherLocation);
                    setFieldReadOnly(fromField);
                }
            } else {
                fromField.setValue(fromOrTo);
                setFieldReadOnly(fromField);
                if(otherLocation != null) {
                    toField.setValue(otherLocation);
                    setFieldReadOnly(toField);
                }
            }
            setFieldHidden("Status");
        }

        @Override
        public boolean isFieldVisible(String fieldName) {
            if("InvoiceNumber".equals(fieldName) || "ReferenceNumber".equals(fieldName) || "InvoiceDate".equals(fieldName)) {
                return requiresInvoiceDate();
            }
            return super.isFieldVisible(fieldName);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName) {
            return switch(fieldName) {
                case "FromLocation" -> fromField;
                case "ToLocation" -> toField;
                default -> super.createField(fieldName);
            };
        }

        @Override
        protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
            if("Items.l".equals(fieldName)) {
                //noinspection unchecked
                mtItemGrid = new MTItemGrid((ObjectLinkField<L>) field);
                return mtItemGrid;
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        protected String getLabel(String fieldName) {
            if(fromOrTo.getType() == 18) { // Custody location
                if("Items.l".equals(fieldName)) {
                    return LABEL_TOOLS;
                }
            }
            return super.getLabel(fieldName);
        }

        @Override
        public void setObject(T object, boolean load) {
            super.setObject(object, load);
            if(object != null && !receiveMode) {
                if(!fromOrTo.getId().equals(object.getFromLocationId())) {
                    fromOrTo = object.getFromLocation();
                    if(fromOrTo instanceof InventoryStoreBin) {
                        mtItemGrid.itemInput.setStore(((InventoryStoreBin) fromOrTo).getStore());
                    } else {
                        mtItemGrid.itemInput.setLocation(fromOrTo);
                    }
                    if(otherLocation != null) {
                        otherLocation = object.getToLocation();
                    }
                }
            }
        }
    }

    private class MTItemGrid extends DetailLinkGrid<L> {

        private ItemInput<?> itemInput;

        public MTItemGrid(ObjectLinkField<L> linkField) {
            super(linkField);
        }

        @Override
        public void constructed() {
            super.constructed();
            if(fromOrTo.getType() == 18) {
                getColumn("Item.PartNumber.Name").setHeader(LABEL_TOOL);
                getColumn("Quantity").setVisible(false);
            }
        }

        @Override
        public ObjectEditor<L> constructObjectEditor() {
            return new MRIEditor();
        }

        @Override
        public void add() {
            if(fromOrTo.getType() != 18) {
                super.add();
                return;
            }
            @SuppressWarnings("unchecked") List<InventoryItem> tools =
                    (List<InventoryItem>) StoredObject.list(iClass(), "Location=" + fromOrTo.getId()).
                            toList();
            new MultiSelectGrid<>(InventoryItem.class, tools,
                    StringList.create("PartNumber.PartNumber", "SerialNumber", "PartNumber.Name"),
                    this::toolEntries).execute(this.getView());
        }

        private void toolEntries(Set<InventoryItem> tools) {
            tools.forEach(ii -> {
                L o = getObjectEditor().newObject();
                o.setItem(ii);
                o.setQuantity(Count.ONE);
                add(o);
            });
        }

        private class MRIEditor extends ObjectEditor<L> {

            private QuantityField quantityField;

            public MRIEditor() {
                super(transferItemClass);
            }

            @Override
            protected HasValue<?, ?> createField(String fieldName, String label) {
                if("Item".equals(fieldName)) {
                    return new ObjectField<>(label, iClass(), true);
                }
                return super.createField(fieldName, label);
            }

            @Override
            protected void formConstructed() {
                super.formConstructed();
                quantityField = (QuantityField) getField("Quantity");
                //noinspection unchecked
                ObjectField<InventoryItem> itemField = (ObjectField<InventoryItem>) getField("Item");
                itemInput = (ItemInput<?>)(itemField).getField();
                if(fromOrTo.getType() != 18) {
                    itemField.addValueChangeListener(e -> changed(itemInput.getValue()));
                }
                if(!receiveMode) {
                    if(fromOrTo instanceof InventoryStoreBin) {
                        itemInput.setStore(((InventoryStoreBin) fromOrTo).getStore());
                    } else {
                        itemInput.setLocation(fromOrTo);
                    }
                }
                if(AbstractSendAndReceiveMaterial.this.getObjectClass() == InventoryRO.class) {
                    setFieldHidden(quantityField);
                }
                if(fromOrTo.getType() == 18) { // Custody location
                    setFieldHidden("Quantity");
                }
            }

            @Override
            protected String getLabel(String fieldName) {
                if(fromOrTo.getType() == 18) { // Custody location
                    if("Item".equals(fieldName)) {
                        return LABEL_TOOL;
                    }
                }
                return super.getLabel(fieldName);
            }

            @Override
            protected L createObjectInstance() {
                L object = super.createObjectInstance();
                if(fromOrTo.getType() == 18) { // Custody location
                    object.setQuantity(Count.ONE);
                }
                return object;
            }

            @Override
            public void setObject(L object, boolean load) {
                super.setObject(object, load);
                if(object != null) {
                    changed(object.getItem());
                }
            }

            private void changed(InventoryItem item) {
                if(item == null || fromOrTo.getType() == 18) {
                    return;
                }
                boolean s = item.isSerialized();
                if(s) {
                    quantityField.setValue(Count.ONE);
                } else {
                    Quantity q = quantityField.getValue();
                    if(q.isZero() || !q.isCompatible(item.getPartNumber().getUnitOfMeasurement())) {
                        quantityField.setValue(item.getQuantity());
                    }
                }
                setFieldVisible(!s, quantityField);
            }
        }
    }

    private Class<? extends InventoryItem> iClass() {
        Class<? extends InventoryItem> ic = itemClass();
        if(ic == null) {
            ic = InventoryItem.class;
        }
        return ic;
    }

    protected Class<? extends InventoryItem> itemClass() {
        return itemClass;
    }

    protected boolean requiresInvoiceDate() {
        return false;
    }
}
