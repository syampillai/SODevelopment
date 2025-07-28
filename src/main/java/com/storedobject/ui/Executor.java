package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.TransactionManager;

/**
 * Represents an abstract base class for defining an executable component.
 * The {@code Executor} class provides a structured framework for execution-related
 * functionality by managing an associated {@link Application} instance.
 * Subclasses are expected to define specific execution behavior.
 * The {@code Executor} class implements the {@link Executable} interface, which
 * signifies its ability to execute designated actions or processes.
 *
 * @author Syam
 */
public abstract class Executor implements Executable {

    /**
     * Holds a reference to the {@link Application} instance associated with this {@code Executor}.
     * This variable is immutable and is initialized through the constructor.
     * It provides access to application-level settings and resources required
     * for execution-related operations within the {@code Executor}.
     */
    protected final Application application;

    /**
     * Constructs a new {@code Executor} object with the specified {@link Application}.
     *
     * @param a the {@link Application} instance to associate with this executor
     */
    public Executor(Application a) {
        this.application = a;
    }

    /**
     * Retrieves the {@link Application} instance associated with this executor.
     *
     * @return the {@link Application} instance used by this executor
     */
    public final Application getApplication() {
        return application;
    }

    /**
     * Retrieves the {@link TransactionManager} instance associated with the current {@link Application}.
     *
     * @return The {@link TransactionManager} instance from the associated {@link Application}, or null
     *         if no {@link TransactionManager} is available.
     */
    public final TransactionManager getTransactionManager() {
        return application.getTransactionManager();
    }
}