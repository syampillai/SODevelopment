package com.storedobject.core;

import com.storedobject.common.IO;
import com.storedobject.common.InputOutputStream;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * The StreamContentProducer class is an abstract implementation of the {@link ContentProducer} interface
 * with additional support for managing output streams, transaction management, and access control.
 * This class is designed for producing content in a stream-based manner, where the actual content generation
 * must be implemented by subclasses.
 * <p>
 * It provides facilities for handling concurrent execution, stream and writer management, and integration
 * with a transaction management system. Additionally, it incorporates mechanisms for determining and enforcing
 * content access restrictions.
 * </p>
 *
 * @author Syam
 */
public abstract class StreamContentProducer implements ContentProducer, Closeable {

    private static final HashMap<String, Boolean> ACCESS = new HashMap<>();
    protected OutputStream out;
    private Writer outWriter;
    private InputStream in;
    private final InputOutputStream io = new InputOutputStream();
    private boolean executing = false;
    private TransactionManager tm;
    protected Entity entity;
    private Throwable error;
    private final CountDownLatch ready = new CountDownLatch(1);

    /**
     * Default constructor for creating a StreamContentProducer instance.
     * Initializes the instance without specifying an output stream.
     * The output stream will be set to null.
     */
    public StreamContentProducer() {
        this(null);
    }

    /**
     * Constructs a StreamContentProducer with the specified output stream.
     * This constructor initializes the instance with the provided OutputStream
     * and sets up a data listener to handle readiness notifications.
     *
     * @param out The output stream where the generated content will be written. Cannot be null.
     */
    public StreamContentProducer(OutputStream out) {
        this.out = out;
        io.setReusable(true);
        io.setDataListener(() -> {
            ready.countDown();
            io.setDataListener(null);
        });
    }

    /**
     * Initiates the content production process and manages its lifecycle.
     * This method ensures thread-safe execution by allowing only one thread
     * to execute the critical section at a time. It prepares the output stream,
     * retrieves the input content, generates the content, and finally, closes
     * the output resources. In case of any error during the content generation
     * or processing, it handles the exception by logging it and invoking the
     * abort mechanism.
     * <pre>
     * The following describes the key stages in the process:
     * - Thread-safe execution is ensured by synchronizing on the current object
     *   and checking the executing flag.
     * - If the output stream has not been initialized, it retrieves it from
     *   the associated IO instance.
     * - Content generation is initiated via the abstract `generateContent`
     *   method, which subclasses must implement to define specific content
     *   creation logic.
     * - Resources are properly closed by invoking the `close` method.
     * - On encountering a throwable, the exception is logged via the
     *   ApplicationServer, and the abort mechanism is triggered with the
     *   error details.
     * </pre>
     * Note: Implementations of the abstract `generateContent` method
     * must ensure that content is written to the 'out' stream.
     * Thread starvation is mitigated by calling `Thread.yield()`
     * if the current thread cannot acquire execution.
     */
    @Override
    public void produce() {
        while(true) {
            synchronized (this) {
                if(!executing) {
                    executing = true;
                    break;
                }
            }
            Thread.yield();
        }
        if(out == null) {
            out = io.getOutputStream();
        }
        getContent();
        try {
            generateContent();
            close();
        } catch(Throwable e) {
            ApplicationServer.log(e);
            abort(e);
        }
        synchronized (this) {
            executing = false;
        }
    }

    /**
     * Waits for the readiness signal, blocking the current thread until the condition is met.
     * This method ensures that dependent threads can coordinate their execution
     * based on the readiness state. It uses the `ready` CountDownLatch to manage synchronization.
     * Any interruption of the waiting thread is caught and ignored, ensuring that
     * the method continues operation without propagating the `InterruptedException`.
     */
    @Override
    public void ready() {
        try {
            ready.await();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public String getFileName() {
        return null;
    }

    /**
     * Generate the content here and write to 'out'
     *
     * @throws Exception Can throw anything
     */
    public abstract void generateContent() throws Exception;

    @Override
    public InputStream getContent() {
        if(in == null) {
            in = io.getInputStream();
        }
        return in;
    }

    @Override
    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    public TransactionManager getTransactionManager() {
        return tm;
    }

    protected Writer getWriter() {
        if(outWriter == null && out != null) {
            outWriter = IO.getWriter(out);
        }
        return outWriter;
    }

    @Override
    public void close() {
        IO.close(outWriter, out);
        out = null;
        outWriter = null;
    }

    @Override
    public void abort(Throwable error) {
        this.error = error;
        io.abort();
    }

    @Override
    public Throwable getError() {
        return error;
    }

    /**
     * Retrieves the report format configuration associated with the current transaction manager
     * or entity context. If a transaction manager is available, the report format is obtained
     * based on it. Otherwise, the report format is derived from the current entity.
     *
     * @return The report format associated with the current transaction manager if available,
     *         otherwise the report format associated with the current entity.
     */
    public final ReportFormat getReportFormat() {
        TransactionManager tm = getTransactionManager();
        if(tm != null) {
            return ReportFormat.get(tm);
        }
        return ReportFormat.get(getEntity());
    }

    @Override
    public final Entity getEntity() {
        if(entity != null) {
            return entity;
        }
        return ContentProducer.super.getEntity();
    }

    public final boolean isBlocked(String type) {
        return isBlocked(this, type);
    }

    public static boolean isBlocked(ContentProducer contentProducer, String type) {
        TransactionManager tm = contentProducer.getTransactionManager();
        if(tm != null && tm.getUser().isAdmin()) return false;
        Entity entity = contentProducer.getEntity();
        if(entity == null) return false;
        type = type == null ? "" : type.toUpperCase();
        String key = entity.getId() + type;
        Boolean blocked = ACCESS.get(key);
        if(blocked == null) {
            AccessControl ac = AccessControl.get(entity);
            blocked = switch (type) {
                case "PDF" -> ac.getBlockPDF();
                case "EXCEL", "XLSX" -> ac.getBlockExcel();
                case "ODT" -> ac.getBlockODT();
                case "ODS" -> ac.getBlockODS();
                default -> ac.getBlockPDF() || ac.getBlockExcel() || ac.getBlockODT() || ac.getBlockODS();
            };
            ACCESS.put(key, blocked);
        }
        return blocked;
    }
}