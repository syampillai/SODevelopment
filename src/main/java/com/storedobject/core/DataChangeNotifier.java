package com.storedobject.core;

/**
 * This interface represents a data change notifier.
 * You may implement this interface and create an entry in the {@link DataChangeNotifierLogic} for processing data
 * changes.
 * <p>Note: The class that implements this interface must have a default public constructor.</p>
 *
 * @author Syam
 */
public interface DataChangeNotifier {

    /**
     * This method is invoked to notify changes.
     *
     * @param change Change details.
     * @return Return <code>true</code> if it is processed successfully so that no further notification will be sent.
     */
    boolean changed(DataChanged change);
}
