package com.storedobject.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileContent implements ContentProducer {

    private final File file;
    private final String contentType;
    private String fileName, fileExtension;
    private TransactionManager tm;
    private Throwable error;

    public FileContent(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    @Override
    public void produce() {
    }

    @Override
    public InputStream getContent() throws Exception {
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            error = e;
            throw e;
        }
    }

    @Override
    public String getFileExtension() {
        if(fileExtension == null) {
            int p = file.getName().lastIndexOf('.');
            if(p > 0) {
                fileExtension = file.getName().substring(p + 1);
            }
        }
        return fileExtension;
    }

    @Override
    public String getFileName() {
        if(fileName == null) {
            fileName = file.getName();
            int p = fileName.lastIndexOf('.');
            if(p > 0) {
                fileName = fileName.substring(0, p);
            }
        }
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        int p = fileName.lastIndexOf('.');
        if(p > 0) {
            this.fileExtension = fileName.substring(p + 1);
            this.fileName = fileName.substring(0, p);
        }
    }

    @Override
    public final String getContentType() {
        return contentType;
    }

    @Override
    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return tm;
    }

    @Override
    public Throwable getError() {
        return error;
    }
}
