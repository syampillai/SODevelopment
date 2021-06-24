package com.storedobject.ui;

import com.storedobject.common.Reentrant;
import com.storedobject.core.HasLogic;
import com.storedobject.core.Logic;
import com.storedobject.core.TransactionControl;
import com.storedobject.core.TransactionManager;
import com.storedobject.vaadin.ExecutableView;

public interface Transactional extends Reentrant, com.storedobject.vaadin.ExecutableView, HasLogic {

    @Override
    default void run() {
        execute();
    }

    @Override
    default void execute() {
        ExecutableView.super.execute();
    }

    default boolean commit(TransactionControl transaction) {
        if(transaction.commit()) {
            return true;
        }
        getApplication().access(() -> Application.error(transaction.getError()));
        return false;
    }

    default boolean transact(TransactionManager.Transact transact) {
        return transact(null, transact);
    }

    default boolean transact(Logic logic, TransactionManager.Transact transact) {
        try {
            int no = getTransactionManager().transact(logic, transact);
            if(no > 0) {
                getApplication().access(() ->
                        Application.warning("Transaction: " + no + ", Approvals Required: " +
                                (logic == null ? 0 : logic.getApprovalCount())));
            }
            return true;
        } catch(Exception e) {
            getApplication().access(() -> Application.error(e));
        }
        return false;
    }

    default TransactionManager getTransactionManager() {
        return ((Application)getApplication()).getTransactionManager();
    }

    default void setLogic(Logic logic) {
    }

    default Logic getLogic() {
        return null;
    }
}