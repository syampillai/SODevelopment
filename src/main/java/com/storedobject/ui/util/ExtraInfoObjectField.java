package com.storedobject.ui.util;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectInput;
import com.storedobject.ui.ObjectLinkField;
import com.storedobject.vaadin.ValueRequired;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.ViewDependent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;

import java.util.function.Consumer;
import java.util.function.Predicate;

class ExtraInfoObjectField<T extends StoredObject> extends CustomField<T>
        implements ObjectInput<T>, ViewDependent, NoDisplayField, HasValidation, ValueRequired {

    final private ExtraInfo<T> extraInfo;
    private final ObjectEditor<T> formEditor;
    private Id objectId;
    private T object;
    private String internalLabel;
    boolean fromClient = false;
    private ObjectEditor<?> masterView;
    private boolean displayCreated = false;
    private Registration masterOpened, masterClosed;
    private boolean fresh = true;
    private boolean required = false;
    private String errorText = null;

    ExtraInfoObjectField(ExtraInfo<T> extraInfo) {
        this.extraInfo = extraInfo;
        this.formEditor = ObjectEditor.create(this);
        this.formEditor.setSaver(oe -> true);
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean canDisplay() {
        return false;
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
        Id id = getObjectId();
        if(errorText != null && !errorText.isEmpty()) {
            return true;
        }
        return required && id == null;
    }

    @Override
    public Class<T> getObjectClass() {
        return extraInfo.infoClass;
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
            errorText = null;
            if(masterView == null) {
                errorText = "Unknown state";
            } else {
                if(formEditor.commit()) {
                    try {
                        object.clearObjectLinks();
                        for(ObjectLinkField<?> linkField : formEditor.linkFields()) {
                            linkField.getValue().copy().attach();
                        }
                        formEditor.validateData();
                        object.validateData(masterView.getTransactionManager());
                        objectId = object.save(masterView.getTransaction(true));
                        formEditor.setObject(object, true);
                    } catch(Throwable error) {
                        errorText = Application.get().getEnvironment().toDisplay(error);
                        masterView.error(error);
                    }
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
            setValue((T) null);
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
        ((PseudoTransaction) transaction).replace(objectId, object);
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
        if(this.masterView != null) {
            masterOpened.remove();
            masterClosed.remove();
        }
        this.masterView = (ObjectEditor<?>) masterView;
        formEditor.setErrorDisplay(new ErrorCapture());
        formEditor.setFieldContainerProvider(this.masterView);
        attach();
        formEditor.setTransactionCreator(this.masterView);
        masterOpened = this.masterView.addOpenedListener(v -> fresh = true);
        masterClosed = this.masterView.addClosedListener(v -> fresh = true);
    }

    @Override
    public View getDependentView() {
        return masterView;
    }

    private void attach() {
        if(!displayCreated) {
            displayCreated = true;
            formEditor.getComponent();
            super.setValue(object);
        }
    }

    @Override
    public void focus() {
    }

    private class ErrorCapture implements HasText {

        @Override
        public Element getElement() {
            return null;
        }

        @Override
        public void setText(String text) {
            if(text == null || text.isEmpty()) {
                errorText = null;
                return;
            }
            errorText = text;
            if(masterView != null) {
                masterView.warning(errorText);
            }
        }

        @Override
        public String getText() {
            return errorText;
        }
    }
}