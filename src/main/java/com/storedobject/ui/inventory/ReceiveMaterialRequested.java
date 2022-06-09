package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ReceiveMaterialRequested extends AbstractRequestMaterial {

    private final ReceiveTreeGrid receiveTreeGrid = new ReceiveTreeGrid();
    private ELabel footer;

    public ReceiveMaterialRequested(String from) {
        super(false, 2, from, NO_ACTIONS);
    }

    public ReceiveMaterialRequested(InventoryLocation from) {
        super(false, 2, from, NO_ACTIONS);
        GridContextMenu<MaterialRequest> contextMenu = new GridContextMenu<>(this);
        GridMenuItem<MaterialRequest> receive = contextMenu.addItem("Receive Materials", e -> receive());
        GridMenuItem<MaterialRequest> request = contextMenu.addItem("Request to issue", e -> receive());
        GridMenuItem<MaterialRequest> release = contextMenu.addItem("Cancel Reservation",
                e -> releaseReservation(e.getItem().orElse(null)));
        contextMenu.setDynamicContentHandler(o -> {
            deselectAll();
            select(o);
            if(switch(o.getStatus()) {
                case 0, 4 -> true;
                default -> false;
            }) {
                return false;
            }
            receive.setVisible(o.getStatus() != 1 && !o.getReserved());
            request.setVisible(o.getStatus() != 1 && o.getReserved());
            release.setVisible(o.getReserved());
            return true;
        });
    }

    @Override
    void created() {
        super.created();
        setFixedFilter("Status>0 AND Status<4");
        setCaption("Receive Materials");
    }

    @Override
    public void createFooters() {
        footer = new ELabel();
        appendFooter().join().setComponent(footer);
    }

    @Override
    public void loaded() {
        if(footer == null) {
            return;
        }
        footer.clearContent();
        if(isEmpty()) {
            footer.append("No entries loaded", "red");
        } else {
            footer.append("Right-click on the entry for entry-specific options", "blue");
        }
        footer.update();
    }

    @Override
    String getFixedSide() {
        return "From";
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        buttonPanel.add(new Button("Receive Materials", VaadinIcon.TRUCK, e -> receive()));
        Checkbox cb = new Checkbox("Include History");
        cb.addValueChangeListener(e -> {
            deselectAll();
            if(e.getValue()) {
                setFixedFilter((String)null);
            } else {
                setFixedFilter("Status>0 AND Status<4");
            }
        });
        buttonPanel.add(cb);
        if(!(getFromOrTo() instanceof InventoryStoreBin)) {
            buttonPanel.add(new Button("\u21f0 Requests Screen", VaadinIcon.FILE_TABLE, e -> request()));
        }
    }

    private void request() {
        close();
        new RequestMaterial(getFromOrTo()).execute();
    }

    private void releaseReservation(MaterialRequest mr) {
        if(mr == null || !mr.getReserved()) {
            return;
        }
        ELabel m = new ELabel();
        if(mr.getStatus() == 1) {
            m.append("Items are not yet reserved.", "red");
        } else {
            m.append("Not yet issued but items are " + (mr.getStatus() == 2 ? "partially" : "fully")
                    + " reserved.", "red");
        }
        m.newLine().append("Do you really want to cancel the reservation?").update();
        new ActionForm("Cancel Reservation", m, () -> {
            if(transact(mr::releaseReservation)) {
                refresh(mr);
                message("Reservation cancelled!");
            }
        }).execute();
    }

    private void receive() {
        MaterialRequest mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getReceived()) {
            message("Whatever sent earlier was already received");
        }
        if(mr.getReserved() && mr.getStatus() == 1) {
            ELabel m = new ELabel("Items are not yet reserved.", "red");
            m.newLine().append("Do you want to request for issuance instead of reservation?").update();
            new ActionForm("Confirm Issuance", m, () -> requestForIssuance(mr)).execute();
            return;
        }
        switch(mr.getStatus()) {
            case 2:
            case 3:
                break;
            default:
                warning("Status is '" + mr.getStatusValue() + "'");
                return;
        }
        if(mr.getReserved()) {
            ELabel m = new ELabel("Not yet issued but items are " + (mr.getStatus() == 2 ? "partially" : "fully")
                    + " reserved.", "red");
            m.newLine().append("Do you want to request for issuance of the reserved items?").update();
            new ActionForm("Confirm Issuance", m, () -> requestForIssuance(mr)).execute();
            return;
        }
        receiveTreeGrid.setMaterialRequest(mr);
        receiveTreeGrid.execute(getView());
    }

    private void requestForIssuance(MaterialRequest mr) {
        if(transact(mr::requestForIssuance)) {
           refresh(mr);
        }
    }

    private class ReceiveTreeGrid extends DataTreeGrid<Object> {

        private MaterialRequest mr;
        private final ELabel mrDetails = new ELabel();
        private final List<MaterialIssued> miList = new ArrayList<>();
        private final Map<Id, List<MaterialIssuedItem>> miiMap = new HashMap<>();
        private final ButtonLayout buttonLayout = new ButtonLayout();
        private final Button receiveButton = new Button("Receive", VaadinIcon.STOCK, e -> receive());

        ReceiveTreeGrid() {
            super(Object.class, StringList.create("Details", "Quantity", "Status"));
            buttonLayout.add(receiveButton, new Button("Exit", e -> close()));
            setDataProvider(new TreeData());
            setCaption("Receive and Bin Materials");
        }

        @Override
        public Component createHeader() {
            return buttonLayout;
        }

        @SuppressWarnings("unused")
        public String getDetails(Object o) {
            if(o instanceof MaterialIssued mi) {
                return "Issue Reference: " + mi.getReference() + " dated " + DateUtility.formatDate(mi.getDate());
            }
            if(o instanceof MaterialIssuedItem mii) {
                return mii.getItem().toDisplay();
            }
            return null;
        }

        public Quantity getQuantity(Object o) {
            if(o instanceof MaterialIssuedItem mii) {
                return mii.getQuantity();
            }
            return null;
        }

        public String getStatus(Object o) {
            if(o instanceof MaterialIssued mi) {
                return mi.getStatus() == 1 ? "Issued" : mi.getStatusValue();
            }
            if(o instanceof MaterialIssuedItem mii) {
                return mii.getItem().getInTransit() ? "In Transit" : "Accepted";
            }
            return null;
        }

        private MaterialIssued mi(MaterialIssuedItem mii) {
            return miList.stream().filter(mi -> items(mi).contains(mii)).findAny().orElse(null);
        }

        private List<MaterialIssuedItem> items(Object mi) {
            List<MaterialIssuedItem> items = miiMap.get(((MaterialIssued)mi).getId());
            if(items == null) {
                items = ((MaterialIssued)mi).listLinks(MaterialIssuedItem.class).toList();
                miiMap.put(((MaterialIssued)mi).getId(), items);
            }
            return items;
        }

        @Override
        public void createFooters() {
            appendFooter().join().setComponent(mrDetails);
        }

        public Object selected() {
            clearAlerts();
            Object o = getSelected();
            if(o == null) {
                warning("Nothing selected");
            }
            return o;
        }

        private void receive() {
            Object o = selected();
            if(o == null) {
                return;
            }
            receive(o);
        }

        private void receive(Object o) {
            MaterialIssued mi;
            TransactionManager.Transact transact;
            List<InventoryItem> items = new ArrayList<>();
            if(o instanceof MaterialIssuedItem mii) {
                items.add(mii.getItem());
                mi = mi(mii);
                if(items(mi).size() == 1) {
                    transact = mi::close;
                } else {
                    transact = null;
                }
            } else if(o instanceof MaterialIssued) {
                mi = (MaterialIssued) o;
                items(mi).forEach(mii -> items.add(mii.getItem()));
                transact = mi::close;
            } else {
                return;
            }
            receive(mi, items, transact);
        }

        private void receive(MaterialIssued mi, List<InventoryItem> items, TransactionManager.Transact transact) {
            if(mi.getStatus() == 2) {
                warning("Already received!");
                return;
            }
            deselectAll();
            clearAlerts();
            new ReceiveAndBin(mi.getDate(),
                    "Request " + mr.getReference() + "/" + DateUtility.formatDate(mr.getDate()) +
                    ", Issue " + mi.getReference() + "/" + DateUtility.formatDate(mi.getDate()),
                    items, transact, () -> refreshAgain(mi)).
                    execute(getView());
        }

        private void refreshAgain(MaterialIssued mi) {
            mr.reload();
            mi.reload();
            miiMap.remove(mi.getId());
            refresh();
            ReceiveMaterialRequested.this.deselectAll();
            ReceiveMaterialRequested.this.refresh(mr);
            ReceiveMaterialRequested.this.select(mr);
            if(mr.getStatus() != 4) {
                message("More items still to be received/accepted");
            } else {
                message("All items are received and the request is closed");
            }
        }

        void setMaterialRequest(MaterialRequest mr) {
            this.mr = mr;
            miList.clear();
            miiMap.clear();
            StoredObject.list(MaterialIssued.class, "Request=" + mr.getId(), "Status,No DESC").collectAll(miList);
            mrDetails.clearContent().append("From: ").append(mr.getToLocation().toDisplay(), "blue").
                    append("  Receiving at: ").append(mr.getFromLocation().toDisplay(), "blue").
                    append("  Request Reference: ").append(mr.getReference(), "blue").
                    append("  Date: ").append(mr.getDate(), "blue").
                    append("  Status: ").append(mr.getStatusValue(), "blue").update();
            setDataProvider(new TreeData());
            receiveButton.setVisible(mr.getStatus() > 1 && mr.getStatus() < 4);
            if(receiveButton.isVisible()) {
                receiveButton.setVisible(miList.stream().anyMatch(mi -> mi.getStatus() == 1));
            }
        }

        private class TreeData extends AbstractHierarchicalDataProvider<Object, String> {

            @Override
            public int getChildCount(HierarchicalQuery<Object, String> query) {
                Object parent = query.getParent();
                if(parent == null) {
                    return Utility.size(miList, query.getOffset(), query.getOffset() + query.getLimit());
                }
                return Utility.size(items(parent), query.getOffset(), query.getOffset() + query.getLimit());
            }

            @Override
            public Stream<Object> fetchChildren(HierarchicalQuery<Object, String> query) {
                Object parent = query.getParent();
                if(parent == null) {
                    return Utility.stream(miList, query.getOffset(), query.getOffset() + query.getLimit())
                                    .map(o -> o);
                }
                return Utility.stream(items(parent), query.getOffset(), query.getOffset() + query.getLimit())
                        .map(o -> o);
            }

            @Override
            public boolean hasChildren(Object o) {
                if(o == null) {
                    return !miList.isEmpty();
                }
                if(o instanceof MaterialIssued) {
                    return !items(o).isEmpty();
                }
                return false;
            }

            @Override
            public boolean isInMemory() {
                return true;
            }
        }
    }
}
