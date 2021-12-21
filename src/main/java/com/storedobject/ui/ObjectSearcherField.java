package com.storedobject.ui;

import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.vaadin.flow.shared.Registration;

import java.util.function.Consumer;

public class ObjectSearcherField<T extends StoredObject> extends ObjectGetField<T> {

    private Registration registration;
    private T previous;
    private ObjectEditor<T> editor;

    public ObjectSearcherField(Class<T> objectClass, Consumer<T> objectConsumer) {
        this(null, objectClass, objectConsumer);
    }

    public ObjectSearcherField(String label, Class<T> objectClass, Consumer<T> objectConsumer) {
        super(label, objectClass);
        setObjectConsumer(objectConsumer);
        setDisplayDetail(o -> {});
    }

    public static <O extends StoredObject> ObjectSearcherField<O> create(Class<O> objectClass, Consumer<O> objectConsumer) {
        return create(null, objectClass, objectConsumer);
    }

    public static <O extends StoredObject> ObjectSearcherField<O> create(String label, Class<O> objectClass, Consumer<O> objectConsumer) {
        return canCreate(objectClass) ? new ObjectSearcherField<>(label, objectClass, objectConsumer) : null;
    }

    public void setObjectConsumer(Consumer<T> objectConsumer) {
        if(objectConsumer instanceof ObjectEditor) {
            editor = (ObjectEditor<T>) objectConsumer;
        }
        ObjectSetter<T> objectSetter;
        if(objectConsumer instanceof ObjectSetter) {
            objectSetter = (ObjectSetter<T>) objectConsumer;
        } else {
            if(objectConsumer == null) {
                objectSetter = o -> { };
            } else {
                objectSetter = objectConsumer::accept;
            }
        }
        if(registration != null) {
            registration.remove();
        }
        final ObjectSetter<T> os = objectSetter;
        registration = addValueChangeListener(e -> {
            T value = e.getValue();
            if(value != null) {
                os.setObject(value);
                setObject((T) null);
                previous = value;
            }
        });
    }

    @Override
    T previousValue() {
        return previous;
    }

    @Override
    protected ObjectEditor<T> editor() {
        return editor;
    }

    @Override
    protected ObjectBrowser<T> createSearcher() {
        ObjectBrowser<T> b = createDefaultSearcher();
        editor.refreshMe(b);
        return b;
    }
}