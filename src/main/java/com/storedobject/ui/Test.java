package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.JavaClass;
import com.storedobject.core.Logic;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.HomeView;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<JavaClass> implements HomeView {

    private final ButtonLayout buttonLayout = new ButtonLayout();
    private final Button viewSource;
    private final Button run;
    private ObjectEditor<JavaClass> objectEditor;
    private final GridSearchField<JavaClass> searchField;

    public Test() {
        super(JavaClass.class, StringList.create("Notes as Examples"));
        viewSource = new Button("View Source", this);
        run = new Button("Run", this);
        searchField = new GridSearchField<>(this);
        searchField.configure(
                this::getKeywords); // I want it to search only in the String returned by getKeywords(...)
        searchField.setWidthFull();
        buttonLayout.add(searchField, viewSource, run);
        setFilter("NOT Generated");
        load();
    }

    @Override
    public void constructed() {
        getView(true).setFirstFocus(searchField);
    }

    public String getNotes(JavaClass jc) {
        String notes = jc.getNotes();
        if (notes.isEmpty()) {
            notes = jc.getName();
        } else {
            if (notes.toLowerCase().startsWith("keyword")) {
                int p = notes.indexOf('\n');
                if (p > 0) {
                    notes = notes.substring(p + 1);
                }
            }
        }
        return notes;
    }

    private String getKeywords(JavaClass jc) {
        String notes = jc.getNotes();
        if (notes.isEmpty()) {
            notes = jc.getName();
        } else {
            if (notes.toLowerCase().startsWith("keyword")) {
                int p = notes.indexOf('\n');
                if (p > 0 && notes.length() > 7) {
                    notes = notes.substring(7, p);
                }
            }
        }
        return notes;
    }

    @Override
    public Component createHeader() {
        return buttonLayout;
    }

    @Override
    public void clicked(Component c) {
        JavaClass jc = getSelected();
        if (jc == null) {
            warning("Please select an example!");
            return;
        }
        if (c == viewSource) {
            if (objectEditor == null) {
                objectEditor = ObjectEditor.create(JavaClass.class);
            }
            objectEditor.viewObject(jc);
            return;
        }
        if (c == run) {
            run(jc);
        }
    }

    private void run(JavaClass jc) {
        if (jc.getName().equals(getClass().getName())) {
            return;
        }
        Logic logic = new Logic();
        logic.setClassName(jc.getName());
        logic.setTitle("Example");
        getApplication().getServer().execute(logic);
    }
}
