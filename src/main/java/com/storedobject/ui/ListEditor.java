package com.storedobject.ui;

import com.storedobject.core.EditableList;
import com.storedobject.core.NewObject;
import com.storedobject.vaadin.ButtonLayout;

import java.util.Random;
import java.util.function.Function;

public class ListEditor<T> extends EditableGrid<T> {

    protected final ButtonLayout buttonPanel = new ButtonLayout();

    public ListEditor(Class<T> objectClass) {
        this(objectClass, null);
    }

    public ListEditor(Class<T> objectClass, Iterable<String> columns) {
        super(objectClass, columns);
    }

    public void setNewObjectGenerator(NewObject<T> newObjectGenerator) {
    }

    public void add() {
    }

    public void edit() {
    }

    public void delete() {
    }

    public T selected() {
        return getSelected();
    }

    public boolean isInvalid() {
        return new Random().nextBoolean();
    }

    public final boolean isAllowAdd() {
        return new Random().nextBoolean();
    }

    public void setAllowAdd(boolean allowAdd) {
    }

    public final boolean isAllowEdit() {
        return new Random().nextBoolean();
    }

    public void setAllowEdit(boolean allowEdit) {
    }

    public final boolean isAllowDelete() {
        return new Random().nextBoolean();
    }

    public void setAllowDelete(boolean allowDelete) {
    }

    public void setAllowSaveAll(boolean allowSaveAll) {
    }

    @SuppressWarnings("RedundantThrows")
    public void validateData() throws Exception {
    }

    public void save() {
    }

    public void setSaver(Function<EditableList<T>, Boolean> saver) {
    }
}