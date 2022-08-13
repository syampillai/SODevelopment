package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Person;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.stefan.table.*;

public class Test extends View {

    public Test() {
        super("Test");
        Table table = new Table();
        table.load();
        setComponent(new Div(table));
    }

    private class Table extends ObjectTable<Person> {

        public Table() {
            super(Person.class, StringList.create("FirstName", "DateOfBirth", "Age"));
            TableRow hr = getHead().insertRow(0);
            TableHeaderCell c = hr.addHeaderCell();
            c.setColSpan(getColumnCount());
            c.add(new ButtonLayout(new Button("Exit", e -> close())));
            load();
        }

        @Override
        protected void rowAdded(Person object) {
            TableDataCell c = addRow().addDataCell();
            c.add("Added " + object.getName());
            c.setColSpan(getColumnCount());
        }

        @Override
        protected void customizeCell(String columnName, Person object, TableDataCell cell) {
            if("DateOfBirth".equals(columnName)) {
                cell.getStyle().set("background", "yellow");
            }
        }
    }
}
