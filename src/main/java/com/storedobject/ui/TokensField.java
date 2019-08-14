package com.storedobject.ui;

import com.storedobject.ui.util.MultiselectComboBox;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.binder.HasItems;

import java.util.*;
import java.util.stream.Stream;

public class TokensField<T> extends MultiselectComboBox<T> implements HasItems<T> {

    private Collection<T> items;

    /**
     * Constructor.
     */
    public TokensField() {
        this((String)null);
    }

    /**
     * Constructor.
     * @param label Label
     */
    public TokensField(String label) {
        this(label, (Collection<T>)null);
    }

    /**
     * Constructor.
     * @param items Items
     */
    public TokensField(Collection<T> items) {
        this(null, items);
    }

    /**
     * Constructor.
     * @param label Label
     * @param items Items
     */
    public TokensField(String label, Collection<T> items) {
        this(label, items, null);
    }

    /**
     * Constructor.
     * @param itemLabelGenerator Label generator
     */
    public TokensField(ItemLabelGenerator<T> itemLabelGenerator) {
        this(null, null, itemLabelGenerator);
    }

    /**
     * Constructor.
     * @param label Label
     * @param itemLabelGenerator Label generator
     */
    public TokensField(String label, ItemLabelGenerator<T> itemLabelGenerator) {
        this(label, null, itemLabelGenerator);
    }

    /**
     * Constructor.
     * @param label Label
     * @param items Items
     * @param itemLabelGenerator Label generator
     */
    public TokensField(String label, Collection<T> items, ItemLabelGenerator<T> itemLabelGenerator) {
        super();
    }

    public void addValue(Set<T> value) {
    }

    @Override
    public void setItems(Collection<T> items) {
    }

    public void addItems(Collection<T> items) {
    }

    @SafeVarargs
    public final void addItems(T... items) {
        this.addItems(Arrays.asList(items));
    }

    public void addItems(Stream<T> streamOfItems) {
    }
}