package com.storedobject.core;

/**
 * The {@code HasStreamData} functional interface represents entities that provide access to an instance
 * of {@link StreamData}. Implementing this interface allows classes to encapsulate functionality for
 * retrieving stream data associated with them. This can be used in scenarios where the handling,
 * processing, or transformation of stream-based data is needed.
 *
 * @author Syam
 */
@FunctionalInterface
public interface HasStreamData extends ContentType {

    /**
     * Retrieves an instance of {@link StreamData}.
     *
     * @return a {@link StreamData} object, representing the stream data associated with the implementing entity.
     */
    StreamData getStreamData();

    @Override
    default String getContentType() {
        return getStreamData().getContentType();
    }

    /**
     * Retrieves the name associated with the implementing entity.
     *
     * @return a {@code String} representing the name, or {@code null} if no name is available.
     */
    default String getName() {
        return null;
    }

    /**
     * Determines the file name based on the name provided by the implementing entity and the file extension
     * associated with its {@link StreamData}. If no name is available, a unique name is generated using
     * {@link UniqueLong#get()} combined with the file extension.
     *
     * @return the generated or determined file name as a {@code String}. If the name is available and already
     * ends with the correct file extension, it is returned as is; otherwise, the correct extension is appended
     * to the name. If no name is available, a unique file name with the correct extension is returned.
     */
    default String getFileName() {
        String name = getName();
        StreamData sd = getStreamData();
        String ext = sd.getFileExtension();
        if(name == null || name.isEmpty()) {
            return UniqueLong.get() + "." + ext;
        }
        if(name.endsWith("." + ext)) {
            return name;
        }
        return name + "." + ext;
    }
}
