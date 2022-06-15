package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApproveTransaction extends ObjectBrowser<PseudoTran> {

    private Button view, approve, delete, viewOld;
    private View tranView;

    public ApproveTransaction() {
        this(true);
    }

    public ApproveTransaction(int no) {
        this(false);
        load(null, no);
    }

    private ApproveTransaction(boolean load) {
        super(PseudoTran.class, EditorAction.RELOAD, "Approve Transactions");
        setFixedFilter("Status<3", false);
        setOrderBy("Date,No", false);
        if(load) {
            load();
        }
    }

    public void load(int no) {
        load(null, no);
    }

    public void load(Date date, int no) {
        if(no < 1) {
            return;
        }
        if(date == null) {
            date = DateUtility.today();
        }
        load("Date='" + DateUtility.format(date) + "' AND No=" + no);
    }

    @Override
    public void loaded() {
        clearAlerts();
        message("Entries: " + size());
    }

    @Override
    protected void createExtraButtons() {
        view = new Button("View", e -> view());
        approve = new ConfirmButton("Approve", e -> approve());
        delete = new ConfirmButton("Delete", e -> delete());
        viewOld = new Button("View Old", e -> viewOld());
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(view, approve, delete, viewOld);
    }

    private PseudoTran select() {
        PseudoTran pt = getSelected();
        if(pt == null) {
            message("Please select an entry first");
        }
        return pt;
    }

    private void approve() {
        PseudoTran pt = select();
        if(pt == null) {
            return;
        }
        try {
            pt.authorize(getTransactionManager());
            status(pt);
            refresh(pt);
        } catch (Exception e) {
            error(e);
            pt.reload();
        }
    }

    private void delete() {
        PseudoTran pt = select();
        if(pt == null) {
            return;
        }
        try {
            pt.delete(getTransactionManager());
            status(pt);
            refresh(pt);
        } catch (Exception e) {
            error(e);
            pt.reload();
        }
    }

    private void status(PseudoTran pt) {
        String m = "Transaction - " + pt.toDisplay() + ", Status: " + pt.getStatusValue();
        if(pt.getStatus()== 3) {
            message(m);
        } else {
            warning(m);
        }
    }

    private void view() {
        PseudoTran pt = select();
        if(pt == null) {
            return;
        }
        view(pt);
    }

    private void view(PseudoTran pt) {
        getObjectEditor().viewObject(pt);
    }

    private void viewOld() {
        if(tranView == null) {
            tranView = new View();
        }
        tranView.execute();
    }

    @Override
    protected ObjectEditor<PseudoTran> createObjectEditor() {
        return new PTEditor();
    }

    private static class PTEditor extends ObjectEditor<PseudoTran> {

        private List<PseudoTranDetail> details = new ArrayList<>();

        PTEditor() {
            super(PseudoTran.class);
            setColumns(4);
            addField("Menu", pt -> pt.getLogic().getTitle(), null);
        }

        @Override
        protected HasValue<?, ?> createField(String fieldName, String label) {
            if("Menu".equals(fieldName)) {
                return new TextField(label);
            }
            return super.createField(fieldName, label);
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Menu".equals(fieldName)) {
                setColumnSpan((Component) field, 4);
            }
        }

        @Override
        protected boolean includeField(String fieldName) {
            if("LogicCode".equals(fieldName)) {
                return false;
            }
            return super.includeField(fieldName);
        }

        @Override
        protected String getLabel(String fieldName) {
            if("Menu".equals(fieldName)) {
                return "Menu Used";
            }
            return super.getLabel(fieldName);
        }

        @Override
        protected int getFieldOrder(String fieldName) {
            if("Menu".equals(fieldName)) {
                return 2;
            }
            return super.getFieldOrder(fieldName);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
            if(fieldName.equals("Details.l")) {
                return new PTDetailBrowser((ObjectLinkField<PseudoTranDetail>) field);
            }
            return super.createLinkFieldGrid(fieldName, field);
        }

        @Override
        public void setObject(PseudoTran object, boolean load) {
            if(object != null && load) {
                try {
                    details = object.buildView(getTransactionManager());
                } catch(Exception e) {
                    details.clear();
                    warning(e);
                }
            }
            super.setObject(object, load);
        }

        private class PTDetailBrowser extends DetailLinkGrid<PseudoTranDetail> {

            private PseudoTranDetail current;

            @SuppressWarnings("unchecked")
            public PTDetailBrowser(ObjectLinkField<PseudoTranDetail> linkField) {
                super(linkField, StringList.create("Status", "ApplicableTo", "Changes"));
                createColumn("ApplicableTo", this::appTo);
                createColumn("Changes", this::changes);
            }

            @Override
            public ObjectEditor<PseudoTranDetail> constructObjectEditor() {
                return new PTDetailEditor();
            }

            private String appTo(PseudoTranDetail ptd) {
                return curr(ptd).getObjectLabel();
            }

            private String changes(PseudoTranDetail ptd) {
                return curr(ptd).getChanges();
            }

            private PseudoTranDetail curr(PseudoTranDetail ptd) {
                if(current == null || !current.getId().equals(ptd.getId())) {
                    Id id = ptd.getId();
                    current = details.stream().filter(p -> p.getId().equals(id)).findAny().orElse(null);
                    if(current == null) {
                        current = ptd;
                    }
                }
                return current;
            }

            private class PTDetailEditor extends ObjectEditor<PseudoTranDetail> {

                public PTDetailEditor() {
                    super(PseudoTranDetail.class);
                    addField("ApplicableTo", this::appTo, null);
                    addField("Changes", this::changes, null);
                }

                @Override
                protected HasValue<?, ?> createField(String fieldName, String label) {
                    return switch(fieldName) {
                        case "ApplicableTo" -> new TextField(label);
                        case "Changes" -> new TextArea("Data/Changes");
                        default -> super.createField(fieldName, label);
                    };
                }

                @Override
                protected void customizeField(String fieldName, HasValue<?, ?> field) {
                    if(fieldName.equals("Changes")) {
                        setColumnSpan((Component) field, 2);
                    }
                    super.customizeField(fieldName, field);
                }

                private String appTo(PseudoTranDetail ptd) {
                    return ptd == null ? "" : curr(ptd).getObjectLabel();
                }

                private String changes(PseudoTranDetail ptd) {
                    return ptd == null ? "" : curr(ptd).getChanges();
                }
            }
        }
    }

    private class View extends DataForm {

        private final DateField dateField = new DateField("Date");
        private final IntegerField noField = new IntegerField("No.");
        private final ELabel helpLabel = new ELabel();

        public View() {
            super("Select Transaction");
        }

        @Override
        public int getMaximumContentWidth() {
            return 25;
        }

        @Override
        protected void buildFields() {
            addField(dateField);
            addField(noField);
            add(helpLabel);
            trackValueChange(dateField);
        }

        @Override
        public void valueChanged(ChangedValues changedValues) {
            if(changedValues.getChanged() == dateField) {
                helpLabel.clear();
                helpLabel.update();
            }
        }

        @Override
        protected boolean process() {
            int no = noField.getValue();
            Date date = dateField.getValue();
            String condition = "Date='" + Database.format(date) + "'";
            if(no > 0) {
                PseudoTran pt = StoredObject.get(PseudoTran.class, condition + " AND No=" + no);
                if(pt != null) {
                    close();
                    view(pt);
                    return true;
                }
                warning("Transaction - Date: " + DateUtility.format(date) + ", No: " + no + " - not found!");
            }
            Query q = StoredObject.query(PseudoTran.class, "/Min(No),Max(No)", condition);
            ResultSet rs = q.getResultSet();
            helpLabel.clear();
            try {
                int min = rs.getInt(1), max = rs.getInt(2);
                if(max > 0) {
                    if(min == max) {
                        helpLabel.append("Only transaction available is No: " + min, Application.COLOR_SUCCESS);
                    } else {
                        helpLabel.append("Range for No: " + min + " - " + max, Application.COLOR_SUCCESS);
                    }
                } else {
                    helpLabel.append("No transactions found on this date!", Application.COLOR_ERROR);
                }
                helpLabel.update();
            } catch (SQLException e) {
                error(e);
            }
            return false;
        }
    }
}
