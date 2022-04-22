package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.MessageGrid;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.storedobject.vaadin.PopupButton;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

public class RequestMaterial extends AbstractRequestMaterial {

    public RequestMaterial(String from) {
        this(ParameterParser.itemTypeClass(from), ParameterParser.location(from, 0, 4, 5, 10, 11, 16));
    }

    public RequestMaterial(InventoryLocation from) {
        this(null, from, null);
    }

    public RequestMaterial(InventoryLocation from, InventoryLocation to) {
        this(null, from, to);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from) {
        this(itemTypeClass, from, null);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from,
                           InventoryLocation to) {
        super(false, from, 0, to);
        if(itemTypeClass != null) {
            this.itemTypeClass = itemTypeClass;
        }
    }

    @Override
    void created() {
        super.created();
        setFixedFilter("Status<=1");
    }

    @Override
    String getFixedSide() {
        return "From";
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        PopupButton sendRequest = new PopupButton("Send Request", VaadinIcon.PAPERPLANE);
        sendRequest.add(new Button("Immediate Requirement", "", e -> send(false)));
        sendRequest.add(new Button("Reserve Items", "", e -> send(true)));
        buttonPanel.add(sendRequest);
        buttonPanel.add(new ConfirmButton("Foreclose", "close", e -> foreclose()));
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<=1"));
        buttonPanel.add(h);
        buttonPanel.add(new Button("\u21f0 Receive Screen", VaadinIcon.TRUCK, e -> receive()));
    }

    private void send(boolean reserve) {
        MaterialRequest mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getStatus() > 1) {
            warning("Can't request again, status is already '" + mr.getStatusValue() + "'.");
            return;
        }
        if(mr.getStatus() == 1) {
            warning("Already sent");
            return;
        }
        if(!mr.existsLinks(MaterialRequestItem.class)) {
            warning("No items to request");
            return;
        }
        if(reserve ? transact(mr::reserve) : transact(mr::request)) {
            refresh(mr);
            message("R" + (reserve ? "eservation r" : "") + "equest sent");
        }
    }

    private void foreclose() {
        MaterialRequest mr = selected();
        if(mr == null) {
            return;
        }
        switch(mr.getStatus()) {
            case 3 -> {
                warning("Can't foreclose it, all items are already issued... Please receive items to close it.");
                return;
            }
            case 4 -> {
                warning("Already closed!");
                return;
            }
        }
        MaterialIssued mi = StoredObject.get(MaterialIssued.class, "Request=" + mr.getId() + " AND Status<2");
        if(transact(mr::foreclose)) {
            refresh(mr);
            String m = "Foreclosed";
            if(mi == null) {
                message(m);
            } else {
                message(m + ", however please receive items that were already issued!");
            }
        }
    }

    private void receive() {
        close();
        new ReceiveMaterialRequested(getFromOrTo()).execute();
    }

    @Override
    public void doDelete(MaterialRequest object) {
        List<MaterialIssued> mis = StoredObject.list(MaterialIssued.class, "Request=" + object.getId()).
                toList();
        if(mis.isEmpty()) {
            super.doDelete(object);
            return;
        }
        new MessageGrid<>(MaterialIssued.class, mis,
                StringList.create("Date", "ReferenceNumber AS Reference", "Location AS Issued from", "Status"),
                "Materials were issued via these. Deletion is possible only after closing and deleting these!").
                execute(this.getView());
    }
}
