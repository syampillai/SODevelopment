package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSendAndReceiveMaterial<T extends InventoryTransfer, L extends InventoryTransferItem>
        extends ObjectBrowser<T> {

    private static final String OLD_ITEM = "Can't edit/delete when Status = Sent";
    private static final String LABEL_TOOL = "Tool/Item under Custody";
    private static final String LABEL_TOOLS = "Tools/Items under Custody";
    static final int[] ALL_TYPES = new int[] { 0, 3, 4, 5, 8, 10, 11 };
    private final Button send = new ConfirmButton("Send Items", VaadinIcon.TRUCK, e -> send());
    private final Button receive = new Button("Receive Items", VaadinIcon.STORAGE, e -> receive());
    private final Button grnButton;
    private final ObjectField<InventoryLocation> fromField, toField, filterField;
    private InventoryLocation fromOrTo;
    private InventoryLocation otherLocation;
    private final boolean receiveMode;
    private final Class<L> transferItemClass;
    Class<? extends InventoryItem> itemClass;
    private GRNEditor grnEditor;
    private InventoryGRN grn;
    private boolean searching = false;
    private Search search;
    private final ELabel searchLabel = new ELabel();
    private final ELabel countLabel = new ELabel("0");
    private T parent;

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
                        StringList.create("Date", "Reference", "ReferenceNumber AS Other Reference",
                                "FromLocation AS From", "Received") :
                        StringList.create("Date", "Reference", "ReferenceNumber AS Other Reference",
                                "Consignment",
                                "ToLocation AS " + (transferClass == MaterialReturned.class ? "Return" :
                                        (transferClass == InventoryRO.class ? "Send" : "Transfer")) + " to",
                                "Status"));
        addConstructedListener(v -> created());
        this.transferItemClass = itemClass;
        this.receiveMode = receiveMode;
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
        LocationField lf, ff;
        if(receiveMode) {
            this.toField = new ObjectField<>("Receive at", fromOrToField);
            lf = this.otherLocation == null ? new LocationField(ALL_TYPES).remove(this.fromOrTo) :
                    LocationField.create(this.otherLocation);
            ff = this.otherLocation == null ? new LocationField(ALL_TYPES).remove(this.fromOrTo) :
                    LocationField.create(this.otherLocation);
            this.fromField = new ObjectField<>("Sent from", lf);
            this.filterField = new ObjectField<>(this.fromField.getLabel(), ff);
        } else {
            this.fromField = new ObjectField<>("Sent from", fromOrToField);
            if(transferClass == MaterialReturned.class) {
                lf = this.otherLocation == null ? new LocationField(0).remove(this.fromOrTo) :
                        LocationField.create(this.otherLocation);
                ff = this.otherLocation == null ? new LocationField(0).remove(this.fromOrTo) :
                        LocationField.create(this.otherLocation);
                this.toField = new ObjectField<>("Return to", lf);
                this.filterField = new ObjectField<>(this.toField.getLabel(), ff);
            } else {
                if(this.otherLocation == null) {
                    if(transferClass == InventoryRO.class) {
                        lf = new LocationField(3);
                        ff = new LocationField(3);
                    } else {
                        lf = new LocationField(ALL_TYPES).remove(this.fromOrTo);
                        ff = new LocationField(ALL_TYPES).remove(this.fromOrTo);
                    }
                } else {
                    lf = LocationField.create(this.otherLocation);
                    ff = LocationField.create(this.otherLocation);
                }
                this.toField = new ObjectField<>((transferClass == InventoryRO.class ? "Sent" : "Transferred") +
                        " to", lf);
                this.filterField = new ObjectField<>(this.toField.getLabel(), ff);
            }
        }
        setOrderBy("Date DESC,No DESC");
        GridContextMenu<T> cm = new GridContextMenu<>(this);
        cm.addItem((receiveMode ? "Receive" : "Send") + " Items", e -> e.getItem().ifPresent(this::rowDoubleClicked));
        GridMenuItem<T> grnMenu = cm.addItem("Associated GRN", e ->  grn());
        cm.setDynamicContentHandler(o -> {
            if(o == null) {
                return false;
            }
            grn = receiveMode ? o.listLinks(InventoryGRN.class).findFirst() : null;
            grnMenu.setVisible(grn != null);
            return grn != null || (receiveMode ? o.getStatus() == 1 : o.getStatus() == 0);
        });
        if(receiveMode) {
            grnButton = new Button("GRN", VaadinIcon.FILE_TABLE, e -> grnSel());
        } else {
            grnButton = null;
        }
        if(transferClass == InventoryRO.class) {
            setCaption("Send Items for Repair");
        } else {
            String c = null;
            if(receiveMode) {
                c = switch(getLocationFrom().getType()) {
                    case 3 -> "Materials";
                    case 18 -> "Tools";
                    default -> null;
                };
            }
            if(c == null) {
                c = "Materials/Tools";
            }
            setCaption((receiveMode ? "Receive" : (transferClass == MaterialReturned.class ? "Return" : "Transfer")) +
                    " " + c);
        }
        if(!receiveMode && print != null && print.definitions()
                .noneMatch(d -> d.getPrintLogicClassName().equals(CreateConsignment.class.getName()))) {
            setColumnVisible("Consignment", false);
        }
    }

    public String getConsignment(T it) {
        Consignment consignment = it.listLinks(Consignment.class).findFirst();
        return consignment == null ? "" : consignment.getReference();
    }

    public Class<L> getItemClass() {
        return transferItemClass;
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

    private void grnSel() {
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
        ButtonLayout buttonLayout = new ButtonLayout();
        if(locSwitch != null) {
            e.update();
            buttonLayout.add(e, locSwitch.asSmall());
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
                append("Double-click" + (receiveMode ? "" : " or right-click") + " on the entry to " +
                        (receiveMode ? "receive" : "send")
                        + (receiveMode ? ". Right-click on the entry for more options." : ""), Application.COLOR_SUCCESS);
        e.update();
        buttonLayout.add(e, searchLabel, new ELabel("| ", Application.COLOR_INFO).append("Entries:").update(),
                countLabel);
        prependHeader().join().setComponent(buttonLayout);
    }

    protected Button getSwitchLocationButton() {
        return null;
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        ConfirmButton amend = null;
        if(receiveMode) {
            add.setVisible(false);
            edit.setVisible(false);
        } else {
            amend = new ConfirmButton("Amend", e -> amend());
        }
        buttonPanel.add(new Button("Search", e -> searchFilter()), send, amend, receive, grnButton);
    }

    protected boolean allowAmendment() {
        return false;
    }

    @Override
    public void rowDoubleClicked(T object) {
        if(object != null) {
            select(object);
            if(receiveMode) {
                receive();
            } else {
                send();
            }
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
        clearAlerts();
        T mt = selected();
        if(mt == null) {
            return;
        }
        if(mt.getStatus() != 0) {
            warning("Already sent!");
            return;
        }
        if(!mt.existsLinks(getItemClass(), "Amendment=" + mt.getAmendment())) {
            warning("No items to send");
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
        boolean allowPNSNChange = switch(getLocationFrom().getType()) {
            case 3, 11 -> true;
            default -> false;
        };
        new ReceiveAndBin(mt.getDate(), "Receipt " + mt.getReference(), items, mt::receive,
                () -> refresh(mt), gEd, allowPNSNChange, allowPNSNChange).execute(getView());
    }

    private int amendment() {
        return parent == null ? 0 : parent.getAmendment();
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
            if("InvoiceNumber".equals(fieldName) || "InvoiceDate".equals(fieldName)) {
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
            parent = object;
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
            if(allowAmendment()) {
                createColumn("Status");
            }
        }

        @Override
        public void constructed() {
            super.constructed();
            if(fromOrTo.getType() == 18) {
                getColumn("Item.PartNumber.Name").setHeader(LABEL_TOOL);
                getColumn("Quantity").setVisible(false);
            }
        }

        @SuppressWarnings("unused")
        public String getStatus(L object) {
            if(parent != null && parent.getStatus() > 0) {
                return parent.getStatusValue();
            }
            if(parent == null || parent.getAmendment() == object.getAmendment()) {
                return "Not sent";
            }
            return "Sent";
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

        @Override
        public boolean canEdit(L item) {
            return can(item);
        }

        @Override
        public boolean canDelete(L item) {
            return can(item);
        }

        private class MRIEditor extends ObjectEditor<L> {

            private QuantityField quantityField;

            public MRIEditor() {
                super(transferItemClass);
                setCaption("Item Detail");
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
                if(AbstractSendAndReceiveMaterial.this.getObjectClass() == InventoryRO.class
                        && itemField.getField() instanceof ItemField<InventoryItem> iField) {
                    iField.getPNField().setLoadFilter(i -> i.isRepairAllowed() || i.isConsumable());
                }
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
                if(fromOrTo.getType() == 18) { // Custody location
                    setFieldHidden(quantityField);
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
                object.setAmendment(amendment());
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

            @Override
            public boolean canEdit() {
                return can(getObject()) && super.canEdit();
            }

            @Override
            public boolean canDelete() {
                return can(getObject()) && super.canDelete();
            }
        }
    }

    private boolean can(L iti) {
        if(iti == null) {
            return false;
        }
        if(amendment() != iti.getAmendment()) {
            clearAlerts();
            warning(OLD_ITEM);
            return false;
        }
        return true;
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
                new String[] { "Part Number", "Date Period", "No.", filterField.getLabel() });
        private final ObjectGetField<InventoryItemType> pnField =
                new ObjectGetField<>("Part Number", InventoryItemType.class, true);
        private final DatePeriodField periodField = new DatePeriodField("Date Period");
        private final IntegerField noField = new IntegerField("No.");

        public Search() {
            super("Search");
            filterField.setVisible(false);
            noField.setVisible(false);
            periodField.setVisible(false);
            search.addValueChangeListener(e -> vis());
            addField(search, pnField, periodField, noField, filterField);
        }

        private void vis() {
            int s = search.getValue();
            pnField.setVisible(s == 0);
            periodField.setVisible(s == 1);
            noField.setVisible(s == 2);
            filterField.setVisible(s == 3);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            vis();
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            AbstractSendAndReceiveMaterial.this.clearAlerts();
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
                    setLoadFilter(p -> p.existsLinks(transferItemClass, "Item.PartNumber=" + pnId, true));
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
                    filter = "No. = " + no;
                    setLoadFilter(p -> p.getNo() == no);
                }
                case 3 -> {
                    InventoryLocation loc = filterField.getObject();
                    if(loc == null) {
                        searching = false;
                        return true;
                    }
                    filter = filterField.getLabel() + " = " + loc.toDisplay();
                    Id id = loc.getId();
                    setLoadFilter(p -> p.getToLocationId().equals(id));
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

    private void amend() {
        clearAlerts();
        T it = selected();
        if(it == null) {
            return;
        }
        switch(it.getStatus()) {
            case 0 -> {
                message("Editing instead of amending...");
                edit.click();
            }
            case 1, 2 -> {
                AtomicReference<Id> id = new AtomicReference<>(null);
                if(transact(t -> id.set(it.amend(t)))) {
                    load();
                    T itNew = StoredObject.get(getObjectClass(), id.get());
                    scrollTo(itNew);
                    select(itNew);
                }
            }
            default -> warning("Can't amend with Status = " + it.getStatusValue());
        }
    }
}
