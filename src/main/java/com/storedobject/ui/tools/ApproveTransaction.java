package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.vaadin.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        setFilter("status < 3");
        setOrderBy("Date,No");
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
        super.loaded();
        Application.warning("Entries: " + size());
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

    private class View extends DataForm {

        private final DateField dateField = new DateField("Date");
        private final IntegerField noField = new IntegerField("No.");
        private final ELabel helpLabel = new ELabel();

        public View() {
            super("Select Transaction");
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
                    helpLabel.append("Range for No: " + min + " - " + max, "blue");
                } else {
                    helpLabel.append("No transactions found on this date!", "red");
                }
                helpLabel.update();
            } catch (SQLException e) {
                error(e);
            }
            return false;
        }
    }
}
