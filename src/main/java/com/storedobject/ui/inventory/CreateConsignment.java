package com.storedobject.ui.inventory;

import com.storedobject.common.Executable;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectListEditor;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public class CreateConsignment implements Executable {

    private final TransactionManager tm;
    private final StoredObject parent;
    private final List<HasInventoryItem> items = new ArrayList<>();
    private Consignment consignment;

    public CreateConsignment(Application application, StoredObject parent) {
        tm = application.getTransactionManager();
        this.parent = parent;
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
        } else {
            Application.message("No consignment editor configured!");
            return;
        }
        try {
            items();
        } catch(Throwable e) {
            Application.message("Invalid items!");
            return;
        }
        consignment = parent.listLinks(Consignment.class, true).single(false);
        if(consignment == null) {
            consignment = new Consignment();
            consignment.setType(type);
            try {
                tm.transact(t -> {
                   consignment.save(t);
                   parent.addLink(t, consignment);
                });
                consignment.reload();
            } catch(Exception e) {
                Application.warning(e);
                return;
            }
        } else {
            if(consignment.getType() != type) {
                Application.message("Consistency error, please contact Technical Support!");
            }
        }
        Editor editor = new Editor();
        editor.setObject(consignment);
        editor.execute();
    }

    private <T extends StoredObject> void items() throws Exception {
        items.clear();
        @SuppressWarnings("unchecked")
        Class<T> itemClass = (Class<T>) JavaClassLoader.getLogic(parent.getClass().getName() + "Item");
        parent.listLinks(itemClass).forEach(i -> {
            if(i instanceof HasInventoryItem hii) {
                items.add(hii);
            }
        });
    }

    private class Editor extends ObjectEditor<Consignment> {

        private final Button assignBoxes = new Button("Assign Boxes", VaadinIcon.PACKAGE, e -> assignBoxes());

        public Editor() {
            super(Consignment.class, EditorAction.EDIT | EditorAction.VIEW);
            addConstructedListener(e -> setFieldReadOnly("Type"));
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
            List<ConsignmentPacket> packets = consignment.listLinks(ConsignmentPacket.class, null,"Number")
                    .toList();
            if(packets.isEmpty()) {
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
            addConstructedListener(f -> buttonPanel.add(new Button("Exit (Without Saving)", e -> close())));
            setAllowAdd(false);
            setAllowDelete(false);
            setAllowReloadAll(false);
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
}
