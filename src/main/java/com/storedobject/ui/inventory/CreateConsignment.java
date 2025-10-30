package com.storedobject.ui.inventory;

import com.storedobject.common.Executable;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CreateConsignment<C extends Consignment> implements Executable {

    private final TransactionManager tm;
    private final OfEntity parent;
    private final List<HasInventoryItem> items = new ArrayList<>();
    private final Class<C> consignmentClass;
    private final Class<? extends ConsignmentItem> itemClass;
    private final Class<? extends ConsignmentPacket> packetClass;
    private final int consignmentType;
    private C consignment;
    private final View parentView;
    @SuppressWarnings("rawtypes")
    private final ObjectBrowser parentBrowser;

    public CreateConsignment(Application application, OfEntity parent) {
        tm = application.getTransactionManager();
        this.parent = parent;
        parentView = application.getActiveView();
        if(parentView instanceof WrappedView wv && wv.getComponent() instanceof ObjectBrowser<?> b) {
            parentBrowser = b;
        } else {
            parentBrowser = null;
        }
        int ct = -1;
        if(parent != null) {
            try {
                ct = Consignment.findTypeFor(parent.getClass());
            } catch (SORuntimeException e) {
                Application.error(e);
            }
        }
        consignmentType = ct;
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
        consignment = ((StoredObject)parent).listLinks(consignmentClass).single(false);
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
        ConsignmentEditor<C> editor = (ConsignmentEditor<C>) ObjectEditor.create(consignmentClass);
        editor.addConstructedListener(o -> editor.setFieldReadOnly("Type"));
        if(parentBrowser != null) {
            editor.addObjectChangedListener(new ObjectChangedListener<>() {
                @Override
                public void saved(C object) {
                    //noinspection unchecked
                    parentBrowser.refresh(parent);
                }

                @Override
                public void deleted(C object) {
                    //noinspection unchecked
                    parentBrowser.refresh(parent);
                    editor.close();
                }
            });
        }
        editor.items = items;
        editor.itemClass = itemClass;
        editor.packetClass = packetClass;
        editor.setConsignment(consignment);
        editor.execute(parentView);
    }

    private void createConsignment() throws NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        consignment = consignmentClass.getConstructor(OfEntity.class).newInstance(parent);
        attachConsignment(true);
    }

    private void attachConsignment(boolean saveConsignment) {
        try {
            tm.transact(t -> {
                if(saveConsignment) {
                    consignment.save(t);
                }
                ((StoredObject)parent).addLink(t, consignment);
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
            parents.add((StoredObject) parent);
        } else {
            consignment.listMasters(((StoredObject)parent).getClass()).map(o -> (StoredObject) o).collectAll(parents);
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
                    .filter(c -> c.listMasters(((StoredObject)parent).getClass())
                            .filter(p -> !p.getId().equals(((StoredObject)parent).getId()))
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
