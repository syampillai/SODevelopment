package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.util.ViewFilterSupport;
import com.storedobject.vaadin.DataList;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.provider.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("RedundantThrows")
public class DataGrid<T> extends com.storedobject.vaadin.ListGrid<T>
        implements ViewFilterSupport<T>, Transactional {

    static final String NOTHING_SELECTED = "Nothing selected";
    static final String NOTHING_TO_SELECT = "No item available to select!";
    static final String ACTION_NOT_ALLOWED = "Your profile doesn't allow the requested action";
    private GridListDataView<T> dataView;

    public DataGrid(Class<T> objectClass) {
        this(objectClass, null);
    }

    public DataGrid(Class<T> objectClass, Iterable<String> columns) {
        this(objectClass, new MemoryCache<>(), columns);
    }

    public DataGrid(Class<T> objectClass, Filtered<T> list, Iterable<String> columns) {
        //noinspection unchecked
        super(objectClass, (List<T>)list, columns(objectClass, columns));
    }

    static <O extends StoredObject> Iterable<String> columns(Class<?> objectClass, Iterable<String> columns) {
        if(columns != null) {
            return columns;
        }
        if(!StoredObject.class.isAssignableFrom(objectClass)) {
            return null;
        }
        @SuppressWarnings("unchecked") Class<O> oClass = (Class<O>) objectClass;
        return StoredObjectUtility.browseColumns(oClass);
    }

    protected boolean isValid(ListDataProvider<T> dataProvider) {
        return dataProvider instanceof ListProvider;
    }

    @Override
    protected ListDataProvider<T> createListDataProvider(DataList<T> data) {
        return new ListProvider<>(getDataClass(), data);
    }

    @Override
    public AbstractListProvider<T> getDataProvider() {
        return (AbstractListProvider<T>) super.getDataProvider();
    }

    @Override
    public final GridListDataView<T> setItems(ListDataProvider<T> dataProvider) {
        if(dataProvider instanceof AbstractListProvider && isValid(dataProvider)) {
            dataView = super.setItems(dataProvider);
        } else {
            if(dataProvider != null) {
                throw new SORuntimeException("Invalid DP: " + dataProvider.getClass().getName());
            }
        }
        return dataView;
    }

    @Override
    public final GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fetchCallback) {
        clear();
        return null;
    }

    @Override
    public final GridDataView<T> setItems(DataProvider<T, Void> dataProvider) {
        clear();
        return null;
    }

    @Override
    public final GridDataView<T> setItems(InMemoryDataProvider<T> inMemoryDataProvider) {
        clear();
        return null;
    }

    @Override
    public final GridLazyDataView<T> setItems(CallbackDataProvider.FetchCallback<T, Void> fetchCallback,
                                              CallbackDataProvider.CountCallback<T, Void> countCallback) {
        clear();
        return null;
    }

    @Override
    public final GridLazyDataView<T> setItems(BackEndDataProvider<T, Void> dataProvider) {
        clear();
        return null;
    }

    @SafeVarargs
    @Override
    public final GridListDataView<T> setItems(T... items) {
        clear();
        load(items);
        return dataView;
    }

    @Override
    public final GridListDataView<T> setItems(Collection<T> items) {
        clear();
        load(items);
        return dataView;
    }

    @SuppressWarnings("unchecked")
    public void load(T... items) {
        load(Arrays.asList(items));
    }

    public void load(Collection<T> items) {
        clear();
        addAll(items);
    }

    protected T selected(Editor<T> editor) {
        clearAlerts();
        T o = getSelected();
        if(o == null) {
            if(editor != null && editor.isOpen()) {
                o = editor.getItem();
                editor.cancel();
                select(o);
                return o;
            }
            switch(size()) {
                case 0 -> {
                    warning(NOTHING_TO_SELECT);
                    return null;
                }
                case 1 -> {
                    o = get(0);
                    select(o);
                    return o;
                }
            }
            warning(NOTHING_SELECTED);
        }
        return o;
    }

    /**
     * Get the currently selected instance. If nothing is selected, a warning message is displayed and
     * <code>null</code> is returned. (If you simply want to a find the selected instance without displaying
     * a warning message, you may use {@link #getSelected()}).
     *
     * @return Selected instance or <code>null</code>.
     */
    public T selected() {
        return selected(null);
    }

    @Override
    public final void sort(Comparator<? super T> comparator) {
        //noinspection unchecked
        ((List<T>)getDataProvider().getData()).sort(comparator);
        refresh();
    }

    @Override
    public final void sort(List<GridSortOrder<T>> order) {
        super.sort(order);
    }

    @Override
    public final Class<T> getObjectClass() {
        return getDataClass();
    }

    /**
     * Inform the grid that we have appended an item.
     * It will append the item to the grid only if {@link #validateAppend(Object)} doesn't raise any exception.
     * For appending the item, it invokes {@link #doAppendAction(Object)} method.
     *
     * @param object Item that is newly added.
     */
    public final void itemAppended(T object) {
        if(shouldAppend(object)) {
            doAppendAction(object);
        }
    }

    /**
     * Internal method to validate the action.
     *
     * @param object Item being validated for the specified action.
     * @return True if the action can be carried out.
     */
    boolean shouldAppend(T object) {
        try {
            validateAppend(object);
        } catch(Exception e) {
            clearAlerts();
            warning(e);
            return false;
        }
        return true;
    }

    /**
     * Carry out the append action for the object. The object is already validated.
     *
     * @param object Item that is appended.
     */
    protected void doAppendAction(T object) {
        super.add(object);
    }

    /**
     * This is invoked when an item is being appended. The item is appended only if this method doesn't raise
     * any exception.
     *
     * @param object Item that is appended.
     * @throws Exception The exception should be a user-friendly one because it will be displayed on the screen.
     */
    protected void validateAppend(T object) throws Exception {
    }

    /**
     * Inform the grid that we have inserted a new item.
     * It will add the item to the grid only if {@link #validateInsert(Object)} doesn't raise any exception.
     * For adding the item, it invokes {@link #doInsertAction(Object)} method.
     *
     * @param object Item that is newly added.
     */
    public final void itemInserted(T object) {
        if(shouldInsert(object)) {
            doInsertAction(object);
        }
    }

    /**
     * Internal method to validate the action.
     *
     * @param object Item being validated for the specified action.
     * @return True if the action can be carried out.
     */
    boolean shouldInsert(T object) {
        try {
            validateInsert(object);
        } catch(Exception e) {
            clearAlerts();
            warning(e);
            return false;
        }
        return true;
    }

    /**
     * Inform the grid that we have appended an item.
     * It will append the item to the grid only if {@link #validateAppend(Object)} doesn't raise any exception.
     * For appending the item, it invokes {@link #doAppendAction(Object)} method.
     * <p>Note: This method is an alias to {@link #itemAppended(Object)} method.</p>
     *
     * @param object Item that is newly added.
     */
    public void append(T object) {
        itemAppended(object);
    }

    /**
     * Carry out the insert action for the object. The object is already validated.
     *
     * @param object Item that is newly added.
     */
    protected void doInsertAction(T object) {
        super.add(object);
    }

    /**
     * This is invoked when a newly created item is being inserted. The item is added only if this method doesn't raise
     * any exception.
     *
     * @param object Item that is newly added.
     * @throws Exception The exception should be a user-friendly one because it will be displayed on the screen.
     */
    protected void validateInsert(T object) throws Exception {
    }

    /**
     * Inform the grid that we have updated an item.
     * It will update the item on the grid only if {@link #validateUpdate(Object)} doesn't raise any exception.
     * For updating the item, it invokes {@link #doUpdateAction(Object)} method.
     *
     * @param object Item that is updated.
     */
    public final void itemUpdated(T object) {
        if(shouldUpdate(object)) {
            doUpdateAction(object);
        }
    }

    /**
     * Internal method to validate the action.
     *
     * @param object Item being validated for the specified action.
     * @return True if the action can be carried out.
     */
    boolean shouldUpdate(T object) {
        try {
            validateUpdate(object);
        } catch(Exception e) {
            clearAlerts();
            warning(e);
            return false;
        }
        return true;
    }

    /**
     * Carry out the update action for the object. The object is already validated.
     *
     * @param object Item that is updated.
     */
    protected void doUpdateAction(T object) {
        refresh(object);
    }

    /**
     * This is invoked when an item is being updated. The item is updated only if this method doesn't raise
     * any exception.
     *
     * @param object Item that is updated.
     * @throws Exception The exception should be a user-friendly one because it will be displayed on the screen.
     */
    protected void validateUpdate(T object) throws Exception {
    }

    /**
     * Inform the grid that we have deleted an item.
     * It will delete the item from the grid only if {@link #validateDelete(Object)} doesn't raise any exception.
     * For deleting the item, it invokes {@link #doDeleteAction(Object)} method.
     *
     * @param object Item that is deleted.
     */
    public final void itemDeleted(T object) {
        if(shouldDelete(object)) {
            doDeleteAction(object);
        }
    }

    /**
     * Internal method to validate the action.
     *
     * @param object Item being validated for the specified action.
     * @return True if the action can be carried out.
     */
    boolean shouldDelete(T object) {
        try {
            validateDelete(object);
        } catch(Exception e) {
            clearAlerts();
            warning(e);
            return false;
        }
        return true;
    }

    /**
     * Carry out the delete action for the object. The object is already validated.
     *
     * @param object Item that is deleted.
     */
    protected void doDeleteAction(T object) {
        remove(object);
        refresh();
    }

    /**
     * This is invoked when an item is being deleted. The item is deleted only if this method doesn't raise
     * any exception.
     *
     * @param object Item that is deleted.
     * @throws Exception The exception should have a user-friendly message because it will be displayed on the screen.
     */
    protected void validateDelete(T object) throws Exception {
    }

    /**
     * Inform the grid that we have undeleted an item.
     * It will undelete the item from the grid only if {@link #validateUndelete(Object)} doesn't raise any exception.
     * For undeleting the item, it invokes {@link #doUndeleteAction(Object)} method.
     *
     * @param object Item that is deleted.
     */
    public final void itemUndeleted(T object) {
        if(shouldUndelete(object)) {
            doUndeleteAction(object);
        }
    }

    /**
     * Internal method to validate the action.
     *
     * @param object Item being validated for the specified action.
     * @return True if the action can be carried out.
     */
    boolean shouldUndelete(T object) {
        try {
            validateUndelete(object);
        } catch(Exception e) {
            clearAlerts();
            warning(e);
            return false;
        }
        return true;
    }

    /**
     * Carry out the undelete action for the object. The object is already validated.
     *
     * @param object Item that is undeleted.
     */
    protected void doUndeleteAction(T object) {
        refresh(object);
    }

    /**
     * This is invoked when an item is being undeleted. The item is undeleted only if this method doesn't raise
     * any exception.
     *
     * @param object Item that is undeleted.
     * @throws Exception The exception should be a user-friendly one because it will be displayed on the screen.
     */
    protected void validateUndelete(T object) throws Exception {
    }

    /**
     * Inform the grid that we have reloaded an item.
     * It will reload the item from the grid only if {@link #validateReload(Object)} doesn't raise any exception.
     * For reloading the item, it invokes {@link #doReloadAction(Object)} method.
     *
     * @param object Item that is deleted.
     */
    public final void itemReloaded(T object) {
        if(shouldUndelete(object)) {
            doReloadAction(object);
        }
    }

    /**
     * Internal method to validate the action.
     *
     * @param object Item being validated for the specified action.
     * @return True if the action can be carried out.
     */
    boolean shouldReload(T object) {
        try {
            validateReload(object);
        } catch(Exception e) {
            clearAlerts();
            warning(e);
            return false;
        }
        return true;
    }

    /**
     * Carry out the reload action for the object. The object is already validated.
     *
     * @param object Item that is undeleted.
     */
    protected void doReloadAction(T object) {
        refresh(object);
    }

    /**
     * Carry out the "reload all" action. All validation are already done before invoking this.
     */
    protected void doReloadAllAction() {
        refresh();
    }

    /**
     * This is invoked when an item is being undeleted. The item is undeleted only if this method doesn't raise
     * any exception.
     *
     * @param object Item that is undeleted.
     * @throws Exception The exception should be a user-friendly one because it will be displayed on the screen.
     */
    protected void validateReload(T object) throws Exception {
    }

    /**
     * Prefix string that is added to the "action" string to determine the actual {@link UIAction} to be checked. See
     * {@link #actionAllowed(String)}. For example, {@link com.storedobject.ui.inventory.POBrowser} returns the value
     * "PO" for this method.
     *
     * @return Prefix string. Default implementation returns null. That means that all the actions are allowed.
     */
    protected String getActionPrefix() {
        return null;
    }

    /**
     * Check whether a specific action is allowed or not. An action is defined in the UI logic as a keyword like
     * "SEND-ITEMS", "PLACE-ORDER", "RECEIVE-ITEMS", "PRINT-VOUCHER", etc. and there could be corresponding access
     * control applicable within the logic. The user's groups determine whether that user can carry out that action or
     * not. This method returns <code>true/false</code> to denote that the user can carry out the action or not.
     * However, it is up to the logic to decide the course of action.
     * <p>The user's groups can be configured to allow various UI actions ({@link com.storedobject.core.UIAction}.
     * Each {@link com.storedobject.core.UIAction} represents a unique "action" string ({@link UIAction#getAction()})
     * and that value should be equal to {@link #getActionPrefix()} + "-" + action in order to allow that action.</p>
     *
     * @param action Action string.
     * @return True/false. Please note that it will always return <code>true</code> if {@link #getActionPrefix()}
     * returns <code>null</code>.
     */
    public boolean actionAllowed(String action) {
        return actionAllowed(getTransactionManager(), action, getActionPrefix());
    }

    /**
     * Same as {@link #actionAllowed(String)} except that this shows a message to the user about it if the action is not
     * allowed.
     *
     * @param action Action string.
     * @return True/false.
     */
    public final boolean canAllowAction(String action) {
        if(!actionAllowed(action)) {
            clearAlerts();
            warning(ACTION_NOT_ALLOWED);
            return false;
        }
        return true;
    }

    static boolean actionAllowed(TransactionManager tm, String action, String prefix) {
        prefix = StoredObject.toCode(prefix);
        if(prefix.isEmpty()) {
            return true;
        }
        action = StoredObject.toCode(action);
        if(!action.startsWith(prefix + "-")) {
            action = prefix + "-" + action;
            while(action.contains("--")) {
                action = action.replace("--", "-");
            }
        }
        return tm.actionAllowed(action);
    }
}
