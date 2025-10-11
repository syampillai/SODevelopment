package com.storedobject.ui.inventory;

import com.storedobject.core.*;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectListEditor;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public class ConsignmentEditor<C extends Consignment> extends ObjectEditor<C> {

    private final Button assignPackages = new Button("Assign Packages", VaadinIcon.PACKAGE, e -> assignPackages());
    private C consignment;
    List<HasInventoryItem> items;
    Class<? extends ConsignmentItem> itemClass;
    Class<? extends ConsignmentPacket> packetClass;

    public ConsignmentEditor(Class<C> consignmentClass) {
        super(consignmentClass, EditorAction.EDIT | EditorAction.VIEW | EditorAction.DELETE);
    }

    void setConsignment(C consignment) {
        this.consignment = consignment;
        setObject(consignment);
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(assignPackages);
    }

    @Override
    protected boolean includeField(String fieldName) {
        if(fieldName.equals("Items.l")) {
            return false;
        }
        return super.includeField(fieldName);
    }

    private <I extends ConsignmentItem, P extends ConsignmentPacket> void assignPackages() {
        clearAlerts();
        @SuppressWarnings("unchecked") List<P> packets = consignment.listLinks(packetClass, null,"Number")
                .map(p -> (P) p).toList();
        if(packets.isEmpty()) {
            message("No packages defined!");
            return;
        }
        if(items.stream().anyMatch(i -> i.getInventoryItem() == null)) {
            message("Please make sure that all items are created/inspected");
            return;
        }
        @SuppressWarnings("unchecked") List<I> previousItems = consignment.listLinks(itemClass)
                .map(i -> (I)i).toList();
        List<I> currentItems = new ArrayList<>();
        items.forEach(i -> {
            I ci = previousItems.stream()
                    .filter(c -> i.getInventoryItem().getId().equals(c.getItemId()))
                    .findAny().orElse(null);
            if(ci == null) {
                try {
                    //noinspection unchecked
                    ci = (I) itemClass.getConstructor().newInstance();
                } catch (Throwable e) {
                    error(e);
                    return;
                }
                InventoryItem item = i.getItem();
                ci.setItem(item);
                ci.setQuantity(i.getQuantity());
                ci.setUnitCost(item.getUnitCost());
                ci.makeVirtual();
                ci.setBoxNumber(1);
            }
            currentItems.add(ci);
        });
        ItemEditor<I, P> itemsEditor = new ItemEditor<>();
        previousItems.forEach(ci -> {
            if(!currentItems.contains(ci)) {
                itemsEditor.toRemove.add(ci);
            }
        });
        itemsEditor.packets = packets;
        currentItems.forEach(itemsEditor::append);
        itemsEditor.execute(this);
    }

    private class ItemEditor<I extends ConsignmentItem, P extends ConsignmentPacket> extends ObjectListEditor<I> {

        private final List<ConsignmentItem> toRemove = new ArrayList<>();
        List<P> packets;

        public ItemEditor() {
            //noinspection unchecked
            super((Class<I>) itemClass);
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
                throw new Invalid_Value("Package #" + no + " not found");
            }
        }

        @Override
        public boolean isColumnEditable(String columnName) {
            return "UnitCost".equals(columnName) || "BoxNumber".equals(columnName);
        }
    }
}
