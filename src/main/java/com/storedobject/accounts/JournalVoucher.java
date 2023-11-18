package com.storedobject.accounts;

import com.storedobject.core.Columns;

/**
 * Concrete implementation of the Journal Voucher (JV) (See {@link com.storedobject.core.JournalVoucher}).
 * This implementation doesn't have any "owner" and can be used to create generic JV entries.
 *
 * @author Syam
 */
public final class JournalVoucher extends com.storedobject.core.JournalVoucher {

    /**
     * Constructor.
     */
    public JournalVoucher() {
        setOwner(this);
    }

    /**
     * Column definitions.
     *
     * @param columns Column holder. Column definitions to be added to this.
     */
    public static void columns(Columns columns) {
    }
}