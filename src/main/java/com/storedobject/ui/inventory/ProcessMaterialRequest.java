package com.storedobject.ui.inventory;

import com.storedobject.common.Executable;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.HTMLText;
import com.storedobject.ui.QuantityField;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.selection.MultiSelectionEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ProcessMaterialRequest extends AbstractRequestMaterial {

    private final IssueTreeGrid issueTreeGrid = new IssueTreeGrid();

    public ProcessMaterialRequest() {
        super(new PromptStore());
    }

    public ProcessMaterialRequest(String store) {
        super(true, store, NO_ACTIONS);
    }

    public ProcessMaterialRequest(InventoryLocation store) {
        super(true, store, NO_ACTIONS);
    }

    @Override
    public String getCaption() {
        return "Process Material Requests";
    }

    @Override
    public final void constructed() {
        super.constructed();
        setExtraFilter("Status IN (1,2)");
    }

    @Override
    String getFixedSide() {
        return "To";
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setExtraFilter(e.getValue() ? null : "Status IN (1,2)"));
        buttonPanel.add(new Button("Process", e -> processRequest()), h);
    }

    private void processRequest() {
        MaterialRequest mr = selected();
        if(mr == null) {
            return;
        }
        mr.reload();
        if(mr.getStatus() > 2) {
            warning("Can not process, status is '" + mr.getStatusValue() + "'");
            refresh(mr);
            return;
        }
        issueTreeGrid.setMaterialRequest(mr);
        issueTreeGrid.execute(getView());
    }

    private class IssueTreeGrid extends DataTreeGrid<Object> {

        private MaterialRequest mr;
        private MaterialIssued mi;
        private final ELabel mrDetails = new ELabel();
        private final List<MaterialRequestItem> mriList = new ArrayList<>();
        private final Map<Id, List<MaterialIssuedItem>> miiMap = new HashMap<>(); // For each MRI
        private final Map<Id, Quantity> readyToIssueMap = new HashMap<>(); // For each MRI
        private final List<MaterialIssuedItem> quantityEdited = new ArrayList<>();
        private final Set<Object> selectedSet = new HashSet<>();
        private int selectionValue;
        private final FillItems fillItems = new FillItems();
        private final RemoveEntries removeEntries = new RemoveEntries();
        private final EditQuantity editQuantity = new EditQuantity();
        private final ButtonLayout buttonLayout = new ButtonLayout();
        private final Button removeButton = new Button("Remove Entries", e -> removeEntries.execute()) {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                uninitialized = !visible;
            }
        };
        Button processButton = new ConfirmButton("Issue Items", VaadinIcon.TRUCK, e -> issue());
        private final Button saveButton = new Button("Save Changes", e -> save()) {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);
                processButton.setVisible(!visible && !uninitialized);
            }
        };
        private boolean uninitialized;

        IssueTreeGrid() {
            super(Object.class,
                    StringList.create("PartNumber", "SerialNumber AS Serial/Batch", "Requested", "ReadyToIssue", "Shortfall", "ItemBin as Item/Bin"));
            createHTMLHierarchyColumn("PartNumber", this::getPartNumber);
            createHTMLColumn("Shortfall", this::getShortfall);
            createHTMLColumn("ItemBin", this::getItemBin);
            setDataProvider(new TreeData());
            setHeightFull();
            Button fillButton = new Button("Fill Items", VaadinIcon.FILL, e -> fillItems.execute());
            buttonLayout.add(fillButton, removeButton, saveButton,
                    new Button("Picking Order", "sort", e -> pickingOrder()),
                    processButton,
                    new Button("Exit", e -> saveAndClose()));
            removeButton.setVisible(false);
            saveButton.setVisible(false);
            processButton.setVisible(false);
            setSelectionMode(SelectionMode.MULTI);
            ((GridMultiSelectionModel<Object>)getSelectionModel()).addMultiSelectionListener(this::selectionChanged);
            setCaption("Process Material Request");
            GridContextMenu<Object> contextMenu = new GridContextMenu<>(this);
            GridMenuItem<Object> addEntry = contextMenu.addItem("Add an Entry", e -> addEntry());
            GridMenuItem<Object> addEntries = contextMenu.addItem("Fill Entries", e -> balanceFill());
            GridMenuItem<Object> removeEntries = contextMenu.addItem("Remove Entries", e -> removeEntries());
            GridMenuItem<Object> removeEntry = contextMenu.addItem("Remove Entry", e -> removeEntries());
            GridMenuItem<Object> qEdit = contextMenu.addItem("Edit Quantity", e -> editQuantity());
            GridMenuItem<Object> qZero = contextMenu.addItem("Set Quantity to Zero", e -> zeroQuantity());
            GridMenuItem<Object> searchEverywhere = contextMenu.addItem("Search Everywhere", e -> {
                Object o = e.getItem().orElse(null);
                if(o instanceof MaterialRequestItem) {
                    LocateItem locateItem = new LocateItem(((MaterialRequestItem) o).getPartNumber());
                    locateItem.setAllowBreaking(true);
                    locateItem.execute();
                }
            });
            contextMenu.setDynamicContentHandler(o -> {
                deselectAll();
                select(o);
                selectedSet.clear();
                selectedSet.add(o);
                selectionValue = 0;
                if(!(o instanceof MaterialIssuedItem)) {
                    if(!(o instanceof MaterialRequestItem)) {
                        return false;
                    }
                    removeEntry.setVisible(false);
                    qEdit.setVisible(false);
                    qZero.setVisible(false);
                    MaterialRequestItem mri = (MaterialRequestItem) o;
                    List<MaterialIssuedItem> miis = items(mri);
                    miis.forEach(this::select);
                    selectedSet.addAll(miis);
                    boolean s = shortfall(mri).isPositive();
                    addEntry.setVisible(s);
                    addEntries.setVisible(s);
                    removeEntries.setVisible(!miis.isEmpty());
                    searchEverywhere.setVisible(s && mri.getPartNumber().isSerialized());
                    return true;
                }
                searchEverywhere.setVisible(false);
                addEntry.setVisible(false);
                addEntries.setVisible(false);
                removeEntries.setVisible(false);
                MaterialIssuedItem mii = (MaterialIssuedItem) o;
                removeEntry.setVisible(true);
                qEdit.setVisible(!mii.getItem().isSerialized());
                qZero.setVisible(mii.getQuantity().isPositive());
                return true;
            });
        }

        private void selectionChanged(MultiSelectionEvent<Grid<Object>, Object> e) {
            if(!e.isFromClient()) {
                return;
            }
            e.getAddedSelection().forEach(o -> {
                if(o instanceof MaterialRequestItem) {
                    items(o).forEach(this::select);
                } else if(o instanceof MaterialIssuedItem) {
                    deselect(mri((MaterialIssuedItem) o));
                }
            });
            e.getRemovedSelection().forEach(o -> {
                if(o instanceof MaterialRequestItem) {
                    items(o).forEach(this::deselect);
                } else if(o instanceof MaterialIssuedItem) {
                    deselect(mri((MaterialIssuedItem) o));
                }
            });
        }

        @Override
        public Component createHeader() {
            return buttonLayout;
        }

        @Override
        public void createFooters() {
            ELabel m = new ELabel(" | ");
            m.append("Right-click on the entry for entry-specific options", "blue").update();
            ButtonLayout b = new ButtonLayout(mrDetails, m);
            mrDetails.getElement().getStyle().set("flex-grow", "1");
            appendFooter().join().setComponent(b);
        }

        @Override
        public ColumnTextAlign getTextAlign(String columnName) {
            if(columnName.equals("Shortfall")) {
                return ColumnTextAlign.END;
            }
            return super.getTextAlign(columnName);
        }

        private void saveAndClose() {
            if(saveButton.isVisible()) {
                new ActionForm("Changes are not saved. Do you really want to exit?", this::close).execute();
            } else {
                close();
            }
        }

        private MaterialRequestItem singleMRI() {
            return selectedSet.stream().filter(o -> o instanceof MaterialRequestItem).
                    map(o -> (MaterialRequestItem)o).findAny().orElse(null);
        }

        private MaterialIssuedItem singleMII() {
            return selectedSet.stream().filter(o -> o instanceof MaterialIssuedItem).
                    map(o -> (MaterialIssuedItem)o).findAny().orElse(null);
        }

        private void zeroQuantity() {
            MaterialIssuedItem mii = singleMII();
            if(mii == null) {
                return;
            }
            setQuantity(mii, mii.getQuantity().zero());
        }

        private void editQuantity() {
            MaterialIssuedItem mii = singleMII();
            if(mii == null) {
                return;
            }
            editQuantity.execute(mii);
        }

        private void addEntry(MaterialRequestItem mri, InventoryItem ii) {
            MaterialIssuedItem mii = new MaterialIssuedItem();
            mii.setItem(ii);
            mii.setRequest(mri);
            Quantity q = shortfall(mri);
            if(ii.getQuantity().isLessThan(q)) {
                mii.setQuantity(ii.getQuantity());
            } else {
                mii.setQuantity(q);
            }
            mii.makeVirtual();
            items(mri).add(mii);
            readyToIssueMap.remove(mri.getId());
            refresh();
            removeButton.setVisible(true);
            saveButton.setVisible(true);
            expand(mri);
            select(mii);
        }

        private void addEntry() {
            MaterialRequestItem mri = singleMRI();
            if(mri == null) {
                return;
            }
            deselectAll();
            AtomicInteger bCount = new AtomicInteger(0);
            List<MaterialIssuedItem> miis = items(mri);
            ObjectIterator<InventoryItem> stock = stockList(mri, bCount);
            if(!miis.isEmpty()) {
                stock = stock.filter(ii -> miis.stream().noneMatch(i -> i.getItemId().equals(ii.getId())));
            }
            if(bCount.get() > 0) {
                message("Blocked items encountered: " + bCount.get());
            }
            new SelectStock(stock, s -> addEntry(mri, s)).execute();
        }

        private void setQuantity(MaterialIssuedItem mii, Quantity q) {
            mii.setQuantity(q);
            quantityEdited.add(mii);
            MaterialRequestItem mri = mri(mii);
            readyToIssueMap.remove(mri.getId());
            refresh(mri);
            refresh(mii);
        }

        private void allFill() {
            readyToIssueMap.clear();
            miiMap.clear();
            quantityEdited.clear();
            balanceFill();
        }

        private ObjectIterator<InventoryItem> stockList(MaterialRequestItem mri, AtomicInteger blockedCounter) {
            InventoryItemType pn = mri.getPartNumber();
            ObjectIterator<InventoryItem> stock;
            InventoryLocation location = mr.getToLocation();
            InventoryStore store = null;
            if(location instanceof InventoryStoreBin) {
                store = ((InventoryStoreBin) location).getStore();
                stock = pn.listStock(store);
            } else {
                stock = pn.listStock(location);
            }
            for(InventoryItemType apn: pn.listAPNs()) {
                if(store == null) {
                    stock = stock.add(apn.listStock(location));
                } else {
                    stock = stock.add(apn.listStock(store));
                }
            }
            return stock.filter(ii -> !(ii.getLocation() instanceof InventoryReservedBin) &&
                    ii.isServiceable() && !blocked(ii, blockedCounter));
        }

        private boolean blocked(InventoryItem ii, AtomicInteger counter) {
            if(ii.isBlocked()) {
                if(counter != null) {
                    counter.incrementAndGet();
                }
                return true;
            }
            return false;
        }

        private void balanceFill() {
            deselectAll();
            AtomicInteger bCount = new AtomicInteger(0);
            int count = 0, replaced = 0;
            Quantity q;
            ObjectIterator<InventoryItem> stock;
            MaterialIssuedItem mii;
            boolean contains;
            for(MaterialRequestItem mri: mriList) {
                if(!selectedSet.isEmpty()) {
                    contains = selectedSet.contains(mri);
                    if((selectionValue == 0 && !contains) || (selectionValue == 1 && contains)) {
                        continue;
                    }
                }
                q = mri.getBalance();
                stock = stockList(mri, bCount);
                for(InventoryItem ii: stock) {
                    if(!q.isPositive()) {
                        break;
                    }
                    mii = items(mri).stream().filter(i -> i.getItemId().equals(ii.getId())).findAny().orElse(null);
                    if(mii != null) {
                        if(quantityEdited.contains(mii)) {
                            q = q.subtract(mii.getQuantity());
                            continue;
                        }
                        if(!selectedSet.isEmpty()) {
                            contains = selectedSet.contains(mii);
                            if(selectionValue == 0) {
                                if(!contains) {
                                    q = q.subtract(mii.getQuantity());
                                    continue;
                                }
                            } else {
                                if(contains) {
                                    q = q.subtract(mii.getQuantity());
                                    continue;
                                }
                            }
                        }
                        if(mii.getQuantity().equals(ii.getQuantity()) || q.equals(mii.getQuantity())) {
                            q = q.subtract(mii.getQuantity());
                            continue;
                        }
                        items(mri).remove(mii);
                        ++replaced;
                    }
                    ++count;
                    mii = new MaterialIssuedItem();
                    mii.setItem(ii);
                    mii.setRequest(mri);
                    if(ii.getQuantity().isLessThan(q)) {
                        mii.setQuantity(ii.getQuantity());
                    } else {
                        mii.setQuantity(q);
                    }
                    mii.makeVirtual();
                    items(mri).add(mii);
                    q = q.subtract(mii.getQuantity());
                    readyToIssueMap.remove(mri.getId());
                }
            }
            StringBuilder m = new StringBuilder("Entries filled: ");
            m.append(count);
            if(replaced > 0) {
                m.append(", Replaced: ").append(replaced);
            }
            if(bCount.get() > 0) {
                m.append(", Blocked items encountered: ").append(bCount.get());
            }
            message(m);
            removeButton.setVisible(count > 0 || miiMap.values().stream().anyMatch(list -> !list.isEmpty()));
            saveButton.setVisible(count > 0 || saveButton.isVisible());
            refresh();
            for(MaterialRequestItem mri: mriList) {
                expand(mri);
            }
        }

        private void removeEntries() {
            deselectAll();
            int pc, count = 0;
            List<MaterialIssuedItem> miis;
            for(MaterialRequestItem mri: mriList) {
                if(selectionValue == 1 && selectedSet.contains(mri)) {
                    continue;
                }
                miis = items(mri);
                pc = miis.size();
                if(miis.removeIf(mii -> {
                    boolean c = selectedSet.contains(mii);
                    if((c && selectionValue == 0) || (!c && selectionValue == 1)) {
                        quantityEdited.remove(mii);
                        return true;
                    }
                    return false;
                })) {
                    readyToIssueMap.remove(mri.getId());
                    count += pc - miis.size();
                }
            }
            message("Entries removed: " + count);
            removeButton.setVisible(miiMap.values().stream().anyMatch(list -> !list.isEmpty()));
            saveButton.setVisible(true);
            refresh();
        }

        private void removeAllEntries() {
            deselectAll();
            readyToIssueMap.clear();
            miiMap.clear();
            quantityEdited.clear();
            removeButton.setVisible(false);
            saveButton.setVisible(true);
            refresh();
        }

        private void save() {
            mr.reload();
            if(mr.getStatus() > 2) {
                statusError("Someone has changed the status of this request to: " + mr.getStatusValue());
                return;
            }
            if(mi == null) {
                mi = StoredObject.get(MaterialIssued.class, "Request=" + this.mr.getId() + " AND Status=0");
                if(mi != null) {
                    statusError();
                    return;
                }
                mi = new MaterialIssued();
                mi.setRequest(mr);
                mi.setLocation(mr.getToLocationId());
            } else if(mi.getStatus() != 0) {
                statusError();
                return;
            }
            if(transact(t -> {
                mi.save(t);
                mi.removeAllLinks(MaterialIssuedItem.class);
                MaterialIssuedItem nmii;
                for(MaterialRequestItem mri: mriList) {
                    for(MaterialIssuedItem mii: items(mri)) {
                        nmii = new MaterialIssuedItem();
                        nmii.setRequest(mri);
                        nmii.setItem(mii.getItemId());
                        nmii.setQuantity(mii.getQuantity());
                        nmii.save(t);
                        mi.addLink(nmii);
                    }
                }
            })) {
                removeButton.setVisible(true);
                saveButton.setVisible(false);
                ProcessMaterialRequest.this.refresh(mr);
            }
        }

        private void statusError() {
            statusError("Someone has processed this request!");
        }

        private void statusError(String error) {
            error(error);
            close();
            ProcessMaterialRequest.this.refresh(mr);
        }

        private void issue() {
            if(mi.listLinks(MaterialIssuedItem.class).stream().noneMatch(mii -> mii.getQuantity().isPositive())) {
                message("Nothing to issue!");
                return;
            }
            if(transact(t -> mi.issue(t))) {
                close();
                mr.reload();
                ProcessMaterialRequest.this.refresh(mr);
            }
        }

        private void pickingOrder() {
            mriList.sort(new POrder());
            refresh();
        }

        private class POrder implements Comparator<MaterialRequestItem> {

            @Override
            public int compare(MaterialRequestItem o1, MaterialRequestItem o2) {
                List<MaterialIssuedItem> l1 = miiMap.get(o1.getId()), l2 = miiMap.get(o2.getId());
                if(l1.isEmpty() && l2.isEmpty()) {
                    return 0;
                }
                if(l1.isEmpty()) {
                    return 1;
                }
                if(l2.isEmpty()) {
                    return -1;
                }
                if(l1.stream().allMatch(mii -> mii.getItem().getLocation() instanceof InventoryStoreBin)) {
                    return 1;
                }
                if(l2.stream().allMatch(mii -> mii.getItem().getLocation() instanceof InventoryStoreBin)) {
                    return -1;
                }
                return min(l1) - min(l2);
            }
        }

        private int min(List<MaterialIssuedItem> list) {
            return list.stream().filter(mii -> !(mii.getItem().getLocation() instanceof InventoryStoreBin)).
                    filter(mii -> mii.getItem().getLocation() instanceof InventoryBin).
                    mapToInt(mii -> ((InventoryBin)mii.getItem().getLocation()).getPickingOrder()).
                    min().orElse(0);
        }

        public String getItemBin(Object o) {
            HTMLText h = new HTMLText();
            if(o instanceof MaterialRequestItem) {
                h.append("Item: ");
                MaterialRequestItem mri = (MaterialRequestItem) o;
                String pn = mri.getPartNumber().getName();
                if(uninitialized) {
                    h.append(pn);
                } else {
                    h.append(pn, readyToIssue(mri).isLessThan(mri.getBalance()) ? "red" : "blue");
                }
            } else if(o instanceof MaterialIssuedItem) {
                h.append(((MaterialIssuedItem) o).getItem().getLocationDisplay());
            }
            return h.getHTML();
        }

        public String getPartNumber(Object o) {
            MaterialRequestItem mri;
            HTMLText h = new HTMLText();
            if(o instanceof MaterialRequestItem) {
                mri = (MaterialRequestItem) o;
                String pn = mri.getPartNumber().getPartNumber();
                if(uninitialized) {
                    h.append(pn);
                } else {
                    h.append(pn, readyToIssue(mri).isLessThan(mri.getBalance()) ? "red" : "blue");
                }
            } else if(o instanceof MaterialIssuedItem) {
                MaterialIssuedItem mii = (MaterialIssuedItem) o;
                if(!mii.getItem().getPartNumberId().equals(mii.getRequest().getPartNumberId())) {
                    h.append("APN: ", "red");
                }
                String pn = mii.getItem().getPartNumber().getPartNumber();
                if(mii.getItem().getQuantity().isGreaterThan(mii.getQuantity())) {
                    h.append(pn);
                } else {
                    h.append(pn, "blue");
                }
            }
            return h.getHTML();
        }

        @SuppressWarnings("unused")
        public String getSerialNumber(Object o) {
            if(o instanceof MaterialIssuedItem) {
                return ((MaterialIssuedItem) o).getItem().getSerialNumber();
            }
            return "";
        }

        public Quantity getRequested(Object o) {
            if(o instanceof MaterialRequestItem) {
                return ((MaterialRequestItem) o).getBalance();
            }
            return null;
        }

        @SuppressWarnings("unused")
        public Quantity getReadyToIssue(Object o) {
            if(uninitialized) {
                return null;
            }
            if(o instanceof MaterialRequestItem) {
                return readyToIssue((MaterialRequestItem)o);
            }
            if(o instanceof MaterialIssuedItem) {
                return ((MaterialIssuedItem) o).getQuantity();
            }
            return null;
        }

        private Quantity shortfall(MaterialRequestItem mri) {
            return mri.getBalance().subtract(readyToIssue(mri));
        }

        public String getShortfall(Object o) {
            HTMLText h = new HTMLText();
            if(!uninitialized && o instanceof MaterialRequestItem) {
                MaterialRequestItem mri = (MaterialRequestItem) o;
                Quantity qi = shortfall(mri);
                if(!qi.isZero()) {
                    h.append(qi, "red");
                }
            }
            return h.getHTML();
        }

        private Quantity readyToIssue(MaterialRequestItem mri) {
            Quantity q = readyToIssueMap.get(mri.getId());
            if(q == null) {
                q = mri.getPartNumber().getUnitOfMeasurement();
                for(MaterialIssuedItem mii: items(mri)) {
                    q = q.add(mii.getQuantity());
                }
                readyToIssueMap.put(mri.getId(), q);
            }
            return q;
        }

        void setMaterialRequest(MaterialRequest mr) {
            this.mr = mr;
            this.mi = StoredObject.get(MaterialIssued.class, "Request=" + this.mr.getId() + " AND Status=0");
            mriList.clear();
            miiMap.clear();
            readyToIssueMap.clear();
            quantityEdited.clear();
            uninitialized = mi == null;
            if(uninitialized) {
                mr.listLinks(MaterialRequestItem.class).filter(mri -> mri.getBalance().isPositive()).collectAll(mriList);
            } else {
                mi.listLinks(MaterialIssuedItem.class).forEach(mii -> items(mri(mii)).add(mii));
            }
            removeButton.setVisible(!uninitialized);
            saveButton.setVisible(false);
            mrDetails.clearContent().append("To: ").append(mr.getFromLocation().toDisplay(), "blue").
                    append("  Reference: ").append(mr.getReferenceNumber(), "blue").
                    append("  Date: ").append(mr.getDate(), "blue").
                    append("  Priority: ").append(mr.getPriority(), "blue").update();
            setDataProvider(new TreeData());
        }

        private MaterialRequestItem mri(MaterialIssuedItem mii) {
            Id id = mii.getRequestId();
            MaterialRequestItem mri = mriList.stream().filter(r -> r.getId().equals(id)).findAny().orElse(null);
            if(mri == null) {
                mri = mii.getRequest();
                mriList.add(mri);
            }
            return mri;
        }

        private List<MaterialIssuedItem> items(Object mri) {
            return miiMap.computeIfAbsent(((MaterialRequestItem)mri).getId(), k -> new ArrayList<>());
        }

        private class TreeData extends AbstractHierarchicalDataProvider<Object, String> {

            @Override
            public int getChildCount(HierarchicalQuery<Object, String> query) {
                Object parent = query.getParent();
                if(parent == null) {
                    return AbstractObjectForestSupplier.subListSize(mriList, query);
                }
                return AbstractObjectForestSupplier.subListSize(items(parent), query);
            }

            @Override
            public Stream<Object> fetchChildren(HierarchicalQuery<Object, String> query) {
                Object parent = query.getParent();
                if(parent == null) {
                    return AbstractObjectForestSupplier.subList(mriList, query).stream().map(o -> o);
                }
                return AbstractObjectForestSupplier.subList(items(parent), query).stream().map(o -> o);
            }

            @Override
            public boolean hasChildren(Object o) {
                if(o == null) {
                    return !mriList.isEmpty();
                }
                if(o instanceof MaterialRequestItem) {
                    return !items(o).isEmpty();
                }
                return false;
            }

            @Override
            public boolean isInMemory() {
                return true;
            }
        }

        private class FillItems extends DataForm {

            private final RadioField<FillAction> fillingMode = new RadioField<>("Filling Mode", new ArrayList<>());
            private final RadioChoiceField selection = new RadioChoiceField("Applicable to", new String[] {
                    "Selected Entries",
                    "Entries that are NOT Selected",
                    "All Entries"
            });

            public FillItems() {
                super("Fill Items from Available Stock");
                selection.setVisible(false);
                addField(fillingMode, selection);
            }

            @Override
            protected boolean process() {
                selectionValue = selection.getValue();
                if(selection.isVisible() && selectionValue == 2) {
                    selectedSet.clear();
                }
                close();
                fillingMode.getValue().run();
                return true;
            }

            @Override
            protected void execute(View parent, boolean doNotLock) {
                selectedSet.clear();
                selectedSet.addAll(getSelectedItems());
                if(uninitialized && selectedSet.isEmpty()) {
                    allFill();
                    return;
                }
                selection.setVisible(!selectedSet.isEmpty());
                List<FillAction> actions = new ArrayList<>();
                if(miiMap.values().stream().allMatch(List::isEmpty)) {
                    actions.add(new FillAction("Fill Items", IssueTreeGrid.this::allFill));
                } else {
                    if(mriList.stream().anyMatch(mri -> mri.getBalance().isPositive())) {
                        actions.add(new FillAction("Fill the Remaining Quantity", IssueTreeGrid.this::balanceFill));
                    }
                    actions.add(new FillAction("Clear Entries & Fill Again", IssueTreeGrid.this::allFill));
                }
                fillingMode.setItems(actions);
                super.execute(parent, doNotLock);
            }
        }

        private class RemoveEntries extends DataForm {

            private final RadioField<FillAction> fillingMode = new RadioField<>(new ArrayList<>());
            private final RadioChoiceField selection = new RadioChoiceField("Remove", new String[] {
                    "Selected Entries",
                    "Entries that are NOT Selected",
                    "All Entries"
            });
            private final BooleanField resetQuantityEdit = new BooleanField("Reset Manually Set Quantities");

            public RemoveEntries() {
                super("Remove Entries");
                selection.setVisible(false);
                addField(fillingMode, selection, resetQuantityEdit);
            }

            @Override
            protected boolean process() {
                if(resetQuantityEdit.isVisible() && resetQuantityEdit.getValue()) {
                    quantityEdited.clear();
                }
                selectionValue = selection.getValue();
                if(selection.isVisible() && selectionValue == 2) {
                    selectedSet.clear();
                }
                close();
                fillingMode.getValue().run();
                return true;
            }

            @Override
            protected void execute(View parent, boolean doNotLock) {
                selectedSet.clear();
                if(uninitialized) {
                    return;
                }
                selectedSet.addAll(getSelectedItems());
                selection.setVisible(!selectedSet.isEmpty());
                List<FillAction> actions = new ArrayList<>();
                if(selectedSet.isEmpty()) {
                    actions.add(new FillAction("Remove All Entries", IssueTreeGrid.this::removeAllEntries));
                    resetQuantityEdit.setVisible(false);
                } else {
                    actions.add(new FillAction("Remove Entries", IssueTreeGrid.this::removeEntries));
                    resetQuantityEdit.setVisible(!quantityEdited.isEmpty());
                }
                fillingMode.setItems(actions);
                fillingMode.setValue(actions.get(0));
                fillingMode.setVisible(selectedSet.isEmpty());
                super.execute(parent, doNotLock);
            }
        }

        private class FillAction implements Runnable {

            private final String actionName;
            private final Runnable action;

            private FillAction(String actionName, Runnable action) {
                this.actionName = actionName;
                this.action = action;
            }

            @Override
            public void run() {
                action.run();
            }

            @Override
            public String toString() {
                return actionName;
            }
        }

        private class EditQuantity extends DataForm {

            private MaterialIssuedItem mii;
            private Quantity shortfall;
            private final ELabelField item = new ELabelField("Item");
            private final ELabelField requirement = new ELabelField("Requirement");
            private final QuantityField quantityField = new QuantityField("Quantity to Issue");

            public EditQuantity() {
                super("Edit Quantity");
                addField(item, requirement, quantityField);
            }

            @Override
            protected boolean process() {
                InventoryItem ii = mii.getItem();
                Quantity iq = ii.getQuantity(), q = quantityField.getValue();
                if(!q.isCompatible(iq)) {
                    warning("Quantity (" + q + ") is not compatible with measurement unit - " + iq.getUnit());
                    return false;
                }
                if(q.isGreaterThan(ii.getQuantity())) {
                    warning("Available quantity is only " + iq);
                    return false;
                }
                if(q.isGreaterThan(shortfall)) {
                    warning("Quantity entered exceeds the requirement");
                    return false;
                }
                close();
                setQuantity(mii, q);
                return true;
            }

            public void execute(MaterialIssuedItem mii) {
                this.mii = mii;
                item.clearContent().append(mii.getItem()).update();
                shortfall = shortfall(mri(mii)).add(mii.getQuantity());
                requirement.clearContent().append(shortfall).update();
                quantityField.setValue(mii.getQuantity());
                execute();
            }
        }
    }

    private static class PromptStore extends DataForm implements Executable {

        private final LocationField locationField = LocationField.create("Store", 0);

        public PromptStore() {
            super("Process Material Requests");
            addField(locationField);
            setRequired(locationField);
        }

        @Override
        protected boolean process() {
            close();
            new ProcessMaterialRequest(locationField.getValue()).execute();
            return true;
        }

        @Override
        public void run() {
            execute();
        }
    }
}
