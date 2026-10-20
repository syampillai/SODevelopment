package com.storedobject.ui.util;

import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectChangedListener;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectInput;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A utility editor class to dynamically create and add a new {@link StoredObject} instance to an
 * {@link ObjectInput} field.
 *
 * @param <O> Type of object.
 * @author Syam
 */
public class ObjectAdder<O extends StoredObject> {

    private ObjectEditor<O> adder;
    private final Consumer<O> consumer;
    private ObjectInput<O> field;

    private ObjectAdder(Consumer<O> consumer) {
        this.consumer = consumer;
    }

    /**
     * Static method to create the adder instance.
     *
     * @param consumer Listener to inform.
     * @param field The {@link ObjectInput} field.
     * @param <OT> Type of object being created.
     * @return Adder instance.
     */
    public static <OT extends StoredObject> ObjectAdder<OT> create(Consumer<OT> consumer, ObjectInput<OT> field) {
        ObjectAdder<OT> oa = new ObjectAdder<>(consumer);
        oa.field = field;
        return oa;
    }

    /**
     * Add a new object.
     */
    public void add() {
        getObjectEditor().addObject(Application.get().getActiveView());
    }

    /**
     * Retrieves the {@link ObjectEditor} instance associated with this {@code ObjectAdder}.
     * If the {@link ObjectEditor} has not been created yet, it initializes a new instance
     * configured for creating a new object of the specified type and adds an {@link ObjectChangedListener}
     * to handle object changes.
     *
     * @return The {@link ObjectEditor} instance used for creating and managing objects.
     */
    public ObjectEditor<O> getObjectEditor() {
        if(adder == null) {
            adder = ObjectEditor.create(field.getObjectClass(), EditorAction.NEW);
            adder.addObjectChangedListener(new Changed());
        }
        return adder;
    }

    /**
     * Add a new object to the master object as a link.
     * @param masterSupplier Master object supplier.
     * @param linkType Link type.
     */
    public void addToMaster(Supplier<StoredObject> masterSupplier, int linkType) {
        StoredObject master = masterSupplier.get();
        if(master == null) {
            return;
        }
        getObjectEditor().setParentObject(master, linkType);
        add();
    }

    private class Changed implements ObjectChangedListener<O> {

        @Override
        public void inserted(O object) {
            if(consumer != null) consumer.accept(object);
            field.setValue(object);
        }
    }
}
