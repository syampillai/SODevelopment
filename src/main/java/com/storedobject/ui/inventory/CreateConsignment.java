package com.storedobject.ui.inventory;

import com.storedobject.common.Executable;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CreateConsignment<C extends Consignment> implements Executable {

    private final TransactionManager tm;
    private final StoredObject parent;
    private final List<HasInventoryItem> items = new ArrayList<>();
    private final Class<C> consignmentClass;
    private final Class<? extends ConsignmentItem> itemClass;
    private final Class<? extends ConsignmentPacket> packetClass;
    private final int consignmentType;
    private C consignment;
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
        if(parent != null) {
            consignmentType = switch (parent) {
                case MaterialReturned ignored -> 0;
                case InventoryRO ignored -> 1;
                case InventoryTransfer ignored -> 2;
                case InventoryGRN ignored -> 3;
                default -> -1;
            };
        } else {
            consignmentType = -1;
        }
        if(parent != null && consignmentType == -1) {
            Application.error("No consignment editor configured for - \"" + StringUtility.makeLabel(parent.getClass()) + "\"");
        }
        Class<C> cClass = null;
        try {
            //noinspection unchecked
            cClass = (Class<C>) JavaClassLoader.createClassFromProperty("CONSIGNMENT-CLASS-" + consignmentType);
        } catch (Throwable e) {
            Application.error(e);
        }
        //noinspection unchecked
        consignmentClass = cClass == null ? (Class<C>) Consignment.class : cClass;
        Class<? extends ConsignmentItem> iclass = null;
        try {
            //noinspection unchecked
            iclass = (Class<? extends ConsignmentItem>) JavaClassLoader.getLogic(consignmentClass.getName() + "Item");
        } catch (Throwable ignored) {
        }
        itemClass = iclass == null ? ConsignmentItem.class : iclass;
        Class<? extends ConsignmentPacket> pClass = null;
        try {
            //noinspection unchecked
            pClass = (Class<? extends ConsignmentPacket>) JavaClassLoader.getLogic(consignmentClass.getName() + "Packet");
        } catch (ClassNotFoundException ignored) {
        }
        packetClass = pClass == null ? ConsignmentPacket.class : pClass;
    }

    @Override
    public void execute() {
        if(parent == null || tm == null || consignmentType == -1) {
            return;
        }
        consignment = parent.listLinks(consignmentClass).single(false);
        try {
            items();
        } catch(Throwable e) {
            Application.message("Invalid items!");
            return;
        }
        if(consignment == null) {
            new AskUser(consignmentType).execute(parentView);
            return;
        } else {
            if(consignment.getType() != consignmentType) {
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

    private void createConsignment() throws NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        consignment = consignmentClass.getConstructor(StoredObject.class).newInstance(parent);
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
        String amendment;
        for(StoredObject p: parents) {
            if(p instanceof InventoryTransfer it) {
                amendment = "Amendment=" + it.getAmendment();
            } else {
                amendment = null;
            }
            p.listLinks(itemClass, amendment).forEach(i -> {
                if(i instanceof HasInventoryItem hii) {
                    items.add(hii);
                }
            });
        }
    }

    private class Editor extends ObjectEditor<C> {

        private final Button assignBoxes = new Button("Assign Boxes", VaadinIcon.PACKAGE, e -> assignBoxes());

        public Editor() {
            super(consignmentClass, EditorAction.EDIT | EditorAction.VIEW | EditorAction.DELETE);
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

        private <I extends ConsignmentItem, P extends ConsignmentPacket> void assignBoxes() {
            clearAlerts();
            @SuppressWarnings("unchecked") List<P> packets = consignment.listLinks(packetClass, null,"Number")
                    .map(p -> (P) p).toList();
            if(packets.isEmpty()) {
                message("No packages defined!");
                return;
            }
            @SuppressWarnings("unchecked") List<I> previousItems = consignment.listLinks(itemClass)
                    .map(i -> (I)i).toList();
            List<I> currentItems = new ArrayList<>();
            items.forEach(i -> {
                I ci = previousItems.stream()
                        .filter(c -> i.getItem().getId().equals(c.getItemId()))
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
            add(new ELabel("No consignment found!", Application.COLOR_ERROR));
            addField(choice, dateField, noField);
            setFieldVisible(false, dateField, noField);
            choice.addValueChangeListener(e -> setFieldVisible(choice.getValue() == 1, dateField, noField));
        }

        @Override
        protected boolean process() {
            clearAlerts();
            if(choice.getValue() == 0) {
                close();
                try {
                    createConsignment();
                } catch(Throwable e) {
                    error(e);
                    return false;
                }
                return true;
            }
            int no = noField.getValue();
            if(no <= 0) {
                warning("Please select a valid consignment number");
                noField.focus();
                return false;
            }
            List<C> consignments = StoredObject.list(consignmentClass,
                    "Type=" + type + " AND No=" + no + " AND Date='"
                            + Database.format(dateField.getValue()) + "'")
                    .filter(c -> c.listMasters(parent.getClass())
                            .filter(p -> !p.getId().equals(parent.getId()))
                            .findFirst() != null).toList();
            if(consignments.isEmpty()) {
                warning("No such consignment found!");
                noField.focus();
                return false;
            }
            close();
            SelectGrid<C> select = new SelectGrid<>(consignmentClass, consignments,
                    StoredObjectUtility.browseColumns(consignmentClass),
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
