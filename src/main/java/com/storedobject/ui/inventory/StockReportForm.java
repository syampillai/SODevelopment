package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public abstract class StockReportForm extends DataForm {

    ObjectField<InventoryStore> storeField;
    final ELabel locInfo = new ELabel();
    final List<InventoryLocation> locations = new ArrayList<>();
    BooleanField zerosField, localCurrencyField;
    ObjectField<InventoryItemType> pnField;
    boolean customized = true;

    public StockReportForm(String caption) {
        super(caption);
    }

    @Override
    protected void buildFields() {
        storeField = new ObjectField<>(InventoryStore.class, true);
        storeField.setPlaceholder("All Stores");
        addField(new CompoundField("Store/Locations", storeField,
                new Button("Custom Selection", VaadinIcon.STOCK, e -> customLocations()),
                locInfo));
        buildDateField();
        zerosField = new BooleanField("Print Zero-Quantity Items");
        addField(zerosField);
        localCurrencyField = new BooleanField("Print Cost in Accounting Currency", true);
        addField(localCurrencyField);
        pnField = new ObjectField<>("Part Number", InventoryItemType.class, true);
        pnField.setHelperText("Leave it blank for printing all items");
        addField(pnField);
    }

    protected abstract void buildDateField();

    private void customLocations() {
        setFieldHidden(storeField);
        LocationField lf = LocationField.create(0, 3, 4, 5, 8, 10, 11, 18);
        MultiSelectGrid<InventoryLocation> ms = new MultiSelectGrid<>(InventoryLocation.class, lf.getLocations(),
                s -> {
                    locations.clear();
                    locations.addAll(s);
                    locLabel();
                }) {
            @Override
            protected void cancel() {
                super.cancel();
                locations.clear();
                locInfo.clear();
                locInfo.update();
                storeField.setVisible(true);
            }
        };
        ms.select(locations);
        ms.setCaption("Select Stores/Locations");
        ms.execute(this);
    }

    private void locLabel() {
        locInfo.clear();
        if(locations.isEmpty()) {
            locInfo.append("All stores");
        } else if(locations.size() == 1) {
            locInfo.append(locations.getFirst().toDisplay());
        } else {
            int stores = (int) locations.stream().filter(loc -> loc instanceof InventoryStoreBin).count();
            int locs = (int) locations.stream().filter(loc -> !(loc instanceof InventoryStoreBin)).count();
            if(stores == 0) {
                locInfo.append(locs + " locations selected", Application.COLOR_SUCCESS);
            } else if(locs == 0) {
                locInfo.append(stores + " stores selected", Application.COLOR_SUCCESS);
            } else {
                locInfo.append(stores + " store" + (stores > 1 ? "s" : "") + " and " + locs + " location"
                        + (locs > 1 ? "s" : "") + " selected", Application.COLOR_SUCCESS);
            }
        }
        locInfo.update();
    }

    @Override
    protected boolean process() {
        close();
        InventoryLocation loc = null;
        if(storeField.isVisible()) {
            InventoryStore store = storeField.getObject();
            if(store != null) {
                loc = store.getStoreBin();
            }
            locations.clear();
        } else {
            if(!locations.isEmpty()) {
                loc = locations.removeFirst();
            }
        }
        process(loc);
        return true;
    }

    protected abstract void process(InventoryLocation location);

    public void generateContent() throws Exception {
        customized = false;
    }
}