package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryGRN;
import com.storedobject.core.StoredObject;

/**
 * Tag a logic to denote that it produces GRN entries.
 *
 * @author Syam
 */
public interface ProducesGRN {

    /**
     * This is invoked whenever status of a GRN is changed.
     *
     * @param grn GRN that is affected.
     */
    default void statusOfGRNChanged(InventoryGRN grn) {
    }
    
    /**
     * This is invoked when an "Extra Information" instance is created for a GRN. At this point, you may set your own
     * values if required.
     *
     * @param grn The GRN instance for which the "Extra Information" is created.
     * @param extraInfo Newly created "Extra Information" instance.
     */
    default void extraGRNInfoCreated(InventoryGRN grn, StoredObject extraInfo) {
    }

    /**
     * This is invoked when an existing "Extra Information" instance is loaded for the current object.
     * At this point, you may set your own values if required.
     *
     * @param grn The GRN instance for which the "Extra Information" is loaded.
     * @param extraInfo The "Extra Information" instance loaded now.
     */
    default void extraGRNInfoLoaded(InventoryGRN grn, StoredObject extraInfo) {
    }

    /**
     * This is invoked when an existing "Extra Information" instance is being saved.
     * <p>If an exception is thrown from this method, the save process will not happen.</p>
     *
     * @param grn The GRN instance for which the "Extra Information" is getting saved.
     * @param extraInfo The "Extra Information" instance to be saved.
     * @throws Exception if any validation error to be notified.
     */
    @SuppressWarnings("RedundantThrows")
    default void savingGRNExtraInfo(InventoryGRN grn, StoredObject extraInfo) throws Exception {
    }

    /**
     * This method is invoked when the button is pressed to mark the GRN as inspected/received. This method may show
     * appropriate messages and may return <code>false</code> if some other associated data is incomplete.
     *
     * @param grn Current GRN.
     * @return True/false.
     */
    default boolean canFinalize(InventoryGRN grn) {
        return true;
    }
}
