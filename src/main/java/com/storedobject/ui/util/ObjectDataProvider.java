package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.vaadin.flow.shared.Registration;

public interface ObjectDataProvider<T extends StoredObject> extends AbstractObjectDataProvider<T, T> {
    Registration addObjectDataLoadedListener(ObjectDataLoadedListener listener);
}