package com.storedobject.ui.common;

import com.storedobject.core.EditorAction;
import com.storedobject.core.PasswordPolicy;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.ObjectEditor;
import com.vaadin.flow.component.HasValue;

public class SetPasswordPolicy extends ObjectEditor<PasswordPolicy> {

    private HasValue<?, String> classField;

    public SetPasswordPolicy() {
        super(PasswordPolicy.class, EditorAction.EDIT | EditorAction.DELETE, "Set Password Policy");
        addConstructedListener(o -> fConstructed());
    }

    private void fConstructed() {
        PasswordPolicy policy = StoredObject.get(PasswordPolicy.class, "DataClass='" + SystemUser.class.getName() + "'");
        if(policy == null) {
            policy = new PasswordPolicy();
        }
        setObject(policy, true);
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if(fieldName.equals("DataClass")) {
            //noinspection unchecked
            classField = (HasValue<?, String>) field;
            setFixedValue(fieldName, SystemUser.class.getName());
            return;
        }
        super.customizeField(fieldName, field);
    }

    @Override
    public boolean isFieldVisible(HasValue<?, ?> field) {
        if(field == classField) {
            return false;
        }
        return super.isFieldVisible(field);
    }
}
