package com.storedobject.ui.util;

import com.storedobject.core.StoredObject;
import com.vaadin.flow.shared.Registration;

public interface ObjectDataProvider<T extends StoredObject, F> extends AbstractObjectDataProvider<T, T, F> {
}