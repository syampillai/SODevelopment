package com.storedobject.ui.util;

import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectChangedListener;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectInput;

import java.util.function.Consumer;

/**
 * A utility editor class to dynamically create and add a new {@link StoredObject} instance to an
 * {@link ObjectInput} field.
 *
 * @param <O> Type of object.
 * @author Syam
 */
public class ObjectAdder<O extends StoredObject> {

    private ObjectEditor<O> adder;
    private Consumer<O> consumer;
    private ObjectInput<O> field;

    private ObjectAdder() {
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
        ObjectAdder<OT> oa = new ObjectAdder<>();
        oa.consumer = consumer;
        oa.field = field;
        return oa;
    }

    /**
     * Create and add a new instance via the associated editor.
     */
    public void add() {
        if(adder == null) {
            adder = ObjectEditor.create(field.getObjectClass(), EditorAction.NEW);
            adder.addObjectChangedListener(new Changed());
        }
        adder.addObject(Application.get().getActiveView());
    }

    private class Changed implements ObjectChangedListener<O> {

        @Override
        public void inserted(O object) {
            consumer.accept(object);
            field.setValue(object);
        }
    }
}
