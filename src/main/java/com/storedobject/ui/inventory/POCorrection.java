package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.MoneyField;
import com.storedobject.ui.QuantityField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;

import java.util.List;

public class POCorrection extends SelectStore implements Transactional {

    private final DateField dateField = new DateField("PO Date");
    private final IntegerField noField = new IntegerField("PO Number");

    public POCorrection() {
        super("PO Correction");
        addField(dateField, noField);
        setRequired(dateField);
        setRequired(noField);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        if(!super.process()) {
            return false;
        }
        InventoryStore store = getStore();
        if(store == null) {
            return false;
        }
        List<InventoryPO> pos = StoredObject.list(InventoryPO.class, "Store=" + store.getId()
                + " AND Date='" + Database.format(dateField.getValue()) + "' AND No=" + noField.getValue(), true)
                .toList();
        if(pos.isEmpty()) {
            warning("No POs found!");
            return false;
        }
        close();
        SelectGrid<InventoryPO> g = new SelectGrid<>(InventoryPO.class, pos, StoredObjectUtility.browseColumns(InventoryPO.class),
                this::process);
        g.setCaption("Select PO");
        g.execute();
        return true;
    }

    private void process(InventoryPO po) {
        switch(po.getStatus()) {
            case 0 -> message("This is editable - " + po.toDisplay());
            case 1 -> new ActionForm(po.toDisplay()
                    + "\nItems are ordered, do you really want to recall this PO?",
                    () -> transact(po::recallOrder)).execute();
            default -> {
                SelectGrid<InventoryPOItem> g = new SelectGrid<>(InventoryPOItem.class, po.listItems().toList(),
                        StoredObjectUtility.browseColumns(InventoryPOItem.class),
                        poi -> new EditItem(po, poi).execute());
                g.setCaption("Select Item to Edit");
                g.execute();
            }
        }
    }

    private static class EditItem extends DataForm implements Transactional {

        private final InventoryPOItem poItem;
        private final QuantityField quantityField = new QuantityField("Quantity");
        private final MoneyField unitPriceField = new MoneyField("Unit Price");

        public EditItem(InventoryPO po, InventoryPOItem poItem) {
            super(po.toDisplay());
            this.poItem = poItem;
            addField(new ELabelField("Item", poItem.getPartNumber().toDisplay()), quantityField, unitPriceField);
            setRequired(quantityField);
            setRequired(unitPriceField);
            if(!poItem.getReceived().isZero()) {
                setFieldReadOnly(quantityField);
            }
            quantityField.setValue(poItem.getQuantity());
            unitPriceField.setValue(poItem.getUnitPrice());
        }

        @Override
        protected boolean process() {
            clearAlerts();
            boolean simple = poItem.getReceived().isZero();
            if(simple) {
                Quantity q = quantityField.getValue();
                InventoryItemType itemType = poItem.getPartNumber();
                if(!q.isCompatible(itemType.getUnitOfMeasurement())) {
                    warning("Unit is not compatible with UOM of " + itemType.toDisplay());
                    return false;
                }
                poItem.setQuantity(q);
                poItem.setUnitPrice(unitPriceField.getValue());
                if(transact(poItem::save)) {
                    message("Corrected successfully");
                }
                return true;
            }
            if(transact(t -> poItem.correctUnitPrice((DBTransaction) t, unitPriceField.getValue()))) {
                message("Corrected successfully");
            }
            return true;
        }
    }
}
