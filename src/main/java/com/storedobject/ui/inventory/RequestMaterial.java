package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.MessageGrid;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

public class RequestMaterial extends AbstractRequestMaterial {

    public RequestMaterial(String from) {
        this(ParameterParser.itemTypeClass(from), ParameterParser.location(from, 0, 4, 5, 10, 11, 16));
    }

    public RequestMaterial(InventoryLocation from) {
        this(null, from);
    }

    public RequestMaterial(Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from) {
        super(false, from, 0);
        if(itemTypeClass != null) {
            this.itemTypeClass = itemTypeClass;
        }
    }

    @Override
    void created() {
        super.created();
        setExtraFilter("Status<=1");
    }

    @Override
    String getFixedSide() {
        return "From";
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        buttonPanel.add(new Button("Send Request", VaadinIcon.PAPERPLANE, e -> send()));
        buttonPanel.add(new ConfirmButton("Foreclose", "close", e -> foreclose()));
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status<=1"));
        buttonPanel.add(h);
        buttonPanel.add(new Button("\u21f0 Receive Screen", VaadinIcon.TRUCK, e -> receive()));
    }

    private void send() {
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
        if(transact(mr::request)) {
            refresh(mr);
            message("Request sent");
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
