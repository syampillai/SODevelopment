package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.TranslatedField;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.ViewDependent;

public class ExtraInfoField<T extends StoredObject> extends TranslatedField<ExtraInfo<T>, T>
        implements ViewDependent, NoDisplayField {

    public ExtraInfoField(ExtraInfo<T> extraInfo) {
        super(new ExtraInfoObjectField<>(extraInfo), (f, t) -> extraInfo, (f, e) -> e.getInfo(), extraInfo);
        //noinspection unchecked
        extraInfo.field = (ExtraInfoObjectField<T>) getField();
    }

    @Override
    public void setDependentView(View dependent) {
        ((ExtraInfoObjectField<?>)getField()).setDependentView(dependent);
    }

    @Override
    public View getDependentView() {
        return ((ExtraInfoObjectField<?>)getField()).getDependentView();
    }

    @Override
    public boolean canDisplay() {
        return false;
    }
}
