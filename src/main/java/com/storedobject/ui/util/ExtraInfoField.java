package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.TranslatedField;
import com.storedobject.vaadin.ValueRequired;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.ViewDependent;
import com.vaadin.flow.component.HasValidation;

public class ExtraInfoField<T extends StoredObject> extends TranslatedField<ExtraInfoValue<T>, T>
        implements ViewDependent, NoDisplayField, HasValidation, ValueRequired {

    public ExtraInfoField(ExtraInfo<T> extraInfo) {
        this(extraInfo, new ExtraInfoObjectField<>(extraInfo));
    }

    private ExtraInfoField(ExtraInfo<T> extraInfo, ExtraInfoObjectField<T> field) {
        super(field,
                (f, t) -> extraInfo.getValue(), (f, e) -> e == null ? null : e.getInfo(), null);
        extraInfo.field = this;
    }

    @Override
    public void setDependentView(View dependent) {
        field().setDependentView(dependent);
    }

    @Override
    public View getDependentView() {
        return field().getDependentView();
    }

    @Override
    public boolean canDisplay() {
        return false;
    }

    private ExtraInfoObjectField<T> field() {
        //noinspection unchecked
        return (ExtraInfoObjectField<T>) getField();
    }

    @Override
    public void setRequired(boolean required) {
        field().setRequired(required);
    }

    @Override
    public boolean isRequired() {
        return field().isRequired();
    }
}
