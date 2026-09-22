package com.storedobject.ui.ai;

import com.storedobject.core.JSONMap;
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
 * @author Syam
 */
public abstract class AcceptData extends DataForm {

    private final Semaphore semaphore = new Semaphore(1, true);
    private final Knowledge knowledge;
    /**
     * A JSONMap instance used to store and manage key-value pairs representing additional data
     * in the AcceptData class. This variable is initialized during the construction of an
     * AcceptData object, either as a user-provided JSONMap or as an empty JSONMap when not specified.
     * <p>
     * This map is designed to hold supplementary information that complements the data
     * managed within the AcceptData instance, ensuring extensibility and flexibility for diverse
     * data-handling scenarios.
     * </p><p>
     * It is declared as {@code final} to ensure that the reference cannot be reassigned after
     * initialization and as {@code protected} to allow access within subclasses
     * of the AcceptData class.
     * </p>
     */
    protected final JSONMap map;
    private boolean dataCollected = false;

    /**
     * Constructs an instance of the AcceptData class with the specified knowledge and caption.
     * This constructor utilizes an empty JSONMap as the default map.
     *
     * @param knowledge the Knowledge object that provides functionality to manage knowledge topics
     *                  and interact with chat views.
     * @param caption   the caption to be displayed in the data form; typically used as a title or label.
     */
    public AcceptData(Knowledge knowledge, String caption) {
        this(knowledge, caption, null);
    }

    /**
     * Constructs an instance of the AcceptData class with the specified knowledge, caption, and JSONMap parameters.
     *
     * @param knowledge the Knowledge object that provides functionality to manage knowledge topics
     *                  and interact with chat views.
     * @param caption   the caption to be displayed in the data form; typically used as a title or label.
     * @param map       the JSONMap object containing key-value pairs for additional data;
     *                  if null, an empty JSONMap is used by default.
     */
    public AcceptData(Knowledge knowledge, String caption, JSONMap map) {
        super(caption);
        this.knowledge = knowledge;
        this.map = map == null ? new JSONMap() : map;
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
     * Retrieves the JSONMap object associated with the AcceptData instance.
     * The method ensures thread-safe access to the map and executes necessary
     * operations if data has not yet been collected. If an error occurs during
     * execution, the map is updated with an error message.
     *
     * @return the JSONMap object containing key-value pairs that represent the
     *         data managed by this instance.
     */
    public final JSONMap getMap() {
        if(!dataCollected) {
            if(!executing()) {
                try {
                    knowledge.semaphore.acquire();
                } catch (InterruptedException e) {
                    err();
                    return map;
                }
                execute();
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    err();
                }
                knowledge.semaphore.release();
                semaphore.release();
            } else {
                try {
                    semaphore.acquire();
                    semaphore.release();
                } catch (InterruptedException e) {
                    err();
                }
            }
        }
        return map;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        map.remove("error");
        if(dataCollected) {
            dataCollected = false;
        }
        knowledge.application.access(() -> super.execute(parent, doNotLock));
        try {
            semaphore.acquire();
        } catch (InterruptedException ignored) {
        }
    }

    private void err() {
        map.put("error", "Unable to provide - system busy");
    }

    @Override
    protected void cancel() {
        super.cancel();
        map.put("error", "Unable to provide - user cancelled the operation");
    }

    @Override
    public void clean() {
        dataCollected = true;
        semaphore.release();
        super.clean();
    }
}
