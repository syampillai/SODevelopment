package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.DataForm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ComputeLandedCost extends DataForm implements Transactional {

    private final InventoryGRN grn;
    private final List<LandedCost> costs = new ArrayList<>();
    private final List<MoneyField> costFields = new ArrayList<>();
    private final ObjectBrowser<InventoryGRN> grns;

    public ComputeLandedCost(InventoryGRN grn, ObjectBrowser<InventoryGRN> grns) throws SOException {
        super("Landed Cost");
        this.grns = grns;
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
        types.removeIf(t -> t.getInactive() && costs.stream().filter(c -> !c.getAmount().isZero())
                .noneMatch(c -> c.getType().getId().equals(t.getId())));
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
            mf = new MoneyField((type.getDeduct() ? "-" : "+") + ' ' + type.getName());
            mf.setValue(cost.getAmount());
            costFields.add(mf);
            addField(mf);
        }
        if(this.costs.isEmpty()) {
            throw new SOException("No landed cost configuration found for the associated PO!");
        }
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
        AtomicBoolean changed = new AtomicBoolean(false);
        if(!transactControl(tc -> {
            Money m;
            LandedCost cost;
            for(int i = 0; i < costFields.size(); i++) {
                m = costFields.get(i).getValue();
                cost = costs.get(i);
                if(!cost.getAmount().equals(m)) {
                    changed.set(true);
                    cost.setAmount(m);
                    tc.save(cost);
                    tc.addLink(grn, cost);
                }
            }
        }, "No changes were made to the landed cost details!")) {
            return false;
        }
        if(changed.get()) {
            message("Landed cost details saved successfully");
        }
        try {
            grn.computeLandedCost(getTransactionManager());
            message("Landed cost " + (changed.get() ? "" : "re") + "computed successfully");
        } catch(Exception e) {
            error(e);
            return false;
        } finally {
            if(grns != null) {
                grn.reload();
                grns.refresh(grn);
            }
        }
        return true;
    }
}
