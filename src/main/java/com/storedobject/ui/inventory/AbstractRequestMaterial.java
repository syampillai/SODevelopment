package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;

public abstract class AbstractRequestMaterial<MR extends MaterialRequest, MRI extends MaterialRequestItem>
        extends ObjectBrowser<MR> {

    private static final int NO_ACTIONS = EditorAction.NEW | EditorAction.EDIT | EditorAction.DELETE;
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
    final Class<MRI> mriClass;

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, String fromOrTo) {
        this(objectClass, issuing, fromOrTo, NO_ACTIONS);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, String fromOrTo, int noActions) {
        this(objectClass, issuing, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, String fromOrTo, int noActions, InventoryLocation otherLocation) {
        this(objectClass, issuing, -1, fromOrTo, noActions, otherLocation);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, InventoryLocation fromOrTo) {
        this(objectClass, issuing, fromOrTo, NO_ACTIONS);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, InventoryLocation fromOrTo, int noActions) {
        this(objectClass, issuing, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, InventoryLocation fromOrTo, int noActions,
                            InventoryLocation otherLocation) {
        this(objectClass, issuing, -1, fromOrTo, noActions, otherLocation);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, int columnStyle, String fromOrTo) {
        this(objectClass, issuing, columnStyle, fromOrTo, NO_ACTIONS);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, int columnStyle, String fromOrTo, int noActions) {
        this(objectClass, issuing, columnStyle, fromOrTo, noActions, null);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, int columnStyle, String fromOrTo, int noActions,
                            InventoryLocation otherLocation) {
        this(objectClass, issuing, columnStyle, issuing ? LocationField.create(null, fromOrTo,0) :
                        LocationField.create(null, fromOrTo, 0, 4, 5, 10, 11, 16),
                noActions, otherLocation);
        this.itemTypeClass = ParameterParser.itemTypeClass(fromOrTo);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, int columnStyle, InventoryLocation fromOrTo) {
        this(objectClass, issuing, columnStyle, fromOrTo, NO_ACTIONS, null);
    }

    AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, int columnStyle, InventoryLocation fromOrTo,
                            int noActions, InventoryLocation otherLocation) {
        this(objectClass, issuing, columnStyle, LocationField.create(fromOrTo), noActions, otherLocation);
        if(this.fromOrTo.equals(otherLocation)) {
            throw new SORuntimeException("Both locations can't be the same - " + otherLocation.toDisplay());
        }
    }

    private AbstractRequestMaterial(Class<MR> objectClass, boolean issuing, int columnStyle, LocationField fromOrTo,
                                    int noActions, InventoryLocation otherLocation) {
        super(objectClass, columns(objectClass, columnStyle, issuing), EditorAction.ALL & (~noActions),
                issuing ? StringList.create("Date") : StringList.create("Date", "ReferenceNumber"));
        Class<MRI> iClass;
        try {
            //noinspection unchecked
            iClass = (Class<MRI>) JavaClassLoader.getLogic(objectClass.getName() + "Item");
        } catch(ClassNotFoundException e) {
            iClass = null;
        }
        this.mriClass = iClass;
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

    private static StringList columns(Class<? extends StoredObject> mrClass, int style, boolean issuing) {
        if(style == -1) {
            style = issuing ? 0 : 1;
        }
        StringList bc = StoredObjectUtility.browseColumns(mrClass);
        bc = bc.minus(StringList.create("Date", "Reference", "Remarks", "FromLocation", "ToLocation", "Status",
                "Priority", "RequiredBefore", "Received"));
        return switch(style) {
            case 0 -> StringList.create("Date", "IssueReference", "Reference AS Request Reference", "Remarks",
                    "FromLocation AS Requested Location", "Person AS By", "Status", "Priority", "RequiredBefore")
                    .concat(bc);
            case 1 -> StringList.create("Date", "Reference", "ToLocation", "Status", "Priority", "RequiredBefore")
                    .concat(bc);
            case 2 -> StringList.create("Date", "Reference", "ToLocation AS From", "Status", "Priority",
                    "RequiredBefore", "Received").concat(bc);
            default -> columns(mrClass, 0, false);
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
        countLabel.clearContent().append(String.valueOf(size()), Application.COLOR_SUCCESS).update();
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
    public boolean canEdit(MR mr) {
        if(mr.getStatus() > 0) {
            warning("Changes not possible with status = " + mr.getStatusValue());
            return false;
        }
        return super.canEdit(mr);
    }

    @Override
    public boolean canDelete(MR mr) {
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
    protected ObjectEditor<MR> createObjectEditor() {
        createPriority();
        return new MREditor();
    }

    private class MREditor extends ObjectEditor<MR> {

        public MREditor() {
            super(AbstractRequestMaterial.this.getObjectClass());
            if(issuing) {
                setNewObjectGenerator(() -> null);
            } else {
                setNewObjectGenerator(() -> {
                    MR mr = createNewInstance();
                    if(mr == null) {
                        return null;
                    }
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
                return new MRIGrid((ObjectLinkField<MRI>) field);
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        public void setObject(MR object, boolean load) {
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

    private class MRIGrid extends DetailLinkGrid<MRI> {

        private QuantityField qField;
        private ObjectField<? extends InventoryItemType> pnField;

        public MRIGrid(ObjectLinkField<MRI> linkField) {
            super(linkField, StringList.create("Item", "PartNumber", "Requested", "Issued", "Balance"));
        }

        @SuppressWarnings("unused")
        public String getItem(MRI mri) {
            return mri.getPartNumber().getName();
        }

        @SuppressWarnings("unused")
        public String getPartNumber(MRI mri) {
            return mri.getPartNumber().getPartNumber();
        }

        @Override
        public ObjectEditor<MRI> constructObjectEditor() {
            return new MRIEditor();
        }

        @SuppressWarnings("unused")
        public Quantity getBalance(MRI mri) {
            return mri.getRequested().subtract(mri.getIssued());
        }

        private class MRIEditor extends ObjectEditor<MRI> {

            public MRIEditor() {
                super(mriClass);
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
                    setLoadFilter(p -> p.existsLinks(mriClass, "PartNumber=" + pnId));
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
