package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.core.MaterialRequest;
import com.storedobject.core.MaterialRequestItem;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

public class RequestMaterial extends AbstractRequestMaterial {

    public RequestMaterial() {
        super(false, 0);
    }

    public RequestMaterial(String from) {
        super(false, from, 0);
    }

    public RequestMaterial(InventoryLocation from) {
        super(false, from, 0);
    }

    @Override
    public final void constructed() {
        super.constructed();
        setExtraFilter("Status<=1");
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        buttonPanel.add(new Button("Send Request", VaadinIcon.PAPERPLANE, e -> sendRequest()));
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status<=1"));
        buttonPanel.add(h);
    }

    private void sendRequest() {
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
}
