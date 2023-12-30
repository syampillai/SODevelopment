package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.Person;
import com.storedobject.ui.DataEditor;
import com.storedobject.ui.DataGrid;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

public class ManageSalutation extends DataGrid<ManageSalutation.Salutation> {

    private Editor editor;

    public ManageSalutation() {
        super(Salutation.class, StringList.create("Salutation", "Male", "Female", "Transgender"));
        reload();
        setCaption("Manage Salutations");
    }

    private void reload() {
        clear();
        String[] titles = Person.getTitleValues();
        for(int i = 0; i < titles.length; i++) {
            add(new Salutation(i, titles[i], Person.isMaleAllowed(i), Person.isFemaleAllowed(i),
                    Person.isTransgenderAllowed(i)));
        }
    }

    @Override
    public Component createHeader() {
        return new ButtonLayout(
                new Button("Add New", e -> addNew()),
                new Button("Edit", e -> edit()),
                new Button("Exit", e -> close())
        );
    }

    private void addNew() {
        clearAlerts();
        if(editor == null) {
            editor = new Editor();
        }
        editor.add = true;
        editor.setObject(new Salutation());
        editor.execute(getView());
    }

    private void edit() {
        Salutation s = selected();
        if(s == null) {
            return;
        }
        if(s.code <= 7) {
            warning("Not editable");
            return;
        }
        clearAlerts();
        if(editor == null) {
            editor = new Editor();
        }
        editor.add = false;
        editor.setObject(s);
        editor.execute(getView());
    }

    private class Editor extends DataEditor<Salutation> {

        private boolean add;

        public Editor() {
            super(Salutation.class, "Edit Salutation");
            setWindowMode(true);
        }

        @Override
        public int getFieldOrder(String columnName) {
            return columnName.equals("Salutation") ? 0 : 100;
        }

        @Override
        protected void save() {
            super.save();
            Salutation s = getObject();
            try {
                if(add) {
                    Person.addSalutation(getTransactionManager(), s.salutation, s.male, s.female, s.transgender);
                } else {
                    Person.updateSalutation(getTransactionManager(), s.code, s.salutation, s.male, s.female, s.transgender);
                }
            } catch(Exception e) {
                ManageSalutation.this.warning(e);
            } finally {
                reload();
            }
        }
    }

    public static final class Salutation {

        private final int code;
        private String salutation;
        private boolean male;
        private boolean female;
        private boolean transgender;

        public Salutation() {
            this(Person.getTitleValues().length, "New", true, true, true);
        }

        private Salutation(int code, String salutation, boolean male, boolean female, boolean transgender) {
            this.code = code;
            this.salutation = salutation;
            this.male = male;
            this.female = female;
            this.transgender = transgender;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Salutation) obj;
            return this.code == that.code;
        }

        @Override
        public int hashCode() {
            return code;
        }

        public String getSalutation() {
            return salutation;
        }

        public void setSalutation(String salutation) {
            this.salutation = salutation;
        }

        public boolean getMale() {
            return male;
        }

        public void setMale(boolean male) {
            this.male = male;
        }

        public boolean getFemale() {
            return female;
        }

        public void setFemale(boolean female) {
            this.female = female;
        }

        public boolean getTransgender() {
            return transgender;
        }

        public void setTransgender(boolean transgender) {
            this.transgender = transgender;
        }

        @Override
        public String toString() {
            return salutation;
        }
    }
}
