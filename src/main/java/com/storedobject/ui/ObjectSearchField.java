package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ImageButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ObjectSearchField<T extends StoredObject> extends AbstractObjectField<T> {

    private ImageButton delete;
    private ImageButton addButton;

    public ObjectSearchField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectSearchField(String label, Class<T> objectClass) {
        this(label, objectClass, false);
    }

    public ObjectSearchField(Class<T> objectClass, boolean allowAny) {
        this(null, objectClass, allowAny);
    }

    public ObjectSearchField(Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        this(null, objectClass, allowAny, allowAdd);
    }

    public ObjectSearchField(String label, Class<T> objectClass, boolean allowAny) {
        this(label, objectClass, allowAny,false);
    }

    public ObjectSearchField(String label, Class<T> objectClass, boolean allowAny, boolean allowAdd) {
        super(objectClass, allowAny);
        setLabel(label);
        if(allowAdd && !isAllowAny()) {
            ObjectField.checkDetailClass(objectClass, label);
            addButton = new ImageButton("Add new", VaadinIcon.PLUS, e -> addNew()).withBox();
        }
    }

    @Override
    protected Component createPrefixComponent() {
        ImageButton search = new ImageButton("Search", e -> doSearch()).withBox();
        delete = new ImageButton("Delete", e -> {
            setModelValue(null, true);
            setPresentationValue(null);
        }).withBox();
        delete.setVisible(false);
        return new ButtonLayout(addButton, search, delete);
    }

    @Override
    protected T generateModelValue() {
        return getValue();
    }

    @Override
    protected void setPresentationValue(T value) {
        super.setPresentationValue(value);
        if(delete != null) {
            delete.setVisible(value != null);
        }
    }
}