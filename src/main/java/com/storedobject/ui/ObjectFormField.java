package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.util.NoDisplayField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.shared.Registration;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ObjectFormField<T extends StoredObject> extends CustomField<T> implements ObjectInput<T>, ViewDependent, NoDisplayField {

    private static final HasContainer DUMMY = () -> null;
    private final ObjectEditor<T> formEditor;
    private Id objectId;
    private T object;
    private String internalLabel;
    boolean fromClient = false;
    private ObjectEditor<?> masterView;
    private final HasContainer mergeTo;
    private boolean displayCreated = false;
    private Registration masterOpened, masterClosed;
    private boolean fresh = true;

    public ObjectFormField(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ObjectFormField(String label, Class<T> objectClass) {
        this(label, ObjectEditor.create(objectClass, EditorAction.ALL, ""));
    }

    public ObjectFormField(ObjectEditor<T> formEditor) {
        this(null, formEditor, (HasContainer)null);
    }

    public ObjectFormField(String label, ObjectEditor<T> formEditor) {
        this(label, formEditor, (HasContainer)null);
    }

    public ObjectFormField(Class<T> objectClass, ObjectField.Type formType) {
        this(null, objectClass, formType);
    }

    public ObjectFormField(String label, Class<T> objectClass, ObjectField.Type formType) {
        this(label, ObjectEditor.create(objectClass), formType);
    }

    public ObjectFormField(ObjectEditor<T> formEditor, ObjectField.Type formType) {
        this(null, formEditor, formType);
    }

    public ObjectFormField(String label, ObjectEditor<T> formEditor, ObjectField.Type formType) {
        this(label, formEditor, formType == ObjectField.Type.FORM ? DUMMY : null);
    }

    public ObjectFormField(Class<T> objectClass, HasContainer mergeTo) {
        this(null, objectClass, mergeTo);
    }

    public ObjectFormField(String label, Class<T> objectClass, HasContainer mergeTo) {
        this(label, ObjectEditor.create(objectClass), mergeTo);
    }

    public ObjectFormField(ObjectEditor<T> formEditor, HasContainer mergeTo) {
        this(null, formEditor, mergeTo);
    }

    public ObjectFormField(String label, ObjectEditor<T> formEditor, HasContainer mergeTo) {
        ObjectField.checkDetailClass(formEditor.getObjectClass(), label);
        this.formEditor = formEditor;
        this.formEditor.setCaption("", true);
        this.mergeTo = mergeTo;
        if(mergeTo != null && mergeTo != DUMMY) {
            this.formEditor.setFieldContainerProvider(mergeTo);
        }
        this.formEditor.setSaver(oe -> true);
        if(mergeTo == null) {
            displayCreated = true;
            add(this.formEditor.getComponent());
            ((Component) this.formEditor.buttonPanel).setVisible(false);
        }
        if(mergeTo == null && label != null) {
            setLabel(label);
        }
        setValue((T)null);
    }

    @Override
    public boolean canDisplay() {
        return formType() == ObjectField.Type.FORM_BLOCK;
    }

    ObjectField.Type formType() {
        return (mergeTo != null && mergeTo == masterView) || mergeTo == DUMMY ? ObjectField.Type.FORM : ObjectField.Type.FORM_BLOCK;
    }

    @Override
    protected void updateValue() {
        fromClient = true;
    }

    @Override
    protected T generateModelValue() {
        return object;
    }

    @Override
    protected void setPresentationValue(T object) {
        formEditor.setObject(object, true);
    }

    @Override
    protected boolean valueEquals(T value1, T value2) {
        return value1 == value2;
    }

    @Override
    public void setDetailComponent(Component detailComponent) {
    }

    @Override
    public Component getDetailComponent() {
        return null;
    }

    @Override
    public void setDisplayDetail(Consumer<T> displayDetail) {
    }

    @Override
    public Consumer<T> getDisplayDetail() {
        return null;
    }

    @Override
    public void setPrefixFieldControl(boolean prefixFieldControl) {
    }

    @Override
    public boolean isInvalid() {
        return getObjectId() == null;
    }

    @Override
    public Class<T> getObjectClass() {
        return formEditor.getObjectClass();
    }

    @Override
    public T getObject() {
        return getValue();
    }

    @Override
    public void setObject(StoredObject object) {
        setValue(convert(object));
    }

    @Override
    public Id getObjectId() {
        if(isReadOnly() || !displayCreated) {
            return objectId;
        }
        boolean newId = objectId == null;
        if(!newId) {
            Transaction t = object.getTransaction();
            if(t != null && t.isActive()) {
                return objectId;
            }
            newId = t == null || !t.isActive();
        }
        if(newId) {
            if(formEditor.commit() && masterView != null) {
                try {
                    object.clearObjectLinks();
                    for(ObjectLinkField<?> linkField: formEditor.linkFields()) {
                        linkField.getValue().copy().attach();
                    }
                    formEditor.validateData();
                    object.validateData(masterView.getTransactionManager());
                    objectId = object.save(masterView.getTransaction(true));
                    formEditor.setObject(object, true);
                } catch (Throwable error) {
                    masterView.warning(error);
                }
            }
        }
        return objectId;
    }

    @Override
    public void setObject(Id objectId) {
        setValue(objectId);
    }

    @Override
    public T getObject(Id objectId) {
        return object;
    }

    @Override
    public void setCached(T cached) {
    }

    @Override
    public T getCached() {
        return object;
    }

    @Override
    public void setPlaceholder(String placeholder) {
    }

    @Override
    public Id getObjectId(T object) {
        return objectId;
    }

    @Override
    public T getValue() {
        return object;
    }

    @Override
    public void setInternalLabel(String label) {
        this.internalLabel = label;
    }

    @Override
    public String getInternalLabel() {
        return internalLabel;
    }

    @Override
    public void setValue(Id id) {
        if(objectId != null && id == objectId) {
            return;
        }
        if(id == null) {
            setValue((T)null);
            return;
        }
        setObject(StoredObject.get(getObjectClass(), id));
    }

    @Override
    public void setValue(T object) {
        fromClient = false;
        if(object != null && this.object == object) {
            return;
        }
        if(fresh) {
            this.object = null;
            this.objectId = null;
            fresh = false;
        }
        if(object == null) {
            object = formEditor.newObject();
        }
        if(this.object == null || objectId == null) {
            if(!object.created()) {
                objectId = object.getId();
            }
            this.object = object;
            if(displayCreated) {
                super.setValue(object);
            }
            return;
        }
        Transaction transaction = this.object.getTransaction();
        this.object = object;
        if(displayCreated) {
            super.setValue(object);
        }
        if(!(objectId instanceof ObjectId)) {
            if(object.created()) {
                objectId = null;
            } else {
                objectId = object.getId();
            }
            return;
        }
        if(transaction == null || !transaction.isActive() || !(transaction instanceof PseudoTransaction)) {
            if(object.created()) {
                objectId = null;
            } else {
                objectId = object.getId();
            }
            return;
        }
        ((PseudoTransaction)transaction).replace(objectId, object);
    }

    @Override
    public void filter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return null;
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return null;
    }

    @Override
    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    public void filterChanged() {
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        formEditor.setReadOnly(readOnly);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setDependentView(View masterView) {
        if(this.masterView == masterView) {
            return;
        }
        if(!(masterView instanceof ObjectEditor)) {
            this.masterView = null;
            formEditor.setTransactionCreator(null);
            return;
        }
        if(this.masterView != null) {
            masterOpened.remove();
            masterClosed.remove();
        }
        this.masterView = (ObjectEditor<?>)masterView;
        formEditor.setErrorDisplay(this.masterView.getErrorDisplay());
        if(mergeTo == DUMMY) {
            formEditor.setFieldContainerProvider(this.masterView);
        }
        attach();
        formEditor.setTransactionCreator(this.masterView);
        masterOpened = this.masterView.addOpenedListener(v -> fresh = true);
        masterClosed = this.masterView.addClosedListener(v -> fresh = true);
    }

    @Override
    public View getDependentView() {
        return masterView;
    }

    /**
     * Attach this to the respective container. There is no need to call this method if you are attaching this to an
     * {@link ObjectEditor}.
     */
    public void attach() {
        if(!displayCreated) {
            displayCreated = true;
            formEditor.formField(this);
            super.setValue(object);
        }
    }

    @Override
    public void focus() {
    }
}