package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSendAndReceiveMaterial<T extends InventoryTransfer, L extends InventoryTransferItem> extends ObjectBrowser<T> {

    private final Button send = new Button("Send", VaadinIcon.TRUCK, e -> send());
    private final Button receive = new Button("Receive", VaadinIcon.STORAGE, e -> receive());
    private final ObjectField<InventoryLocation> fromField, toField;
    private InventoryLocation fromOrTo;
    private final boolean receiveMode;
    private final Class<L> itemClass;

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, boolean receiveMode) {
        this(transferClass, itemClass, (String) null, receiveMode);
    }

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, String fromOrTo, boolean receiveMode) {
        this(transferClass, itemClass, fromOrToField(fromOrTo, receiveMode), receiveMode);
    }

    public AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, InventoryLocation fromOrTo, boolean receiveMode) {
        this(transferClass, itemClass, LocationField.create(fromOrTo), receiveMode);
    }

    private AbstractSendAndReceiveMaterial(Class<T> transferClass, Class<L> itemClass, LocationField fromOrToField, boolean receiveMode) {
        super(transferClass,
                receiveMode ? StringList.create("Date", "ReferenceNumber", "FromLocation as Sent from", "Received") :
                        StringList.create("Date", "ReferenceNumber", "ToLocation as " + (transferClass == MaterialReturned.class ? "Return" : "Transfer") + " to", "Status"));
        this.itemClass = itemClass;
        this.receiveMode = receiveMode;
        setCaption((receiveMode ? "Receive" : "Sent") + " Materials");
        send.setVisible(!receiveMode);
        receive.setVisible(receiveMode);
        this.fromOrTo = fromOrToField.getValue();
        if(receiveMode) {
            this.toField = new ObjectField<>("Receive at", fromOrToField);
            this.fromField = new ObjectField<>("Sent from", new LocationField(0, 4, 5, 11));
        } else {
            this.fromField = new ObjectField<>("Sent from", fromOrToField);
            if(transferClass == MaterialReturned.class) {
                this.toField = new ObjectField<>("Return to", new LocationField(0));
            } else {
                this.toField = new ObjectField<>("Transferred to", new LocationField(0, 4, 5, 11));
            }
        }
        setOrderBy("Date DESC,ReferenceNumber");
        setExtraFilter((String)null);
    }

    @Override
    public void setExtraFilter(String extraFilter) {
        String f = (receiveMode ? "To" : "From") + "Location=" + this.fromOrTo.getId() + (receiveMode ? " AND Status=1" : "");
        if(extraFilter != null) {
            f += " AND (" + extraFilter + ")";
        }
        super.setExtraFilter(f);
    }

    private static LocationField fromOrToField(String fromOrTo, boolean receiveMode) {
        if(receiveMode) {
            return LocationField.create(null, fromOrTo, 0);
        }
        return LocationField.create(null, fromOrTo, 4, 5, 11);
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(new ELabel(receiveMode ? "Receive at: " : "From: ")
                .append(fromOrTo.toDisplay(), "blue").update());
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        if(receiveMode) {
            add.setVisible(false);
            edit.setVisible(false);
        }
        buttonPanel.add(send, receive);
    }

    public String getReceived(T mr) {
        return mr != null && mr.getStatus() == 2 ? "Yes" : "No";
    }

    @Override
    protected ObjectEditor<T> createObjectEditor() {
        return new MREditor();
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
        T mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getStatus() != 0) {
            warning("Already sent!");
            return;
        }
        if(transact(mr::send)) {
            refresh(mr);
            message("Sent successfully");
        }
    }

    private void receive() {
        T mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getStatus() != 1) {
            warning("Already received!");
            return;
        }
        List<InventoryItem> items = new ArrayList<>();
        mr.listLinks(itemClass).map(InventoryTransferItem::getItem).collectAll(items);
        new ReceiveAndBin(mr.getDate(), "Receipt " + mr.getReferenceNumber(), items, mr::receive, () -> refresh(mr)).execute(getView());
    }

    private class MREditor extends ObjectEditor<T> {

        private MRItemGrid mrItemGrid;

        public MREditor() {
            super(AbstractSendAndReceiveMaterial.this.getObjectClass());
            if(!receiveMode) {
                setNewObjectGenerator(() -> {
                    T mr = getObjectClass().getDeclaredConstructor().newInstance();
                    mr.setFromLocation(fromOrTo);
                    return mr;
                });
            }
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            if(receiveMode) {
                toField.setValue(fromOrTo);
                setFieldReadOnly(toField);
            } else {
                fromField.setValue(fromOrTo);
                setFieldReadOnly(fromField);
            }
            setFieldHidden("Status");
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName) {
            switch(fieldName) {
                case "FromLocation":
                    return fromField;
                case "ToLocation":
                    return toField;
            }
            return super.createField(fieldName);
        }

        @Override
        protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
            if("Items.l".equals(fieldName)) {
                //noinspection unchecked
                mrItemGrid = new MRItemGrid((ObjectLinkField<L>) field);
                return mrItemGrid;
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        public void setObject(T object, boolean load) {
            super.setObject(object, load);
            if(object != null && !receiveMode) {
                if(!fromOrTo.getId().equals(object.getFromLocationId())) {
                    fromOrTo = object.getFromLocation();
                    if(fromOrTo instanceof InventoryStoreBin) {
                        mrItemGrid.itemInput.setStore(((InventoryStoreBin) fromOrTo).getStore());
                    } else {
                        mrItemGrid.itemInput.setLocation(fromOrTo);
                    }
                }
            }
        }
    }

    private class MRItemGrid extends DetailLinkGrid<L> {

        private ItemInput<?> itemInput;

        public MRItemGrid(ObjectLinkField<L> linkField) {
            super(linkField);
        }

        @Override
        public ObjectEditor<L> constructObjectEditor() {
            return new MRIEditor();
        }

        private class MRIEditor extends ObjectEditor<L> {

            private QuantityField quantityField;

            public MRIEditor() {
                super(itemClass);
            }

            @Override
            protected void formConstructed() {
                super.formConstructed();
                quantityField = (QuantityField) getField("Quantity");
                //noinspection unchecked
                ObjectField<InventoryItem> itemField = (ObjectField<InventoryItem>) getObjectEditor().getField("Item");
                itemInput = (ItemInput<?>)(itemField).getField();
                itemField.addValueChangeListener(e -> changed(itemInput.getValue()));
                if(!receiveMode) {
                    itemInput.setLocation(fromOrTo);
                }
            }

            @Override
            public void setObject(L object, boolean load) {
                super.setObject(object, load);
                if(object != null) {
                    changed(object.getItem());
                }
            }

            private void changed(InventoryItem item) {
                if(item == null) {
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
}
