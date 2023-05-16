package com.storedobject.ui.inventory;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectListEditor;
import com.storedobject.ui.QuantityField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReturnMaterial<M extends MaterialReturned, L extends MaterialReturnedItem> extends
        AbstractSendAndReceiveMaterial<M, L> {

    private FromGRN fromGRN;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private CreateFromGRN createFromGRN;

    public AbstractReturnMaterial(Class<M> mrClass, Class<L> mriClass, String fromLocation) {
        super(mrClass, mriClass, fromLocation, false);
    }

    public AbstractReturnMaterial(Class<M> mrClass, Class<L> mriClass, InventoryLocation fromLocation) {
        this(mrClass, mriClass, fromLocation, null);
    }

    public AbstractReturnMaterial(Class<M> mrClass, Class<L> mriClass, InventoryLocation fromLocation, InventoryLocation otherLocation) {
        super(mrClass, mriClass, fromLocation, false, otherLocation);
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        Checkbox h = new Checkbox("Include History");
        h.addValueChangeListener(e -> setFixedFilter(e.getValue() ? null : "Status<2"));
        buttonPanel.add(h);
        setFixedFilter("Status<2");
        InventoryLocation from = getLocationFrom(), to = getLocationTo();
        if(from != null && to != null) {
            if(from.getType() == 0 && to.getType() == 1) {
                setCaption("Purchase Return");
                fromGRN = new FromGRN(from, to);
            }
        }
    }

    @Override
    public void doAdd() {
        if(fromGRN == null) {
            superAdd();
            return;
        }
        fromGRN.execute();
    }

    private void superAdd() {
        super.doAdd();
    }

    private class FromGRN extends DataForm {

        private final InventoryLocation from, to;
        private final RadioChoiceField choice = new RadioChoiceField("Create",
                StringList.create("From an active GRN", "From recent GRNs", "New"));
        private final DateField grnDate = new DateField("From Date");

        FromGRN(InventoryLocation from, InventoryLocation to) {
            super("New Purchase Return");
            this.from = from;
            this.to = to;
            addField(choice, grnDate);
            grnDate.setValue(DateUtility.addMonth(DateUtility.today(), -1));
            grnDate.setVisible(false);
            choice.addValueChangeListener(e -> {
                if(e.getValue() == 1) {
                    grnDate.setVisible(true);
                    grnDate.focus();
                } else {
                    grnDate.setVisible(false);
                }
            });
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            choice.setValue(0);
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            clearAlerts();
            int selected = choice.getValue();
            if(selected == 2) {
                close();
                superAdd();
                return true;
            }
            StringBuilder s = new StringBuilder("Status");
            s.append(selected == 0 ? "=1" : ">=1");
            s.append(" AND Store=").append(((InventoryBin)from).getStoreId()).append(" AND Supplier=")
                    .append(to.getEntityId());
            if(selected == 1) {
                s.append(" AND Date>='").append(Database.format(grnDate.getValue())).append('\'');
            }
            List<InventoryGRN> grns = StoredObject.list(InventoryGRN.class, s.toString()).toList();
            if(grns.isEmpty()) {
                s = new StringBuilder("No ");
                if(selected == 0) {
                    s.append("active GRNs");
                } else {
                    s.append("recent GRNs dated on or after ").append(DateUtility.format(grnDate.getValue()));
                }
                s.append(" found for this supplier");
                message(s);
                return false;
            }
            close();
            if(grns.size() == 1) {
                createFrom(grns.get(0));
            } else {
                SelectGrid<InventoryGRN> select;
                select = new SelectGrid<>(InventoryGRN.class, grns,
                        StringList.create("Date", "Reference"), AbstractReturnMaterial.this::createFrom);
                select.setCaption("Select a GRN");
                select.execute();
            }
            return true;
        }
    }

    private void createFrom(InventoryGRN grn) {
        if(createFromGRN == null) {
            createFromGRN = new CreateFromGRN();
        }
        createFromGRN.setGrn(grn);
    }

    @Override
    public void clearAlerts() {
        super.clearAlerts();
        if(fromGRN != null) {
            fromGRN.clearAlerts();
        }
        if(createFromGRN != null) {
            createFromGRN.clearAlerts();
        }
    }

    private class CreateFromGRN extends ObjectListEditor<MaterialReturnedItem> {

        private InventoryGRN grn;
        private M mr;
        private final Map<Id, InventoryGRNItem> grnItems = new HashMap<>();

        CreateFromGRN() {
            super(MaterialReturnedItem.class);
            buttonPanel.add(new Button("Exit", e -> exit()).asSmall());
            //noinspection unchecked
            createColumn("Procured", mri -> grnItems.get(mri.getId()).getQuantity());
            setCaption("Purchase Return (From GRN)");
            setAllowAdd(false);
            setAllowView(false);
            setAllowReload(false);
            setAllowReloadAll(false);
        }

        @Override
        public Component createHeader() {
            return new ELabel("Please input quantity for the items to return", Application.COLOR_SUCCESS);
        }

        @Override
        public String getColumnCaption(String columnName) {
            if("Quantity".equals(columnName)) {
                return "Quantity to Return";
            }
            return super.getColumnCaption(columnName);
        }

        @Override
        public boolean isColumnEditable(String columnName) {
            return "Quantity".equals(columnName);
        }

        @Override
        public boolean editItem(MaterialReturnedItem mri) {
            if(super.editItem(mri)) {
                ((QuantityField)getField("Quantity"))
                        .setMaximumAllowed(grnItems.get(mri.getId()).getQuantity());
                return true;
            }
            return false;
        }

        void setGrn(InventoryGRN grn) {
            this.grn = grn;
            clear();
            grnItems.clear();
            grn.listLinks(InventoryGRNItem.class).forEach(gi -> {
                InventoryItem ii = gi.getItem();
                if(ii != null) {
                    L mri;
                    try {
                        mri = getItemClass().getDeclaredConstructor().newInstance();
                        mri.setItem(ii);
                        if(ii.isServiceable()) {
                            mri.setQuantity(gi.getQuantity().zero());
                        } else {
                            mri.setQuantity(gi.getQuantity());
                        }
                        mri.makeVirtual();
                        grnItems.put(mri.getId(), gi);
                        add(mri);
                    } catch(Throwable e) {
                        error(e);
                    }
                }
            });
            execute();
        }

        private void exit() {
            if(isSavePending()) {
                new ActionForm("Changes were not saved.\nDo you really want to exit?", this::close).execute();
            } else {
                close();
            }
        }

        @Override
        protected void aboutToSave(Transaction transaction) throws Exception {
            mr = AbstractReturnMaterial.this.getObjectClass().getDeclaredConstructor().newInstance();
            mr.setFromLocation(getLocationFrom());
            mr.setToLocation(getLocationTo());
            mr.setRemark("From " + grn.getReference());
            mr.save(transaction);
        }

        @Override
        protected void saved(Transaction transaction, MaterialReturnedItem mri) throws Exception {
            mr.addLink(transaction, mri);
        }

        @Override
        protected boolean skipSave(Transaction transaction, MaterialReturnedItem mri) {
            return mri.getQuantity().isZero();
        }

        @Override
        public void validateData() throws Exception {
            if(removeIf(mri -> mri.getQuantity().isZero())) {
                String m = "Zero quantity items are removed. ";
                if(isEmpty()) {
                    throw new SOException(m + "No items to save.");
                }
                throw new SOException(m + "Please check and click the \"Save Changes\" button again.");
            }
            super.validateData();
        }

        @Override
        protected void saveCompleted() {
            close();
            AbstractReturnMaterial.this.reload();
            AbstractReturnMaterial.this.select(mr);
            AbstractReturnMaterial.this.doEdit(mr);
        }
    }
}
