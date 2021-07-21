package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.pdf.*;
import com.storedobject.ui.HTMLText;
import com.storedobject.ui.Transactional;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractAssembly<T extends InventoryItem, C extends InventoryItem> extends DataTreeGrid<InventoryFitmentPosition> implements Transactional {

    private static final StringList COLUMNS = StringList.create("Name", "Position", "PartNumber", "SerialNumber", "Quantity", "Accessory", "Optional");
    private final SelectRootItem selectRootItem;
    private final FitItem fitItem;
    private final RemoveItem removeItem;
    final Button fit, remove;
    private final Button selectAnother;
    private InventoryItem rootItem;
    private InventoryFitmentPosition root;
    private final Map<Id, ArrayList<InventoryFitmentPosition>> assemblies = new HashMap<>();
    final LocationField locationField;
    Date date = DateUtility.today();
    String reference = "";

    AbstractAssembly(InventoryLocation location, T item, Class<T> itemClass, Class<C> componentClass) {
        super(InventoryFitmentPosition.class, COLUMNS);
        locationField = new LocationField("Location");
        if(item == null) {
            selectRootItem = new SelectRootItem(location, itemClass);
        } else {
            selectRootItem = null;
        }
        if(componentClass == null) {
            //noinspection unchecked
            componentClass = (Class<C>) InventoryItem.class;
        }
        fitItem = createFitItem(componentClass);
        removeItem = createRemoveItem();
        fit = new Button("Fit", "add", this);
        remove = new Button("Remove", this);
        if(item == null) {
            selectAnother = new Button("Select Another", VaadinIcon.SPLIT, e -> selectRootItem.execute());
        } else {
            selectAnother = null;
        }
        setDataProvider(new TreeData());
        setHeightFull();
        if(item != null) {
            setItem(item);
        }
    }

    public final HTMLGenerator getName(InventoryFitmentPosition fitmentPosition) {
        HTMLText s = new HTMLText();
        if(fitmentPosition == root) {
            s.append(rootItem.getPartNumber().getName() + " [" + rootItem.getLocation().getName() + "]");
            return s;
        }
        InventoryItem item = fitmentPosition.getFittedItem();
        InventoryAssembly a = fitmentPosition.getAssembly();
        InventoryItemType itemType = item == null ? fitmentPosition.getAssembly().getItemType() : item.getPartNumber();
        if(item == null || item.getQuantity().isZero()) {
            s.append(itemType.getName() + (itemType.isSerialized() ? "" : (" (Required: " + a.getQuantity() + ")")), "var(--lumo-error-color)");
        } else {
            if(item.getQuantity().isLessThan(a.getQuantity())) {
                s.append(itemType.getName() + " (Required: " +a.getQuantity() + ")", "var(--lumo-primary-color)");
            } else {
                s.append(itemType.getName());
            }
        }
        return s;
    }

    @Override
    public int getRelativeColumnWidth(String columnName) {
        if("Name".equals(columnName)) {
            return 4;
        }
        return super.getRelativeColumnWidth(columnName);
    }

    public final String getPosition(InventoryFitmentPosition fitmentPosition) {
        return fitmentPosition == root ? "" : fitmentPosition.getPosition();
    }

    public final String getPartNumber(InventoryFitmentPosition fitmentPosition) {
        InventoryItem item = fitmentPosition == root ? rootItem : fitmentPosition.getFittedItem();
        return (item == null ? fitmentPosition.getAssembly().getItemType() : item.getPartNumber()).getPartNumber();
    }

    public final String getSerialNumber(InventoryFitmentPosition fitmentPosition) {
        InventoryItem item = fitmentPosition == root ? rootItem : fitmentPosition.getFittedItem();
        return item == null ? "" : item.getSerialNumber();
    }

    public final Quantity getQuantity(InventoryFitmentPosition fitmentPosition) {
        if(fitmentPosition == root) {
            return Count.ONE;
        }
        InventoryItem item = fitmentPosition.getFittedItem();
        return item == null ? fitmentPosition.getAssembly().getItemType().getUnitOfMeasurement() : item.getQuantity();
    }

    public final String getOptional(InventoryFitmentPosition fitmentPosition) {
        return fitmentPosition != root && fitmentPosition.getAssembly().getOptional() ? "Yes" : "";
    }

    public final String getAccessory(InventoryFitmentPosition fitmentPosition) {
        return fitmentPosition != root && fitmentPosition.getAssembly().getAccessory() ? "Yes" : "";
    }

    @Override
    public Component createHeader() {
        ButtonLayout buttonLayout = new ButtonLayout(fit, remove);
        if(selectAnother != null) {
            buttonLayout.add(selectAnother);
        }
        buttonLayout.add(new Button("Print", e -> new FitList()), new Button("Exit", e -> close()));
        return buttonLayout;
    }

    @Override
    public void execute() {
        super.execute();
        if(rootItem == null && selectRootItem != null) {
            selectRootItem.execute();
        }
    }

    public void setItem(T item) {
        if(item != null && item.equals(this.rootItem)) {
            return;
        }
        if(item != null) {
            if(!item.isSerialized() || !canSet(item)) {
                item = null;
            }
        }
        if(item == null) {
            rootItem = null;
            root = null;
            clear();
            refresh();
            if(selectRootItem == null) {
                close();
            } else {
                selectRootItem.execute();
            }
            return;
        }
        InventoryLocation location = item.getLocation();
        clear();
        this.rootItem = item;
        if(location instanceof InventoryFitmentPosition) {
            root = (InventoryFitmentPosition) location;
        } else {
            root = new InventoryFitmentPosition();
            root.makeVirtual();
        }
        refresh();
    }

    boolean canSet(InventoryItem item) {
        return true;
    }

    private void clear() {
        assemblies.clear();
    }

    private static final  ArrayList<InventoryFitmentPosition> EMPTY = new ArrayList<>();

    private ArrayList<InventoryFitmentPosition> subassemblies(InventoryFitmentPosition fitmentPosition) {
        InventoryItem item = fitmentPosition == root ? rootItem : fitmentPosition.getFittedItem();
        if(item == null) {
            return EMPTY;
        }
        Id itemId = item.getId();
        ArrayList<InventoryFitmentPosition> sa = assemblies.get(fitmentPosition.getId());
        if(sa == null) {
            sa = new ArrayList<>();
            List<InventoryAssembly> subassemblies;
            if(fitmentPosition == root) {
                subassemblies = rootItem.getPartNumber().listImmediateAssemblies().toList();
            } else if(fitmentPosition.getAssembly().getItemType().isSerialized()) {
                subassemblies = fitmentPosition.getAssembly().listImmediateAssemblies().toList();
            } else {
                subassemblies = new ArrayList<>();
            }
            assemblies.put(fitmentPosition.getId(), sa);
            if(!subassemblies.isEmpty()) {
                InventoryFitmentPosition position;
                Transaction t = null;
                for(InventoryAssembly a: subassemblies) {
                    position = InventoryFitmentPosition.get(item, a);
                    if(position == null) {
                        position = new InventoryFitmentPosition();
                        position.setItem(itemId);
                        position.setAssembly(a);
                        if(t == null) {
                            t = createTran();
                        }
                        save(position, t);
                    }
                    sa.add(position);
                }
                if(t != null) {
                    commit(t);
                }
            }
        }
        return sa;
    }

    private Transaction createTran() {
        try {
            return getTransactionManager().createTransaction();
        } catch(Exception e) {
            throw new SORuntimeException("Unable to create transaction", e);
        }
    }

    private void save(StoredObject so, Transaction t) {
        try {
            so.save(t);
        } catch(Exception e) {
            throw new SORuntimeException("Error saving " + so, e);
        }
    }

    private void commit(Transaction t) {
        if(t != null) {
            try {
                t.commit();
            } catch(Exception e) {
                t.rollback();
                throw new SORuntimeException("Error committing transaction", e);
            }
        }
    }

    @Override
    public void clicked(Component c) {
        if(root == null) {
            warning("No item was set!");
            return;
        }
        InventoryFitmentPosition selected = getSelected();
        if(selected == null) {
            warning("Nothing selected!");
            return;
        }
        if(selected == root) {
            warning("Please select a sub-assembly");
            return;
        }
        InventoryAssembly ia = selected.getAssembly();
        InventoryItem item = selected.getFittedItem();
        if(c == fit) {
            if(item != null) {
                if(item.isSerialized() || item.getQuantity().equals(ia.getQuantity())) {
                    warning("This position is already occupied");
                    return;
                }
            }
            fitItem.setAssemblyPosition(selected, item == null ? null : item.getQuantity());
            fitItem.execute(getView());
            return;
        }
        if(c == remove) {
            if(item == null || item.getQuantity().isZero()) {
                warning("Position is vacant, nothing can be removed");
                return;
            }
            removeItem.setAssemblyPosition(selected);
            removeItem.execute(getView());
            return;
        }
        super.clicked(c);
    }

    private class TreeData extends AbstractHierarchicalDataProvider<InventoryFitmentPosition, String> {

        @Override
        public Object getId(InventoryFitmentPosition item) {
            return item.getId();
        }

        @Override
        public int getChildCount(HierarchicalQuery<InventoryFitmentPosition, String> query) {
            InventoryFitmentPosition fitmentPosition = query.getParent();
            if(fitmentPosition == null) {
                return root == null ? 0 : 1;
            }
            return AbstractObjectForestSupplier.subListSize(subassemblies(fitmentPosition), query);
        }

        @Override
        public Stream<InventoryFitmentPosition> fetchChildren(HierarchicalQuery<InventoryFitmentPosition, String> query) {
            InventoryFitmentPosition fitmentPosition = query.getParent();
            if(fitmentPosition == null) {
                return Stream.ofNullable(root);
            }
            return AbstractObjectForestSupplier.subList(subassemblies(fitmentPosition), query).stream();
        }

        @Override
        public boolean hasChildren(InventoryFitmentPosition fitmentPosition) {
            if(fitmentPosition == null) {
                return root != null;
            }
            return !subassemblies(fitmentPosition).isEmpty();
        }

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public void refreshAll() {
            clear();
            super.refreshAll();
        }
    }

    private class SelectRootItem extends DataForm {

        private final ItemInput<T> itemField;

        public SelectRootItem(InventoryLocation location, Class<T> itemClass) {
            super("Select");
            itemField = ItemInput.create(StringUtility.makeLabel(itemClass), itemClass, true);
            if(location != null) {
                locationField.setObject(location);
                locationField.setReadOnly(true);
            }
            if(location instanceof InventoryBin) {
                itemField.setStore(((InventoryBin) location).getStore());
            } else {
                itemField.setStore(() -> {
                    InventoryLocation loc = locationField.getValue();
                    return loc instanceof InventoryBin ? ((InventoryBin) loc).getStore() : null;
                });
            }
            addField(locationField, (HasValue<?, ?>) itemField);
            setRequired(locationField);
            setRequired((HasValue<?, ?>)itemField);
        }

        @Override
        protected boolean process() {
            InventoryItem item = itemField.getValue();
            if(!item.isSerialized()) {
                warning("Not an assembly: " + item.toDisplay());
                return false;
            }
            setItem(itemField.getObject());
            return true;
        }

        @Override
        protected void cancel() {
            super.cancel();
            if(rootItem == null) {
                AbstractAssembly.this.close();
            }
        }
    }

    abstract FitItem createFitItem(Class<C> itemClass);

    abstract RemoveItem createRemoveItem();

    interface FitItem {
        void setAssemblyPosition(InventoryFitmentPosition fitmentPosition, Quantity quantityAlreadyFitted);
        void execute(View lock);
    }

    interface RemoveItem {
        void setAssemblyPosition(InventoryFitmentPosition fitmentPosition);
        void execute(View lock);
    }

    private class FitList extends PDFReport {

        private PDFTable table;

        public FitList() {
            super((com.storedobject.ui.Application)Application.get());
            if(rootItem == null) {
                return;
            }
            setTitleText(
                    new Text("Fit List", 16, PDFFont.BOLD).
                    newLine().
                    append(rootItem.toDisplay(), 14, PDFFont.BOLD)
            );
            execute();
        }

        @Override
        public void generateContent() throws Exception {
            table = createTable(3, 3, 3, 3, 3, 3, 3, 3, 60, 20, 40, 40, 20, 20, 20);
            boolean first = true;
            PDFCell cell;
            for(String h: COLUMNS) {
                cell = createCenteredCell(createTitleText(StringUtility.makeLabel(h)));
                if(first) {
                    first = false;
                    cell.setColumnSpan(9);
                }
                table.addCell(cell);
            }
            table.setHeaderRows(1);
            print(0, root);
            add(table);
        }

        private void print(int level, InventoryFitmentPosition pos) {
            for(InventoryFitmentPosition p: subassemblies(pos)) {
                print(p, level);
                print(level + 1, p);
            }
        }

        private void print(InventoryFitmentPosition pos, int level) {
            PDFCell cell;
            for(int i = 0; i < Math.min(8, level); i++) {
                cell = createCenteredCell("-", c -> { c.setBorder(0); return c; });
                table.addCell(cell);
            }
            StringBuilder name = new StringBuilder(name(pos));
            while(level > 8) {
                name.insert(0, "- ");
                --level;
            }
            cell = createCell(name);
            cell.setColumnSpan(9 - level);
            table.addCell(cell);
            cCell(getPosition(pos));
            tCell(getPartNumber(pos));
            InventoryItem item = pos.getFittedItem();
            if(item == null) {
                tCell(new Text().append("Missing", PDFColor.RED));
            } else {
                tCell(item.getSerialNumber());
            }
            tCell(item == null ? pos.getAssembly().getItemType().getUnitOfMeasurement() : item.getQuantity());
            cCell(getAccessory(pos));
            cCell(getOptional(pos));
        }

        private String name(InventoryFitmentPosition fitmentPosition) {
            StringBuilder s = new StringBuilder();
            InventoryItem item = fitmentPosition.getFittedItem();
            InventoryAssembly a = fitmentPosition.getAssembly();
            InventoryItemType itemType = item == null ? fitmentPosition.getAssembly().getItemType() : item.getPartNumber();
            if(item == null || item.getQuantity().isZero()) {
                s.append(itemType.getName()).append(itemType.isSerialized() ? "" : (" (Required: " + a.getQuantity() + ")"));
            } else {
                if(item.getQuantity().isLessThan(a.getQuantity())) {
                    s.append(itemType.getName()).append(" (Required: ").append(a.getQuantity()).append(")");
                } else {
                    s.append(itemType.getName());
                }
            }
            return s.toString();
        }

        private void tCell(Object o) {
            table.addCell(createCell(o));
        }

        private void cCell(Object o) {
            table.addCell(createCenteredCell(o));
        }

        @Override
        public int getPageOrientation() {
            return ORIENTATION_LANDSCAPE;
        }
    }
}
