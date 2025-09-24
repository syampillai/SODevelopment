package com.storedobject.core;

import com.storedobject.common.IO;
import com.storedobject.common.InputOutputStream;

import java.io.*;
import java.util.HashMap;

public abstract class StreamContentProducer implements ContentProducer, Closeable {

    private static final HashMap<String, Boolean> ACCESS = new HashMap<>();
    protected OutputStream out;
    private Writer outWriter;
    private InputStream in;
    private InputOutputStream io;
    private boolean executing = false;
    private TransactionManager tm;
    protected Entity entity;
    private Throwable error;

    public StreamContentProducer() {
        this(null);
    }

    public StreamContentProducer(OutputStream out) {
        this.out = out;
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
            if(io == null) {
                io = new InputOutputStream();
                io.setReusable(true);
            }
            out = io.getOutputStream();
            in = io.getInputStream();
        }
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
        if(outWriter == null) {
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