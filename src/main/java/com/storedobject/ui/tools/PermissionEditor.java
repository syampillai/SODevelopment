package com.storedobject.ui.tools;

import com.storedobject.core.EditorAction;
import com.storedobject.core.ObjectPermission;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.TranslatedField;
import com.vaadin.flow.component.HasValue;

import java.util.function.BiFunction;

public abstract class PermissionEditor<T extends ObjectPermission> extends ObjectEditor<T> {

    public PermissionEditor(Class<T> permissiomClass, String title) {
        super(permissiomClass, EditorAction.ALL, title);
    }

    @Override
    public HasValue<?, ?> getField(String fieldName) {
        if(fieldName.equals("ClassFamily")) {
            BiFunction<HasValue<?, String>, String, Integer> convertToV = (f, s) -> ((ClassNameField)f).getObjectFamily(s);
            BiFunction<HasValue<?, String>, Integer, String> convertToIV = (f, i) -> ((ClassNameField)f).getObjectClassName(i);
            return new TranslatedField<>(new ClassNameField(), convertToV, convertToIV);
        }
        return super.getField(fieldName);
    }
}
