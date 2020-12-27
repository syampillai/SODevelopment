package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.vaadin.flow.component.HasValue;

import java.util.ArrayList;
import java.util.List;

public class RequestMaterial extends ObjectBrowser<MaterialRequest> {

    private final ObjectField<InventoryLocation> fromField, toField;
    private InventoryLocation from;
    private MaterialRequestPriority normalPriority;

    public RequestMaterial() {
        this((String) null);
    }

    public RequestMaterial(String from) {
        this(fromField(from));
    }

    public RequestMaterial(InventoryLocation from) {
        this(fromField(from));
    }

    private RequestMaterial(LocationField from) {
        super(MaterialRequest.class);
        fromField = new ObjectField<>("From", from);
        this.from = from.getValue();
        toField = new ObjectField<>("Send to", LocationField.create(0));
    }

    private static LocationField fromField(InventoryLocation from) {
        List<InventoryLocation> list = new ArrayList<>();
        list.add(from);
        return new LocationField(list);
    }

    private static LocationField fromField(String from) {
        if(from == null || from.isEmpty()) {
            throw new SORuntimeException("Location not specified!");
        }
        LocationField fromField = new LocationField(4, 5, 11);
        InventoryLocation fromLoc = fromField.getLocations().stream().
                filter(loc -> loc.getName().equalsIgnoreCase(from)).findFirst().orElse(null);
        if(fromLoc == null) {
            throw new SORuntimeException("Location not found - " + from);
        }
        return fromField(fromLoc);
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(new ELabel("From: ")
                .append(from.toDisplay(), "blue").update());
    }

    @Override
    public boolean canEdit(MaterialRequest object) {
        if(object.getStatus() > 0) {
            warning("Changes not possible with status = " + object.getStatusValue());
            return false;
        }
        return super.canEdit(object);
    }

    @Override
    public boolean canDelete(MaterialRequest object) {
        if(object.getStatus() == 1) {
            warning("Can't delete when status is '" + object.getStatusValue() + "'");
        }
        return super.canDelete(object);
    }

    @Override
    protected boolean canAdd() {
        if(!createPriority()) {
            return false;
        }
        return super.canAdd();
    }

    private boolean createPriority() {
        if(normalPriority != null) {
            return true;
        }
        normalPriority = StoredObject.get(MaterialRequestPriority.class, null, "Priority");
        if(normalPriority == null) {
            normalPriority = new MaterialRequestPriority();
            normalPriority.setName("Normal");
            normalPriority.setPriority(0);
            return transact(normalPriority::save);
        }
        return true;
    }

    @Override
    protected ObjectEditor<MaterialRequest> createObjectEditor() {
        createPriority();
        return new MREditor();
    }

    private class MREditor extends ObjectEditor<MaterialRequest> {

        public MREditor() {
            super(MaterialRequest.class);
            setNewObjectGenerator(() -> {
                MaterialRequest mr = new MaterialRequest();
                mr.setFromLocation(from);
                mr.setPriority(normalPriority);
                return mr;
            });
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            fromField.setValue(from);
            setFieldReadOnly(fromField);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName) {
            switch(fieldName) {
                case "FromLocation":
                    return fromField;
                case "ToLocation":
                    return toField;
            }
            return super.createField(fieldName);
        }

        @Override
        protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
            if("Items.l".equals(fieldName)) {
                //noinspection unchecked
                return new MRIGrid((ObjectLinkField<MaterialRequestItem>) field);
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        public void setObject(MaterialRequest object, boolean load) {
            super.setObject(object, load);
            if(object != null && !from.getId().equals(object.getFromLocationId())) {
                from = object.getFromLocation();
            }
        }
    }

    private static class MRIGrid extends DetailLinkGrid<MaterialRequestItem> {

        private QuantityField qField;
        private ObjectField<InventoryItemType> pnField;

        public MRIGrid(ObjectLinkField<MaterialRequestItem> linkField) {
            super(linkField);
        }

        @Override
        public ObjectEditor<MaterialRequestItem> constructObjectEditor() {
            return new MRIEditor();
        }

        private class MRIEditor extends ObjectEditor<MaterialRequestItem> {

            public MRIEditor() {
                super(MaterialRequestItem.class);
            }

            @Override
            protected void formConstructed() {
                super.formConstructed();
                qField = (QuantityField) getField("Requested");
                //noinspection unchecked
                pnField = (ObjectField<InventoryItemType>)getField("PartNumber");
                pnField.addValueChangeListener(e -> pnChanged());
            }

            private void pnChanged() {
                InventoryItemType pn = pnField.getObject();
                qField.setValue(pn.getUnitOfMeasurement());
            }
        }
    }
}
