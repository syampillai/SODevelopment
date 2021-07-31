package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectLinkField;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.MultiSelectGrid;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReportEditor<T extends ReportDefinition> extends ObjectEditor<T> {

    private final Button addCols = new Button("Generate Columns", VaadinIcon.COG_O, e -> genCols());

    public ReportEditor(Class<T> objectClass) {
        super(objectClass);
    }

    public ReportEditor(Class<T> objectClass, int actions) {
        super(objectClass, actions);
    }

    public ReportEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public ReportEditor(String className) throws Exception {
        super(className);
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName, String label) {
        if("DataClass".equals(fieldName)) {
            return new ClassNameField(label);
        }
        return super.createField(fieldName, label);
    }

    @Override
    protected void addExtraEditingButtons() {
        buttonPanel.add(addCols);
    }

    private <O extends StoredObject> void genCols() {
        List<ReportColumnDefinition> colsRCD = new ArrayList<>();
        @SuppressWarnings("unchecked") HasValue<?, String> dataClassField = (HasValue<?, String>) getField("DataClass");
        String dClassName = dataClassField.getValue();
        if(dClassName.isBlank()) {
            warning("Enter name of the data class first!");
            return;
        }
        dClassName = ApplicationServer.guessClass(dClassName);
        dataClassField.setValue(dClassName);
        try {
            Class<O> dClass;
            //noinspection unchecked
            dClass = (Class<O>) JavaClassLoader.getLogic(dClassName);
            ObjectLinkField<ReportColumnDefinition> rcdField;
            //noinspection unchecked
            rcdField = (ObjectLinkField<ReportColumnDefinition>) getLinkField("Report Columns");
            List<String> cols = rcdField.getItems().map(ReportColumnDefinition::getAttribute).toList();
            ReportColumnDefinition rcd;
            for(String name : ClassAttribute.get(dClass).getAllAttributes()) {
                if(cols.contains(name)) {
                    continue;
                }
                rcd = new ReportColumnDefinition();
                rcd.setAttribute(name);
                rcd.makeVirtual();
                colsRCD.add(rcd);
            }
        } catch(Throwable e) {
            warning("Data class not found: " + dClassName);
            return;
        }
        if(colsRCD.isEmpty()) {
            warning("No more attributes found!");
            return;
        }
        new MultiSelectGrid<>(ReportColumnDefinition.class, colsRCD, StringList.create("Attribute AS Report Column"),
                this::addCols).execute();
    }

    private void addCols(Set<ReportColumnDefinition> list) {
        if(list.isEmpty()) {
            return;
        }
        ObjectLinkField<ReportColumnDefinition> rcdField;
        //noinspection unchecked
        rcdField = (ObjectLinkField<ReportColumnDefinition>) getLinkField("Report Columns");
        List<String> cols = rcdField.getItems().map(ReportColumnDefinition::getAttribute).toList();
        list.forEach(rcd -> {
            rcd.makeNew();
            if(!cols.contains(rcd.getAttribute())) {
                rcdField.add(rcd);
            }
        });
    }
}
