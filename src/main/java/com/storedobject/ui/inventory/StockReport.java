package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.report.StockReportExcel;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class StockReport extends DataForm {

    private ObjectField<InventoryStore> storeField;
    private final ELabel locInfo = new ELabel();
    private final List<InventoryLocation> locations = new ArrayList<>();
    private DateField dateField;
    private BooleanField zerosField, localCurrencyField;
    private ChoiceField outputField;
    private boolean customized = true;
    private Report pdf;
    private ExReport excel;

    public StockReport(Application a) {
        super(a.getLogicTitle("Stock Report"));
    }

    @Override
    protected void buildFields() {
        storeField = new ObjectField<>(InventoryStore.class, true);
        storeField.setPlaceholder("All Stores");
        addField(new CompoundField("Store/Locations", storeField,
                new Button("Custom Selection", VaadinIcon.STOCK, e -> customLocations()),
                locInfo));
        dateField = new DateField("As of");
        addField(dateField);
        zerosField = new BooleanField("Print Zero-Quantity Items");
        addField(zerosField);
        localCurrencyField = new BooleanField("Print Cost in Accounting Currency", true);
        addField(localCurrencyField);
        outputField = new ChoiceField("Format", new String[] { "PDF", "Excel" });
        addField(outputField);
    }

    private void customLocations() {
        storeField.setVisible(false);
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
            locInfo.append(locations.get(0).toDisplay());
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
                loc = locations.remove(0);
            }
        }
        switch(outputField.getValue()) {
            case 0 -> {
                pdf = new Report(getApplication(), loc, dateField.getValue());
                locations.forEach(pdf::addLocation);
                pdf.execute();
            }
            case 1 -> {
                excel = new ExReport(getApplication(), loc, dateField.getValue());
                locations.forEach(excel::addLocation);
                excel.execute();
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    public void generateContent() throws Exception {
        customized = false;
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers) {
        printStock(partNumbers, null);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading) {
        printStock(partNumbers, categoryHeading, false);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, boolean newPage) {
        printStock(partNumbers, null, newPage);
    }

    public void printStock(ObjectIterator<? extends InventoryItemType> partNumbers, String categoryHeading, boolean newPage) {
        if(pdf != null) {
            pdf.printStock(partNumbers, categoryHeading, newPage);
        } else if(excel != null) {
            excel.printStock(partNumbers, categoryHeading);
        }
    }

    public String getItemTypeTitle(InventoryItemType itemType) {
        return null;
    }

    public void configure(com.storedobject.report.StockReport report) {
    }

    public void configure(com.storedobject.report.StockReportExcel report) {
    }

    public boolean canPrint(InventoryItem item) {
        return true;
    }

    public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
        return false;
    }

    private class Report extends com.storedobject.report.StockReport {

        public Report(Device device, InventoryLocation location, Date date) {
            super(device, location, date);
            printZeros(zerosField.getValue());
            printCostInLocalCurrency(localCurrencyField.getValue());
            setCaption(getCaption());
            setItemFilter(StockReport.this::canPrint);
            configure(this);
        }

        @Override
        public void generateContent() throws Exception {
            StockReport.this.generateContent();
            if(customized) {
                return;
            }
            super.generateContent();
        }

        @Override
        public String getItemTypeTitle(InventoryItemType itemType) {
            String title = StockReport.this.getItemTypeTitle(itemType);
            return title == null ? super.getItemTypeTitle(itemType) : title;
        }

        public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
            return StockReport.this.printZeroQuantity(store, itemType);
        }
    }

    private class ExReport extends StockReportExcel {

        public ExReport(Device device, InventoryLocation location, Date date) {
            super(device, location, date);
            printZeros(zerosField.getValue());
            printCostInLocalCurrency(localCurrencyField.getValue());
            setCaption(getCaption());
            setItemFilter(StockReport.this::canPrint);
            configure(this);
        }

        @Override
        public void generateContent() throws Exception {
            StockReport.this.generateContent();
            if(customized) {
                return;
            }
            super.generateContent();
        }

        @Override
        public String getItemTypeTitle(InventoryItemType itemType) {
            String title = StockReport.this.getItemTypeTitle(itemType);
            return title == null ? super.getItemTypeTitle(itemType) : title;
        }

        public boolean printZeroQuantity(InventoryStore store, InventoryItemType itemType) {
            return StockReport.this.printZeroQuantity(store, itemType);
        }
    }
}