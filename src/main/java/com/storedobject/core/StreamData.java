package com.storedobject.core;

import java.io.*;

public class StreamData extends StoredObject implements ContentType {
	
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

	/**
	 * Get the {@link StreamData} instance for the given name. The name could be the {@link Id} of the instance or the
	 * {@link Id} or name of a {@link FileData} instance as a string.
	 *
	 * @param name Name.
	 * @return {@link StreamData} instance if available.
	 */
	public static StreamData get(String name) {
		if(name == null || name.isBlank() || name.equals("0")) {
			return null;
		}
		StreamData sd = null;
		if(StringUtility.isDigit(name)) {
			sd = StreamData.get(StreamData.class, "Id=" + name);
			if(sd == null) {
				FileData file = FileData.get(FileData.class, "Id=" + name, true);
				if(file != null) {
					sd = file.getFile();
				}
			}
		} else {
			FileData file = FileData.get(name);
			if(file != null) {
				sd = file.getFile();
			}
		}
		return sd;
	}
}
