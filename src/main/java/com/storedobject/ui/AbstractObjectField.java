package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.ObjectSearchFilter;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.util.ObjectInput;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.ValueRequired;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractObjectField<T extends StoredObject> extends CustomField<T> implements ObjectInput<T>, ValueRequired {

    public AbstractObjectField(Class<T> objectClass, boolean allowAny) {
        super(null);
    }

    protected ButtonLayout initComponent() {
        return null;
    }

    protected ButtonLayout getContent() {
        return null;
    }

    @Override
    public void setInternalLabel(String label) {
    }

    @Override
    public String getInternalLabel() {
        return null;
    }

    @Override
    public final T getCached() {
        return null;
    }

    @Override
    public final void setCached(T cached) {
    }

    protected abstract Component createPrefixComponent();

    @Override
    public final Class<T> getObjectClass() {
        return null;
    }

    @Override
    public final boolean isAllowAny() {
        return false;
    }

    public Component getPrefixComponent() {
        return null;
    }

    protected T reget(T value) {
        return null;
    }

    protected T filter(T value) {
        return null;
    }

    protected ObjectIterator<T> filteredList(ObjectIterator<T> list) {
        return null;
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return null;
    }

    @Override
    public void setFilter(FilterProvider filterProvider) {
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
    }

    @Override
    public void setFilter(String filterClause) {
    }

    @Override
    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
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
    public void filterChanged() {
    }

    protected void doSearch() {
    }

    protected ObjectBrowser<T> getSearcher() {
        return null;
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
    protected void setPresentationValue(T value) {
    }

    @Override
    public void setPrefixFieldControl(boolean prefixFieldControl) {
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setReadOnly(boolean readOnly) {
    }
}