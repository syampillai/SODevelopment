package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveAndBin extends ListGrid<InventoryItem> implements Transactional {

    private final TransactionManager.Transact update;
    private final Runnable refresher;
    private final Map<Id, InventoryBin> bins = new HashMap<>();
    @SuppressWarnings("rawtypes")
    private ObjectEditor itemEditor;
    private InventoryLocation previousLocation;
    private final BinEditor binner = new BinEditor();
    protected final Button process = new ConfirmButton("Accept Bin Changes", VaadinIcon.STOCK, e -> process());
    protected final Button exit = new Button("Exit", e -> close());
    protected ButtonLayout buttonLayout = new ButtonLayout();
    private final DateField dateField = new DateField();
    private final Date date;
    private final TextField referenceField = new TextField();

    public ReceiveAndBin(Date date, String reference, List<InventoryItem> itemList) {
        this(date, reference, itemList, null, null);
    }

    public ReceiveAndBin(Date date, String reference, List<InventoryItem> itemList, TransactionManager.Transact update) {
        this(date, reference, itemList, update, null);
    }

    public ReceiveAndBin(Date date, String reference, List<InventoryItem> itemList, TransactionManager.Transact update,
                         Runnable refresher) {
        this(date, reference, itemList, update, refresher, null);
    }

    public ReceiveAndBin(Date date, String reference, List<InventoryItem> itemList, TransactionManager.Transact update,
                         Runnable refresher, GRNEditor grnEditor) {
        super(InventoryItem.class, filtered(itemList),
                StringList.create("PartNumber.Name AS Item", "PartNumber.PartNumber AS Part Number",
                        "SerialNumberDisplay as Serial/Batch", "Quantity", "InTransit", "Location"));
        this.update = update;
        this.refresher = refresher;
        buttonLayout.add(new ELabel("Date"), dateField, new ELabel("Reference"), referenceField, process);
        if(grnEditor != null && grnEditor.getObject() != null) {
            buttonLayout.add(new Button("GRN", VaadinIcon.FILE_TABLE, e -> {
                grnEditor.abort();
                grnEditor.execute(getView());
            }));
        }
        buttonLayout.add(exit);
        addConstructedListener((g) -> con());
        setCaption("Receive Items");
        this.date = date;
        dateField.setValue(date);
        referenceField.setValue(reference);
        process.setVisible(update != null || refresher != null);
        if(process.isVisible()) {
            process.setText("Receive");
        }
    }

    public ReceiveAndBin(List<InventoryItem> itemList) {
        this(DateUtility.today(), "", itemList, null, null);
        setCaption("Inspect & Bin");
    }

    private static List<InventoryItem> filtered(List<InventoryItem> items) {
        items.removeIf(i -> {
            if(i.getQuantity().isZero()) {
                return true;
            }
            return switch(i.getLocation().getType()) {
                case 0, 3, 4, 5, 9, 10, 11 -> false;
                default -> true;
            };
        });
        return items;
    }

    @Override
    public int getRelativeColumnWidth(String columnName) {
        return switch(columnName) {
            case "PartNumber" -> 4;
            case "Location" -> 3;
            case "InTransit" -> 1;
            default -> 2;
        };
    }

    @Override
    public Component createHeader() {
        return buttonLayout;
    }

    private void con() {
        addComponentColumn(this::actionButton).setFlexGrow(0).setWidth("200px");
    }

    @Override
    public void execute(View lock) {
        if(isEmpty()) {
            if(update == null && refresher == null) {
                warning("No items to inspect/bin");
            } else {
                process();
            }
            return;
        }
        super.execute(lock);
    }

    private ButtonLayout actionButton(InventoryItem item) {
        ButtonLayout b = new ButtonLayout(new Button("Inspect", VaadinIcon.STOCK, e -> inspect(item)).asSmall());
        if(item.getLocation() instanceof InventoryBin) {
            b.add(new Button("Bin", VaadinIcon.STORAGE, e -> binner.binItem(item)).asSmall());
        }
        return b;
    }

    public String getLocation(InventoryItem item) {
        StringBuilder s = new StringBuilder(item.getLocationDisplay());
        InventoryLocation newLoc = bins.get(item.getId());
        if(newLoc != null && !newLoc.getId().equals(item.getLocationId())) {
            s.append(" -> ");
            s.append(newLoc.toDisplay());
        }
        return s.toString();
    }

    private void process() {
        process(true);
    }

    private void process(boolean check) {
        clearAlerts();
        Date d = dateField.getValue();
        if(d.before(date)) {
            warning("Date shouldn't be less than " + DateUtility.formatDate(date));
            return;
        }
        String reference = referenceField.getValue();
        if(reference.isEmpty()) {
            warning("Reference can't be empty");
            return;
        }
        clearAlerts();
        List<InventoryItem> list = stream().filter(InventoryItem::getInTransit).toList();
        if(check) {
            if(!list.isEmpty()) {
                new ActionGrid<>(InventoryItem.class, list, "The following items are not inspected!",
                        () -> process(false)) {
                    @Override
                    public boolean includeColumn(String columnName) {
                        return !"InTransit".equals(columnName);
                    }
                }.execute();
                return;
            }
        } else {
            for(InventoryItem ii: list) {
                previousLocation = ii.getLocation();
                ii.setInTransit(false);
                if(!transact(ii::save) || !saveItem2(ii)) {
                    return;
                }
            }
        }
        if(transact(t -> {
            InventoryTransaction it = new InventoryTransaction(getTransactionManager(), d, reference);
            boolean any = false;
            InventoryLocation newBin;
            for(InventoryItem item: this) {
                newBin = bins.get(item.getId());
                if(newBin != null && !newBin.getId().equals(item.getLocationId())) {
                    any = true;
                    it.moveTo(item, reference, newBin);
                }
            }
            if(any) {
                it.save(t);
            }
            if(update != null) {
                update.transact(t);
            }
        })) {
            close();
            if(refresher != null) {
                refresher.run();
            }
            message("Done");
        }
    }

    private void inspect(InventoryItem item) {
        clearAlerts();
        if(itemEditor != null && itemEditor.getObjectClass() != item.getClass()) {
            itemEditor = null;
        }
        if(itemEditor == null) {
            itemEditor = ObjectEditor.create(item.getClass());
            itemEditor.setCaption("Inspect Item");
            itemEditor.setFieldHidden("Location");
            itemEditor.setFieldReadOnly("Quantity", "Cost", "Location", "PartNumber", "SerialNumber");
            //noinspection unchecked
            itemEditor.setSaver(e -> saveItem(item));
        }
        previousLocation = item.getLocation();
        item.setInTransit(false);
        //noinspection unchecked
        itemEditor.editObject(item, getView());
    }

    private boolean saveItem(InventoryItem item) {
        if(!transact(t -> itemEditor.save(t))) {
            return false;
        }
        return saveItem2(item);
    }

    private boolean saveItem2(InventoryItem item) {
        if(item.isSerialized()) {
            refresh(item);
        } else {
            removeIf(i -> {
                if(i.getId().equals(item.getId()) ||
                        !i.getPartNumberId().equals(item.getPartNumberId()) ||
                        !i.getLocationId().equals(item.getLocationId()) ||
                        !i.getOwnerId().equals(item.getOwnerId())) {
                    return false;
                }
                return StoredObject.get(InventoryItem.class, i.getId(), true).getQuantity().isZero();
            });
            refresh();
        }
        InventoryLocation bin = bins.get(item.getId());
        if(bin != null && !bin.canBin(item)) {
            warning("This item can't be stored at location '" + bin.toDisplay() +
                    "'. Please choose a suitable location.");
        }
        if(!item.getLocationId().equals(previousLocation.getId())) {
            warning("Due to change in serviceability status, item is removed from '" + previousLocation.toDisplay()
                    + "'. Please move it to suitable location.");
        }
        return true;
    }

    private class BinEditor extends DataForm {

        private InventoryItem item;
        private final ELabelField itemField = new ELabelField("Item");
        private final ELabelField currentLocField = new ELabelField("Current Location");
        private final BinField binField = new BinField("Bin");

        private BinEditor() {
            super("Bin");
            addField(itemField, currentLocField, binField);
            binField.addValueChangeListener(e -> invalidBin(e.getValue()));
        }

        public void binItem(InventoryItem item) {
            clearAlerts();
            this.item = item;
            itemField.clearContent().append(item.toDisplay()).update();
            InventoryLocation loc = item.getLocation();
            currentLocField.clearContent().append(loc.toDisplay());
            InventoryBin bin = bins.get(item.getId());
            if(loc instanceof InventoryBin) {
                InventoryStore store = ((InventoryBin) loc).getStore();
                binField.setStore(store);
                if(bin == null) {
                    bin = store.findBin(item);
                    if(bin == null) {
                        message("Unable to suggest a suitable storage location from prior experience!");
                    }
                }
                setFieldEditable(binField);
            } else {
                setFieldReadOnly(binField);
                currentLocField.append(" (Change not allowed)", Application.COLOR_ERROR);
            }
            currentLocField.update();
            binField.setValue(bin);
            execute();
        }

        private boolean invalidBin(InventoryBin bin) {
            if(bin != null && (bin instanceof InventoryReservedBin || (item != null && !bin.canBin(item)))) {
                warning("Can't store it there!");
                binField.focus();
                return true;
            }
            return false;
        }

        @Override
        protected void cancel() {
            clearAlerts();
            super.cancel();
        }

        @Override
        protected boolean process() {
            clearAlerts();
            InventoryBin bin = binField.getValue();
            if(invalidBin(bin)) {
                return false;
            }
            if(bin == null) {
                bins.remove(item.getId());
            } else {
                bins.put(item.getId(), bin);
            }
            refresh(item);
            recalculateColumnWidths();
            if(!process.isVisible()) {
                process.setVisible(true);
            }
            return true;
        }
    }
}
