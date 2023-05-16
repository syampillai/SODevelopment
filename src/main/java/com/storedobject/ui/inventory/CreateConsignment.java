package com.storedobject.ui.inventory;

import com.storedobject.common.Executable;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public class CreateConsignment implements Executable {

    private final TransactionManager tm;
    private final StoredObject parent;
    private final List<HasInventoryItem> items = new ArrayList<>();
    private Consignment consignment;
    private final View parentView;
    @SuppressWarnings("rawtypes")
    private final ObjectBrowser parentBrowser;

    public CreateConsignment(Application application, StoredObject parent) {
        tm = application.getTransactionManager();
        this.parent = parent;
        parentView = application.getActiveView();
        if(parentView instanceof WrappedView wv && wv.getComponent() instanceof ObjectBrowser<?> b) {
            parentBrowser = b;
        } else {
            parentBrowser = null;
        }
    }

    @Override
    public void execute() {
        if(parent == null || tm == null) {
            return;
        }
        int type;
        if(parent instanceof MaterialReturned) {
            type = 0;
        } else if(parent instanceof InventoryRO) {
            type = 1;
        } else if(parent instanceof InventoryTransfer) {
            type = 2;
        } else {
            Application.message("No consignment editor configured!");
            return;
        }
        consignment = parent.listLinks(Consignment.class).single(false);
        try {
            items();
        } catch(Throwable e) {
            Application.message("Invalid items!");
            return;
        }
        if(consignment == null) {
            new AskUser(type).execute(parentView);
            return;
        } else {
            if(consignment.getType() != type) {
                Application.message("Consistency error, please contact Technical Support!");
                return;
            }
        }
        editConsignment();
    }

    private void editConsignment() {
        Editor editor = new Editor();
        editor.setObject(consignment);
        editor.execute(parentView);
    }

    private void createConsignment(int type) {
        consignment = new Consignment();
        consignment.setType(type);
        attachConsignment(true);
    }

    private void attachConsignment(boolean saveConsignment) {
        try {
            tm.transact(t -> {
                if(saveConsignment) {
                    consignment.save(t);
                }
                parent.addLink(t, consignment);
                if(parentBrowser != null) {
                    //noinspection unchecked
                    parentBrowser.refresh(parent);
                }
            });
            consignment.reload();
            if(!saveConsignment) {
                items();
            }
            editConsignment();
        } catch(Exception e) {
            Application.warning(e);
        }
    }

    private <T extends StoredObject> void items() throws Exception {
        items.clear();
        List<StoredObject> parents = new ArrayList<>();
        if(consignment == null) {
            parents.add(parent);
        } else {
            consignment.listMasters(parent.getClass()).map(o -> (StoredObject) o).collectAll(parents);
        }
        @SuppressWarnings("unchecked")
        Class<T> itemClass = (Class<T>) JavaClassLoader.getLogic(parent.getClass().getName() + "Item");
        for(StoredObject p: parents) {
            p.listLinks(itemClass).forEach(i -> {
                if(i instanceof HasInventoryItem hii) {
                    items.add(hii);
                }
            });
        }
    }

    private class Editor extends ObjectEditor<Consignment> {

        private final Button assignBoxes = new Button("Assign Boxes", VaadinIcon.PACKAGE, e -> assignBoxes());

        public Editor() {
            super(Consignment.class, EditorAction.EDIT | EditorAction.VIEW | EditorAction.DELETE);
            addConstructedListener(e -> setFieldReadOnly("Type"));
            if(parentBrowser != null) {
                addObjectChangedListener(new ObjectChangedListener<>() {
                    @Override
                    public void saved(Consignment object) {
                        //noinspection unchecked
                        parentBrowser.refresh(parent);
                    }

                    @Override
                    public void deleted(Consignment object) {
                        //noinspection unchecked
                        parentBrowser.refresh(parent);
                        close();
                    }
                });
            }
        }

        @Override
        protected void addExtraButtons() {
            buttonPanel.add(assignBoxes);
        }

        @Override
        protected boolean includeField(String fieldName) {
            if(fieldName.equals("Items.l")) {
                return false;
            }
            return super.includeField(fieldName);
        }

        private void assignBoxes() {
            clearAlerts();
            List<ConsignmentPacket> packets = consignment.listLinks(ConsignmentPacket.class, null,"Number")
                    .toList();
            if(packets.isEmpty()) {
                message("No packages defined!");
                return;
            }
            List<ConsignmentItem> previousItems = consignment.listLinks(ConsignmentItem.class).toList();
            List<ConsignmentItem> currentItems = new ArrayList<>();
            items.forEach(i -> {
                ConsignmentItem ci = previousItems.stream().filter(p -> i.getItem().getId().equals(p.getItemId()))
                        .findAny().orElse(null);
                if(ci == null) {
                    ci = new ConsignmentItem();
                    InventoryItem item = i.getItem();
                    ci.setItem(item);
                    ci.setQuantity(i.getQuantity());
                    ci.setUnitCost(item.getUnitCost());
                    ci.makeVirtual();
                    ci.setBoxNumber(1);
                }
                currentItems.add(ci);
            });
            ItemEditor itemsEditor = new ItemEditor();
            previousItems.forEach(ci -> {
                if(!currentItems.contains(ci)) {
                    itemsEditor.toRemove.add(ci);
                }
            });
            itemsEditor.packets = packets;
            currentItems.forEach(itemsEditor::append);
            itemsEditor.execute(this);
        }
    }

    private class ItemEditor extends ObjectListEditor<ConsignmentItem> {

        private final List<ConsignmentItem> toRemove = new ArrayList<>();
        List<ConsignmentPacket> packets;

        public ItemEditor() {
            super(ConsignmentItem.class);
            addConstructedListener(f -> buttonPanel.add(new Button("Exit", e -> checkAndClose())));
            setAllowAdd(false);
            setAllowDelete(false);
            setAllowReloadAll(false);
        }

        private void checkAndClose() {
            if(isSavePending()) {
                new ActionForm("Changes will be lost!\nDo you really want to exit?", this::close, () -> {})
                        .execute();
            } else {
                close();
            }
        }

        @Override
        public void save(Transaction transaction) throws Exception {
            super.save(transaction);
            for(ConsignmentItem ci: toRemove) {
                ci.delete(transaction);
            }
        }

        @Override
        protected void saved(Transaction transaction, ConsignmentItem object) throws Exception {
            consignment.addLink(transaction, object);
        }

        @Override
        public void validateData(ConsignmentItem item) throws Exception {
            int no = item.getBoxNumber();
            if(no <= 0 || packets.stream().noneMatch(p -> p.getNumber() == no)) {
                throw new Invalid_Value("Box #" + no + " not found");
            }
        }

        @Override
        public boolean isColumnEditable(String columnName) {
            return "UnitCost".equals(columnName) || "BoxNumber".equals(columnName);
        }
    }

    private class AskUser extends DataForm {

        private final int type;
        private final RadioChoiceField choice = new RadioChoiceField("Choose",
                StringList.create("Create a New Consignment", "Add to an Existing Consignment"));
        private final DateField dateField = new DateField("Consignment Date");
        private final IntegerField noField = new IntegerField("Consignment No.");

        public AskUser(int type) {
            super("");
            this.type = type;
            String caption = "Consignment";
            try {
                caption += " for " + parent.getClass().getMethod("getReference")
                        .invoke(parent);
            } catch(Throwable ignored) {
            }
            setCaption(caption);
            add(new ELabel("No consignment found!", "red"));
            addField(choice, dateField, noField);
            setFieldVisible(false, dateField, noField);
            choice.addValueChangeListener(e -> setFieldVisible(choice.getValue() == 1, dateField, noField));
        }

        @Override
        protected boolean process() {
            clearAlerts();
            if(choice.getValue() == 0) {
                close();
                createConsignment(type);
                return true;
            }
            int no = noField.getValue();
            if(no <= 0) {
                warning("Please select a valid consignment number");
                noField.focus();
                return false;
            }
            List<Consignment> consignments = StoredObject.list(Consignment.class,
                    "Type=" + type + " AND No=" + no + " AND Date='"
                            + Database.format(dateField.getValue()) + "'")
                    .filter(c -> c.listMasters(parent.getClass()).filter(p -> !p.getId().equals(parent.getId()))
                            .findFirst() != null).toList();
            if(consignments.isEmpty()) {
                warning("No such consignment found!");
                noField.focus();
                return false;
            }
            close();
            SelectGrid<Consignment> select = new SelectGrid<>(Consignment.class, consignments,
                    StoredObjectUtility.browseColumns(Consignment.class),
                    c -> {
                        consignment = c;
                        attachConsignment(false);
                    });
            select.setCaption(getCaption());
            select.execute(parentView);
            return true;
        }
    }
}
