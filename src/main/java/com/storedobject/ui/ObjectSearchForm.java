package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.ObjectSearchBuilder;
import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.Form;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

import java.util.function.Consumer;

public abstract class ObjectSearchForm<T extends StoredObject> extends Form implements SearchBuilder<T>, ObjectSearchBuilder<T> {

    private final Class<T> objectClass;
    protected Consumer<ObjectSearchBuilder<T>> changeConsumer;

    public ObjectSearchForm(Class<T> objectClass) {
        this.objectClass = objectClass;
        setColumns(2);
    }

    @Override
    public final Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public void add(Component... components) {
        super.add(components);
        for(Component component : components) {
            if(component instanceof HasValue<?,?> hv) {
                hv.addValueChangeListener(e -> {
                    if(changeConsumer != null) {
                        changeConsumer.accept(this);
                    }
                });
            }
        }
    }

    @Override
    public final ObjectSearchBuilder<T> createSearchBuilder(Class<T> objectClass, StringList searchColumns,
                                                            Consumer<ObjectSearchBuilder<T>> changeConsumer) {
        if(objectClass == this.objectClass) {
            this.changeConsumer = changeConsumer;
            return this;
        }
        return null;
    }
}
