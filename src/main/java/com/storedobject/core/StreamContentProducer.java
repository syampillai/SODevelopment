package com.storedobject.core;

import com.storedobject.common.IO;
import com.storedobject.common.InputOutputStream;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

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

    public StreamContentProducer() {
        this(null);
    }

    public StreamContentProducer(OutputStream out) {
        this.out = out;
        io.setReusable(true);
        io.setDataListener(() -> {
            ready.countDown();
            io.setDataListener(null);
        });
    }

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
        String key = entity.getId() + type;
        Boolean blocked = ACCESS.get(key);
        if(blocked == null) {
            AccessControl ac = AccessControl.get(entity);
            blocked = switch (type) {
                case "PDF" -> ac.getBlockPDF();
                case "Excel" -> ac.getBlockExcel();
                case "ODT" -> ac.getBlockODT();
                case "ODS" -> ac.getBlockODS();
                default -> true;
            };
            ACCESS.put(key, blocked);
        }
        return blocked;
    }
}