package com.storedobject.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import jakarta.activation.DataSource;

import com.storedobject.core.Columns;
import com.storedobject.core.Detail;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StreamData;

public class Attachment extends StoredObject implements Detail, DataSource {

    public Attachment() {
    }

    public static void columns(Columns columns) {
    }

    public void setFile(Id fileId) {
    }

    public void setFile(BigDecimal idValue) {
    }

    public void setFile(StreamData file) {
    }

    public Id getFileId() {
        return null;
    }

    public StreamData getFile() {
        return null;
    }

    public void setContentID(String contentID) {
    }

    public String getContentID() {
        return null;
    }

    public void setFileName(String fileName) {
    }

    public String getFileName() {
        return null;
    }

	@Override
	public Id getUniqueId() {
		return getFileId();
	}

	@Override
	public void copyValuesFrom(Detail detail) {
	}

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        return masterClass == Mail.class;
	}

	@Override
	public String getContentType() {
        return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
        return null;
	}

	@Override
	public String getName() {
        return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}
}
