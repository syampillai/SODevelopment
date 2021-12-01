package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.vaadin.flow.component.HasValue;

public abstract class AbstractRequestMaterial extends ObjectBrowser<MaterialRequest> {

    static final int NO_ACTIONS = EditorAction.NEW | EditorAction.EDIT | EditorAction.DELETE;
    private final ObjectField<InventoryLocation> fromField, toField;
    private InventoryLocation fromOrTo;
    private MaterialRequestPriority normalPriority;
    private final boolean issuing;
    Class<? extends InventoryItemType> itemTypeClass;

    AbstractRequestMaterial(boolean issuing, int noActions) {
        this(issuing, (String) null, noActions);
    }

    AbstractRequestMaterial(boolean issuing, String fromOrTo, int noActions) {
        this(issuing, -1, fromOrTo, noActions);
    }

    AbstractRequestMaterial(boolean issuing, InventoryLocation fromOrTo, int noActions) {
        this(issuing, -1, fromOrTo, noActions);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, int noActions) {
        this(issuing, columnStyle, (String) null, noActions);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, String fromOrTo, int noActions) {
        this(issuing, columnStyle, issuing ? LocationField.create(null, fromOrTo,0) :
                LocationField.create(null, fromOrTo, 0, 4, 5, 10, 11, 16),
                noActions);
        this.itemTypeClass = ParameterParser.itemTypeClass(fromOrTo);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, InventoryLocation fromOrTo, int noActions) {
        this(issuing, columnStyle, LocationField.create(fromOrTo), noActions);
    }

    private AbstractRequestMaterial(boolean issuing, int columnStyle, LocationField fromOrTo, int noActions) {
        super(MaterialRequest.class, columns(columnStyle, issuing), EditorAction.ALL & (~noActions),
                issuing ? StringList.create("Date") : StringList.create("Date", "ReferenceNumber"));
        addConstructedListener(v -> created());
        this.issuing = issuing;
        if(issuing) {
            fromField = new ObjectField<>("From", LocationField.create(4, 5, 10, 11));
            toField = new ObjectField<>("Send to", fromOrTo);
        } else {
            fromField = new ObjectField<>("From", fromOrTo);
            toField = new ObjectField<>("Send to", LocationField.create(0));
        }
        this.fromOrTo = fromOrTo.getValue();
        if(issuing && !(this.fromOrTo instanceof InventoryStoreBin)) {
            throw new SORuntimeException("Not a store - " + this.fromOrTo.toDisplay());
        }
        setOrderBy("Date DESC,No DESC");
    }

    void created() {
    }

    private static StringList columns(int style, boolean issuing) {
        if(style == -1) {
            style = issuing ? 0 : 1;
        }
        return switch(style) {
            case 0 -> StringList.create("Date", "ReferenceNumber", "FromLocation AS Requested Location",
                    "Person AS By", "Status", "Priority", "RequiredBefore");
            case 1 -> StringList.create("Date", "ReferenceNumber", "ToLocation", "Status", "Priority",
                    "RequiredBefore");
            case 2 -> StringList.create("Date", "ReferenceNumber", "ToLocation AS From", "Status", "Priority",
                    "RequiredBefore", "Received");
            default -> columns(0, false);
        };
    }

    @Override
    public void setFixedFilter(String fixedFilter) {
        String f = getFixedSide() + "Location=" + fromOrTo.getId();
        if(fixedFilter != null) {
            f += " AND (" + fixedFilter + ")";
        }
        super.setFixedFilter(f);
    }

    InventoryLocation getFromOrTo() {
        return fromOrTo;
    }

    abstract String getFixedSide();

    @Override
    public void constructed() {
        super.constructed();
        setCaption(getCaption());
    }

    @Override
    public void createHeaders() {
        prependHeader().join().setComponent(new ELabel(fromOrTo instanceof InventoryCustodyLocation ?
                "Custodian: " : "Current Location: ")
                .append(fromOrTo.toDisplay(), "blue").update());
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
        switch(object.getStatus()) {
            case 0:
            case 4:
                break;
            default:
                warning("Can't delete when status is '" + object.getStatusValue() + "'");
                return false;
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
            if(issuing) {
                setNewObjectGenerator(() -> null);
            } else {
                setNewObjectGenerator(() -> {
                    MaterialRequest mr = new MaterialRequest();
                    mr.setFromLocation(fromOrTo);
                    mr.setPriority(normalPriority);
                    return mr;
                });
            }
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            if(issuing) {
                toField.setValue(fromOrTo);
                setFieldReadOnly(toField);
            } else {
                fromField.setValue(fromOrTo);
                setFieldReadOnly(fromField);
            }
            if(issuing) {
                setFieldHidden(toField);
                fromField.setLabel("Requested by");
            }
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName) {
            return switch(fieldName) {
                case "FromLocation" -> fromField;
                case "ToLocation" -> toField;
                default -> super.createField(fieldName);
            };
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
            if(issuing) {
                if(object != null && !fromOrTo.getId().equals(object.getToLocationId())) {
                    fromOrTo = object.getToLocation();
                }
            } else {
                if(object != null && !fromOrTo.getId().equals(object.getFromLocationId())) {
                    fromOrTo = object.getFromLocation();
                }
            }
        }
    }

    private class MRIGrid extends DetailLinkGrid<MaterialRequestItem> {

        private QuantityField qField;
        private ObjectField<? extends InventoryItemType> pnField;

        public MRIGrid(ObjectLinkField<MaterialRequestItem> linkField) {
            super(linkField, StringList.create("Item", "PartNumber", "Requested", "Issued", "Balance"));
        }

        public String getItem(MaterialRequestItem mri) {
            return mri.getPartNumber().getName();
        }

        @SuppressWarnings("unused")
        public String getPartNumber(MaterialRequestItem mri) {
            return mri.getPartNumber().getPartNumber();
        }

        @Override
        public ObjectEditor<MaterialRequestItem> constructObjectEditor() {
            return new MRIEditor();
        }

        @SuppressWarnings("unused")
        public Quantity getBalance(MaterialRequestItem mri) {
            return mri.getRequested().subtract(mri.getIssued());
        }

        private class MRIEditor extends ObjectEditor<MaterialRequestItem> {

            public MRIEditor() {
                super(MaterialRequestItem.class);
            }

            @Override
            protected HasValue<?, ?> createField(String fieldName, String label) {
                if("PartNumber".equals(fieldName)) {
                    pnField = new ObjectField<>(label, iTypeClass(), true);
                    return pnField;
                }
                return super.createField(fieldName, label);
            }

            @Override
            protected void formConstructed() {
                super.formConstructed();
                qField = (QuantityField) getField("Requested");
                pnField.addValueChangeListener(e -> pnChanged());
            }

            private void pnChanged() {
                InventoryItemType pn = pnField.getObject();
                qField.setValue(pn == null ? Count.ZERO : pn.getUnitOfIssue());
            }
        }
    }

    private Class<? extends InventoryItemType> iTypeClass() {
        Class<? extends InventoryItemType> itc = itemTypeClass();
        if(itc == null) {
            itc = InventoryItemType.class;
        }
        return itc;
    }

    protected Class<? extends InventoryItemType> itemTypeClass() {
        return itemTypeClass;
    }
}
