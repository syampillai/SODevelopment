package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.MessageGrid;
import com.storedobject.ui.ObjectChangedListener;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

public class BaseRequestMaterial<MR extends MaterialRequest, MRI extends MaterialRequestItem>
        extends AbstractRequestMaterial<MR, MRI> {

    private ObjectEditor<MR> restrictedEditor;

    public BaseRequestMaterial(Class<MR> materialRequestClass) {
        this(materialRequestClass, SelectLocation.get(0, 4, 5, 10, 11, 16));
    }

    public BaseRequestMaterial(Class<MR> materialRequestClass, String from) {
        this(materialRequestClass, ParameterParser.itemTypeClass(from), ParameterParser.location(from, 0, 4, 5, 10, 11, 16));
    }

    public BaseRequestMaterial(Class<MR> materialRequestClass, InventoryLocation from) {
        this(materialRequestClass, null, from, null);
    }

    public BaseRequestMaterial(Class<MR> materialRequestClass, InventoryLocation from, InventoryLocation to) {
        this(materialRequestClass, null, from, to);
    }

    public BaseRequestMaterial(Class<MR> materialRequestClass, Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from) {
        this(materialRequestClass, itemTypeClass, from, null);
    }

    public BaseRequestMaterial(Class<MR> materialRequestClass, Class<? extends InventoryItemType> itemTypeClass, InventoryLocation from,
                               InventoryLocation to) {
        super(materialRequestClass, false, from, 0, to);
        if(itemTypeClass != null) {
            this.itemTypeClass = itemTypeClass;
        }
    }

    @Override
    void created() {
        super.created();
        setFixedFilter("Status<=1");
    }

    @Override
    String getFixedSide() {
        return "From";
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        PopupButton sendRequest = new PopupButton("Send Request", VaadinIcon.PAPERPLANE);
        sendRequest.add(new Button("Immediate Requirement", "", e -> send(false)));
        sendRequest.add(new Button("Reserve Items", "", e -> send(true)));
        buttonPanel.add(sendRequest);
        buttonPanel.add(new ConfirmButton("Foreclose", "close", e -> foreclose()));
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<=1"));
        buttonPanel.add(h);
        buttonPanel.add(new Button("⇰ Receive Screen", VaadinIcon.TRUCK, e -> receive()));
    }

    private void send(boolean reserve) {
        MR mr = selected();
        if(mr == null) {
            return;
        }
        if(mr.getStatus() > 1) {
            warning("Can't request again, status is already '" + mr.getStatusValue() + "'.");
            return;
        }
        if(mr.getStatus() == 1) {
            warning("Already sent");
            return;
        }
        if(!mr.existsLinks(mriClass)) {
            warning("No items to request");
            return;
        }
        if(reserve ? transact(mr::reserve) : transact(mr::request)) {
            refresh(mr);
            message("R" + (reserve ? "eservation r" : "") + "equest sent");
        }
    }

    private void foreclose() {
        MR mr = selected();
        if(mr == null) {
            return;
        }
        switch(mr.getStatus()) {
            case 3 -> {
                warning("Can't foreclose it, all items are already issued... Please receive items to close it.");
                return;
            }
            case 4 -> {
                warning("Already closed!");
                return;
            }
        }
        MaterialIssued mi = StoredObject.get(MaterialIssued.class, "Request=" + mr.getId() + " AND Status<2");
        if(transact(mr::foreclose)) {
            refresh(mr);
            String m = "Foreclosed";
            if(mi == null) {
                message(m);
            } else {
                message(m + ", however please receive items that were already issued!");
            }
        }
    }

    private void receive() {
        close();
        Class<? extends BaseReceiveMaterialRequested<MR, MRI>> logic = getReceiveLogic();
        if(logic == null) {
            new BaseReceiveMaterialRequested<>(getObjectClass(), getFromOrTo()).execute();
            return;
        }
        try {
            logic.getConstructor(InventoryLocation.class).newInstance(getFromOrTo()).execute();
        } catch(Throwable e) {
            error(e);
        }
    }

    protected Class<? extends BaseReceiveMaterialRequested<MR, MRI>> getReceiveLogic() {
        return null;
    }

    @Override
    public void doDelete(MR object) {
        List<MaterialIssued> mis = StoredObject.list(MaterialIssued.class, "Request=" + object.getId()).
                toList();
        if(mis.isEmpty()) {
            super.doDelete(object);
            return;
        }
        new MessageGrid<>(MaterialIssued.class, mis,
                StringList.create("Date", "Reference", "Location AS Issued from", "Status"),
                "Materials were issued via these. Deletion is possible only after closing and deleting these!").
                execute(this.getView());
    }

    @Override
    public void doEdit(MR mr) {
        clearAlerts();
        if(mr.getStatus() == 1) { // Initiated
            if(restrictedEditor == null) {
                restrictedEditor = createMREditor();
                if(restrictedEditor == null) {
                    restrictedEditor = new Editor();
                }
                restrictedEditor.addConstructedListener(f -> {
                    restrictedEditor.removeSetNotAllowed("ToLocation");
                    restrictedEditor.setFieldReadOnly("Items.l");
                });
                restrictedEditor.addObjectChangedListener(new ObjectChangedListener<>() {
                    @Override
                    public void updated(MR object) {
                        refresh(object);
                    }
                });
            }
            restrictedEditor.editObject(mr, getView(), false);
            return;
        }
        super.doEdit(mr);
    }

    private class Editor extends ObjectEditor<MR> {

        public Editor() {
            super(BaseRequestMaterial.this.getObjectClass(), EditorAction.EDIT);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if("ToLocation".equals(fieldName)) {
                return new ObjectField<>(label, LocationField.create(0));
            }
            return super.createField(fieldName, label);
        }
    }

    @Override
    protected Button getSwitchLocationButton() {
        return new Button("Change", (String) null, e -> new SwitchLocation().execute());
    }

    private class SwitchLocation extends DataForm {

        private final LocationField currentLoc = LocationField.create("Current Location", getFromOrTo());
        private final LocationField newLoc = LocationField.create("Change to", 0, 4, 5, 10, 11, 16);

        public SwitchLocation() {
            super("Change Location");
            addField(currentLoc, newLoc);
            setFieldReadOnly(currentLoc);
            setRequired(newLoc);
        }

        @Override
        protected boolean process() {
            close();
            InventoryLocation loc = newLoc.getValue();
            if(loc.getId().equals(currentLoc.getObjectId())) {
                message("Not changed!");
                return true;
            }
            message("Location changed to '" + loc.toDisplay() + "'");
            BaseRequestMaterial.this.close();
            try {
                BaseRequestMaterial<?, ?> exec = BaseRequestMaterial.this.getClass()
                        .getConstructor(InventoryLocation.class).newInstance(loc);
                if(BaseRequestMaterial.this.itemTypeClass != null) {
                    exec.itemTypeClass = BaseRequestMaterial.this.itemTypeClass;
                }
                exec.execute();
            } catch(Throwable e) {
                error(e);
            }
            return true;
        }
    }

    @Override
    protected void selectLocation() {
        new SelectLocation(0, 4, 5, 10, 11, 16).execute();
    }

    protected ObjectEditor<MR> createMREditor() {
        return null;
    }
}
