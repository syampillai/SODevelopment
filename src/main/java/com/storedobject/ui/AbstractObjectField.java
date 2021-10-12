package com.storedobject.ui;

import com.storedobject.common.FilterProvider;
import com.storedobject.core.*;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CustomField;
import com.storedobject.vaadin.ValueRequired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.ItemLabelGenerator;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The base field for accepting a {@link StoredObject} instance. (This is the base for a couple of fields that
 * accept object instances).
 *
 * @param <T> Type of object instance accepted.
 * @author Syam
 */
public abstract class AbstractObjectField<T extends StoredObject> extends CustomField<T> implements ObjectInput<T>, ValueRequired {

    private final Class<T> objectClass;
    private final boolean allowAny;
    private boolean required = false;
    /**
     * The prefix component.
     */
    Component prefixComponent;
    /**
     * The detail component.
     */
    Component detailComponent;
    private Consumer<T> displayDetail;
    private boolean prefixFieldControl = true;
    /**
     * The searcher.
     */
    ObjectBrowser<T> searcher;
    private ObjectSetter<T> searchSetter;
    private ButtonLayout layout;
    private T cached;
    private String label;
    private ObjectEditor<T> objectAdder;
    private ItemLabelGenerator<T> itemLabelGenerator;

    /**
     * Constructor.
     *
     * @param objectClass Class of the objects that are valid.
     * @param allowAny Whether subclasses should be allowed or not.
     */
    public AbstractObjectField(Class<T> objectClass, boolean allowAny) {
        super(null);
        this.objectClass = objectClass;
        this.allowAny = allowAny;
        detailComponent = new ELabel();
    }

    /**
     * This is where the display parts of the field is initialized.
     * If overridden, you need to make sure to always return the same component between calls.
     *
     * @return The layout component containing other display components.
     */
    protected ButtonLayout initComponent() {
        if(layout != null) {
            return layout;
        }
        prefixComponent = createPrefixComponent();
        layout = new ButtonLayout(prefixComponent, detailComponent);
        layout.setWidthFull();
        layout.setFlexGrow(1, detailComponent);
        prefixComponent.setVisible(isEnabled() && !isReadOnly());
        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getContent();
        super.onAttach(attachEvent);
    }

    /**
     * This is where the content of the field display is created. The default implementation invokes
     * {@link #initComponent()} and stuffs the return value into the layout.
     * If overridden, you need to make sure to always return the same component between calls.
     *
     * @return Content part of the field.
     */
    protected ButtonLayout getContent() {
        if(layout == null) {
            layout = initComponent();
            add(layout);
        }
        return layout;
    }

    /**
     * Add a new value. This will be invoked to add a new value if supported by the concrete implementation.
     */
    void addNew() {
        if(objectAdder == null) {
            objectAdder = ObjectEditor.create(getObjectClass(), EditorAction.NEW);
            objectAdder.addObjectChangedListener(new ObjectChangedListener<>() {
                @Override
                public void inserted(T object) {
                    setValue(object);
                }
            });
        }
        objectAdder.addObject(Application.get().getActiveView());
    }

    @Override
    public void setInternalLabel(String label) {
        this.label = label;
    }

    @Override
    public String getInternalLabel() {
        return label;
    }

    @Override
    public final T getCached() {
        return cached;
    }

    @Override
    public final void setCached(T cached) {
        this.cached = cached;
    }

    protected abstract Component createPrefixComponent();

    @Override
    public final Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public final boolean isAllowAny() {
        return allowAny;
    }

    public Component getPrefixComponent() {
        return prefixComponent;
    }

    /**
     * Check a given value to see if it satisfies the filter condition of the searcher.
     *
     * @param value Value to check.
     * @return The value itself if the condition is satisfied. Otherwise, <code>null</code>.
     */
    protected T filter(T value) {
        return getSearcher().validateFilterCondition(value) ? value : null;
    }

    /**
     * Check a given set of values to see if they satisfy the filter condition of the searcher.
     *
     * @param list Values to check.
     * @return The list of values that satisfy the filter condition.
     */
    protected ObjectIterator<T> filteredList(ObjectIterator<T> list) {
        try {
            if(searcher == null) {
                return list;
            }
            return list.filter(getSearcher()::validateFilterCondition);
        } catch (Exception e) {
            return ObjectIterator.create();
        }
    }

    /**
     * Check the current value to see if it is satisfying the filter condition of the searcher or not.
     */
    protected void reget() {
        T v1 = getValue();
        if(v1 == null) {
            return;
        }
        T v2 = filter(v1);
        if(v2 == null) {
            setValue((T) null);
        }
    }

    @Override
    public void setFilter(ObjectSearchFilter filter) {
        getSearcher().setFilter(filter);
        reget();
    }

    @Override
    public void setFilter(FilterProvider filterProvider, String extraFilterClause) {
        getSearcher().setFilter(filterProvider, extraFilterClause);
        reget();
    }

    @Override
    public void filter(Predicate<T> filter) {
        getSearcher().filter(filter);
    }

    @Override
    public Predicate<T> getFilterPredicate() {
        return searcher == null ? null : searcher.getFilterPredicate();
    }

    @Override
    public void setLoadFilter(Predicate<T> filter) {
        getSearcher().setLoadFilter(filter);
        reget();
    }

    @Override
    public Predicate<T> getLoadFilter() {
        return searcher == null ? null : searcher.getLoadFilter();
    }

    @Override
    public ObjectSearchFilter getFilter(boolean create) {
        return (create || searcher != null) ? getSearcher().getFilter(create) : null;
    }

    @Override
    public void filterChanged() {
        if(searcher != null) {
            searcher.filterChanged();
        }
        reget();
    }

    /**
     * Do a search using the searcher component.
     */
    protected void doSearch() {
        if(searchSetter == null) {
            searchSetter = object -> {
                setPresentationValue(object);
                setModelValue(object, true);
            };
        }
        Application a = Application.get();
        a.setPostFocus(this);
        getSearcher().search(a.getTransactionManager().getEntity(), searchSetter);
    }

    /**
     * Get the searcher for this field. If you want to return a customized searcher, override
     * {@link #createSearcher()}.
     *
     * @return Typically, an instance of the {@link ObjectBrowser} that has search capability.
     */
    protected final ObjectBrowser<T> getSearcher() {
        if(searcher == null) {
            searcher = createSearcher();
            if(searcher == null) {
                searcher = ObjectBrowser.create(getObjectClass(),
                        EditorAction.SEARCH | EditorAction.RELOAD | (isAllowAny() ? EditorAction.ALLOW_ANY : 0));
                searcher.editor = editor();
            }
        }
        return searcher;
    }

    /**
     * Return the {@link ObjectEditor} associated with this. This will be invoked when the searcher is set and the
     * editor will be checked for additional filter conditions..
     *
     * @return Editor instance is available.
     */
    protected ObjectEditor<T> editor() {
        return null;
    }

    @Override
    public void load(ObjectIterator<T> objects) {
        clear();
        getSearcher().load(objects.filter(getLoadFilter()));
    }

    /**
     * Get the searcher for this field.
     *
     * @return Typically, an instance of the {@link ObjectBrowser} that has search capability.
     */
    protected ObjectBrowser<T> createSearcher() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDetailComponent(Component detailComponent) {
        if (this.detailComponent == detailComponent) {
            return;
        }
        if (this.detailComponent != null) {
            getContent().remove(this.detailComponent);
        }
        this.detailComponent = detailComponent;
        if (detailComponent != null) {
            getContent().add(detailComponent);
            setPresentationValue(getValue());
            if (detailComponent instanceof Consumer) {
                try {
                    setDisplayDetail(((Consumer<T>) detailComponent));
                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Override
    public Component getDetailComponent() {
        return detailComponent;
    }

    @Override
    public void setDisplayDetail(Consumer<T> displayDetail) {
        if (this.displayDetail == displayDetail) {
            return;
        }
        this.displayDetail = displayDetail;
        setDetailComponent(displayDetail instanceof Component ? (Component) displayDetail : null);
    }

    @Override
    public Consumer<T> getDisplayDetail() {
        return displayDetail;
    }

    @Override
    protected void setPresentationValue(T value) {
        if (displayDetail != null) {
            displayDetail.accept(value);
        } else {
            if(detailComponent instanceof HasText) {
                String v;
                if(value == null) {
                    v = "";
                } else {
                    if(itemLabelGenerator != null) {
                        v = itemLabelGenerator.apply(value);
                    } else {
                        v = value.toDisplay();
                    }
                }
                ((HasText) detailComponent).setText(v);
            }
        }
    }

    @Override
    public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
        this.itemLabelGenerator = itemLabelGenerator;
        T v = getValue();
        if(v != null && displayDetail == null) {
            setPresentationValue(v);
        }
    }

    @Override
    public void setPrefixFieldControl(boolean prefixFieldControl) {
        this.prefixFieldControl = prefixFieldControl;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if (!prefixFieldControl) {
            return;
        }
        if(prefixComponent != null) {
            prefixComponent.setVisible(!readOnly && isEnabled());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!prefixFieldControl) {
            return;
        }
        if(prefixComponent != null) {
            prefixComponent.setVisible(!isReadOnly() && enabled);
        }
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }
}