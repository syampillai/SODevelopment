package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.Person;
import com.storedobject.ui.DataEditor;
import com.storedobject.ui.DataGrid;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

public class ManageSuffix extends DataGrid<ManageSuffix.Suffix> {

    private Editor editor;

    public ManageSuffix() {
        super(Suffix.class, StringList.create("Suffix"));
        reload();
        setCaption("Manage Suffixes");
    }

    private void reload() {
        clear();
        String[] suffixes = Person.getSuffixValues();
        for(int i = 0; i < suffixes.length; i++) {
            add(new Suffix(i, suffixes[i]));
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
        editor.setObject(new Suffix());
        editor.execute(getView());
    }

    private void edit() {
        Suffix s = selected();
        if(s == null) {
            return;
        }
        if(s.code <= 2) {
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

    private class Editor extends DataEditor<Suffix> {

        private boolean add;

        public Editor() {
            super(Suffix.class, "Edit Suffix");
            setWindowMode(true);
        }

        @Override
        protected void save() {
            super.save();
            Suffix s = getObject();
            try {
                if(add) {
                    Person.addSuffix(getTransactionManager(), s.suffix);
                } else {
                    Person.updateSuffix(getTransactionManager(), s.code, s.suffix);
                }
            } catch(Exception e) {
                ManageSuffix.this.warning(e);
            } finally {
                reload();
            }
        }
    }

    public static final class Suffix {

        private final int code;
        private String suffix;

        public Suffix() {
            this(Person.getSuffixValues().length, "New");
        }

        private Suffix(int code, String suffix) {
            this.code = code;
            this.suffix = suffix;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Suffix) obj;
            return this.code == that.code;
        }

        @Override
        public int hashCode() {
            return code;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public String toString() {
            return suffix;
        }
    }
}
