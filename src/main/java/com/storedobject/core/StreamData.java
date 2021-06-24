package com.storedobject.core;

import java.io.*;

public class StreamData extends StoredObject {
	
	public StreamData(String contentType) {
	}
	
	public StreamData() {
	}
	
	public static void columns(Columns columns) {
	}
	
	public void setStreamDataProvider(StreamDataProvider provider) {
	}

	public StreamDataProvider getStreamDataProvider() {
		return null;
	}

	public String getContentType() {
		return null;
	}

	public void setContentType(String contentType) {
	}

	public final String getMimeType() {
		return "";
	}

	public boolean isLink() {
		return false;
	}

	public boolean isImage() {
		return false;
    }
    
    public boolean isVideo() {
		return false;
    }
    
    public boolean isAudio() {
		return false;
    }
	
    public String getLink() {
    	return null;
    }
	
	public InputStream getContent() {
		return null;
	}

	public static StreamData getViaLink(String link) {
		return null;
	}

	public String getFileExtension() {
		return null;
	}
    
    public void view(Device device) {
    }
}
