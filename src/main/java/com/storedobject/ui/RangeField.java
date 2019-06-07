package com.storedobject.ui;

import com.storedobject.common.Range;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;

import java.util.function.Function;

public abstract class RangeField<T extends Range<P>, P> extends CustomField<T> {

    public RangeField(Function<?, HasValue<?, P>> fieldGenerator) {
        this(fieldGenerator, fieldGenerator);
    }

    public RangeField(Function<?, HasValue<?, P>> fromFieldGenerator, Function<?, HasValue<?, P>> toFieldGenerator) {
    }

    @Override
    protected T generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(T value) {
    }

    @SuppressWarnings("unchecked")
    public P getFrom() {
        return (P)((Range)this.getValue()).getFrom();
    }

    @SuppressWarnings("unchecked")
    public P getTo() {
        return (P)((Range)this.getValue()).getTo();
    }

    protected abstract T create(P from, P to);
}
