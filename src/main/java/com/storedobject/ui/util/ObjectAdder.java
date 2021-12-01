package com.storedobject.ui.util;

import com.storedobject.core.EditorAction;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectChangedListener;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectInput;

/**
 * A utility editor class to dynamically create and add a new {@link StoredObject} instance to an
 * {@link ObjectInput} field.
 *
 * @param <O> Type of object.
 * @author Syam
 */
public class ObjectAdder<O extends StoredObject> {

    private ObjectEditor<O> adder;
    private ObjectChangedListener<O> listener;
    private ObjectInput<O> field;

    private ObjectAdder() {
    }

    /**
     * Static method to create the adder instance.
     *
     * @param listener Listener to inform.
     * @param field The {@link ObjectInput} field.
     * @param <OT> Type of object being created.
     * @return Adder instance.
     */
    public static <OT extends StoredObject> ObjectAdder<OT> create(ObjectChangedListener<OT> listener,
                                                                   ObjectInput<OT> field) {
        ObjectAdder<OT> oa = new ObjectAdder<>();
        oa.listener = listener;
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
            listener.inserted(object);
            field.setValue(object);
        }

        @Override
        public void updated(O object) {
            listener.updated(object);
            field.setValue(object);
        }

        @Override
        public void deleted(O object) {
            listener.deleted(object);
            if(field.getValue().equals(object)) {
                field.setValue((O)null);
            }
        }
    }
}
