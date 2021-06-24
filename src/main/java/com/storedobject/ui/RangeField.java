package com.storedobject.ui;

import com.storedobject.common.Range;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.util.function.Function;

public abstract class RangeField<T extends Range<P>, P> extends CustomField<T> {

    private final HasValue<?, P> field1;
    private final HasValue<?, P> field2;

    public RangeField(Function<?, HasValue<?, P>> fieldGenerator) {
        this(fieldGenerator, fieldGenerator);
    }

    public RangeField(Function<?, HasValue<?, P>> fromFieldGenerator, Function<?, HasValue<?, P>> toFieldGenerator) {
        this.field1 = fromFieldGenerator.apply(null);
        this.field2 = toFieldGenerator.apply(null);
        Div d = new Div();
        d.add((Component)this.field1, new Span(" - "), (Component)this.field2);
        add(d);
    }


    @Override
    protected T generateModelValue() {
        return create(this.field1.getValue(), this.field2.getValue());
    }

    @Override
    protected void setPresentationValue(T value) {
        this.field1.setValue(value.getFrom());
        this.field2.setValue(value.getTo());
    }

    public P getFrom() {
        return this.getValue().getFrom();
    }

    public P getTo() {
        return this.getValue().getTo();
    }

    protected abstract T create(P from, P to);
}
