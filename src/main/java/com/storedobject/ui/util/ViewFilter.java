package com.storedobject.ui.util;

import com.storedobject.core.ObjectToString;
import com.storedobject.core.StoredObject;

import java.util.function.BiFunction;

public class ViewFilter<T extends StoredObject> {

    public ViewFilter(AbstractObjectDataProvider<T, ?, ?> dataProvider) {
    }

    public void setDataProvider(AbstractObjectDataProvider<T, ?, ?> dataProvider) {
    }

    public boolean setObjectConverter(ObjectToString<T> objectConverter) {
        return true;
    }

    public boolean setMatcher(BiFunction<T, String[], Boolean> matcher) {
        return true;
    }
}