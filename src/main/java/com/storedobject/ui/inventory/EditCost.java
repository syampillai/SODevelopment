package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.MoneyField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.checkbox.Checkbox;

public class EditCost extends DataForm implements Transactional {

    private final InventoryItem item;
    private final boolean viewMode;
    private MoneyField newCost;
    private Checkbox updateAll;
    private final Runnable runMe;

    public EditCost(InventoryItem item, boolean viewMode) {
        this(item, viewMode, null);
    }

    public EditCost(InventoryItem item, boolean viewMode, Runnable runMe) {
        super(viewMode ? "Cost Details" : "Edit Cost", "Save", "Cancel", viewMode);
        this.runMe = runMe;
        this.viewMode = viewMode;
        this.item = item;
        setButtonsAtTop(!viewMode);
        addField(new ELabelField("Item", item.toDisplay()));
        addField(new ELabelField("Location", LocateItem.locationDisplay(item)));
        addField(new ELabelField("GRN Details", grn()));
        poro();
        addField(new ELabelField("Owned by", item.getOwner().toDisplay()));
        if(item.isSerialized()) {
            addField(new ELabelField("Cost of the Item", item.getCost()));
        } else {
            addField(new ELabelField("Quantity & Cost", item.getQuantity() + ", " + item.getCost()));
        }
        if(viewMode) {
            return;
        }
        newCost = new MoneyField("Cost to Set");
        newCost.setValue(item.getCost());
        addField(newCost);
        if(item.isSerialized()) {
            addField(updateAll = new Checkbox("Update all Serial Numbers?"));
        }
        setFirstFocus(newCost);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        if(viewMode) {
            ok.setVisible(false);
            cancel.setText("Close");
        }
    }

    @Override
    protected boolean process() {
        Money cost = newCost.getValue();
        if(item.getCost().equals(cost)) {
            return false;
        }
        try {
            if(item.updateCost(getTransactionManager(), cost, item.isSerialized() ? updateAll.getValue() : false)) {
                message("Cost updated successfully");
            } else {
                message("Not updated");
            }
        } catch(Exception e) {
            warning(e);
            return false;
        }
        if(runMe != null) {
            close();
            runMe.run();
        }
        return true;
    }

    private String grn() {
        InventoryGRN grn = item.getGRN();
        if(grn == null) {
            return "N/A";
        }
        return grn.getReference() + " dated " + DateUtility.format(grn.getDate()) + " (" + grn.getTypeValue() + ")";
    }

    private void poro() {
        InventoryRO ro = item.getRO();
        if(ro != null) {
            addField(new ELabelField("Received Via Repair Order",
                    ro.getReference() + " dated " + DateUtility.format(ro.getDate())));
        }
        String r;
        InventoryPO po = item.getPO();
        if(po == null) {
            r = "N/A";
        } else {
            r = po.getReference() + " dated " + DateUtility.format(po.getDate());
        }
        addField(new ELabelField("Received Via", r));
    }
}
