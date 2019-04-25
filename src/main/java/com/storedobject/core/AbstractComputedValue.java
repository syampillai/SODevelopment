package com.storedobject.core;

import com.storedobject.common.ComputedValue;
import com.storedobject.common.Storable;

public abstract class AbstractComputedValue<T> implements Storable, ComputedValue<T> {

    protected boolean computed;

    public final boolean isComputed() {
        return computed;
    }

    public void setComputed(boolean computed) {
        this.computed = computed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ComputedValue<T> clone() throws CloneNotSupportedException {
        return (ComputedValue<T>) super.clone();
    }
}