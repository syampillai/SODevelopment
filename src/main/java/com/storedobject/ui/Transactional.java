package com.storedobject.ui;

import com.storedobject.common.Reentrant;
import com.storedobject.core.*;
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
        return transact(null, null, transact);
    }

    default boolean transact(Logic logic, Transaction pseudo, TransactionManager.Transact transact) {
        try {
            PseudoTransaction pt;
            if(pseudo == null || pseudo instanceof PseudoTransaction) {
                pt = (PseudoTransaction) pseudo;
            } else {
                throw new Invalid_State("Invalid transaction mix");
            }
            int no = getTransactionManager().transact(logic, pt, transact);
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