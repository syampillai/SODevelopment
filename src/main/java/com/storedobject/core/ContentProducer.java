package com.storedobject.core;

import com.storedobject.common.Executable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The ContentProducer interface defines a contract for producing and managing content.
 * It supports functionality for generating content, handling file-related metadata,
 * managing transactions, and providing stream-based access to the content.
 * ContentProducer implementations may handle content generation processes,
 * error handling, and saving content to file storage.
 *
 * @author Syam
 */
public interface ContentProducer extends Executable, ContentType, RequiresTransactionManager {

    /**
     * Certain types of content may have to be produced. This method is used to do that.
     */
    void produce();

    /**
     * Retrieves the content as an InputStream.
     * <p>Note: This may not invoke {@link #produce()} and it may just return the content if it is already available.
     * It is implementation-dependent.</p>
     *
     * @return an InputStream representing the content. This may be null if the content is not yet available.
     * @throws Exception if there is an error during content retrieval
     */
    InputStream getContent() throws Exception;

    /**
     * Retrieves the file extension associated with the content.
     *
     * @return a String representing the file extension, or an empty string if no extension is available.
     */
    String getFileExtension();

    /**
     * Retrieves the name of the file associated with the content.
     *
     * @return a String representing the name of the file, or null if the file name is unavailable.
     */
    String getFileName();

    /**
     * Signals that the content is ready to consume.
     * The exact behavior or implications of this readiness state are dependent on the implementation.
     */
    default void ready() {
    }

    @Override
    default TransactionManager getTransactionManager() {
        return null;
    }

    /**
     * Retrieves the system entity associated with the current transaction manager.
     * This method checks if a {@link TransactionManager} instance is available and,
     * if so, retrieves the associated {@link SystemEntity}. If no transaction
     * manager is available, it returns null.
     *
     * @return the {@link SystemEntity} associated with the current transaction
     * manager, or null if the transaction manager is not available.
     */
    default SystemEntity getSystemEntity() {
        TransactionManager tm = getTransactionManager();
        return tm == null ? null : tm.getEntity();
    }

    /**
     * Retrieves the {@link Entity} associated with the current context.
     * This method internally checks for a {@link SystemEntity} using {@link #getSystemEntity()},
     * and if it exists, retrieves the corresponding {@link Entity}.
     *
     * @return an {@link Entity} object if a {@link SystemEntity} is available and associated with an {@link Entity},
     *         or null if no such association exists.
     */
    default Entity getEntity() {
        SystemEntity se = getSystemEntity();
        return se == null ? null : se.getEntity();
    }

    /**
     * Extracts and retrieves the content as an InputStream. If the content is not
     * immediately available, this method triggers the production of the content in a
     * separate virtual thread and waits for the content to become available.
     *
     * @return an InputStream representing the extracted content, or null if the content
     *         cannot be produced or retrieved.
     * @throws Exception if there is an error during content production or retrieval.
     */
    default InputStream extractContent() throws Exception {
        Thread p = Thread.ofVirtual().start(this::produce);
        InputStream in = getContent();
        while(in == null && p.isAlive()) {
            Thread.yield();
            in = getContent();
        }
        return in;
    }

    /**
     * Retrieves a default implementation of the {@link StreamDataProvider}.
     * The returned {@link StreamDataProvider} suitable for saving content to the database via {@link StreamData}.
     *
     * @return An instance of {@link StreamDataProvider} tailored to provide data of this producer for saving to the database.
     */
    default StreamDataProvider getStreamDataProvider() {
        return new StreamDataProvider() {

            @Override
            public void writeStream(StreamData streamData, OutputStream output) {
            }

            @Override
            public InputStream getStream(StreamData streamData) throws Exception {
                return extractContent();
            }
        };
    }

    /**
     * Creates and returns a new instance of {@link StreamData}, populating it with
     * the content type and stream data provider associated with this instance.
     *
     * @return an instance of {@link StreamData} containing the content type and stream
     *         data provider of this producer.
     */
    default StreamData getStreamData() {
        StreamData sd = new StreamData();
        sd.setContentType(getContentType());
        sd.setStreamDataProvider(getStreamDataProvider());
        return sd;
    }

    /**
     * Executes the operation associated with the content producer.
     * This method serves as the entry point for triggering the production of content.
     * The implementation calls the {@link #produce()} method to perform the content production.
     */
    @Override
    default void execute() {
        produce();
    }

    /**
     * Saves the content produced by this instance to the specified folder path
     * and associates it with the provided transaction. The content is retrieved
     * using the {@link StreamData} generated by this instance.
     *
     * @param folderPath The folder path where the content should be saved.
     *                   This must be a valid directory path of the SO platform database.
     * @param transaction The transaction to associate with the saved content.
     * @return A {@link FileData} object representing the saved file, including
     *         metadata and a reference to the stored content.
     * @throws Exception If an error occurs during the save operation, such as
     *                   invalid parameters, failure during content production,
     *                   or failure to save the data to the provided path.
     */
    default FileData saveTo(String folderPath, Transaction transaction) throws Exception {
        return FileData.create(folderPath, getStreamData(), transaction);
    }

    /**
     * Saves the given {@link FileData} instance using the content produced by this instance
     * and associates it with the specified {@link Transaction}. The process involves generating
     * a {@link StreamData} object, saving it, assigning it to the provided {@link FileData} instance,
     * and then saving the {@link FileData} itself.
     *
     * @param fileData The {@link FileData} object to save. This object will be populated with the
     *                 produced content and metadata during the operation.
     * @param transaction The {@link Transaction} to associate with the save operation.
     * @return The updated {@link FileData} object containing the saved content and metadata.
     * @throws Exception If an error occurs during the save operation, such as failure to generate or
     *                   save the {@link StreamData}, or issues related to the {@link Transaction}.
     */
    @SuppressWarnings("UnusedReturnValue")
    default FileData saveTo(FileData fileData, Transaction transaction) throws Exception {
        StreamData sd = getStreamData();
        sd.save(transaction);
        fileData.setFile(sd);
        fileData.save(transaction);
        return fileData;
    }

    /**
     * Saves the current content to a specified folder path using the provided {@link TransactionManager}.
     * This method handles the transaction lifecycle, including committing or rolling back
     * in case of exceptions. The content is saved by creating a {@link StreamData} object
     * retrieved from this producer, associating it with the transaction, and then saving it
     * to the given folder path in the SO platform database.
     *
     * @param folderPath The folder path where the content should be saved. It must be a valid directory path.
     * @param tm The {@link TransactionManager} used to manage the transaction during the save operation.
     * @return A {@link FileData} object representing the saved file, including metadata and a reference
     *         to the stored content.
     * @throws Exception If an error occurs during the save operation, including invalid parameters,
     *                   transaction creation failure, failure in content production, or writing issues.
     */
    default FileData saveTo(String folderPath, TransactionManager tm) throws Exception {
        Transaction t = null;
        try {
            t = tm.createTransaction();
            FileData fd = FileData.create(folderPath, getStreamData(), t);
            t.commit();
            return fd;
        } catch(Exception e) {
            if(t != null) {
                t.rollback();
            }
            throw e;
        }
    }

    /**
     * Saves the given {@link FileData} instance using the content produced by this instance
     * and associates it with the specified {@link TransactionManager}. This method manages
     * the transaction lifecycle and delegates the actual save operation to the underlying
     * transaction.
     *
     * @param fileData The {@link FileData} object to save. This object will be populated with
     *                 the produced content and metadata during the operation.
     * @param tm The {@link TransactionManager} used to manage the transaction during the save
     *           operation. Ensures proper transaction handling, including rollback in case of failure.
     * @return The updated {@link FileData} object containing the saved content and metadata.
     * @throws Exception If an error occurs during the save operation, including errors in
     *                   transaction handling, content generation, or saving process.
     */
    default FileData saveTo(FileData fileData, TransactionManager tm) throws Exception {
        tm.transact(t -> saveTo(fileData, t));
        return fileData;
    }

    /**
     * This method may be called when the content generation is aborted due to some error.
     * <p>Note: This could be invoked multiple times.</p>
     *
     * @param error Error.
     */
    default void abort(Throwable error) {
    }

    /**
     * Get the current error (set via {@link #abort(Throwable)}).
     *
     * @return Current error, if any.
     */
    default Throwable getError() {
        return null;
    }
}