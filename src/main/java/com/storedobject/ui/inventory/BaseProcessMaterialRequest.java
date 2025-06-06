package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.DataTreeGrid;
import com.storedobject.ui.*;
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

import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class BaseProcessMaterialRequest<MR extends MaterialRequest, MRI extends MaterialRequestItem>
        extends AbstractRequestMaterial<MR, MRI> {

    private final IssueTreeGrid issueTreeGrid = new IssueTreeGrid();
    private ReservedMIIGrid reservedMIIGrid;
    private Date dateOfIssue = DateUtility.today();

    public BaseProcessMaterialRequest(Class<MR> materialRequestClass) {
        this(materialRequestClass, SelectStore.get());
    }

    public BaseProcessMaterialRequest(Class<MR> materialRequestClass, String store) {
        super(materialRequestClass, true, store);
        init();
    }

    public BaseProcessMaterialRequest(Class<MR> materialRequestClass, InventoryLocation store) {
        super(materialRequestClass,true, store);
        init();
    }

    private void init() {
        GridContextMenu<MR> contextMenu = new GridContextMenu<>(this);
        GridMenuItem<MR> process = contextMenu.addItem("Process Request", e -> e.getItem()
                .ifPresent(i -> processRequest()));
        GridMenuItem<MR> viewItems = contextMenu.addItem("", e -> e.getItem()
                .ifPresent(i -> viewItems()));
        contextMenu.setDynamicContentHandler(mr -> {
            deselectAll();
            if(mr == null) {
                return false;
            }
            select(mr);
            int s = mr.getStatus();
            process.setVisible(s > 0 && (s > 4 || s <= 2));
            switch(s) {
                case 2, 3 -> {
                    viewItems.setText("View Issued Items");
                    viewItems.setVisible(true);
                }
                case 5, 6 -> {
                    viewItems.setText("View Reserved Items");
                    viewItems.setVisible(true);
                }
                default -> viewItems.setVisible(false);
            }
            return process.isVisible() || viewItems.isVisible();
        });
    }

    @Override
    protected void selectLocation() {
        new SelectStore().execute();
    }

    @Override
    public void createFooters() {
        appendFooter().join()
                .setComponent(new ELabel("Right-click on the entry for available options", Application.COLOR_SUCCESS));
    }

    @Override
    public String getCaption() {
        return "Process Material Requests";
    }

    @Override
    void created() {
        super.created();
        setFixedFilter("(Status>0 AND Status<3) OR Reserved");
    }

    @Override
    String getFixedSide() {
        return "To";
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "(Status>0 AND Status<3) OR Reserved"));
        buttonPanel.add(new Button("Process", e -> processRequest()),
                new Button("View Issued/Reserved Items", VaadinIcon.STOCK, e -> viewItems()), h);
    }

    private void processRequest() {
        MR mr = selected();
        if(mr == null) {
            return;
        }
        mr.reload();
        if(mr.getStatus() > 4) { // Reserved - issue now
            ELabel m = new ELabel("Items were reserved!", Application.COLOR_ERROR);
            m.newLine().append("Do you want to issue them?").update();
            new ActionForm("Confirm Issuance", m, () -> issueReserved(mr)).execute();
            return;
        }
        if(mr.getStatus() == 0 || mr.getStatus() > 2) {
            warning("Can not process, status is '" + mr.getStatusValue() + "'");
            refresh(mr);
            return;
        }
        issueTreeGrid.setMaterialRequest(mr);
        issueTreeGrid.processButton.setText((mr.getReserved() ? "Reserv" : "Issu") + "e Items");
        issueTreeGrid.execute(getView());
    }

    private void issueReserved(MR mr) {
        if(reservedMIIGrid == null) {
            reservedMIIGrid = new ReservedMIIGrid();
        }
        reservedMIIGrid.fill(mr);
        if(reservedMIIGrid.isEmpty()) {
            transact(mr::requestForIssuance);
            refresh(mr);
            return;
        }
        reservedMIIGrid.execute(getView());
    }

    private class IssueTreeGrid extends DataTreeGrid<Object> {

        private MR mr;
        private MaterialIssued mi;
        private final ELabel mrDetails = new ELabel();
        private final List<MRI> mriList = new ArrayList<>();
        private final Map<Id, List<MaterialIssuedItem>> miiMap = new HashMap<>(); // For each MRI
        private final Map<Id, Quantity> readyToIssueMap = new HashMap<>(); // For each MRI
        private final Map<Id, Integer> missingAssemblies = new HashMap<>(); // For each MII. 0: OK, 1: Black, -1: Grey
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
            super(Object.class, StringList.create("PartNumber", "SerialNumber AS Serial/Batch", "Requested",
                    "ReadyToIssue", "Shortfall", "ItemBin as Item/Bin"));
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
            GridMenuItem<Object> addEntry = contextMenu.addItem("Add Entries", e -> addEntries());
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
            GridMenuItem<Object> viewAssembly = contextMenu.addItem("View Assembly", e -> viewAssembly());
            contextMenu.setDynamicContentHandler(o -> {
                deselectAll();
                select(o);
                selectedSet.clear();
                selectedSet.add(o);
                selectionValue = 0;
                if(!(o instanceof MaterialIssuedItem mii)) {
                    if(!(o instanceof MaterialRequestItem mri)) {
                        return false;
                    }
                    removeEntry.setVisible(false);
                    viewAssembly.setVisible(false);
                    qEdit.setVisible(false);
                    qZero.setVisible(false);
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
                removeEntry.setVisible(true);
                viewAssembly.setVisible(missing(mii) != Integer.MIN_VALUE);
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
                if(o.getClass() == mriClass) {
                    items(o).forEach(this::select);
                } else if(o instanceof MaterialIssuedItem) {
                    deselect(mri((MaterialIssuedItem) o));
                }
            });
            e.getRemovedSelection().forEach(o -> {
                if(o.getClass() == mriClass) {
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
            m.append("Right-click on the entry for entry-specific options", Application.COLOR_SUCCESS).update();
            Button bid = new Button("Change", (String) null, e -> dateOfIssueForm().execute()).asSmall();
            ELabel filler = new ELabel("");
            ButtonLayout b = new ButtonLayout(mrDetails, bid, filler, m);
            filler.getElement().getStyle().set("flex-grow", "1");
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

        private MRI singleMRI() {
            //noinspection unchecked
            return selectedSet.stream().filter(o -> o.getClass() == mriClass).
                    map(o -> (MRI)o).findAny().orElse(null);
        }

        private MaterialIssuedItem singleMII() {
            return selectedSet.stream().filter(o -> o instanceof MaterialIssuedItem).
                    map(o -> (MaterialIssuedItem)o).findAny().orElse(null);
        }

        private void viewAssembly() {
            MaterialIssuedItem mii = singleMII();
            if(mii == null) {
                return;
            }
            new ViewAssembly<>(mii.getItem()).execute();
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

        private void addEntry(MRI mri, InventoryItem ii) {
            MaterialIssuedItem mii = new MaterialIssuedItem();
            mii.setItem(ii);
            mii.setRequest(mri);
            Quantity q = shortfall(mri);
            if(ii.getQuantity().isLessThan(q)) {
                q = ii.getQuantity();
            }
            if(q.isZero()) {
                return;
            }
            mii.setQuantity(q);
            mii.makeVirtual();
            items(mri).add(mii);
            readyToIssueMap.remove(mri.getId());
            refresh();
            removeButton.setVisible(true);
            saveButton.setVisible(true);
            expand(mri);
            select(mii);
        }

        private void addEntries() {
            MRI mri = singleMRI();
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
            new MultiSelectStock(stock, s -> s.forEach(i -> addEntry(mri, i))).execute();
        }

        private void setQuantity(MaterialIssuedItem mii, Quantity q) {
            mii.setQuantity(q);
            quantityEdited.add(mii);
            MRI mri = mri(mii);
            readyToIssueMap.remove(mri.getId());
            refresh(mri);
            refresh(mii);
        }

        private void allFill() {
            missingAssemblies.clear();
            readyToIssueMap.clear();
            miiMap.clear();
            quantityEdited.clear();
            balanceFill();
        }

        private ObjectIterator<InventoryItem> stockList(MRI mri, AtomicInteger blockedCounter) {
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
            for(MRI mri: mriList) {
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
                    mii = items(mri).stream().filter(i -> i.getItemId().equals(ii.getId())).findAny()
                            .orElse(null);
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
                        mii.setQuantity(ii.getQuantity().convert(mri.getRequested().getUnit()));
                    } else {
                        mii.setQuantity(q.convert(mri.getRequested().getUnit()));
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
            for(MRI mri: mriList) {
                expand(mri);
            }
        }

        private void removeEntries() {
            deselectAll();
            int pc, count = 0;
            List<MaterialIssuedItem> miis;
            for(MRI mri: mriList) {
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
            setMaterialRequest(mr, true);
        }

        private void save() {
            mr.reload();
            if(mr.getStatus() == 0 || mr.getStatus() > 2 || !mr.getToLocationId().equals(getFromOrTo().getId())) {
                statusError("Someone has changed the status of this request to: "
                        + mr.getStatusValue() + " (Store: " + mr.getToLocation().toDisplay() + ")");
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
            mi.setDate(dateOfIssue);
            if(transact(t -> {
                mi.save(t);
                mi.removeAllLinks(MaterialIssuedItem.class);
                MaterialIssuedItem nmii;
                for(MRI mri: mriList) {
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
                BaseProcessMaterialRequest.this.refresh(mr);
            }
        }

        private void statusError() {
            statusError("Someone has processed this request!");
        }

        private void statusError(String error) {
            error(error);
            close();
            BaseProcessMaterialRequest.this.refresh(mr);
        }

        private void issue() {
            if(mi.listLinks(MaterialIssuedItem.class).noneMatch(mii -> mii.getQuantity().isPositive())) {
                message("Nothing to " + (mr.getReserved() ? "reserve" : "issue") + "!");
                return;
            }
            if(transact(t -> mi.issue(t))) {
                close();
                mr.reload();
                BaseProcessMaterialRequest.this.refresh(mr);
            }
        }

        private void pickingOrder() {
            mriList.sort(new POrder());
            refresh();
        }

        private class POrder implements Comparator<MRI> {

            @Override
            public int compare(MRI o1, MRI o2) {
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
            if(o instanceof MaterialRequestItem mri) {
                h.append("Item: ");
                String pn = mri.getPartNumber().getName();
                if(uninitialized) {
                    h.append(pn);
                } else {
                    h.append(pn, readyToIssue(mri)
                            .isLessThan(mri.getBalance()) ? Application.COLOR_ERROR : Application.COLOR_SUCCESS);
                }
            } else if(o instanceof MaterialIssuedItem) {
                h.append(((MaterialIssuedItem) o).getItem().getLocationDisplay());
            }
            return h.getHTML();
        }

        public String getPartNumber(Object o) {
            HTMLText h = new HTMLText();
            if(o instanceof MaterialRequestItem mri) {
                String pn = mri.getPartNumber().getPartNumber();
                if(uninitialized) {
                    h.append(pn);
                } else {
                    h.append(pn, readyToIssue(mri)
                            .isLessThan(mri.getBalance()) ? Application.COLOR_ERROR : Application.COLOR_SUCCESS);
                }
            } else if(o instanceof MaterialIssuedItem mii) {
                if(!mii.getItem().getPartNumberId().equals(mii.getRequest().getPartNumberId())) {
                    h.append("APN: ", Application.COLOR_ERROR);
                }
                String pn = mii.getItem().getPartNumber().getPartNumber();
                if(mii.getItem().getQuantity().isGreaterThan(mii.getQuantity())) {
                    h.append(pn);
                } else {
                    h.append(pn, Application.COLOR_SUCCESS);
                }
            }
            return h.getHTML();
        }

        @SuppressWarnings("unused")
        public String getSerialNumber(Object o) {
            if(o instanceof MaterialIssuedItem) {
                return ((MaterialIssuedItem) o).getItem().getSerialNumberDisplay();
            }
            return "";
        }

        @SuppressWarnings("unused")
        public Quantity getRequested(Object o) {
            if(o instanceof MaterialRequestItem mri) {
                return mri.getBalance();
            }
            return null;
        }

        @SuppressWarnings("unused")
        public Quantity getReadyToIssue(Object o) {
            if(uninitialized) {
                return null;
            }
            if(o instanceof MaterialRequestItem mri) {
                return readyToIssue(mri);
            }
            if(o instanceof MaterialIssuedItem mii) {
                return mii.getQuantity();
            }
            return null;
        }

        private Quantity shortfall(MaterialRequestItem mri) {
            return mri.getBalance().subtract(readyToIssue(mri));
        }

        public String getShortfall(Object o) {
            HTMLText h = new HTMLText();
            if(!uninitialized) {
                int m = Integer.MIN_VALUE;
                if(o instanceof MaterialRequestItem mri) {
                    Quantity qi = shortfall(mri);
                    if(!qi.isZero()) {
                        h.append(qi, Application.COLOR_ERROR);
                    } else {
                        AtomicInteger ma = new AtomicInteger(Integer.MIN_VALUE);
                        items(mri).forEach(mii -> ma.set(Math.max(ma.get(), missing(mii))));
                        m = ma.get();
                    }
                } else if(o instanceof MaterialIssuedItem mii) {
                    m = missing(mii);
                }
                if(m > Integer.MIN_VALUE && m != 0) {
                    h.append("Incomplete", m > 0 ? Application.COLOR_ERROR : "pink");
                }
            }
            return h.getHTML();
        }

        private int missing(MaterialIssuedItem mii) {
            return missing(mii.getItem());
        }

        private int missing(InventoryItem item) {
            if(item == null || !item.isSerialized() || !item.getPartNumber().isAssembly()) {
                return Integer.MIN_VALUE;
            }
            Integer m = missingAssemblies.get(item.getId());
            if(m == null) {
                AtomicInteger am = new AtomicInteger(0);
                item.listMissingAssemblies().map(InventoryFitmentPosition::getAssembly)
                        .filter(a -> !a.getAccessory() && am.get() <= 0).forEach(a -> {
                            if(a.getOptional()) {
                                if(am.get() == 0) {
                                    am.set(-1);
                                }
                            } else {
                                am.set(1);
                            }
                        });
                m = am.get();
                missingAssemblies.put(item.getId(), m);
            }
            return m;
        }

        private Quantity readyToIssue(MaterialRequestItem mri) {
            Quantity q = readyToIssueMap.get(mri.getId());
            if(q == null) {
                q = mri.getPartNumber().getUnitOfMeasurement();
                for(MaterialIssuedItem mii: items(mri)) {
                    q = q.add(mii.getQuantity());
                }
                q = q.convert(mri.getRequested(), 6);
                readyToIssueMap.put(mri.getId(), q);
            }
            return q;
        }

        void setMaterialRequest(MR mr) {
            setMaterialRequest(mr, false);
        }

        private void setMaterialRequest(MR mr, boolean clear) {
            this.mr = mr;
            this.mi = StoredObject.get(MaterialIssued.class, "Request=" + this.mr.getId() + " AND Status=0");
            List<MaterialIssuedItem> miiList = new ArrayList<>();
            if(mi != null && !clear) {
                miiList = mi.listLinks(MaterialIssuedItem.class).toList();
            }
            missingAssemblies.clear();
            mriList.clear();
            miiMap.clear();
            readyToIssueMap.clear();
            quantityEdited.clear();
            uninitialized = miiList.isEmpty();
            if(uninitialized) {
                AtomicInteger unitCheck = new AtomicInteger(0);
                mr.listLinks(mriClass)
                        .filter(mri -> checkUnit(mri, unitCheck) && mri.getBalance().isPositive()).collectAll(mriList);
                if(unitCheck.get() > 0) {
                    error("Invalid entr" + (unitCheck.get() == 1 ? "y is" : "ies are")
                            + " ignored for the time being!");
                }
            } else {
                miiList.forEach(mii -> items(mri(mii)).add(mii));
            }
            for(MaterialRequestItem mri: mriList) {
                if(shortfall(mri).isNegative()) {
                    warning("Previously selected list is cleared because some of the items have changed locations!");
                    setMaterialRequest(mr, true);
                    return;
                }
            }
            removeButton.setVisible(!uninitialized);
            saveButton.setVisible(false);
            updateFooter();
            setDataProvider(new TreeData());
        }

        private boolean checkUnit(MaterialRequestItem mri, AtomicInteger unitCheck) {
            Quantity uom = mri.getPartNumber().getUnitOfMeasurement();
            if(mri.getRequested().isCompatible(uom) && mri.getIssued().isCompatible(uom)) {
                return true;
            }
            error("Incompatible unit: " + mri.getPartNumber().toDisplay() + ", Requested: " + mri.getRequested()
                    + ", Actual UoM: " + uom.getUnit());
            unitCheck.incrementAndGet();
            return false;
        }

        private void updateFooter() {
            mrDetails.clearContent().append("To: ").append(mr.getFromLocation().toDisplay(), Application.COLOR_SUCCESS).
                    append("  Reference: ").append(mr.getReference(), Application.COLOR_SUCCESS).
                    append("  Request Date: ").append(mr.getDate(), Application.COLOR_SUCCESS).
                    append(" (").append(mr.getPriority(), Application.COLOR_SUCCESS).append(")").
                    append("  Issue Date: ").append(dateOfIssue, Application.COLOR_SUCCESS).
                    update();
        }

        private MRI mri(MaterialIssuedItem mii) {
            Id id = mii.getRequestId();
            MRI mri = mriList.stream().filter(r -> r.getId().equals(id)).findAny().orElse(null);
            if(mri == null) {
                //noinspection unchecked
                mri = (MRI)mii.getRequest();
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
                    return Utility.size(mriList, query.getOffset(), query.getOffset() + query.getLimit());
                }
                return Utility.size(items(parent), query.getOffset(), query.getOffset() + query.getLimit());
            }

            @Override
            public Stream<Object> fetchChildren(HierarchicalQuery<Object, String> query) {
                @SuppressWarnings("DuplicatedCode") Object parent = query.getParent();
                if(parent == null) {
                    return Utility.stream(mriList, query.getOffset(), query.getOffset() + query.getLimit())
                            .map(o -> o);
                }
                return Utility.stream(items(parent), query.getOffset(), query.getOffset() + query.getLimit())
                        .map(o -> o);
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
                close();
                if(resetQuantityEdit.isVisible() && resetQuantityEdit.getValue()) {
                    quantityEdited.clear();
                }
                selectionValue = selection.getValue();
                if(selection.isVisible() && selectionValue == 2) {
                    selectedSet.clear();
                    removeAllEntries();
                    return true;
                }
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
                fillingMode.setValue(actions.getFirst());
                fillingMode.setVisible(selectedSet.isEmpty());
                super.execute(parent, doNotLock);
            }
        }

        private record FillAction(String actionName, Runnable action) implements Runnable {

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

        private DateOfIssueForm dateOfIssueForm;

        private DateOfIssueForm dateOfIssueForm() {
            if(dateOfIssueForm == null) {
                dateOfIssueForm = new DateOfIssueForm();
            }
            return dateOfIssueForm;
        }

        private class DateOfIssueForm extends DataForm {

            private final DateField doi = new DateField("Date of Issue");

            public DateOfIssueForm() {
                super("Set Date of Issue");
                addField(doi);
            }

            @Override
            protected boolean process() {
                close();
                dateOfIssue = doi.getValue();
                updateFooter();
                return true;
            }

            @Override
            protected void execute(View parent, boolean doNotLock) {
                doi.setMin(mr.getDate());
                doi.setMax(DateUtility.today());
                super.execute(parent, doNotLock);
            }
        }
    }

    private static List<MaterialIssuedItem> reservedItems(List<MaterialIssued> mis) {
        List<MaterialIssuedItem> miis = new ArrayList<>();
        mis.forEach(mi -> mi.listLinks(MaterialIssuedItem.class).collectAll(miis));
        miis.sort(new MIIOrder());
        return miis;
    }

    private static List<MaterialIssued> reservedIssues(MaterialRequest mr) {
        return StoredObject.list(MaterialIssued.class, "Request=" + mr.getId() + " AND Status=3").toList();
    }

    private static class MIIOrder implements Comparator<MaterialIssuedItem> {

        @Override
        public int compare(MaterialIssuedItem mii1, MaterialIssuedItem mii2) {
            return Integer.compare(pOrder(mii1), pOrder(mii2));
        }

        private int pOrder(MaterialIssuedItem mii) {
            InventoryLocation loc = mii.getItem().getLocation();
            return loc instanceof InventoryBin b ? b.getPickingOrder() : 0;
        }
    }

    private class ReservedMIIGrid extends ObjectGrid<MaterialIssuedItem> {

        private MR mr;
        private List<MaterialIssued> mis;

        ReservedMIIGrid() {
            super(MaterialIssuedItem.class, StringList.create(
                    "Item.PartNumber.Name AS Item",
                    "Item.PartNumber.PartNumber AS Part Number",
                    "Item.SerialNumber AS Serial Number",
                    "Quantity",
                    "Location"
            ));
            setCaption("Issue Reserved Items");
        }

        @Override
        public Component createHeader() {
            return new ButtonLayout(new Button("Issue Items", VaadinIcon.TRUCK, e -> issue()),
                    new Button("Quit", e -> close()));
        }

        void fill(MR mr) {
            this.mr = mr;
            mis = reservedIssues(mr);
            load(ObjectIterator.create(reservedItems(mis)));
        }

        private void issue() {
            if(transact(this::issue)) {
                BaseProcessMaterialRequest.this.refresh(mr);
                fill(mr);
                if(size() == 0) {
                    transact(mr::requestForIssuance);
                }
                message("Items issued successfully");
            }
            close();
        }

        private void issue(Transaction transaction) throws Exception {
            for(MaterialIssued mi: mis) {
                mi.issueReserved(transaction);
            }
        }

        @SuppressWarnings("unused")
        public String getLocation(MaterialIssuedItem mii) {
            return mii.getItem().getLocation().toDisplay();
        }

        @SuppressWarnings("unused")
        public Quantity getQuantity(MaterialIssuedItem mii) {
            return mii.getItem().getQuantity();
        }
    }

    private ViewItems viewItems;

    private void viewItems() {
        MR mr = selected();
        if(mr == null) {
            return;
        }
        mr.reload();
        switch(mr.getStatus()) {
            case 2, 3, 4 -> {
            }
            default -> {
                message("Not yet " + (mr.getReserved() ? "reserv" : "issu") + "ed");
                return;
            }
        }
        if(viewItems == null) {
            viewItems = new ViewItems(mr);
        } else {
            viewItems.setRequest(mr);
        }
        viewItems.execute();
    }

    private class ViewItems extends ObjectGrid<MaterialIssuedItem> {

        ViewItems(MR mr) {
            super(MaterialIssuedItem.class);
            setRequest(mr);
        }

        @Override
        public Window createWindow(View view) {
            return createDecoratedWindow(view);
        }

        void setRequest(MR mr) {
            setCaption((mr.getReserved() ? "Reserv" : "Issu") + "ed Items (Requested by " + mr.getPerson() + ")");
            load(StoredObject.list(MaterialIssued.class, "Request=" + mr.getId())
                    .expand(mi -> mi.listLinks(MaterialIssuedItem.class, "(Quantity).Quantity>0")));
        }
    }

    @Override
    protected Button getSwitchLocationButton() {
        return new Button("Change", (String) null, e -> new SwitchStore().execute());
    }

    private class SwitchStore extends DataForm {

        private final LocationField currentLoc = LocationField.create("Current Store", getFromOrTo());
        private final LocationField newLoc = LocationField.create("Change to", 0);

        public SwitchStore() {
            super("Change Store");
            addField(currentLoc, newLoc);
            setFieldReadOnly(currentLoc);
            setRequired(newLoc);
        }

        @Override
        protected boolean process() {
            InventoryLocation loc = newLoc.getValue();
            if(loc.getId().equals(currentLoc.getObjectId())) {
                message("Not changed!");
                return true;
            }
            close();
            BaseProcessMaterialRequest.this.close();
            try {
                BaseProcessMaterialRequest.this.getClass().getConstructor(InventoryLocation.class)
                        .newInstance(loc).execute();
                message("Store changed to '" + loc.toDisplay() + "'");
            } catch(Exception e) {
                error(e);
            }
            return true;
        }
    }
}
