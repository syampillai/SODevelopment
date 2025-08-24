package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.Div;

public class Test extends View implements CloseableView {

    private final ObjectEditor<Person> editor = ObjectEditor.create(Person.class);
    private final PersonGrid grid = new PersonGrid();

    public Test() {
        super("Test");
        //ModelViewer modelViewer = new ModelViewer(MediaCSS.mediaURL("test-model"));
        editor.setEmbeddedView(this);
        ButtonLayout buttons = new ButtonLayout(new ELabel("Test"), new Button("Edit", e ->  edit()));
        FoldingLayout fl = new FoldingLayout(grid, (HasSize) editor.getComponent());
        fl.setProportionalWidths(3, 1);
        Div div = new Div(buttons, fl);
        setComponent(div);
    }

    private void edit() {
        Person person = grid.getSelected();
        if(person == null) {
            message("Please select a person");
            return;
        }
        editor.setObject(person);
        editor.doEdit();
    }

    public static class PersonGrid extends ListGrid<Person> {

        public PersonGrid() {
            super(Person.class);
            StoredObject.list(Person.class).forEach(this::add);
        }
    }
}