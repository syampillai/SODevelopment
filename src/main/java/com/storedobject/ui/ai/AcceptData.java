package com.storedobject.ui.ai;

import com.storedobject.ui.Application;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

import java.util.concurrent.Semaphore;

/**
 * Represents an abstract class for handling data acceptance and form submission workflows.
 * It provides mechanisms to collect, modify, and retrieve data encapsulated in a JSON map.
 * The class extends the functionality of {@link DataForm} and integrates with a {@link Knowledge} instance.
 * This class uses a semaphore to ensure thread-safe execution of critical regions.
 * <p>
 *     This class can be used to accept data from users, modify it, and submit it to a {@link Knowledge} instance as part of
 *     tool-calls for the AI chat for creating interactive workflows.
 * </p>
 * <p>
 *     Note: Please make sure that the map values are set in the {@link #process()} method.
 * </p>
 *
 * @param <T> The type of data to be accepted and processed.
 *
 * @author Syam
 */
public abstract class AcceptData<T> extends DataForm {

    private final Semaphore semaphore = new Semaphore(1, true);
    private final Knowledge knowledge;
    private boolean dataCollected = false;
    /**
     * The class of the data to be accepted.
     */
    protected final Class<T> dataClass;
    /**
     * The data that is accepted. Make sure that this is set in the {@link #process()} method.
     */
    protected T data;

    /**
     * Constructs an instance of the AcceptData class with the specified knowledge, caption, and JSONMap parameters.
     *
     * @param dataClass the class of the data to be accepted.
     * @param knowledge the Knowledge object that provides functionality to manage knowledge topics
     *                  and interact with chat views.
     * @param caption   the caption to be displayed in the data form; typically used as a title or label.
     */
    public AcceptData(Class<T> dataClass, Knowledge knowledge, String caption) {
        super(caption);
        this.dataClass = dataClass;
        this.knowledge = knowledge;
    }

    /**
     * Retrieves the Application instance associated with the current context.
     * If the `knowledge` field is null, the method defaults to retrieving the
     * application from the superclass. Otherwise, it returns the application
     * managed by the `knowledge` object.
     *
     * @return the Application instance tied to the current context, either from
     *         the superclass or the `knowledge` object.
     */
    @SuppressWarnings("unchecked")
    public final Application getApplication() {
        return knowledge == null ? super.getApplication() : knowledge.application;
    }

    /**
     * Retrieves the Knowledge instance associated with this object.
     *
     * @return the Knowledge instance that provides functionality for managing
     *         knowledge topics and interacting with a chat view.
     */
    public final Knowledge getKnowledge() {
        return knowledge;
    }

    /**
     * Retrieves the data object associated with the AcceptData instance.
     * The method ensures thread-safe access to the data and executes necessary
     * operations if data has not yet been collected.
     *
     * @return the data object that is accepted. This should be set in the {@link #process()} method.
     */
    public final T retrieveData() {
        if(!dataCollected) {
            if(!executing()) {
                knowledge.semaphore.acquireUninterruptibly();
                semaphore.acquireUninterruptibly();
                execute();
            }
            semaphore.acquireUninterruptibly();
            semaphore.release();
        }
        return data;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        data = null;
        dataCollected = true;
        knowledge.application.access(() -> super.execute(parent, doNotLock));
    }

    @Override
    protected void cancel() {
        super.cancel();
        dataCollected = false;
    }

    @Override
    public void clean() {
        knowledge.semaphore.release();
        semaphore.release();
        super.clean();
    }

    /**
     * Get the label of the data that is accepted.
     * @return Label of the data.
     */
    public abstract String getDataLabel();
}
