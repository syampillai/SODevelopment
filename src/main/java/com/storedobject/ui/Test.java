package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.ui.common.EntityEditor;
import com.storedobject.vaadin.*;

public class Test extends DataForm implements FullScreen {

    private final TextField textField = new TextField();
    private final Clock clock = new Clock();

    public Test() {
        super("Test");
        setRequired(textField);
        add(clock);
        add(new Button("Local", (String) null, e -> clock.setUTC(false)));
        add(new Button("AM/PM", (String) null, e -> clock.setAMPM(true)));
        add(new Button("Person", (String) null, e -> person()));
        add(new Button("Info", (String) null, e -> info()));
        add(new Button("Entity", (String) null, e -> entity()));
    }

    @Override
    protected boolean process() {
        message(textField.getValue());
        return false;
    }

    private void person() {
        ObjectEditor<Person> pe = new ObjectEditor<>(Person.class);
        pe.setFullScreen(true);
        close();
        pe.execute();
    }

    private void entity() {
        new EntityEditor().execute();
    }

    private void info() {
        new InformationMessage("Hello").execute();
    }
}
