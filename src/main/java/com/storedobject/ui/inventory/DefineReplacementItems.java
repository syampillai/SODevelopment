package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.HTMLText;
import com.storedobject.ui.ObjectListGrid;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;

import java.util.List;
import java.util.Objects;

/**
 * Receive material from another location.
 *
 * @author Syam
 */
public class DefineReplacementItems extends DataForm {

    private static final String[] TYPE_4_STORES = new String[] {
            "Repair/Maintenance Organizations",
            "Lease/Loan/Rental Out Locations",
            "Tool Custodians",
    };
    private boolean valuesSet = false;
    private final LocationField sentFromField;
    private final ChoiceField typeFieldForStores = new ChoiceField("Type", TYPE_4_STORES);

    /**
     * Constructor.
     */
    public DefineReplacementItems() {
        this(null, null, -1);
    }

    /**
     * Constructor.
     *
     * @param locationName Name of the location.
     */
    public DefineReplacementItems(String locationName) {
        this(ParameterParser.location(locationName, true, 0),
                ParameterParser.number(locationName));
    }

    /**
     * Constructor.
     *
     * @param fromStore Location fromStore.
     */
    public DefineReplacementItems(InventoryLocation fromStore) {
        this(fromStore, -1);
    }

    /**
     * Constructor.
     *
     * @param fromStore Location fromStore.
     * @param type Type of receipt.
     */
    public DefineReplacementItems(InventoryLocation fromStore, int type) {
        this(null, fromStore, type);
    }

    private DefineReplacementItems(LocationField sentFromField, InventoryLocation fromStore, int type) {
        super("Define Replacement Items / Quantities");
        if(sentFromField == null) {
            if(fromStore == null) {
                this.sentFromField = LocationField.create("Items Sent from", 0);
            } else {
                this.sentFromField = LocationField.create("Items Sent from", fromStore);
                setFieldReadOnly(this.sentFromField);
            }
            this.sentFromField.setValue((Id)null);
        } else {
            this.sentFromField = sentFromField;
            if(fromStore != null) {
                this.sentFromField.setValue(fromStore);
                setFieldReadOnly(this.sentFromField);
            }
        }
        addField(this.sentFromField, typeFieldForStores);
        setRequired(this.sentFromField);
        toChanged(this.sentFromField.getValue());
        this.sentFromField.addValueChangeListener(e -> toChanged(e.getValue()));
        if(type >= 0 && fromStore != null) {
            if(fromStore instanceof InventoryStoreBin) {
                if(type < TYPE_4_STORES.length) {
                    typeFieldForStores.setValue(type);
                    typeFieldForStores.setReadOnly(true);
                    valuesSet = true;
                } else {
                    showTypeError(type);
                }
            } else {
                throw new SORuntimeException("Not a store - " + fromStore.toDisplay());
            }
        }
    }

    private void showTypeError(int type) {
        HTMLText h = new HTMLText("Incorrect type value: " + type);
        h.newLine().append("Please set one of the following values.");
        for(int i = 0; i < TYPE_4_STORES.length; i++) {
            h.newLine().append(i + ": " + TYPE_4_STORES[i]);
        }
        throw new SORuntimeException(h.getHTML());
    }

    private void toChanged(InventoryLocation location) {
        boolean isStore = location instanceof InventoryStoreBin;
        typeFieldForStores.setVisible(isStore);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(valuesSet) {
            process();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        InventoryLocation fromStore = sentFromField.getValue();
        if(fromStore == null) {
            return false;
        }
        if(fromStore instanceof InventoryStoreBin) {
            close();
            switch(typeFieldForStores.getValue()) {
                case 0 -> new ReplacementItems(3, (InventoryStoreBin) fromStore).execute();
                case 1 -> new ReplacementItems(8, (InventoryStoreBin) fromStore).execute();
                case 2 -> new ReplacementItems(18, (InventoryStoreBin) fromStore).execute();
            }
            return true;
        } else {
            warning("Not a store - " + fromStore.toDisplay());
            return false;
        }
    }

    private static class ReplacementItems extends HandleReturnedItems {

        ReplacementItems(int type, InventoryStoreBin storeBin) {
            super(caption(type), type, storeBin, null, false);
        }

        private static String caption(int type) {
            String caption = switch(type) {
                case 3 -> "Replace Repaired Items";
                case 8 -> "Replace Loan/Rent/Lease Items";
                case 18 -> "Replace Tools Issued";
                default -> null;
            };
            if(caption == null) {
                throw new SORuntimeException("Invalid type: " + InventoryLocation.getTypeValue(type));
            }
            return caption;
        }

        @Override
        protected void processOld() {
        }

        @Override
        protected void proceed(List<InventoryItem> items) {
            StoredObject.list(MaterialReturned.class,
                    "FromLocation=" + eo.getId() + " AND Status<2", true).forEach(r ->
                    r.listLinks(MaterialReturnedItem.class, true)
                            .map(InventoryTransferItem::getItem)
                            .filter(Objects::nonNull)
                            .forEach(items::remove));
            new ReplacementGrid(items, eo).execute();
        }
    }

    private static class ReplacementGrid extends ObjectListGrid<InventoryItem> implements CloseableView {

        private final InventoryLocation eo;

        public ReplacementGrid(List<InventoryItem> items, InventoryLocation eo) {
            super(InventoryItem.class, StringList.create("PartNumber", "SerialNumberDisplay AS Serial Number", "Quantity", "Location"), true);
            this.eo = eo;
            load(items);
            ItemContextMenu<InventoryItem> m = new ItemContextMenu<>(this);
            m.addItem("Replace Item - ", e -> e.getItem().ifPresent(this::replace));
            m.addItem("Mark as Consumed - ", e -> e.getItem().ifPresent(this::markAsConsumed));
            var splitQuantity = m.addItem("Split Quantity - ", e -> e.getItem().ifPresent(this::splitQuantity));
            m.setDynamicContentHandler(i -> {
                splitQuantity.setVisible(!i.isSerialized());
                return true;
            });
        }

        @Override
        public Component createHeader() {
            ButtonLayout b = new ButtonLayout();
            b.add(new Button("Exit", e -> close()));
            return b;
        }

        @Override
        public void createHeaders() {
            prependHeader().join().setComponent(new ButtonLayout(
                    new ELabel("Set Replacement Items for - " + eo.getName(), Application.COLOR_SUCCESS),
                    new ELabel(" | ", Application.COLOR_INFO),
                    new ELabel("Right-click on the respective row to see options", Application.COLOR_SUCCESS)
            ));
        }

        private void replace(InventoryItem item) {
        }

        private void markAsConsumed(InventoryItem item) {
        }

        private void splitQuantity(InventoryItem item) {
        }
    }
}
