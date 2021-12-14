package com.storedobject.ui;

import com.storedobject.core.Entity;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public class Test extends View {

    @SuppressWarnings("rawtypes")
    private final GenericListEditor genericListEditor;
    private final ObjectEditor<Person> personObjectEditor;
    private final ObjectEditor<Entity> entityObjectEditor;

    public Test() {
        setCaption("TestObjectListEditor");

        // Parent Container
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();

        // ObjectListEditor
        genericListEditor = new GenericListEditor<>(StoredObject.class);
        genericListEditor.setAllRowsVisible(true);

        // ObjectEditors
        personObjectEditor = ObjectEditor.create(Person.class);
        entityObjectEditor = ObjectEditor.create(Entity.class);

        // Add
        container.add(genericListEditor);
        container.add(new H2("Person Editor"));
        container.add(personObjectEditor.getComponent());
        container.add(new H2("Entity Editor"));
        container.add(entityObjectEditor.getComponent());

        setComponent(container);
    }

    class GenericListEditor<T extends StoredObject> extends ObjectListEditor<T> {

        public GenericListEditor(Class<T> clazz) {
            super(clazz, Arrays.asList("Name", "Type"), true);
            this.add.setText("Add Person");
            this.add.setIcon(VaadinIcon.USER);

            this.edit.setText("Add Entity");
            this.edit.setIcon(VaadinIcon.OFFICE);
        }

        public String getName(StoredObject so) {
            if (so instanceof Person person) {
                return person.getName();
            }
            if (so instanceof Entity entity) {
                return entity.getName();
            }
            return "No Data";
        }

        public String getType(StoredObject so) {
            return so.getClass().getSimpleName();
        }

        @Override
        public void add() {
            // Adding a new core.Person
            // Launch ObjectEditor<Person>
            Person person = new Person();
            editItem(person);
        }

        @Override
        public void edit() {
            // Adding a new core.Entity
            // Launch ObjectEditor<Entity>
            Entity entity = new Entity();
            editItem(entity);
        }

        @Override
        public boolean editItem(StoredObject item) {
            try {
                if (item instanceof Person person) {
                    //noinspection unchecked
                    genericListEditor.setExternalEditor(personObjectEditor);
                    personObjectEditor.setObject(person, true);
                    personObjectEditor.doEdit();
                } else if (item instanceof Entity entity) {
                    //noinspection unchecked
                    genericListEditor.setExternalEditor(entityObjectEditor);
                    entityObjectEditor.setObject(entity, true);
                    entityObjectEditor.doEdit();
                }
            } catch (Exception e) {
                log(StringUtility.getTrace(e));
                return false;
            }
            return true;
        }
    }
}
