package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSendAndReceiveMaterial extends ObjectBrowser<MaterialReturned> {

    private final Button send = new Button("Send", VaadinIcon.TRUCK, e -> send());
    private final Button receive = new Button("Receive", VaadinIcon.STORAGE, e -> receive());
    private final ObjectField<InventoryLocation> fromField, toField;
    private InventoryLocation fromOrTo;
    private final boolean receiveMode;

    public AbstractSendAndReceiveMaterial(boolean receiveMode) {
        this((String) null, receiveMode);
    }

    public AbstractSendAndReceiveMaterial(String fromOrTo, boolean receiveMode) {
        this(fromOrToField(fromOrTo, receiveMode), receiveMode);
    }

    public AbstractSendAndReceiveMaterial(InventoryLocation fromOrTo, boolean receiveMode) {
        this(LocationField.create(fromOrTo), receiveMode);
    }

    private AbstractSendAndReceiveMaterial(LocationField fromOrToField, boolean receiveMode) {
        super(MaterialReturned.class,
                receiveMode ? StringList.create("Date", "ReferenceNumber", "FromLocation as Sent from", "Received") :
                        StringList.create("Date", "ReferenceNumber", "ToLocation as Return to", "Status"));
        this.receiveMode = receiveMode;
        setCaption((receiveMode ? "Receive" : "Sent") + " Materials");
        send.setVisible(!receiveMode);
        receive.setVisible(receiveMode);
        this.fromOrTo = fromOrToField.getValue();
        if(receiveMode) {
            this.toField = new ObjectField<>("Receive at", fromOrToField);
            this.fromField = new ObjectField<>("Sent from", new LocationField(4, 5, 11));
        } else {
            this.fromField = new ObjectField<>("Sent from", fromOrToField);
            this.toField = new ObjectField<>("Return to", new LocationField(0));
        }
        setFilter((receiveMode ? "To" : "From") + "Location=" + this.fromOrTo.getId() + (receiveMode ? " AND Status=1" : ""));
        setOrderBy("Date DESC,ReferenceNumber");
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

    public String getReceived(MaterialReturned mr) {
        return mr != null && mr.getStatus() == 2 ? "Yes" : "No";
    }

    @Override
    protected ObjectEditor<MaterialReturned> createObjectEditor() {
        return new MREditor();
    }

    @Override
    public boolean canEdit(MaterialReturned object) {
        if(object.getStatus() > 0) {
            warning("Changes not possible with status = " + object.getStatusValue());
            return false;
        }
        return super.canEdit(object);
    }

    @Override
    public boolean canDelete(MaterialReturned object) {
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
        MaterialReturned mr = selected();
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
        MaterialReturned mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getStatus() != 1) {
            warning("Already received!");
            return;
        }
        List<InventoryItem> items = new ArrayList<>();
        mr.listLinks(MaterialReturnedItem.class).map(MaterialReturnedItem::getItem).collectAll(items);
        new ReceiveAndBin(mr.getDate(), "Receipt " + mr.getReferenceNumber(), items, mr::receive, () -> refresh(mr)).execute(getView());
    }

    private class MREditor extends ObjectEditor<MaterialReturned> {

        private MRItemGrid mrItemGrid;

        public MREditor() {
            super(MaterialReturned.class);
            if(!receiveMode) {
                setNewObjectGenerator(() -> {
                    MaterialReturned mr = new MaterialReturned();
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
                mrItemGrid = new MRItemGrid((ObjectLinkField<MaterialReturnedItem>) field);
                return mrItemGrid;
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        public void setObject(MaterialReturned object, boolean load) {
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

    private class MRItemGrid extends DetailLinkGrid<MaterialReturnedItem> {

        private ItemInput<?> itemInput;

        public MRItemGrid(ObjectLinkField<MaterialReturnedItem> linkField) {
            super(linkField);
        }

        @Override
        public ObjectEditor<MaterialReturnedItem> constructObjectEditor() {
            return new MRIEditor();
        }

        private class MRIEditor extends ObjectEditor<MaterialReturnedItem> {

            private QuantityField quantityField;

            public MRIEditor() {
                super(MaterialReturnedItem.class);
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
            public void setObject(MaterialReturnedItem object, boolean load) {
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
