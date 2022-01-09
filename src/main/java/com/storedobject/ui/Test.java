package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Person;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.data.renderer.LitRenderer;

public class Test extends ObjectGrid<Person> implements CloseableView {

    public Test() {
        super(Person.class, StringList.create("X", "FirstName", "LastName", "DateOfBirth", "Age"));
        String template = "<vaadin-icon icon='vaadin:<1>' " +
                "@click='${myClick}' " +
                "style='color:<2>;cursor:pointer;width:12px;height:12px'>" +
                "<3>" +
                "</vaadin-icon>";
        //noinspection unchecked
        createColumn("X", template,
                p -> p.getGender() == 1 ? "close" : "check",
                p -> p.getGender() == 1 ? "blue" : "red",
                Person::getGenderValue);
        load();
    }

    @Override
    public void customizeRenderer(String columnName, LitRenderer<Person> renderer) {
        if("X".equals(columnName)) {
            renderer.withFunction("myClick", p -> System.err.println("Clicked: " + p));
        }
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("X".equals(columnName)) {
            return ""; // We don't want column caption for this column
        }
        return super.getColumnCaption(columnName);
    }
}
