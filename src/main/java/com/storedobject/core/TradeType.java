package com.storedobject.core;

/**
 * The TradeType interface represents the type of purchase/sale.
 * <p>Note: Type value must be less than 1000. Type value 1001 is reserved for repair orders / loan out
 * (where the items are expected to return).</p>
 *
 * @author Syam
 */
public interface TradeType {

    /**
     * Get the type of purchase/sale. Overridden classes may define this if required.
     * <p>Note: Type value must be less than 1000. Type value 1001 is reserved for repair orders (where the items are
     * expected to return).</p>
     *
     * @return Type.
     */
    default int getType() {
        return 0;
    }
}
