package com.storedobject.ui.common;

import com.storedobject.core.AbstractCheckList;
import com.storedobject.core.DateUtility;
import com.storedobject.ui.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.GridLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.checkbox.Checkbox;

import java.sql.Date;
import java.util.ArrayList;

public class ProcessCheckList extends DataForm implements Transactional {

    private final AbstractCheckList checkList;
    private final ArrayList<Object[]> items = new ArrayList<>();

    public ProcessCheckList(AbstractCheckList checkList) {
        super("Check List - " + checkList.getName());
        this.checkList = checkList;
        setScrollable(true);
    }

    @Override
    protected HasComponents createFieldContainer() {
        return new GridLayout(3);
    }

    @Override
    protected void buildFields() {
        createField(checkList, 0);
    }

    private void createField(AbstractCheckList node, int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf((char) 187).repeat(Math.max(0, level)));
        if(sb.length() > 0) {
            sb.append(' ');
        }
        ELabel label = new ELabel(sb + node.getName());
        DateField df = new DateField();
        df.setValue(node.getCompleted() ? node.getCompletedOn() : DateUtility.today());
        Checkbox cb = new Checkbox();
        cb.setValue(node.getCompleted());
        items.add(new Object[] { node, df, cb });
        add(label);
        addField(df);
        addField(cb);
        node.listItems().forEach(n -> {
            df.setReadOnly(true);
            cb.setReadOnly(true);
            createField(n, level + 1);
        });
    }

    @Override
    protected boolean process() {
        ArrayList<AbstractCheckList> modified = new ArrayList<>();
        AbstractCheckList node;
        Date date;
        boolean completed;
        for(Object[] row: items) {
            if(((DateField)row[1]).isReadOnly()) {
                continue;
            }
            node = (AbstractCheckList) row[0];
            completed = ((Checkbox) row[2]).getValue();
            date = ((DateField)row[1]).getValue();
            if(node.getCompleted() == completed && date.compareTo(node.getCompletedOn()) == 0) {
                continue;
            }
            node.setCompletedOn(date);
            node.setCompleted(completed);
            modified.add(node);
        }
        if(modified.isEmpty()) {
            message("No changes done");
            return true;
        }
        completed = transact(t -> {
            for(AbstractCheckList m: modified) {
                m.save(t);
            }
        });
        if(!completed) {
            return true;
        }
        //noinspection SuspiciousMethodCalls
        items.removeIf(r -> modified.contains(r[0]));
        modified.clear();
        items.removeIf(r -> {
            AbstractCheckList item = (AbstractCheckList) r[0];
            if(item.getCompleted() == item.checkCompleteness()) {
                return true;
            }
            modified.add(item);
            return false;
        });
        transact(t -> {
            for(AbstractCheckList m: modified) {
                m.save(t);
            }
        });
        return true;
    }
}