package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.MoneyField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComputeLandedCost extends DataForm implements Transactional {

    private final InventoryGRN grn;
    private final List<LandedCost> costs = new ArrayList<>();
    private final List<MoneyField> costFields = new ArrayList<>();

    public ComputeLandedCost(InventoryGRN grn) throws SOException {
        super("Landed Cost");
        this.grn = grn;
        setButtonsAtTop(true);
        setColumns(1);
        addField(new ELabelField("Supplier", grn.getSupplier().toDisplay()));
        List<InventoryPO> pos = grn.listMasters(InventoryPO.class, true).toList();
        if(pos.isEmpty()) {
            throw new SOException("Unable to locate the PO!");
        }
        addField(new ELabelField("GRN & PO Reference", grn.getReference() + ", " + pos.stream()
                .map(po -> po.getReference() + " (" + DateUtility.format(po.getDate()) + ")")
                        .collect(Collectors.joining(", "))));
        List<LandedCost> costs = grn.listLinks(LandedCost.class).toList();
        List<LandedCostType> types = StoredObject.list(LandedCostType.class, null, "DisplayOrder")
                .toList();
        LandedCost cost;
        MoneyField mf;
        for(LandedCostType type: types) {
            cost = costs.stream().filter(c -> c.getTypeId().equals(type.getId())).findAny().orElse(null);
            if(cost == null) {
                if(pos.stream().noneMatch(po -> po.isApplicable(type, grn))) {
                    continue;
                }
                cost = new LandedCost();
                cost.setType(type);
            }
            this.costs.add(cost);
            mf = new MoneyField(type.getName());
            mf.setValue(cost.getAmount());
            costFields.add(mf);
            addField(mf);
        }
        if(this.costs.isEmpty()) {
            throw new SOException("No landed cost configuration found for the associated PO!");
        }
    }

    public ComputeLandedCost(Application application, InventoryGRN grn) throws SOException {
        this(grn);
    }

    @Override
    public int getMaximumContentWidth() {
        return 40;
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Save & Compute");
    }

    @Override
    protected boolean process() {
        clearAlerts();
        if(!transactControl(tc -> {
            Money m;
            LandedCost cost;
            for(int i = 0; i < costFields.size(); i++) {
                m = costFields.get(i).getValue();
                cost = costs.get(i);
                if(!cost.getAmount().equals(m)) {
                    cost.setAmount(m);
                    tc.save(cost);
                    tc.addLink(grn, cost);
                }
            }
        })) {
            return false;
        }
        message("Landed cost details saved successfully");
        try {
            grn.computeLandedCost(getTransactionManager());
            message("Landed cost computed successfully");
        } catch(Exception e) {
            error(e);
            return false;
        }
        return true;
    }
}
