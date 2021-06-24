package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.util.AbstractObjectForestSupplier;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.DataTreeGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
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

    public ReceiveMaterialRequested(String from) {
        super(false, 2, from, NO_ACTIONS);
    }

    public ReceiveMaterialRequested(InventoryLocation from) {
        super(false, 2, from, NO_ACTIONS);
    }

    @Override
    void created() {
        super.created();
        setExtraFilter("Status<4");
        setCaption("Receive Materials");
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
                setExtraFilter((String)null);
            } else {
                setExtraFilter("Status<4");
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

    private void receive() {
        MaterialRequest mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getReceived()) {
            message("Whatever sent earlier was already received");
        }
        switch(mr.getStatus()) {
            case 2:
            case 3:
            case 4:
                break;
            default:
                warning("Status is '" + mr.getStatusValue() + "'");
                return;
        }
        receiveTreeGrid.setMaterialRequest(mr);
        receiveTreeGrid.execute(getView());
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
                return "Issue Reference: " + mi.getReferenceNumber() + " dated " + DateUtility.formatDate(mi.getDate());
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
                    "Request " + mr.getReferenceNumber() + "/" + DateUtility.formatDate(mr.getDate()) +
                    ", Issue " + mi.getReferenceNumber() + "/" + DateUtility.formatDate(mi.getDate()),
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
                    append("  Request Reference: ").append(mr.getReferenceNumber(), "blue").
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
                    return AbstractObjectForestSupplier.subListSize(miList, query);
                }
                return AbstractObjectForestSupplier.subListSize(items(parent), query);
            }

            @Override
            public Stream<Object> fetchChildren(HierarchicalQuery<Object, String> query) {
                Object parent = query.getParent();
                if(parent == null) {
                    return AbstractObjectForestSupplier.subList(miList, query).stream().map(o -> o);
                }
                return AbstractObjectForestSupplier.subList(items(parent), query).stream().map(o -> o);
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
