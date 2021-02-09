package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Person;
import com.storedobject.vaadin.CloseableView;

public class Test extends ObjectGrid<Person> implements CloseableView {

    public Test() {
        super(Person.class, StringList.create("Title", "FirstName", "LastName", "DateOfBirth"));
        load();
    }

    @Override
    public String getColumnCaption(String columnName) {
        if(columnName.equals("Title")) {
            return "Long Title";
        }
        return super.getColumnCaption(columnName);
    }

    @Override
    public String getFixedColumnWidth(String columnName) {
        if("Title".equals(columnName)) {
            return "70px";
        }
        return null;
    }

    @Override
    public int getRelativeColumnWidth(String columnName) {
        switch(columnName) {
            case "FirstName":
            case "LastName":
                return 4;
        }
        return 1;
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return !"Title".equals(columnName);
    }
}
