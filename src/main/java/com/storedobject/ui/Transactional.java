package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.*;
import com.storedobject.vaadin.ExecutableView;

public interface Transactional extends Executable, com.storedobject.vaadin.ExecutableView, HasLogic {

    @Override
    default void run() {
        execute();
    }

    @Override
    default void execute() {
        ExecutableView.super.execute();
    }

    default boolean commit(TransactionControl transaction) {
        return false;
    }

    default boolean transact(TransactionManager.Transact transact) {
        return false;
    }

    default boolean transact(Logic logic, TransactionManager.Transact transact) {
        return false;
    }

    default TransactionManager getTransactionManager() {
        return null;
    }

    @Override
    default void setLogic(Logic logic) {
    }

    @Override
    default Logic getLogic() {
        return null;
    }
}