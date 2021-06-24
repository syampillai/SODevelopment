package com.storedobject.core;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stream data provider that provides data to StreamData objects.
 */
@FunctionalInterface
public interface StreamDataProvider {
    /**
     * Get an input stream for reading the data. If a stream for reading data can not be provided, return "null" so that writeStream() will be called.
     *
     * @return Input stream from which data can be obtained. The stream will be closed after reading the data from it.
     * @param streamData Stream data that is requesting the stream.
     * @throws Data_Not_Changed If this is thrown no data change will happen and previously stored data will remain. This should not be thrown for newly created StreamData.
     * @throws Exception Any exception other than Data_Not_Changed will cause the transaction "rollback".
     */
    InputStream getStream(StreamData streamData) throws Data_Not_Changed, Exception;

    /**
     * This method will be called only if getStream() returns "null".
     *
     * @param streamData Stream data that is requesting the stream.
     * @param output Output stream to which data can be written. There is no need to close the stream after writing.
     * @throws Exception Any exception thrown will cause the transaction "rollback".
     */
    default void writeStream(StreamData streamData, OutputStream output) throws Exception {
    }
}