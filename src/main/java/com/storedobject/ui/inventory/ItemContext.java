package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DataGrid;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ItemContext {

    @SuppressWarnings("rawtypes")
    private ObjectEditor editor;
    @SuppressWarnings("rawtypes")
    private ObjectEditor pnEditor;
    private GRNEditor grnEditor;
    private Runnable refresher;
    private ExecutableView view;

    public ItemContext() {
    }

    public void setRefresher(Runnable refresher) {
        this.refresher = refresher;
    }

    public void setView(ExecutableView view) {
        this.view = view;
    }

    private void refresh() {
        if(refresher != null) {
            refresher.run();
        }
    }

    public void inspect(InventoryItem item) {
        if(item == null) {
            return;
        }
        if(view != null) {
            view.close();
        }
        List<InventoryItem> list = new ArrayList<>();
        list.add(item);
        new ReceiveAndBin(list).execute();
    }

    public void inspect(HasInventoryItem hasItem) {
        inspect(hasItem == null ? null : hasItem.getItem());
    }

    public void split(InventoryItem item) {
        if(item == null) {
            return;
        }
        if(view != null) {
            view.close();
        }
        new SplitQuantity(item).execute();
    }

    public void split(HasInventoryItem hasItem) {
        split(hasItem == null ? null : hasItem.getItem());
    }

    public void breakAssembly(InventoryItem item) {
        if(view != null) {
            return;
        }
        ELabel m = new ELabel(item.toDisplay(), Application.COLOR_SUCCESS);
        m.newLine().append("Do you really want to take out this item?", Application.COLOR_ERROR).newLine();
        m.append("Fitment location:").newLine();
        m.append(locationDisplay(item));
        if(view instanceof DataGrid<?> dg) {
            dg.deselectAll();
        }
        new ActionForm(m.update(), () -> new DetachFromAssembly(item).execute()).execute();
    }

    public void breakAssembly(HasInventoryItem hasItem) {
        breakAssembly(hasItem == null ? null : hasItem.getItem());
    }

    public void view(InventoryItem item) {
        if(item != null) {
            //noinspection unchecked
            editor(item).viewObject(item);
        }
    }

    public void view(HasInventoryItem hasItem) {
        view(hasItem == null ? null : hasItem.getItem());
    }

    public void view(InventoryItemType itemType) {
        if(itemType != null) {
            //noinspection unchecked
            editor(itemType).viewObject(itemType);
        }
    }

    public void view(HasInventoryItemType hasItemType) {
        view(hasItemType == null ? null : hasItemType.getInventoryItemType());
    }

    public void viewMovements(InventoryItem item) {
        if(item != null) {
            new ItemMovementView(item).execute();
        }
    }

    public void viewMovements(HasInventoryItem hasItem) {
        viewMovements(hasItem == null ? null : hasItem.getItem());
    }

    public void viewAssembly(InventoryItem item) {
        if(item != null) {
            new ViewAssembly<>(item).execute();
        }
    }

    public void viewAssembly(HasInventoryItem hasItem) {
        viewAssembly(hasItem == null ? null : hasItem.getItem());
    }

    public void viewParentAssembly(InventoryItem item) {
        if(item == null) {
            return;
        }
        if(item.getLocation() instanceof InventoryFitmentPosition f) {
            new ViewAssembly<>(f.getItem()).execute();
        }
    }

    public void viewParentAssembly(HasInventoryItem hasItem) {
        viewParentAssembly(hasItem == null ? null : hasItem.getItem());
    }

    public void viewStock(InventoryItem item) {
        if(item == null) {
            return;
        }
        viewStock(item.getPartNumber());
    }

    public void viewStock(InventoryItemType itemType) {
        new LocateItem(itemType).execute();
    }

    public void viewStock(HasInventoryItem hasItem) {
        viewStock(hasItem == null ? null : hasItem.getItem());
    }

    public void viewStock(HasInventoryItemType hasInventoryItemType) {
        viewStock(hasInventoryItemType == null ? null : hasInventoryItemType.getInventoryItemType());
    }

    @SuppressWarnings("rawtypes")
    private ObjectEditor editor(InventoryItem item) {
        if(editor != null && editor.getObjectClass() != item.getClass()) {
            editor = null;
        }
        if(editor == null) {
            editor = ObjectEditor.create(item.getClass());
        }
        return editor;
    }

    @SuppressWarnings("rawtypes")
    private ObjectEditor editor(InventoryItemType itemType) {
        if(pnEditor != null && pnEditor.getObjectClass() != itemType.getClass()) {
            pnEditor = null;
        }
        if(pnEditor == null) {
            pnEditor = ObjectEditor.create(itemType.getClass());
        }
        return pnEditor;
    }

    static String locationDisplay(InventoryItem item) {
        if(item == null) {
            return "";
        }
        InventoryLocation loc = item.getLocation();
        if(!(loc instanceof InventoryFitmentPosition)) {
            return item.getLocationDisplay();
        }
        InventoryLocation location;
        String s;
        StringBuilder sb = new StringBuilder();
        while(item != null) {
            location = item.getLocation();
            s = location instanceof InventoryFitmentPosition ?
                    ((InventoryFitmentPosition) location).toDisplay(false) :
                    item.getLocationDisplay();
            if(!sb.isEmpty()) {
                sb.append('\n');
            }
            sb.append(s);
            if(!(location instanceof InventoryFitmentPosition)) {
                break;
            }
            item = item.getParentItem();
        }
        return sb.toString();
    }

    private class DetachFromAssembly extends DataForm implements Transactional {

        private final InventoryItem item;
        private final DateField dateField = new DateField("Date");
        private final TextField referenceField = new TextField("Reference");
        private final InventoryLocation location;

        public DetachFromAssembly(InventoryItem item) {
            super("Detach from Assembly");
            this.item = item;
            location = item.getRealLocation();
            addField(new ELabelField("Item", item.toDisplay(), Application.COLOR_SUCCESS),
                    new ELabelField("Current location", locationDisplay(item), Application.COLOR_SUCCESS),
                    new ELabelField("After removal, it will be available at", location.toDisplay(), Application.COLOR_SUCCESS),
                    dateField, referenceField);
            setRequired(referenceField);
        }

        @Override
        protected boolean process() {
            Date d = dateField.getValue();
            if(!d.before(DateUtility.tomorrow())) {
                warning("Invalid date!");
                dateField.focus();
                return false;
            }
            String ref = referenceField.getValue().trim();
            if(ref.isEmpty()) {
                referenceField.setValue("");
                referenceField.focus();
                return false;
            }
            close();
            InventoryTransaction it = new InventoryTransaction(getTransactionManager(), dateField.getValue());
            it.moveTo(item, ref, location);
            if(transact(it::save)) {
                message("Item '" + item.toDisplay() +
                        "' is removed from the assembly and is available at '" + location + "' now!");
                refresh();
            }
            if(view instanceof DataGrid<?> dg) {
                if(InventoryItem.class.isAssignableFrom(dg.getDataClass())) {
                    //noinspection unchecked
                    ((DataGrid<InventoryItem>)dg).select(item);
                }
            }
            return true;
        }
    }

    public void viewFitment(InventoryItem item) {
        if(item == null || !(item.getLocation() instanceof InventoryFitmentPosition loc)) {
            return;
        }
        InventoryAssembly ia = loc.getAssembly();
        TextView tv = new TextView("Fitment Details");
        tv.append("Assembly Configuration: ").append(ia.toDisplay(), Application.COLOR_SUCCESS).newLine()
                .append("Fitted Item: ").append(item.toDisplay(), Application.COLOR_SUCCESS);
        if(!ia.getItemTypeId().equals(item.getPartNumberId())) {
            tv.append(" (APN)", Application.COLOR_INFO);
        }
        tv.newLine().append("Fitted on: ").append(loc.getItem().toDisplay(), Application.COLOR_SUCCESS);
        tv.popup();
    }

    public void viewFitment(HasInventoryItem hasItem) {
        viewFitment(hasItem == null ? null : hasItem.getItem());
    }

    public void viewFitmentLocations(InventoryItem item) {
        if(item == null) {
            return;
        }
        new FitmentLocations(item);
    }

    public void viewFitmentLocations(HasInventoryItem hasItem) {
        viewFitmentLocations(hasItem == null ? null : hasItem.getItem());
    }

    public void viewGRN(InventoryItem item) {
        viewGRN(item, false);
    }

    public void viewGRN(InventoryItem item, boolean includeSource) {
        if(item == null) {
            return;
        }
        if(view instanceof ListGrid<?> dg) {
            dg.clearAlerts();
        } else if(view instanceof View v) {
            v.clearAlerts();
        }
        InventoryGRN grn = item.getGRN();
        if(grn == null) {
            String m = "No associated GRN found for " + item.toDisplay();
            if(view instanceof ListGrid<?> dg) {
                dg.message(m);
            } else if(view instanceof View v) {
                v.message(m);
            }
            return;
        }
        if(grnEditor == null) {
            grnEditor = new GRNEditor();
        }
        grnEditor.viewObject(grn);
        grnEditor.setCaption(grn.getReference());
        if(!includeSource) {
            return;
        }
        Application a = Application.get();
        grn.listMasters(StoredObject.class, true)
                .forEach(m -> a.view(grn.getReference() + " - " + makeLabel(m), m));
    }

    private static String  makeLabel(StoredObject so) {
        if(so instanceof InventoryTransfer transfer) return transfer.getReference();
        if(so instanceof InventoryPO po) return po.getReference();
        return StringUtility.makeLabel(so.getClass());
    }

    public void viewGRN(HasInventoryItem hasItem) {
        viewGRN(hasItem, false);
    }

    public void viewGRN(HasInventoryItem hasItem, boolean includeSource) {
        viewGRN(hasItem == null ? null : hasItem.getItem(), includeSource);
    }

    public void viewCost(InventoryItem item) {
        cost(item, true);
    }

    public void viewCost(HasInventoryItem hasItem) {
        viewCost(hasItem == null ? null : hasItem.getItem());
    }

    public void editCost(InventoryItem item) {
        cost(item, false);
    }

    public void editCost(HasInventoryItem hasItem) {
        editCost(hasItem == null ? null : hasItem.getItem());
    }

    private void cost(InventoryItem item, boolean viewMode) {
        if(item == null) {
            return;
        }
        new EditCost(item, viewMode).execute();
    }

    private static class FitmentLocations extends ObjectGrid<InventoryFitmentPosition> implements CloseableView {

        private final InventoryItem item;

        public FitmentLocations(InventoryItem item) {
            super(InventoryFitmentPosition.class);
            this.item = item;
            setCaption("Fitment Locations");
            load(item.listImmediateFitmentPositions());
            execute();
        }

        @Override
        public boolean includeColumn(String columnName) {
            return !"Item".equals(columnName);
        }

        @Override
        public Component createHeader() {
            return new ButtonLayout(new ELabel("Fitment Locations under ").append(item.toDisplay()).update(),
                    new Button("Exit", e -> close()).asSmall());
        }
    }

    private static class SplitQuantity extends DataForm implements Transactional {

        private final InventoryItem item;
        private final QuantityField splitQuantityField;
        private final Quantity quantity;

        public SplitQuantity(InventoryItem item) {
            super("Split Quantity");
            addField(new ELabelField("Item", item.toDisplay(), Application.COLOR_SUCCESS));
            this.item = item;
            quantity = item.getQuantity();
            QuantityField quantityField = new QuantityField("Current Quantity");
            quantityField.setValue(quantity);
            splitQuantityField = new QuantityField("Quantity to Split");
            splitQuantityField.setValue(quantity.zero());
            addField(quantityField, splitQuantityField);
            setFieldReadOnly(quantityField);
            setRequired(splitQuantityField);
            setFirstFocus(splitQuantityField);
        }

        @Override
        protected boolean process() {
            clearAlerts();
            Quantity q = splitQuantityField.getValue();
            try {
                if(!quantity.canConvert(q)) {
                    return false;
                }
            } catch(Invalid_State e) {
                warning(e);
                return false;
            }
            if(q.isGreaterThanOrEqual(quantity)) {
                warning("Invalid quantity specified");
                return false;
            }
            close();
            String reference = "Split";
            InventoryTransaction it = new InventoryTransaction(getTransactionManager(), DateUtility.today(), reference);
            it.splitQuantity(item, q, reference);
            if(transact(it::save)) {
                message("Quantity split successfully");
            }
            return true;
        }
    }
}
