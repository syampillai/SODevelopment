package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;

public abstract class AbstractRequestMaterial extends ObjectBrowser<MaterialRequest> {

    static final int NO_ACTIONS = EditorAction.NEW | EditorAction.EDIT | EditorAction.DELETE;
    private final ObjectField<InventoryLocation> fromField, toField;
    private InventoryLocation fromOrTo;
    private MaterialRequestPriority normalPriority;
    private final boolean issuing;
    private final InventoryLocation otherLocation;
    Class<? extends InventoryItemType> itemTypeClass;
    private boolean searching = false;
    private Search search;
    private final ELabel searchLabel = new ELabel();
    private final ELabel countLabel = new ELabel("0");

    AbstractRequestMaterial(boolean issuing, int noActions) {
        this(issuing, noActions, null);
    }

    AbstractRequestMaterial(boolean issuing, int noActions, InventoryLocation otherLocation) {
        this(issuing, (String) null, noActions, otherLocation);
    }

    AbstractRequestMaterial(boolean issuing, String fromOrTo, int noActions) {
        this(issuing, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(boolean issuing, String fromOrTo, int noActions, InventoryLocation otherLocation) {
        this(issuing, -1, fromOrTo, noActions, otherLocation);
    }

    AbstractRequestMaterial(boolean issuing, InventoryLocation fromOrTo, int noActions) {
        this(issuing, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(boolean issuing, InventoryLocation fromOrTo, int noActions,
                            InventoryLocation otherLocation) {
        this(issuing, -1, fromOrTo, noActions, otherLocation);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, int noActions) {
        this(issuing, columnStyle, noActions, null);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, int noActions, InventoryLocation otherLocation) {
        this(issuing, columnStyle, (String) null, noActions, otherLocation);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, String fromOrTo, int noActions) {
        this(issuing, columnStyle, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, String fromOrTo, int noActions,
                                                        InventoryLocation otherLocation) {
        this(issuing, columnStyle, issuing ? LocationField.create(null, fromOrTo,0) :
                        LocationField.create(null, fromOrTo, 0, 4, 5, 10, 11, 16),
                noActions, otherLocation);
        this.itemTypeClass = ParameterParser.itemTypeClass(fromOrTo);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, InventoryLocation fromOrTo, int noActions) {
        this(issuing, columnStyle, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(boolean issuing, int columnStyle, InventoryLocation fromOrTo, int noActions,
                            InventoryLocation otherLocation) {
        this(issuing, columnStyle, LocationField.create(fromOrTo), noActions, otherLocation);
        if(this.fromOrTo.equals(otherLocation)) {
            throw new SORuntimeException("Both locations can't be the same - " + otherLocation.toDisplay());
        }
    }

    private AbstractRequestMaterial(boolean issuing, int columnStyle, LocationField fromOrTo, int noActions,
                                    InventoryLocation otherLocation) {
        super(MaterialRequest.class, columns(columnStyle, issuing), EditorAction.ALL & (~noActions),
                issuing ? StringList.create("Date") : StringList.create("Date", "ReferenceNumber"));
        addConstructedListener(v -> created());
        this.issuing = issuing;
        if(issuing) {
            fromField = new ObjectField<>("From", otherLocation == null ? LocationField.create(4, 5, 10, 11, 16)
                    : LocationField.create(otherLocation));
            toField = new ObjectField<>("Send to", fromOrTo);
        } else {
            fromField = new ObjectField<>("From", fromOrTo);
            toField = new ObjectField<>("Send to", otherLocation == null ? LocationField.create(0)
                    : LocationField.create(otherLocation));
        }
        this.fromOrTo = fromOrTo.getValue();
        if(this.fromOrTo == null) {
            throw new LogicRedirected(this::selectLocation);
        }
        if(issuing && !(this.fromOrTo instanceof InventoryStoreBin)) {
            throw new LogicRedirected(() -> new SelectStore().execute());
        }
        this.otherLocation = otherLocation;
        setOrderBy("Date DESC,No DESC", false);
    }

    @Override
    public boolean canSearch() {
        return false;
    }

    protected void selectLocation() {
    }

    @Override
    public String getMenuIconName() {
        return "vaadin:stock";
    }

    void created() {
    }

    private static StringList columns(int style, boolean issuing) {
        if(style == -1) {
            style = issuing ? 0 : 1;
        }
        return switch(style) {
            case 0 -> StringList.create("Date", "IssueReference", "Reference AS Request Reference", "Remarks",
                    "FromLocation AS Requested Location", "Person AS By", "Status", "Priority", "RequiredBefore");
            case 1 -> StringList.create("Date", "Reference", "ToLocation", "Status",
                    "Priority", "RequiredBefore");
            case 2 -> StringList.create("Date", "Reference", "ToLocation AS From", "Status",
                    "Priority", "RequiredBefore", "Received");
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
    public void loaded() {
        if(searching) {
            searching = false;
            setLoadFilter(null, false);
        } else {
            searchLabel.clearContent().update();
        }
        countLabel.clearContent().append("" + size(), Application.COLOR_SUCCESS).update();
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(new Button("Search", e -> searchFilter()));
    }

    private void searchFilter() {
        if(search == null) {
            search = new Search();
        }
        search.execute();
    }

    @Override
    public void createHeaders() {
        ELabel label = new ELabel(fromOrTo instanceof InventoryCustodyLocation ?
                "Custodian: " : "Current Location: ")
                .append(fromOrTo.toDisplay(), Application.COLOR_SUCCESS).update();
        Button locSwitch = getSwitchLocationButton();
        ButtonLayout buttonLayout = new ButtonLayout(label);
        if(locSwitch != null) {
            buttonLayout.add(locSwitch.asSmall());
        }
        buttonLayout.add(searchLabel, new ELabel("| ", Application.COLOR_INFO).append("Entries:").update(),
                countLabel);
        prependHeader().join().setComponent(buttonLayout);
    }

    protected Button getSwitchLocationButton() {
        return null;
    }

    @Override
    public boolean canEdit(MaterialRequest mr) {
        if(mr.getStatus() > 0) {
            warning("Changes not possible with status = " + mr.getStatusValue());
            return false;
        }
        return super.canEdit(mr);
    }

    @Override
    public boolean canDelete(MaterialRequest mr) {
        switch(mr.getStatus()) {
            case 0:
            case 4:
                break;
            default:
                warning("Can't delete when status is '" + mr.getStatusValue() + "'");
                return false;
        }
        return super.canDelete(mr);
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
                    mr.setSystemEntity(getTransactionManager().getEntity());
                    mr.setFromLocation(fromOrTo);
                    mr.setPriority(normalPriority);
                    if(otherLocation != null) {
                        mr.setToLocation(otherLocation);
                    }
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
                if(otherLocation != null) {
                    setFieldReadOnly(fromField);
                }
            } else {
                fromField.setValue(fromOrTo);
                setFieldReadOnly(fromField);
                if(otherLocation != null) {
                    setFieldReadOnly(toField);
                }
            }
            if(issuing) {
                setFieldHidden(toField);
                fromField.setLabel("Requested by");
            }
            setFieldVisible(TransactionManager.isMultiTenant(), getField("SystemEntity"));
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

    private class Search extends DataForm {

        private final ChoiceField search = new ChoiceField("Search",
                new String[] { "Part Number", "Date Period", "Request No." });
        private final ObjectGetField<InventoryItemType> pnField =
                new ObjectGetField<>("Part Number", InventoryItemType.class, true);
        private final DatePeriodField periodField = new DatePeriodField("Date Period");
        private final IntegerField noField = new IntegerField("Request No.");

        public Search() {
            super("Search");
            noField.setVisible(false);
            periodField.setVisible(false);
            search.addValueChangeListener(e -> vis());
            addField(search, pnField, periodField, noField);
        }

        private void vis() {
            int s = search.getValue();
            pnField.setVisible(s == 0);
            periodField.setVisible(s == 1);
            noField.setVisible(s == 2);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            vis();
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            AbstractRequestMaterial.this.clearAlerts();
            int s = search.getValue();
            searching = true;
            String filter = null;
            switch(s) {
                case 0 -> {
                    InventoryItemType pn = pnField.getValue();
                    if(pn == null) {
                        searching = false;
                        return true;
                    }
                    filter = "Contains " + pn.toDisplay();
                    Id pnId = pn.getId();
                    setLoadFilter(p -> p.existsLinks(MaterialRequestItem.class, "PartNumber=" + pnId, true));
                }
                case 1 -> {
                    DatePeriod period = periodField.getValue();
                    filter = "Period = " + period;
                    setLoadFilter(p -> period.inside(p.getDate()));
                }
                case 2 -> {
                    int no = noField.getValue();
                    if(no <= 0) {
                        searching = false;
                        return true;
                    }
                    filter = "No. = " + no;
                    setLoadFilter(p -> p.getNo() == no);
                }
            }
            if(filter != null) {
                searchLabel.clearContent().append(" | ", Application.COLOR_INFO)
                        .append(" Filter: ", Application.COLOR_ERROR)
                        .append(filter, Application.COLOR_INFO).update();
            }
            return true;
        }
    }
}
