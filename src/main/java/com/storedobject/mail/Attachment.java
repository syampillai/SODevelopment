package com.storedobject.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import jakarta.activation.DataSource;

public class Attachment extends StoredObject implements Detail, DataSource {

    private Id fileId;
    private String fileName;
    private StreamData file;

    public Attachment() {
    }

    public static void columns(Columns columns) {
        columns.add("File", "id");
        columns.add("FileName", "text");
    }

    public void setFile(Id fileId) {
        this.fileId = fileId;
        file = null;
    }

    public void setFile(BigDecimal idValue) {
        setFile(new Id(idValue));
    }

    public void setFile(StreamData file) {
        setFile(file.getId());
    }

    public Id getFileId() {
        return fileId;
    }

    public StreamData getFile() {
    	if(file == null) {
    		file = get(StreamData.class, fileId);
    	}
    	return file;
    }

    public String getContentID() {
        return fileId.toString();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(required = false)
    public String getFileName() {
        return fileName;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        fileId = tm.checkType(this, fileId, StreamData.class, false);
        super.validateData(tm);
    }

	@Override
	public Id getUniqueId() {
		return getFileId();
	}

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == Mail.class;
	}

	@Override
	public String getContentType() {
		return getFile().getMimeType();
	}

	@Override
	public InputStream getInputStream() {
		return getFile().getContent();
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException(new UnsupportedOperationException());
	}
}
