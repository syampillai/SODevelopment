package com.storedobject.ui.tools;

import com.storedobject.core.SerialConfigurator;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.ChoiceField;
import com.vaadin.flow.component.HasValue;

public class SerialConfiguratorEditor extends ObjectEditor<SerialConfigurator> {

    private ChoiceField typeChoice, patternTypeChoice;

    public SerialConfiguratorEditor() {
        super(SerialConfigurator.class);
    }

    public SerialConfiguratorEditor(String className) {
        this();
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName, String label) {
        if("Type".equals(fieldName)) {
            typeChoice = new ChoiceField(label, new String[] {});
            return typeChoice;
        }
        if("PatternType".equals(fieldName)) {
            patternTypeChoice = new ChoiceField(label, new String[] {});
            return patternTypeChoice;
        }
        return super.createField(fieldName, label);
    }

    @Override
    public void setObject(SerialConfigurator object, boolean load) {
        if(object == null) {
            typeChoice.setChoices(new String[] {});
            patternTypeChoice.setChoices(new String[] {});
        } else {
            typeChoice.setChoices(object.getTypeValues());
            patternTypeChoice.setChoices(object.getPatternTypeValues());
        }
        super.setObject(object, load);
    }
}
