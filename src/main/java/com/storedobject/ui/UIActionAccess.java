package com.storedobject.ui;

import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionManager;
import com.storedobject.core.UIAction;

/**
 * An interface that provides methods for checking whether a specific action is allowed or not.
 *
 * @author Syam
 */
public interface UIActionAccess {

    /**
     * A prefix-string that is added to the "action" string to determine the actual {@link UIAction} to be checked. See
     * {@link #actionAllowed(String)}. For example, {@link com.storedobject.ui.inventory.POBrowser} returns the value
     * "PO" for this method.
     *
     * @return Prefix string. The default implementation returns null.
     */
    default String getActionPrefix() {
        return null;
    }

    /**
     * Get the transaction manager associated with this logic.
     * @return Transaction manager.
     */
    TransactionManager getTransactionManager();

    /**
     * Check whether a specific action is allowed or not. An action is defined in the UI logic as a keyword like
     * "SEND-ITEMS", "PLACE-ORDER", "RECEIVE-ITEMS", "PRINT-VOUCHER", etc. and there could be corresponding access
     * control applicable within the logic. The user's groups determine whether that user can carry out that action or
     * not. This method returns <code>true/false</code> to denote that the user can carry out the action or not.
     * However, it is up to the logic to decide the course of action.
     * <p>The user's groups can be configured to allow various UI actions ({@link com.storedobject.core.UIAction}.
     * Each {@link com.storedobject.core.UIAction} represents a unique "action" string ({@link UIAction#getAction()})
     * and that value should be equal to {@link #getActionPrefix()} + "-" + action to allow that action.</p>
     *
     * @param action Action string.
     * @return True/false. Please note that it will always return <code>true</code> if {@link #getActionPrefix()}
     * returns <code>null</code>.
     */
    default boolean actionAllowed(String action) {
        return actionAllowed(getTransactionManager(), action, getActionPrefix());
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
