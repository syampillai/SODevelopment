package com.storedobject.ui.util;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.GridLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.contextmenu.ContextMenu;

import java.util.Collection;
import java.util.function.Consumer;

public class ItemContextMenu<T> extends ContextMenu {

    private final GridLayout container = new GridLayout(1);
    private final Consumer<T> consumer;
    private ItemLabelGenerator<T> itemLabelGenerator = Object::toString;

    public ItemContextMenu(Component target, Consumer<T> inform) {
        this.consumer = inform;
        setOpenOnClick(true);
        addItem(container);
        setTarget(target);
    }

    public void setItems(Collection<T> items) {
        items.forEach(item -> container.add(new Button(itemLabelGenerator.apply(item), "", e -> consumer.accept(item))));
    }

    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        this.itemLabelGenerator = itemLabelGenerator;
    }
}
